package online.darki.glass

import android.text.format.DateFormat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.RepeatMode
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

val bg = Color(0xFF050505)
val glass = Color(0xD916161B)
val crimson = Color(0xFFDC143C)
val muted = Color(0xFF9999A4)

@Composable
fun Dashboard(onSettings: () -> Unit, onAppearance: () -> Unit) {
    val context = LocalContext.current
    var expanded by remember { mutableStateOf(false) }
    var showHidden by remember { mutableStateOf(false) }
    var tick by remember { mutableLongStateOf(System.currentTimeMillis()) }
    LaunchedEffect(Unit) { while (true) { tick = System.currentTimeMillis(); kotlinx.coroutines.delay(1000) } }
    val time = remember(tick) { SimpleDateFormat(if (DateFormat.is24HourFormat(context)) "HH:mm" else "h:mm", Locale.getDefault()).format(Date(tick)) }
    val date = remember(tick) { SimpleDateFormat("EEEE, d MMMM", Locale.getDefault()).format(Date(tick)) }
    val pulse by rememberInfiniteTransition(label = "ambient").animateFloat(
        initialValue = .18f, targetValue = .38f,
        animationSpec = infiniteRepeatable(tween(3200), RepeatMode.Reverse), label = "glow"
    )
    BoxWithConstraints(Modifier.fillMaxSize().background(
        Brush.radialGradient(listOf(Color(0xFF291018), bg), radius = 1200f)
    ).padding(18.dp)) {
        Row(Modifier.fillMaxSize(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Column(Modifier.weight(1.05f).fillMaxHeight(), verticalArrangement = Arrangement.Center) {
                Text("DARKI  /  GLASS", color = muted, fontSize = 11.sp, letterSpacing = 3.sp, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(12.dp))
                Box(contentAlignment = Alignment.Center) {
                    Box(Modifier.size(170.dp).alpha(pulse).border(1.dp, crimson.copy(alpha = .5f), CircleShape))
                    Column(horizontalAlignment = Alignment.Start) {
                        Text(time, color = Color.White, fontSize = 62.sp, fontWeight = FontWeight.Light, letterSpacing = (-2).sp)
                        Text(date, color = muted, fontSize = 13.sp)
                    }
                }
                Spacer(Modifier.height(22.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(7.dp).clip(CircleShape).background(if (Hub.access.value) Color(0xFF62D99A) else crimson))
                    Spacer(Modifier.width(8.dp))
                    Text(if (Hub.access.value) "NOTIFICATIONS CONNECTED" else "NOTIFICATION ACCESS OFF", color = muted, fontSize = 10.sp, letterSpacing = 1.sp)
                }
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    SmallAction("Access") { onSettings() }
                    SmallAction("Appearance") { onAppearance() }
                }
            }
            Column(Modifier.weight(1.35f).fillMaxHeight()) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.weight(1f)) {
                        Text("NOTIFICATION ORBIT", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                        Text("${Hub.notes.count { !it.hidden }} recent notifications", color = muted, fontSize = 11.sp)
                    }
                    SmallAction(if (expanded) "Collapse" else "View all") { expanded = !expanded }
                }
                Spacer(Modifier.height(12.dp))
                val visible = Hub.notes.filter { showHidden || !it.hidden }.take(if (expanded) 100 else 4)
                if (visible.isEmpty()) {
                    Box(Modifier.fillMaxSize().clip(RoundedCornerShape(24.dp)).background(glass).border(1.dp, Color.White.copy(alpha=.08f), RoundedCornerShape(24.dp)), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(if (Hub.access.value) "All quiet." else "Enable notification access", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Medium)
                            Spacer(Modifier.height(6.dp))
                            Text(if (Hub.access.value) "New notifications will appear here." else "Tap Access to allow DARKI GLASS to display notifications.", color = muted, fontSize = 12.sp)
                            if (!Hub.access.value) { Spacer(Modifier.height(14.dp)); Button(onClick = onSettings, colors = ButtonDefaults.buttonColors(containerColor = crimson)) { Text("Enable access") } }
                        }
                    }
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), contentPadding = PaddingValues(bottom = 8.dp)) {
                        items(visible, key = { it.key }) { note ->
                            NotificationCard(note, onDismiss = { Hub.hide(note.key) }, onOpen = {
                                runCatching { Hub.live[note.key]?.open?.send() }
                            })
                        }
                    }
                }
            }
            Column(Modifier.width(116.dp).fillMaxHeight(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("AT A GLANCE", color = muted, fontSize = 10.sp, letterSpacing = 1.4.sp)
                StatusTile("INBOX", Hub.notes.count { !it.hidden }.toString())
                StatusTile("MODE", "LANDSCAPE")
                StatusTile("PRIVACY", "LOCAL")
                Spacer(Modifier.weight(1f))
                SmallAction("Clear feed") { Hub.clear() }
                SmallAction(if (showHidden) "Hide dismissed" else "Show dismissed") { showHidden = !showHidden }
                Text("v1.0  •  LOCAL ONLY", color = muted, fontSize = 8.sp, letterSpacing = 1.sp)
            }
        }
    }
}

@Composable
private fun NotificationCard(note: Note, onDismiss: () -> Unit, onOpen: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp))
            .background(Brush.horizontalGradient(listOf(Color(0xE61D1D23), Color(0xB5121216))))
            .border(1.dp, Color.White.copy(alpha = .10f), RoundedCornerShape(18.dp))
            .clickable { onOpen() }.padding(horizontal = 14.dp, vertical = 11.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(Modifier.size(34.dp).clip(RoundedCornerShape(11.dp)).background(crimson.copy(alpha = .18f)), contentAlignment = Alignment.Center) {
            Text(note.app.take(1).uppercase(), color = crimson, fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }
        Spacer(Modifier.width(11.dp))
        Column(Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(note.app.uppercase(), color = if (note.important) crimson else muted, fontSize = 9.sp, letterSpacing = 1.sp, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
                Text(SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(note.time)), color = muted, fontSize = 9.sp)
            }
            if (note.title.isNotBlank()) Text(note.title, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(if (Cfg.enabled("privacy")) "Message preview hidden" else note.text, color = Color(0xFFC7C7D0), fontSize = 11.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
        }
        Spacer(Modifier.width(8.dp))
        Text("×", color = muted, fontSize = 20.sp, modifier = Modifier.clickable { onDismiss() }.padding(4.dp))
    }
}

@Composable
private fun SmallAction(label: String, onClick: () -> Unit) {
    Text(label, color = Color.White, fontSize = 10.sp, modifier = Modifier.clip(RoundedCornerShape(50)).background(Color.White.copy(alpha = .07f)).border(1.dp, Color.White.copy(alpha = .10f), RoundedCornerShape(50)).clickable(onClick = onClick).padding(horizontal = 11.dp, vertical = 8.dp))
}

@Composable
private fun StatusTile(title: String, value: String) {
    Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(glass).border(1.dp, Color.White.copy(alpha=.08f), RoundedCornerShape(16.dp)).padding(12.dp)) {
        Text(title, color = muted, fontSize = 9.sp, letterSpacing = 1.sp)
        Spacer(Modifier.height(5.dp))
        Text(value, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
    }
}
