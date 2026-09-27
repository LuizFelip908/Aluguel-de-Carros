package com.example.aluguelcarros.data.repository

import androidx.room.withTransaction
import com.example.aluguelcarros.data.AppDatabase
import com.example.aluguelcarros.data.model.Customer
import com.example.aluguelcarros.data.model.Rental
import com.example.aluguelcarros.data.model.RentalStatus
import com.example.aluguelcarros.data.model.Vehicle
import com.example.aluguelcarros.data.model.VehicleStatus
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.flow.Flow

class RentalRepository(private val database: AppDatabase) {
    private val vehicleDao = database.vehicleDao()
    private val customerDao = database.customerDao()
    private val rentalDao = database.rentalDao()

    val vehicles: Flow<List<Vehicle>> = vehicleDao.observeAll()
    val customers: Flow<List<Customer>> = customerDao.observeAll()
    val rentals: Flow<List<Rental>> = rentalDao.observeAll()
    val availableVehicles: Flow<List<Vehicle>> = vehicleDao.observeByStatus(VehicleStatus.AVAILABLE)
    val activeRentals: Flow<List<Rental>> = rentalDao.observeByStatus(RentalStatus.ACTIVE)

    suspend fun saveVehicle(vehicle: Vehicle) {
        if (vehicle.id == 0L) vehicleDao.insert(vehicle) else vehicleDao.update(vehicle)
    }

    suspend fun deleteVehicle(vehicle: Vehicle) = vehicleDao.delete(vehicle)

    suspend fun saveCustomer(customer: Customer) {
        if (customer.id == 0L) customerDao.insert(customer) else customerDao.update(customer)
    }

    suspend fun deleteCustomer(customer: Customer) = customerDao.delete(customer)

    suspend fun createRental(
        vehicleId: Long,
        customerId: Long,
        startDate: Long,
        expectedReturnDate: Long,
    ) {
        database.withTransaction {
            val vehicle = requireNotNull(vehicleDao.findById(vehicleId))
            val rentalDays = maxOf(
                1L,
                TimeUnit.MILLISECONDS.toDays(expectedReturnDate - startDate),
            )
            rentalDao.insert(
                Rental(
                    vehicleId = vehicleId,
                    customerId = customerId,
                    startDate = startDate,
                    expectedReturnDate = expectedReturnDate,
                    totalAmount = rentalDays * vehicle.dailyRate,
                ),
            )
            vehicleDao.updateStatus(vehicleId, VehicleStatus.RENTED)
        }
    }

    suspend fun finishRental(rental: Rental) {
        database.withTransaction {
            rentalDao.updateStatus(
                id = rental.id,
                status = RentalStatus.COMPLETED,
                returnDate = System.currentTimeMillis(),
            )
            vehicleDao.updateStatus(rental.vehicleId, VehicleStatus.AVAILABLE)
        }
    }

    suspend fun cancelRental(rental: Rental) {
        database.withTransaction {
            rentalDao.updateStatus(
                id = rental.id,
                status = RentalStatus.CANCELLED,
                returnDate = null,
            )
            vehicleDao.updateStatus(rental.vehicleId, VehicleStatus.AVAILABLE)
        }
    }

    suspend fun seedIfEmpty() {
        if (vehicleDao.count() > 0 || customerDao.count() > 0 || rentalDao.count() > 0) return

        database.withTransaction {
            val cityId = vehicleDao.insert(
                Vehicle(
                    brand = "Chevrolet",
                    model = "Onix",
                    year = 2024,
                    plate = "ABC1D23",
                    dailyRate = 149.90,
                ),
            )
            val suvId = vehicleDao.insert(
                Vehicle(
                    brand = "Jeep",
                    model = "Renegade",
                    year = 2023,
                    plate = "EFG4H56",
                    dailyRate = 229.90,
                    status = VehicleStatus.RENTED,
                ),
            )
            vehicleDao.insert(
                Vehicle(
                    brand = "Toyota",
                    model = "Corolla",
                    year = 2025,
                    plate = "IJK7L89",
                    dailyRate = 289.90,
                    status = VehicleStatus.MAINTENANCE,
                ),
            )

            val anaId = customerDao.insert(
                Customer(
                    name = "Ana Paula Souza",
                    cpf = "123.456.789-00",
                    driverLicense = "01234567890",
                    phone = "(11) 98888-7777",
                    email = "ana.souza@email.com",
                ),
            )
            val brunoId = customerDao.insert(
                Customer(
                    name = "Bruno Martins",
                    cpf = "987.654.321-00",
                    driverLicense = "09876543210",
                    phone = "(11) 97777-6666",
                    email = "bruno.martins@email.com",
                ),
            )

            val today = System.currentTimeMillis()
            rentalDao.insert(
                Rental(
                    vehicleId = suvId,
                    customerId = brunoId,
                    startDate = today - TimeUnit.DAYS.toMillis(2),
                    expectedReturnDate = today + TimeUnit.DAYS.toMillis(3),
                    totalAmount = 5 * 229.90,
                ),
            )
            // Mantém o primeiro cliente utilizado pelo exemplo no banco para a tela de clientes.
            check(anaId > 0 && cityId > 0)
        }
    }
}
