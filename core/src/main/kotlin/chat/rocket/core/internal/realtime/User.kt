package chat.rocket.core.internal.realtime

import chat.rocket.common.model.UserStatus
import chat.rocket.core.RocketChatClient
import chat.rocket.core.internal.model.Subscription
import chat.rocket.core.internal.realtime.message.*
import chat.rocket.core.internal.realtime.socket.MethodCallback
import chat.rocket.core.internal.realtime.socket.callMethod
import chat.rocket.core.model.Room
import com.squareup.moshi.Types
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.withContext

suspend fun RocketChatClient.setDefaultStatus(status: UserStatus) = withContext(CommonPool) {
    socket.send(defaultStatusMessage(socket.generateId(), status))
}

suspend fun RocketChatClient.setTemporaryStatus(status: UserStatus) = withContext(CommonPool) {
    when {
        (status is UserStatus.Online || status is UserStatus.Away) -> {
            socket.send(temporaryStatusMessage(socket.generateId(), status))
        }
        else -> {
            logger.warn { "Only \"UserStatus.Online\" and \"UserStatus.Away\" are accepted as temporary status" }
        }
    }
}

fun RocketChatClient.subscribeUserData(callback: (Boolean, String) -> Unit): String {
    with(socket) {
        val id = generateId()
        send(userDataChangesMessage(id))
        subscriptionsMap[id] = callback
        return id
    }
}

fun RocketChatClient.subscribeActiveUsers(callback: (Boolean, String) -> Unit): String {
    with(socket) {
        val id = generateId()
        send(activeUsersMessage(id))
        subscriptionsMap[id] = callback
        return id
    }
}

fun RocketChatClient.subscriptions(callback: MethodCallback<List<Subscription>>) {
    val adapter = moshi.adapter<List<Subscription>>(Types.newParameterizedType(List::class.java, Subscription::class.java))
    val id = socket.generateId()
    callMethod(id, subscriptionsMethod(id), adapter, callback)
}

fun RocketChatClient.rooms(callback: MethodCallback<List<Room>>) {
    val adapter = moshi.adapter<List<Room>>(Types.newParameterizedType(List::class.java, Room::class.java))
    val id = socket.generateId()
    callMethod(id, roomsMethod(id), adapter, callback)
}
