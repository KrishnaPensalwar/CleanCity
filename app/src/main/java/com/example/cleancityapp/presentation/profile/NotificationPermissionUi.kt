package com.example.cleancityapp.presentation.profile

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner

data class NotificationPermissionUi(
    val isOn: Boolean,
    val onCheckedChange: (Boolean) -> Unit,
)

@Composable
fun rememberNotificationPermissionUi(
    notificationsEnabled: Boolean,
    onNotificationsToggle: (Boolean) -> Unit,
): NotificationPermissionUi {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var osGranted by remember { mutableStateOf(isNotificationPermissionGranted(context)) }
    var hasRequestedOnce by remember { mutableStateOf(false) }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                osGranted = isNotificationPermissionGranted(context)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        osGranted = granted
        onNotificationsToggle(granted)
    }

    val isOn = notificationsEnabled && osGranted
    return NotificationPermissionUi(
        isOn = isOn,
        onCheckedChange = { enable ->
            if (!enable) {
                onNotificationsToggle(false)
                return@NotificationPermissionUi
            }
            if (osGranted) {
                onNotificationsToggle(true)
                return@NotificationPermissionUi
            }
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                onNotificationsToggle(true)
                return@NotificationPermissionUi
            }
            val activity = context.findActivity()
            val showRationale = activity?.shouldShowRequestPermissionRationale(
                Manifest.permission.POST_NOTIFICATIONS
            ) == true
            if (!showRationale && hasRequestedOnce) {
                context.startActivity(
                    Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.fromParts("package", context.packageName, null)
                    }
                )
                return@NotificationPermissionUi
            }
            hasRequestedOnce = true
            launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    )
}

private fun isNotificationPermissionGranted(context: Context): Boolean {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return true
    return ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.POST_NOTIFICATIONS
    ) == PackageManager.PERMISSION_GRANTED
}

private fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}
