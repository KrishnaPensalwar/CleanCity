package com.example.cleancityapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.cleancityapp.presentation.main.MainApp
import com.example.cleancityapp.presentation.main.MainViewModel
import com.example.cleancityapp.presentation.main.MainContract
import com.example.cleancityapp.ui.theme.CleanCityAppTheme
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        handleNotificationIntent(intent)
        setContent {
            MainApp(viewModel)
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleNotificationIntent(intent)
    }

    private fun handleNotificationIntent(intent: Intent) {
        val complaintId = intent.getStringExtra("complaintId")
        if (complaintId != null) {
            viewModel.processIntent(MainContract.Intent.HandleDeepLink(complaintId))
        }
    }
}
