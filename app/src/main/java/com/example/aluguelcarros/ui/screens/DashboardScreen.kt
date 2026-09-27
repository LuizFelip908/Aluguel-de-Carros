package com.example.aluguelcarros.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AttachMoney
import androidx.compose.material.icons.rounded.DirectionsCar
import androidx.compose.material.icons.rounded.People
import androidx.compose.material.icons.rounded.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import com.example.aluguelcarros.data.model.Customer
import com.example.aluguelcarros.data.model.Rental
import com.example.aluguelcarros.data.model.RentalStatus
import com.example.aluguelcarros.data.model.Vehicle
import com.example.aluguelcarros.data.model.VehicleStatus
import com.example.aluguelcarros.ui.components.EmptyState
import com.example.aluguelcarros.ui.components.MetricCard
import com.example.aluguelcarros.ui.components.ScreenHeader
import com.example.aluguelcarros.ui.components.SectionTitle
import com.example.aluguelcarros.ui.components.StatusBadge
import com.example.aluguelcarros.ui.formatCurrency
import com.example.aluguelcarros.ui.formatDate

@Composable
fun DashboardScreen(
    vehicles: List<Vehicle>,
    customers: List<Customer>,
    rentals: List<Rental>,
    onOpenVehicles: () -> Unit,
    onOpenRentals: () -> Unit,
) {
    val activeRentals = rentals.filter { it.status == RentalStatus.ACTIVE }
    val revenue = rentals
        .filter { it.status != RentalStatus.CANCELLED }
        .sumOf { it.totalAmount }
    val availableVehicles = vehicles.count { it.status == VehicleStatus.AVAILABLE }
    val rentedVehicles = vehicles.count { it.status == VehicleStatus.RENTED }
    val maintenanceVehicles = vehicles.count { it.status == VehicleStatus.MAINTENANCE }
    val vehicleById = vehicles.associateBy { it.id }
    val customerById = customers.associateBy { it.id }

    LazyColumn(
        contentPadding = PaddingValues(bottom = 28.dp),
    ) {
        item {
            ScreenHeader(
                title = "Visão geral",
                subtitle = "Acompanhe a operação da sua locadora",
            )
        }
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                MetricCard(
                    modifier = Modifier.weight(1f),
                    label = "Faturamento",
                    value = formatCurrency(revenue),
                    icon = Icons.Rounded.AttachMoney,
                    tint = Color(0xFF2E7D5B),
                )
                MetricCard(
                    modifier = Modifier.weight(1f),
                    label = "Locações ativas",
                    value = activeRentals.size.toString(),
                    icon = Icons.Rounded.TrendingUp,
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
        }
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                MetricCard(
                    modifier = Modifier.weight(1f),
                    label = "Veículos",
                    value = vehicles.size.toString(),
                    icon = Icons.Rounded.DirectionsCar,
                    tint = Color(0xFF8B5E34),
                )
                MetricCard(
                    modifier = Modifier.weight(1f),
                    label = "Clientes",
                    value = customers.size.toString(),
                    icon = Icons.Rounded.People,
                    tint = Color(0xFF7055A6),
                )
            }
        }
        item {
            SectionTitle(title = "Resumo da frota")
        }
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                ),
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    FleetLine("Disponíveis", availableVehicles, Color(0xFF2E7D5B))
                    FleetLine("Alugados", rentedVehicles, MaterialTheme.colorScheme.primary)
                    FleetLine("Em manutenção", maintenanceVehicles, Color(0xFFB26A00))
                }
            }
        }
        item {
            SectionTitle(title = "Ações rápidas")
        }
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Button(
                    onClick = onOpenVehicles,
                    modifier = Modifier.weight(1f),
                ) {
                    Text("Ver frota")
                }
                OutlinedButton(
                    onClick = onOpenRentals,
                    modifier = Modifier.weight(1f),
                ) {
                    Text("Nova locação")
                }
            }
        }
        item {
            SectionTitle(title = "Locações recentes")
        }
        if (activeRentals.isEmpty()) {
            item {
                EmptyState(
                    title = "Nenhuma locação ativa",
                    message = "Quando uma locação for criada, ela aparecerá aqui.",
                    modifier = Modifier.padding(horizontal = 20.dp),
                )
            }
        } else {
            items(activeRentals.take(3), key = { it.id }) { rental ->
                RentalSummaryCard(
                    rental = rental,
                    vehicleName = vehicleById[rental.vehicleId]?.let { "${it.brand} ${it.model}" }
                        ?: "Veículo não encontrado",
                    customerName = customerById[rental.customerId]?.name ?: "Cliente não encontrado",
                )
            }
        }
    }
}

@Composable
private fun FleetLine(label: String, quantity: Int, color: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            androidx.compose.material3.Surface(
                modifier = Modifier.size(10.dp),
                shape = RoundedCornerShape(50),
                color = color,
            ) {}
            Text(label, style = MaterialTheme.typography.bodyMedium)
        }
        Text(
            text = quantity.toString(),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun RentalSummaryCard(
    rental: Rental,
    vehicleName: String,
    customerName: String,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 5.dp),
        shape = RoundedCornerShape(18.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(vehicleName, fontWeight = FontWeight.SemiBold)
                StatusBadge("Ativa", MaterialTheme.colorScheme.primary)
            }
            Text(
                text = customerName,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp),
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = "Retorno ${formatDate(rental.expectedReturnDate)}",
                    style = MaterialTheme.typography.labelLarge,
                )
                Text(
                    text = formatCurrency(rental.totalAmount),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}
