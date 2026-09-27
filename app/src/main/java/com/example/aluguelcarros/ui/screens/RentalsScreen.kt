package com.example.aluguelcarros.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.verticalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.CalendarToday
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.DirectionsCar
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.aluguelcarros.data.model.Customer
import com.example.aluguelcarros.data.model.Rental
import com.example.aluguelcarros.data.model.RentalStatus
import com.example.aluguelcarros.data.model.Vehicle
import com.example.aluguelcarros.ui.components.EmptyState
import com.example.aluguelcarros.ui.components.ScreenHeader
import com.example.aluguelcarros.ui.components.StatusBadge
import com.example.aluguelcarros.ui.formatCurrency
import com.example.aluguelcarros.ui.formatDate

private const val MILLIS_PER_DAY = 86_400_000L

@Composable
fun RentalsScreen(
    rentals: List<Rental>,
    vehicles: List<Vehicle>,
    customers: List<Customer>,
    availableVehicles: List<Vehicle>,
    onCreate: (Long, Long, Long, Long) -> Unit,
    onFinish: (Rental) -> Unit,
    onCancel: (Rental) -> Unit,
) {
    var showActiveOnly by rememberSaveable { mutableStateOf(true) }
    var formVisible by rememberSaveable { mutableStateOf(false) }
    val vehicleById = vehicles.associateBy { it.id }
    val customerById = customers.associateBy { it.id }
    val visibleRentals = if (showActiveOnly) {
        rentals.filter { it.status == RentalStatus.ACTIVE }
    } else {
        rentals
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 96.dp),
        ) {
            item {
                ScreenHeader(
                    title = "Locações",
                    subtitle = "Acompanhe contratos e devoluções",
                    action = {
                        Icon(
                            imageVector = Icons.Rounded.CalendarToday,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                        )
                    },
                )
            }
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    FilterChip(
                        selected = showActiveOnly,
                        onClick = { showActiveOnly = true },
                        label = { Text("Ativas") },
                    )
                    FilterChip(
                        selected = !showActiveOnly,
                        onClick = { showActiveOnly = false },
                        label = { Text("Todas") },
                    )
                }
            }
            if (visibleRentals.isEmpty()) {
                item {
                    EmptyState(
                        title = if (showActiveOnly) "Nenhuma locação ativa" else "Nenhuma locação registrada",
                        message = "Use o botão + para criar um novo contrato.",
                        modifier = Modifier.padding(horizontal = 20.dp),
                    )
                }
            } else {
                items(visibleRentals, key = { it.id }) { rental ->
                    RentalCard(
                        rental = rental,
                        vehicleName = vehicleById[rental.vehicleId]?.let { "${it.brand} ${it.model}" }
                            ?: "Veículo não encontrado",
                        customerName = customerById[rental.customerId]?.name ?: "Cliente não encontrado",
                        onFinish = { onFinish(rental) },
                        onCancel = { onCancel(rental) },
                    )
                }
            }
        }

        FloatingActionButton(
            onClick = { formVisible = true },
            modifier = Modifier
                .align(androidx.compose.ui.Alignment.BottomEnd)
                .padding(20.dp),
        ) {
            Icon(Icons.Rounded.Add, contentDescription = "Nova locação")
        }
    }

    if (formVisible) {
        RentalFormDialog(
            availableVehicles = availableVehicles,
            customers = customers,
            onDismiss = { formVisible = false },
            onCreate = { vehicleId, customerId, startDate, returnDate ->
                onCreate(vehicleId, customerId, startDate, returnDate)
                formVisible = false
            },
        )
    }
}

@Composable
private fun RentalCard(
    rental: Rental,
    vehicleName: String,
    customerName: String,
    onFinish: () -> Unit,
    onCancel: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 5.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        ),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = vehicleName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        text = customerName,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                StatusBadge(rental.status.label, rentalStatusColor(rental.status))
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column {
                    Text("Retirada", style = MaterialTheme.typography.labelMedium)
                    Text(formatDate(rental.startDate), style = MaterialTheme.typography.bodyMedium)
                }
                Column {
                    Text("Devolução", style = MaterialTheme.typography.labelMedium)
                    Text(formatDate(rental.expectedReturnDate), style = MaterialTheme.typography.bodyMedium)
                }
                Column(horizontalAlignment = androidx.compose.ui.Alignment.End) {
                    Text("Total", style = MaterialTheme.typography.labelMedium)
                    Text(
                        formatCurrency(rental.totalAmount),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
            if (rental.status == RentalStatus.ACTIVE) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.End,
                ) {
                    TextButton(onClick = onCancel) {
                        Icon(Icons.Rounded.Close, contentDescription = null)
                        Text("Cancelar", modifier = Modifier.padding(start = 4.dp))
                    }
                    Button(onClick = onFinish) {
                        Icon(Icons.Rounded.Check, contentDescription = null)
                        Text("Concluir", modifier = Modifier.padding(start = 4.dp))
                    }
                }
            }
        }
    }
}

private fun rentalStatusColor(status: RentalStatus): Color = when (status) {
    RentalStatus.ACTIVE -> Color(0xFF2457A6)
    RentalStatus.COMPLETED -> Color(0xFF2E7D5B)
    RentalStatus.CANCELLED -> Color(0xFF9E3D3D)
}

@Composable
private fun RentalFormDialog(
    availableVehicles: List<Vehicle>,
    customers: List<Customer>,
    onDismiss: () -> Unit,
    onCreate: (Long, Long, Long, Long) -> Unit,
) {
    var selectedVehicleId by remember(availableVehicles) {
        mutableStateOf(availableVehicles.firstOrNull()?.id)
    }
    var selectedCustomerId by remember(customers) {
        mutableStateOf(customers.firstOrNull()?.id)
    }
    var daysText by remember { mutableStateOf("1") }
    var vehicleMenuExpanded by remember { mutableStateOf(false) }
    var customerMenuExpanded by remember { mutableStateOf(false) }

    val selectedVehicle = availableVehicles.firstOrNull { it.id == selectedVehicleId }
    val selectedCustomer = customers.firstOrNull { it.id == selectedCustomerId }
    val days = daysText.toLongOrNull()?.takeIf { it > 0 }
    val canCreate = selectedVehicle != null && selectedCustomer != null && days != null
    val total = selectedVehicle?.dailyRate?.let { rate -> days?.times(rate) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nova locação") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Text(
                    text = "Selecione os envolvidos no contrato.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(
                        onClick = { vehicleMenuExpanded = true },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = availableVehicles.isNotEmpty(),
                    ) {
                        Icon(Icons.Rounded.DirectionsCar, contentDescription = null)
                        Text(
                            text = selectedVehicle?.let { "${it.brand} ${it.model}" }
                                ?: "Nenhum veículo disponível",
                            modifier = Modifier.padding(start = 8.dp),
                        )
                    }
                    DropdownMenu(
                        expanded = vehicleMenuExpanded,
                        onDismissRequest = { vehicleMenuExpanded = false },
                    ) {
                        availableVehicles.forEach { vehicle ->
                            DropdownMenuItem(
                                text = { Text("${vehicle.brand} ${vehicle.model} • ${vehicle.plate}") },
                                onClick = {
                                    selectedVehicleId = vehicle.id
                                    vehicleMenuExpanded = false
                                },
                            )
                        }
                    }
                }
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(
                        onClick = { customerMenuExpanded = true },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = customers.isNotEmpty(),
                    ) {
                        Text(
                            text = selectedCustomer?.name ?: "Nenhum cliente cadastrado",
                        )
                    }
                    DropdownMenu(
                        expanded = customerMenuExpanded,
                        onDismissRequest = { customerMenuExpanded = false },
                    ) {
                        customers.forEach { customer ->
                            DropdownMenuItem(
                                text = { Text(customer.name) },
                                onClick = {
                                    selectedCustomerId = customer.id
                                    customerMenuExpanded = false
                                },
                            )
                        }
                    }
                }
                OutlinedTextField(
                    value = daysText,
                    onValueChange = { daysText = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Quantidade de diárias") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                )
                if (total != null) {
                    Text(
                        text = "Total estimado: ${formatCurrency(total)}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
                if (availableVehicles.isEmpty() || customers.isEmpty()) {
                    Text(
                        text = "É necessário ter um veículo disponível e um cliente cadastrado.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val startDate = System.currentTimeMillis()
                    onCreate(
                        selectedVehicle!!.id,
                        selectedCustomer!!.id,
                        startDate,
                        startDate + days!! * MILLIS_PER_DAY,
                    )
                },
                enabled = canCreate,
            ) {
                Text("Criar locação")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        },
    )
}
