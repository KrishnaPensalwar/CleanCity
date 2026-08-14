package com.example.cleancityapp.presentation.profile

import android.annotation.SuppressLint
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

private const val PRIVACY_POLICY_HTML = """
<!DOCTYPE html>
<html>
<head>
<meta name="viewport" content="width=device-width, initial-scale=1"/>
<style>
  body { font-family: -apple-system, Roboto, sans-serif; padding: 16px; color: #1A1A1A; line-height: 1.55; }
  h1 { font-size: 22px; }
  h2 { font-size: 16px; margin-top: 20px; }
  p, li { font-size: 14px; color: #444; }
</style>
</head>
<body>
  <h1>Privacy Policy</h1>
  <p>Clean City ("we", "our", or "us") respects your privacy. This policy explains how we collect, use, and protect information when you use the Clean City mobile application.</p>
  <h2>Information we collect</h2>
  <ul>
    <li>Account details such as name, email, phone, and city</li>
    <li>Report content including photos, descriptions, and location</li>
    <li>Device identifiers used for push notifications</li>
  </ul>
  <h2>How we use information</h2>
  <ul>
    <li>To create and manage your account</li>
    <li>To process and track waste reports</li>
    <li>To assign and complete driver tasks</li>
    <li>To send status notifications about your reports</li>
  </ul>
  <h2>Data security</h2>
  <p>Access tokens and sensitive preferences are stored using encrypted local storage on your device. Communication with our servers uses HTTPS.</p>
  <h2>Contact</h2>
  <p>For privacy questions, contact support at support@cleancity.app.</p>
  <p><em>Last updated: August 2026</em></p>
</body>
</html>
"""

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun PrivacyPolicyScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Privacy policy") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { padding ->
        AndroidView(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            factory = { context ->
                WebView(context).apply {
                    webViewClient = WebViewClient()
                    settings.javaScriptEnabled = false
                    loadDataWithBaseURL(null, PRIVACY_POLICY_HTML, "text/html", "UTF-8", null)
                }
            }
        )
    }
}
