package com.lestec.eventify

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.lestec.eventify.data.LocalRepo
import com.lestec.eventify.ui.CalendarScreen
import com.lestec.eventify.ui.MainViewModel
import com.lestec.eventify.ui.Screens
import com.lestec.eventify.ui.SettingsScreen
import com.lestec.eventify.ui.theme.EventifyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val vm: MainViewModel = viewModel(factory = MainViewModel.Factory(LocalRepo.getInstance(this)))
            val nav = rememberNavController()
            EventifyTheme {
                NavHost(
                    navController = nav,
                    startDestination = Screens.Calendar.name,
                    modifier = Modifier.fillMaxSize()
                ) {
                    composable(Screens.Calendar.name) {
                        CalendarScreen(
                            onBack = ::finish,
                            onSettings = { nav.navigate(Screens.Settings.name) },
                            vm = vm
                        )
                    }
                    composable(Screens.Settings.name) {
                        SettingsScreen(
                            onBack = nav::popBackStack,
                            vm = vm
                        )
                    }
                }
            }
        }
    }
    override fun onDestroy() {
        LocalRepo.getInstance(this).closeDB()
        super.onDestroy()
    }
}