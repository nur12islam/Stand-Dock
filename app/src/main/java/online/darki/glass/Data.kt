package online.darki.glass

import android.app.PendingIntent
import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import org.json.JSONArray
import org.json.JSONObject

object Cfg {
    private lateinit var sp: SharedPreferences
    private val values = mutableStateMapOf<String, String>()
    fun init(c: Context) {
        sp = c.getSharedPreferences("settings", Context.MODE_PRIVATE)
        sp.all.forEach { (k, v) -> values[k] = v.toString() }
    }
    fun get(key: String, default: String) = values[key] ?: default
    fun enabled(key: String, default: Boolean = false) = values[key]?.toBooleanStrictOrNull() ?: default
    fun set(key: String, value: Any) {
        values[key] = value.toString()
        sp.edit().putString(key, value.toString()).apply()
    }
    fun blocked(): Set<String> = get("blocked", "").split("|").filter(String::isNotBlank).toSet()
}

data class Note(
    val key: String, val packageName: String, val app: String, val title: String,
    val text: String, val time: Long, val important: Boolean = false, val hidden: Boolean = false
)
data class LiveNotification(val open: PendingIntent?, val actions: List<Pair<String, PendingIntent>>)

object Hub {
    private lateinit var sp: SharedPreferences
    val notes = mutableStateListOf<Note>()
    val live = HashMap<String, LiveNotification>()
    var access = mutableStateOf(false)
    var listener: DarkiListener? = null

    fun init(c: Context) {
        sp = c.getSharedPreferences("notification_history", Context.MODE_PRIVATE)
        val saved = runCatching { JSONArray(sp.getString("items", "[]")) }.getOrElse { JSONArray() }
        for (i in 0 until saved.length()) {
            runCatching {
                val o = saved.getJSONObject(i)
                notes.add(Note(o.getString("key"), o.getString("pkg"), o.getString("app"),
                    o.getString("title"), o.getString("text"), o.getLong("time"),
                    o.optBoolean("important"), o.optBoolean("hidden")))
            }
        }
        purge()
    }
    fun add(note: Note) {
        notes.removeAll { it.key == note.key }
        notes.add(0, note)
        purge()
    }
    fun hide(key: String) {
        val i = notes.indexOfFirst { it.key == key }
        if (i >= 0) notes[i] = notes[i].copy(hidden = true)
        save()
    }
    fun clear() { notes.clear(); save() }
    fun purge() {
        val cutoff = System.currentTimeMillis() - 24L * 60L * 60L * 1000L
        notes.removeAll { it.time < cutoff }
        while (notes.size > 100) notes.removeAt(notes.lastIndex)
        save()
    }
    private fun save() {
        if (!::sp.isInitialized) return
        val arr = JSONArray()
        notes.forEach { n -> arr.put(JSONObject().put("key", n.key).put("pkg", n.packageName)
            .put("app", n.app).put("title", n.title).put("text", n.text)
            .put("time", n.time).put("important", n.important).put("hidden", n.hidden)) }
        sp.edit().putString("items", arr.toString()).apply()
    }
}
