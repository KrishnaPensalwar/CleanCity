package com.example.cleancityapp.presentation.auth.debug

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Debug wiring for the test-accounts shortcut. Release counterpart is a no-op.
 */
object TestAccountsNav {
    const val isEnabled: Boolean = true

    @Composable
    fun DebugLoginButton(enabled: Boolean, onClick: () -> Unit) {
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedButton(
            onClick = onClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(16.dp),
            enabled = enabled,
        ) {
            Text("Continue with test accounts", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
        }
    }

    @Composable
    fun Content(onLoginSuccess: () -> Unit, onBack: () -> Unit) {
        TestAccountsScreen(onLoginSuccess = onLoginSuccess, onBack = onBack)
    }
}
