package com.example.aluguelcarros.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "customers",
    indices = [Index(value = ["cpf"], unique = true)],
)
data class Customer(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val cpf: String,
    val driverLicense: String,
    val phone: String,
    val email: String,
)
