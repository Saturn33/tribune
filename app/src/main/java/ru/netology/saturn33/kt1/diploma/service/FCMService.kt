package ru.netology.saturn33.kt1.diploma.service

import com.auth0.android.jwt.JWT
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.netology.saturn33.kt1.diploma.helpers.Notify
import ru.netology.saturn33.kt1.diploma.helpers.SPref
import ru.netology.saturn33.kt1.diploma.repositories.Repository

class FCMService : FirebaseMessagingService() {
    override fun onMessageReceived(message: RemoteMessage) {
        println(message)
        val recipientId = message.data["recipientId"]?.toLong() ?: 0
        val title = message.data["title"] ?: ""
        val text = message.data["text"] ?: ""

        val token = SPref.getToken(applicationContext) ?: return unregisterPushToken(recipientId)

        val jwt = JWT(token)
        if (jwt.getClaim("id").asLong() == recipientId) {
            Notify.simpleNotification(applicationContext, title, text)
        } else {
            unregisterPushToken(recipientId)
        }
    }

    private fun unregisterPushToken(userId: Long) {
        kotlinx.coroutines.CoroutineScope(Dispatchers.IO).launch {
            Repository.unregisterPushToken(userId)
        }
    }

    override fun onNewToken(pushToken: String) {
        println(pushToken)
        kotlinx.coroutines.CoroutineScope(Dispatchers.Default).launch {
            Repository.registerPushToken(pushToken)
        }
    }
}
