package chat.rocket.core.internal.realtime

import chat.rocket.common.RocketChatAuthException
import chat.rocket.core.RocketChatClient
import chat.rocket.core.internal.realtime.message.*
import chat.rocket.core.internal.realtime.socket.callMethod
import chat.rocket.core.internal.realtime.socket.model.RoomHistory
import chat.rocket.core.internal.realtime.socket.MethodCallback
import chat.rocket.core.model.Message
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.withContext

suspend fun RocketChatClient.setTypingStatus(roomId: String, username: String, isTyping: Boolean) =
    withContext(CommonPool) {
        socket.send(typingMessage(socket.generateId(), roomId, username, isTyping))
    }

fun RocketChatClient.subscribeTypingStatus(roomId: String, callback: (Boolean, String) -> Unit): String {
    with(socket) {
        val id = generateId()
        send(streamTypingMessage(id, roomId))
        subscriptionsMap[id] = callback
        return id
    }
}

fun RocketChatClient.subscribeRooms(callback: (Boolean, String) -> Unit): String {
    with(socket) {
        client.tokenRepository.get(client.url)?.let { (userId) ->
            val id = generateId()
            send(roomsStreamMessage(id, userId))
            subscriptionsMap[id] = callback
            return id
        }
        throw RocketChatAuthException("Missing user id and token")
    }
}

fun RocketChatClient.subscribeRoomMessages(roomId: String, callback: (Boolean, String) -> Unit): String {
    with(socket) {
        val id = generateId()
        send(streamRoomMessages(id, roomId))
        subscriptionsMap[id] = callback
        return id
    }
}


suspend fun RocketChatClient.createDirectMessage(username: String) =
    withContext(CommonPool) {
        socket.send(createDirectMessage(socket.generateId(), username))
    }

fun RocketChatClient.roomHistory(roomId: String, limit: Int, callback: MethodCallback<RoomHistory>) {
    val adapter = moshi.adapter(RoomHistory::class.java)
    val id = socket.generateId()
    callMethod(id, roomHistoryMethod(id, roomId, limit), adapter, callback)
}

fun RocketChatClient.sendMessage(messageId: String, roomId: String, text: String, callback: MethodCallback<Message>) {
    val adapter = moshi.adapter(Message::class.java)
    val id = socket.generateId()
    callMethod(id, sendMessageMethod(id, messageId, roomId, text), adapter, callback)
}

fun RocketChatClient.markRoomAsRead(roomId: String) {
    val id = socket.generateId()
    callMethod<Unit>(id, markRoomAsReadMethod(id, roomId))
}
