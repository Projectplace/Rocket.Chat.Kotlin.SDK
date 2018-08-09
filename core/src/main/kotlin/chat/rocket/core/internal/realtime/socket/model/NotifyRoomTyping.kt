package chat.rocket.core.internal.realtime.socket.model

data class NotifyRoomTyping(
        val roomId: String,
        val userName: String,
        val typing: Boolean
)
