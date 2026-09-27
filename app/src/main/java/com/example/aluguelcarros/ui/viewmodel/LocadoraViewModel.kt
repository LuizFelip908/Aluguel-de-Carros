package com.example.aluguelcarros.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.aluguelcarros.data.model.Customer
import com.example.aluguelcarros.data.model.Rental
import com.example.aluguelcarros.data.model.Vehicle
import com.example.aluguelcarros.data.repository.RentalRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class LocadoraViewModel(private val repository: RentalRepository) : ViewModel() {
    val vehicles: StateFlow<List<Vehicle>> = repository.vehicles.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList(),
    )

    val customers: StateFlow<List<Customer>> = repository.customers.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList(),
    )

    val rentals: StateFlow<List<Rental>> = repository.rentals.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList(),
    )

    val availableVehicles: StateFlow<List<Vehicle>> = repository.availableVehicles.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList(),
    )

    private val _feedback = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val feedback = _feedback.asSharedFlow()

    fun saveVehicle(vehicle: Vehicle) = execute("Veículo salvo com sucesso.") {
        repository.saveVehicle(vehicle)
    }

    fun deleteVehicle(vehicle: Vehicle) = execute("Veículo removido.") {
        repository.deleteVehicle(vehicle)
    }

    fun saveCustomer(customer: Customer) = execute("Cliente salvo com sucesso.") {
        repository.saveCustomer(customer)
    }

    fun deleteCustomer(customer: Customer) = execute("Cliente removido.") {
        repository.deleteCustomer(customer)
    }

    fun createRental(
        vehicleId: Long,
        customerId: Long,
        startDate: Long,
        expectedReturnDate: Long,
    ) = execute("Locação criada com sucesso.") {
        repository.createRental(vehicleId, customerId, startDate, expectedReturnDate)
    }

    fun finishRental(rental: Rental) = execute("Locação concluída. O veículo está disponível.") {
        repository.finishRental(rental)
    }

    fun cancelRental(rental: Rental) = execute("Locação cancelada.") {
        repository.cancelRental(rental)
    }

    private fun execute(successMessage: String, action: suspend () -> Unit) {
        viewModelScope.launch {
            try {
                action()
                _feedback.emit(successMessage)
            } catch (_: Exception) {
                _feedback.emit("Não foi possível concluir a operação.")
            }
        }
    }

    companion object {
        fun factory(repository: RentalRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    if (modelClass.isAssignableFrom(LocadoraViewModel::class.java)) {
                        return LocadoraViewModel(repository) as T
                    }
                    throw IllegalArgumentException("ViewModel desconhecida: ${modelClass.name}")
                }
            }
    }
}
