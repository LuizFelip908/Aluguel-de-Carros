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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.DirectionsCar
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import com.example.aluguelcarros.data.model.Vehicle
import com.example.aluguelcarros.data.model.VehicleStatus
import com.example.aluguelcarros.ui.components.EmptyState
import com.example.aluguelcarros.ui.components.ScreenHeader
import com.example.aluguelcarros.ui.components.StatusBadge

@Composable
fun VehiclesScreen(
    vehicles: List<Vehicle>,
    onSave: (Vehicle) -> Unit,
    onDelete: (Vehicle) -> Unit,
) {
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var statusFilter by remember { mutableStateOf<VehicleStatus?>(null) }
    var formVisible by rememberSaveable { mutableStateOf(false) }
    var editingVehicle by remember { mutableStateOf<Vehicle?>(null) }

    val filteredVehicles = vehicles.filter { vehicle ->
        val matchesSearch = searchQuery.isBlank() || listOf(
            vehicle.brand,
            vehicle.model,
            vehicle.plate,
        ).any { it.contains(searchQuery, ignoreCase = true) }
        val matchesStatus = statusFilter == null || vehicle.status == statusFilter
        matchesSearch && matchesStatus
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 96.dp),
        ) {
            item {
                ScreenHeader(
                    title = "Veículos",
                    subtitle = "Gerencie a frota e os valores das diárias",
                    action = {
                        Icon(
                            imageVector = Icons.Rounded.DirectionsCar,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                        )
                    },
                )
            }
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    singleLine = true,
                    label = { Text("Buscar veículo") },
                    leadingIcon = {
                        Icon(Icons.Rounded.Search, contentDescription = null)
                    },
                )
            }
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    FilterChip(
                        selected = statusFilter == null,
                        onClick = { statusFilter = null },
                        label = { Text("Todos") },
                    )
                    VehicleStatus.values().forEach { status ->
                        FilterChip(
                            selected = statusFilter == status,
                            onClick = { statusFilter = status },
                            label = { Text(status.label) },
                        )
                    }
                }
            }
            if (filteredVehicles.isEmpty()) {
                item {
                    EmptyState(
                        title = "Nenhum veículo encontrado",
                        message = "Cadastre um veículo ou ajuste os filtros da busca.",
                        modifier = Modifier.padding(horizontal = 20.dp),
                    )
                }
            } else {
                items(filteredVehicles, key = { it.id }) { vehicle ->
                    VehicleCard(
                        vehicle = vehicle,
                        onEdit = {
                            editingVehicle = vehicle
                            formVisible = true
                        },
                        onDelete = { onDelete(vehicle) },
                    )
                }
            }
        }

        FloatingActionButton(
            onClick = {
                editingVehicle = null
                formVisible = true
            },
            modifier = Modifier
                .align(androidx.compose.ui.Alignment.BottomEnd)
                .padding(20.dp),
        ) {
            Icon(Icons.Rounded.Add, contentDescription = "Adicionar veículo")
        }
    }

    if (formVisible) {
        VehicleFormDialog(
            initialVehicle = editingVehicle,
            onDismiss = { formVisible = false },
            onSave = {
                onSave(it)
                formVisible = false
            },
        )
    }
}

@Composable
private fun VehicleCard(
    vehicle: Vehicle,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
) {
    Card(
        onClick = onEdit,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 5.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        ),
    ) {
        Row(
            modifier = Modifier.padding(start = 16.dp, top = 14.dp, bottom = 14.dp),
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Rounded.DirectionsCar,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 14.dp),
            ) {
                Text(
                    text = "${vehicle.brand} ${vehicle.model}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = "${vehicle.plate} • ${vehicle.year}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = "${com.example.aluguelcarros.ui.formatCurrency(vehicle.dailyRate)} / dia",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
            Column(
                horizontalAlignment = androidx.compose.ui.Alignment.End,
                modifier = Modifier.padding(end = 4.dp),
            ) {
                StatusBadge(vehicle.status.label, vehicleStatusColor(vehicle.status))
                Row {
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Rounded.Edit, contentDescription = "Editar veículo")
                    }
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Rounded.Delete, contentDescription = "Excluir veículo")
                    }
                }
            }
        }
    }
}

private fun vehicleStatusColor(status: VehicleStatus): Color = when (status) {
    VehicleStatus.AVAILABLE -> Color(0xFF2E7D5B)
    VehicleStatus.RENTED -> Color(0xFF2457A6)
    VehicleStatus.MAINTENANCE -> Color(0xFFB26A00)
}

@Composable
private fun VehicleFormDialog(
    initialVehicle: Vehicle?,
    onDismiss: () -> Unit,
    onSave: (Vehicle) -> Unit,
) {
    var brand by remember(initialVehicle?.id) { mutableStateOf(initialVehicle?.brand.orEmpty()) }
    var model by remember(initialVehicle?.id) { mutableStateOf(initialVehicle?.model.orEmpty()) }
    var year by remember(initialVehicle?.id) { mutableStateOf(initialVehicle?.year?.toString().orEmpty()) }
    var plate by remember(initialVehicle?.id) { mutableStateOf(initialVehicle?.plate.orEmpty()) }
    var dailyRate by remember(initialVehicle?.id) {
        mutableStateOf(initialVehicle?.dailyRate?.toString()?.replace('.', ',').orEmpty())
    }
    val canSave = brand.isNotBlank() && model.isNotBlank() && plate.isNotBlank() &&
        year.toIntOrNull() != null && dailyRate.replace(',', '.').toDoubleOrNull() != null

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initialVehicle == null) "Novo veículo" else "Editar veículo") },
        text = {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                OutlinedTextField(
                    value = brand,
                    onValueChange = { brand = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Marca") },
                    singleLine = true,
                )
                OutlinedTextField(
                    value = model,
                    onValueChange = { model = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Modelo") },
                    singleLine = true,
                )
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = year,
                        onValueChange = { year = it },
                        modifier = Modifier.weight(1f),
                        label = { Text("Ano") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                    )
                    OutlinedTextField(
                        value = plate,
                        onValueChange = { plate = it.uppercase() },
                        modifier = Modifier.weight(1.5f),
                        label = { Text("Placa") },
                        singleLine = true,
                    )
                }
                OutlinedTextField(
                    value = dailyRate,
                    onValueChange = { dailyRate = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Diária (R$)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(
                        Vehicle(
                            id = initialVehicle?.id ?: 0,
                            brand = brand.trim(),
                            model = model.trim(),
                            year = year.toInt(),
                            plate = plate.trim(),
                            dailyRate = dailyRate.replace(',', '.').toDouble(),
                            status = initialVehicle?.status ?: VehicleStatus.AVAILABLE,
                        ),
                    )
                },
                enabled = canSave,
            ) {
                Text("Salvar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        },
    )
}
