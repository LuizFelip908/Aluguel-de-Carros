package com.example.aluguelcarros.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CalendarToday
import androidx.compose.material.icons.rounded.DirectionsCar
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.aluguelcarros.ui.screens.CustomersScreen
import com.example.aluguelcarros.ui.screens.DashboardScreen
import com.example.aluguelcarros.ui.screens.RentalsScreen
import com.example.aluguelcarros.ui.screens.VehiclesScreen
import com.example.aluguelcarros.ui.viewmodel.LocadoraViewModel

private object Route {
    const val DASHBOARD = "dashboard"
    const val VEHICLES = "vehicles"
    const val CUSTOMERS = "customers"
    const val RENTALS = "rentals"
}

private data class Destination(
    val route: String,
    val label: String,
    val icon: ImageVector,
)

private val destinations = listOf(
    Destination(Route.DASHBOARD, "Início", Icons.Rounded.Home),
    Destination(Route.VEHICLES, "Veículos", Icons.Rounded.DirectionsCar),
    Destination(Route.CUSTOMERS, "Clientes", Icons.Rounded.Person),
    Destination(Route.RENTALS, "Locações", Icons.Rounded.CalendarToday),
)

@Composable
fun AluguelCarrosApp(viewModel: LocadoraViewModel) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val snackbarHostState = remember { SnackbarHostState() }
    val vehicles by viewModel.vehicles.collectAsStateWithLifecycle()
    val customers by viewModel.customers.collectAsStateWithLifecycle()
    val rentals by viewModel.rentals.collectAsStateWithLifecycle()
    val availableVehicles by viewModel.availableVehicles.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.feedback.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            NavigationBar {
                destinations.forEach { destination ->
                    NavigationBarItem(
                        selected = currentRoute == destination.route,
                        onClick = {
                            navController.navigate(destination.route) {
                                launchSingleTop = true
                            }
                        },
                        icon = {
                            androidx.compose.material3.Icon(
                                imageVector = destination.icon,
                                contentDescription = destination.label,
                            )
                        },
                        label = { Text(destination.label) },
                    )
                }
            }
        },
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Route.DASHBOARD,
            modifier = androidx.compose.ui.Modifier.padding(paddingValues),
        ) {
            composable(Route.DASHBOARD) {
                DashboardScreen(
                    vehicles = vehicles,
                    customers = customers,
                    rentals = rentals,
                    onOpenVehicles = { navController.navigate(Route.VEHICLES) },
                    onOpenRentals = { navController.navigate(Route.RENTALS) },
                )
            }
            composable(Route.VEHICLES) {
                VehiclesScreen(
                    vehicles = vehicles,
                    onSave = viewModel::saveVehicle,
                    onDelete = viewModel::deleteVehicle,
                )
            }
            composable(Route.CUSTOMERS) {
                CustomersScreen(
                    customers = customers,
                    onSave = viewModel::saveCustomer,
                    onDelete = viewModel::deleteCustomer,
                )
            }
            composable(Route.RENTALS) {
                RentalsScreen(
                    rentals = rentals,
                    vehicles = vehicles,
                    customers = customers,
                    availableVehicles = availableVehicles,
                    onCreate = viewModel::createRental,
                    onFinish = viewModel::finishRental,
                    onCancel = viewModel::cancelRental,
                )
            }
        }
    }
}
