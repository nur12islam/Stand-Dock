package online.darki.glass

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.core.app.NotificationManagerCompat
import androidx.core.view.WindowCompat

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            MaterialTheme(colorScheme = darkColorScheme(primary = crimson, background = bg, surface = glass)) {
                Dashboard(
                    onSettings = { startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)) },
                    onAppearance = { startActivity(Intent(this, PreferencesActivity::class.java)) }
                )
            }
        }
    }
    override fun onResume() {
        super.onResume()
        Hub.access.value = NotificationManagerCompat.getEnabledListenerPackages(this).contains(packageName)
        Hub.purge()
    }
}
