package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.data.local.AppDatabase
import com.example.data.remote.GeminiScamService
import com.example.data.repository.SafeNestRepository
import com.example.ui.navigation.SafeNestApp
import com.example.ui.theme.BackgroundLight
import com.example.ui.theme.SafeNestTheme
import com.example.ui.viewmodel.SafeNestViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: SafeNestViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SafeNestTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = BackgroundLight
                ) {
                    SafeNestApp(viewModel = viewModel)
                }
            }
        }
    }
}

