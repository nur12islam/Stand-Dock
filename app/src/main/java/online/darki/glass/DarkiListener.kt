package online.darki.glass

import android.app.Application
import android.app.Notification
import android.app.NotificationManager
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification

class DarkiApp : Application() {
    override fun onCreate() {
        super.onCreate()
        Cfg.init(this)
        Hub.init(this)
    }
}

class DarkiListener : NotificationListenerService() {
    override fun onListenerConnected() {
        Hub.listener = this
        Hub.access.value = true
        activeNotifications?.forEach(::receive)
    }
    override fun onListenerDisconnected() {
        Hub.listener = null
        Hub.access.value = false
    }
    override fun onNotificationPosted(sbn: StatusBarNotification) = receive(sbn)
    override fun onNotificationRemoved(sbn: StatusBarNotification) { Hub.live.remove(sbn.key) }

    private fun receive(sbn: StatusBarNotification) {
        val n = sbn.notification
        if (sbn.packageName == packageName || (n.flags and Notification.FLAG_GROUP_SUMMARY) != 0) return
        if (sbn.packageName in Cfg.blocked()) return
        val extras = n.extras
        val title = extras.getCharSequence(Notification.EXTRA_TITLE)?.toString().orEmpty()
        val body = (extras.getCharSequence(Notification.EXTRA_BIG_TEXT) ?: extras.getCharSequence(Notification.EXTRA_TEXT))?.toString().orEmpty()
        if (title.isBlank() && body.isBlank()) return
        val appName = runCatching {
            packageManager.getApplicationLabel(packageManager.getApplicationInfo(sbn.packageName, 0)).toString()
        }.getOrDefault(sbn.packageName)
        val ranking = NotificationListenerService.Ranking()
        val important = runCatching {
            currentRanking.getRanking(sbn.key, ranking) && ranking.importance >= NotificationManager.IMPORTANCE_HIGH
        }.getOrDefault(false)
        val actions = n.actions.orEmpty().mapNotNull { action ->
            val intent = action.actionIntent
            val label = action.title?.toString()?.trim().orEmpty()
            if (intent != null && action.remoteInputs.isNullOrEmpty() && label.isNotBlank()) label to intent else null
        }
        Hub.live[sbn.key] = LiveNotification(n.contentIntent, actions)
        Hub.add(Note(sbn.key, sbn.packageName, appName, title, body, sbn.postTime, important))
    }
}
