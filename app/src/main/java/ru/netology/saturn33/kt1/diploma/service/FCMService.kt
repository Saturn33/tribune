package ru.netology.saturn33.kt1.diploma.service

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.netology.saturn33.kt1.diploma.repositories.Repository

class FCMService : FirebaseMessagingService() {
    override fun onMessageReceived(message: RemoteMessage) {
        println(message)
        //TODO раскомментировать, когда будет готов хелпер
/*
        val recipientId = message.data["recipientId"]?.toLong() ?: 0
        val title = message.data["title"] ?: ""
        val text = message.data["text"] ?: ""

        Notify.simpleNotification(baseContext, title, text)
*/
    }

    override fun onNewToken(pushToken: String) {
        println(pushToken)
        kotlinx.coroutines.CoroutineScope(Dispatchers.Default).launch {
            Repository.registerPushToken(pushToken)
        }
    }
}
