package online.darki.glass

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class PreferencesActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MaterialTheme(colorScheme = darkColorScheme(primary = crimson, background = bg, surface = glass)) {
            PreferencesScreen(onBack = { finish() })
        } }
    }
}

@Composable
private fun PreferencesScreen(onBack: () -> Unit) {
    var privacy by remember { mutableStateOf(Cfg.enabled("privacy")) }
    var clock by remember { mutableStateOf(Cfg.get("clock", "Liquid")) }
    Column(Modifier.fillMaxSize().background(bg).verticalScroll(rememberScrollState()).padding(24.dp)) {
        Text("APPEARANCE STUDIO", color = crimson, fontSize = 11.sp, letterSpacing = 2.sp)
        Spacer(Modifier.height(8.dp))
        Text("Make it yours", color = Color.White, fontSize = 28.sp)
        Text("Liquid Glass • Obsidian • Crimson", color = Color.LightGray, fontSize = 13.sp)
        Spacer(Modifier.height(24.dp))
        Text("CLOCK STYLE", color = Color.LightGray, fontSize = 11.sp)
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("Digital", "Liquid", "Orbit", "Minimal").forEach { option ->
                FilterChip(selected = clock == option, onClick = { clock = option; Cfg.set("clock", option) }, label = { Text(option) })
            }
        }
        HorizontalDivider(Modifier.padding(vertical = 14.dp), color = Color.White.copy(alpha=.1f))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column(Modifier.weight(1f)) {
                Text("Private previews", color = Color.White)
                Text("Hide notification message contents on the dashboard.", color = Color.LightGray, fontSize = 12.sp)
            }
            Switch(checked = privacy, onCheckedChange = { privacy = it; Cfg.set("privacy", it) })
        }
        Spacer(Modifier.height(24.dp))
        Button(onClick = onBack, colors = ButtonDefaults.buttonColors(containerColor = crimson)) { Text("Done") }
    }
}
