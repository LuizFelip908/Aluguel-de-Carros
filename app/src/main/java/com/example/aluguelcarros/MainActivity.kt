package com.example.aluguelcarros

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.aluguelcarros.ui.navigation.AluguelCarrosApp
import com.example.aluguelcarros.ui.theme.AluguelCarrosTheme
import com.example.aluguelcarros.ui.viewmodel.LocadoraViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val app = application as AluguelCarrosApplication
        setContent {
            val viewModel: LocadoraViewModel = viewModel(
                factory = LocadoraViewModel.factory(app.repository),
            )
            AluguelCarrosTheme {
                AluguelCarrosApp(viewModel)
            }
        }
    }
}
