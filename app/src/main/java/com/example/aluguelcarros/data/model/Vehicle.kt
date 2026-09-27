package com.example.aluguelcarros.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

enum class VehicleStatus(val label: String) {
    AVAILABLE("Disponível"),
    RENTED("Alugado"),
    MAINTENANCE("Manutenção"),
}

@Entity(
    tableName = "vehicles",
    indices = [Index(value = ["plate"], unique = true)],
)
data class Vehicle(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val brand: String,
    val model: String,
    val year: Int,
    val plate: String,
    val dailyRate: Double,
    val status: VehicleStatus = VehicleStatus.AVAILABLE,
)
