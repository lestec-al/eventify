package com.lestec.eventify

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lestec.eventify.data.LocalRepo
import com.lestec.eventify.ui.MainScreen
import com.lestec.eventify.ui.MainViewModel
import com.lestec.eventify.ui.theme.EventifyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EventifyTheme {
                MainScreen(
                    onBack = { finish() },
                    vm = viewModel(
                        factory = MainViewModel.Factory(LocalRepo.getInstance(this))
                    )
                )
            }
        }
    }
    override fun onDestroy() {
        LocalRepo.getInstance(this).closeDB()
        super.onDestroy()
    }
}