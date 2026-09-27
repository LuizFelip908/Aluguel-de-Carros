package com.example.aluguelcarros.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.aluguelcarros.data.dao.CustomerDao
import com.example.aluguelcarros.data.dao.RentalDao
import com.example.aluguelcarros.data.dao.VehicleDao
import com.example.aluguelcarros.data.model.Customer
import com.example.aluguelcarros.data.model.DatabaseConverters
import com.example.aluguelcarros.data.model.Rental
import com.example.aluguelcarros.data.model.Vehicle

@Database(
    entities = [Vehicle::class, Customer::class, Rental::class],
    version = 1,
    exportSchema = false,
)
@TypeConverters(DatabaseConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun vehicleDao(): VehicleDao
    abstract fun customerDao(): CustomerDao
    abstract fun rentalDao(): RentalDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "aluguel_carros.db",
                ).fallbackToDestructiveMigration().build().also { instance = it }
            }
    }
}
