package com.example.aluguelcarros.data.model

import androidx.room.TypeConverter

class DatabaseConverters {
    @TypeConverter
    fun fromVehicleStatus(status: VehicleStatus): String = status.name

    @TypeConverter
    fun toVehicleStatus(value: String): VehicleStatus =
        VehicleStatus.values().firstOrNull { it.name == value } ?: VehicleStatus.AVAILABLE

    @TypeConverter
    fun fromRentalStatus(status: RentalStatus): String = status.name

    @TypeConverter
    fun toRentalStatus(value: String): RentalStatus =
        RentalStatus.values().firstOrNull { it.name == value } ?: RentalStatus.ACTIVE
}
