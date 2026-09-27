package com.example.aluguelcarros.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

enum class RentalStatus(val label: String) {
    ACTIVE("Ativa"),
    COMPLETED("Concluída"),
    CANCELLED("Cancelada"),
}

@Entity(
    tableName = "rentals",
    foreignKeys = [
        ForeignKey(
            entity = Vehicle::class,
            parentColumns = ["id"],
            childColumns = ["vehicleId"],
            onDelete = ForeignKey.RESTRICT,
        ),
        ForeignKey(
            entity = Customer::class,
            parentColumns = ["id"],
            childColumns = ["customerId"],
            onDelete = ForeignKey.RESTRICT,
        ),
    ],
    indices = [Index("vehicleId"), Index("customerId")],
)
data class Rental(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val vehicleId: Long,
    val customerId: Long,
    val startDate: Long,
    val expectedReturnDate: Long,
    val actualReturnDate: Long? = null,
    val totalAmount: Double,
    val status: RentalStatus = RentalStatus.ACTIVE,
)
