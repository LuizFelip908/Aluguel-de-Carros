package com.example.aluguelcarros

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.aluguelcarros.ui.StarterScreen
import com.example.aluguelcarros.ui.theme.AluguelCarrosTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AluguelCarrosTheme {
                StarterScreen()
            }
        }
    }
}
