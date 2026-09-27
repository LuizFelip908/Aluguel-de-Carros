package com.example.aluguelcarros.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.aluguelcarros.data.model.Rental
import com.example.aluguelcarros.data.model.RentalStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface RentalDao {
    @Query("SELECT * FROM rentals ORDER BY startDate DESC")
    fun observeAll(): Flow<List<Rental>>

    @Query("SELECT * FROM rentals WHERE status = :status ORDER BY startDate DESC")
    fun observeByStatus(status: RentalStatus): Flow<List<Rental>>

    @Query("SELECT * FROM rentals WHERE id = :id LIMIT 1")
    suspend fun findById(id: Long): Rental?

    @Query("SELECT COUNT(*) FROM rentals")
    suspend fun count(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(rental: Rental): Long

    @Update
    suspend fun update(rental: Rental)

    @Query("UPDATE rentals SET status = :status, actualReturnDate = :returnDate WHERE id = :id")
    suspend fun updateStatus(id: Long, status: RentalStatus, returnDate: Long?)
}
