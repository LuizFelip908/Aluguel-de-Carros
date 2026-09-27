package com.example.aluguelcarros.ui

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val brazilianLocale = Locale("pt", "BR")

fun formatCurrency(value: Double): String =
    NumberFormat.getCurrencyInstance(brazilianLocale).format(value)

fun formatDate(value: Long): String =
    SimpleDateFormat("dd/MM/yyyy", brazilianLocale).format(Date(value))
