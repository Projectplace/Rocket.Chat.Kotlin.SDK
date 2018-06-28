package chat.rocket.core.internal.realtime.socket.model

import chat.rocket.core.model.Message
import com.squareup.moshi.Json
import se.ansman.kotshi.JsonSerializable

@JsonSerializable
data class RoomHistory(
        @Json(name = "messages")
        val messages: List<Message>,
        @Json(name = "unreadNotLoaded")
        val unreadNotLoaded: Int
)
