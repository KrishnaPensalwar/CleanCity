package com.example.cleancityapp.presentation.auth.debug

import androidx.compose.runtime.Composable

/**
 * Release no-op: test accounts must not appear in production builds.
 */
object TestAccountsNav {
    const val isEnabled: Boolean = false

    @Composable
    fun DebugLoginButton(enabled: Boolean, onClick: () -> Unit) = Unit

    @Composable
    fun Content(onLoginSuccess: () -> Unit, onBack: () -> Unit) = Unit
}
