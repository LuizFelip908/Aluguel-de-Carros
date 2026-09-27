package com.example.aluguelcarros

import android.app.Application
import com.example.aluguelcarros.data.AppDatabase
import com.example.aluguelcarros.data.repository.RentalRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class AluguelCarrosApplication : Application() {
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    val database: AppDatabase by lazy { AppDatabase.getInstance(this) }
    val repository: RentalRepository by lazy { RentalRepository(database) }

    override fun onCreate() {
        super.onCreate()
        applicationScope.launch {
            repository.seedIfEmpty()
        }
    }
}
