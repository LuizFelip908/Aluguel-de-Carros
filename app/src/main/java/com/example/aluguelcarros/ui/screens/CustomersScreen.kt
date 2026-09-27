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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.aluguelcarros.data.model.Customer
import com.example.aluguelcarros.ui.components.EmptyState
import com.example.aluguelcarros.ui.components.ScreenHeader

@Composable
fun CustomersScreen(
    customers: List<Customer>,
    onSave: (Customer) -> Unit,
    onDelete: (Customer) -> Unit,
) {
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var formVisible by rememberSaveable { mutableStateOf(false) }
    var editingCustomer by remember { mutableStateOf<Customer?>(null) }

    val filteredCustomers = customers.filter { customer ->
        searchQuery.isBlank() || listOf(
            customer.name,
            customer.cpf,
            customer.driverLicense,
        ).any { it.contains(searchQuery, ignoreCase = true) }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 96.dp),
        ) {
            item {
                ScreenHeader(
                    title = "Clientes",
                    subtitle = "Mantenha os dados dos locatários organizados",
                    action = {
                        Icon(
                            imageVector = Icons.Rounded.Person,
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
                    label = { Text("Buscar por nome, CPF ou CNH") },
                    leadingIcon = {
                        Icon(Icons.Rounded.Search, contentDescription = null)
                    },
                )
            }
            if (filteredCustomers.isEmpty()) {
                item {
                    EmptyState(
                        title = "Nenhum cliente encontrado",
                        message = "Cadastre o primeiro cliente para iniciar uma locação.",
                        modifier = Modifier.padding(horizontal = 20.dp),
                    )
                }
            } else {
                items(filteredCustomers, key = { it.id }) { customer ->
                    CustomerCard(
                        customer = customer,
                        onEdit = {
                            editingCustomer = customer
                            formVisible = true
                        },
                        onDelete = { onDelete(customer) },
                    )
                }
            }
        }

        FloatingActionButton(
            onClick = {
                editingCustomer = null
                formVisible = true
            },
            modifier = Modifier
                .align(androidx.compose.ui.Alignment.BottomEnd)
                .padding(20.dp),
        ) {
            Icon(Icons.Rounded.Add, contentDescription = "Adicionar cliente")
        }
    }

    if (formVisible) {
        CustomerFormDialog(
            initialCustomer = editingCustomer,
            onDismiss = { formVisible = false },
            onSave = {
                onSave(it)
                formVisible = false
            },
        )
    }
}

@Composable
private fun CustomerCard(
    customer: Customer,
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
                imageVector = Icons.Rounded.Person,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 14.dp),
            ) {
                Text(
                    text = customer.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = "CPF ${customer.cpf}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = "${customer.phone} • CNH ${customer.driverLicense}",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
            Column {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Rounded.Edit, contentDescription = "Editar cliente")
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Rounded.Delete, contentDescription = "Excluir cliente")
                }
            }
        }
    }
}

@Composable
private fun CustomerFormDialog(
    initialCustomer: Customer?,
    onDismiss: () -> Unit,
    onSave: (Customer) -> Unit,
) {
    var name by remember(initialCustomer?.id) { mutableStateOf(initialCustomer?.name.orEmpty()) }
    var cpf by remember(initialCustomer?.id) { mutableStateOf(initialCustomer?.cpf.orEmpty()) }
    var driverLicense by remember(initialCustomer?.id) {
        mutableStateOf(initialCustomer?.driverLicense.orEmpty())
    }
    var phone by remember(initialCustomer?.id) { mutableStateOf(initialCustomer?.phone.orEmpty()) }
    var email by remember(initialCustomer?.id) { mutableStateOf(initialCustomer?.email.orEmpty()) }
    val canSave = name.isNotBlank() && cpf.isNotBlank() && driverLicense.isNotBlank() &&
        phone.isNotBlank() && email.isNotBlank()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initialCustomer == null) "Novo cliente" else "Editar cliente") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Nome completo") },
                    singleLine = true,
                )
                OutlinedTextField(
                    value = cpf,
                    onValueChange = { cpf = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("CPF") },
                    singleLine = true,
                )
                OutlinedTextField(
                    value = driverLicense,
                    onValueChange = { driverLicense = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("CNH") },
                    singleLine = true,
                )
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Telefone") },
                    singleLine = true,
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("E-mail") },
                    singleLine = true,
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(
                        Customer(
                            id = initialCustomer?.id ?: 0,
                            name = name.trim(),
                            cpf = cpf.trim(),
                            driverLicense = driverLicense.trim(),
                            phone = phone.trim(),
                            email = email.trim(),
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
