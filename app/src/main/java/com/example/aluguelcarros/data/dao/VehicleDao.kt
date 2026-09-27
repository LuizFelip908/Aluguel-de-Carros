package com.example.aluguelcarros.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.aluguelcarros.data.model.Vehicle
import com.example.aluguelcarros.data.model.VehicleStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface VehicleDao {
    @Query("SELECT * FROM vehicles ORDER BY brand COLLATE NOCASE, model COLLATE NOCASE")
    fun observeAll(): Flow<List<Vehicle>>

    @Query("SELECT * FROM vehicles WHERE status = :status ORDER BY brand COLLATE NOCASE, model COLLATE NOCASE")
    fun observeByStatus(status: VehicleStatus): Flow<List<Vehicle>>

    @Query("SELECT * FROM vehicles WHERE id = :id LIMIT 1")
    suspend fun findById(id: Long): Vehicle?

    @Query("SELECT COUNT(*) FROM vehicles")
    suspend fun count(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vehicle: Vehicle): Long

    @Update
    suspend fun update(vehicle: Vehicle)

    @Delete
    suspend fun delete(vehicle: Vehicle)

    @Query("UPDATE vehicles SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: Long, status: VehicleStatus)
}
