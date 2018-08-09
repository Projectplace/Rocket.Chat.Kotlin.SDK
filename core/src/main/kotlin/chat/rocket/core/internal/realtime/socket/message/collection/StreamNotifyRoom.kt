package chat.rocket.core.internal.realtime.socket.message.collection

import chat.rocket.core.internal.realtime.socket.Socket
import chat.rocket.core.internal.realtime.socket.model.NotifyRoomTyping
import kotlinx.coroutines.experimental.launch
import org.json.JSONObject

internal const val STREAM_NOTIFY_ROOM = "stream-notify-room"

internal fun Socket.processNotifyRoomStream(text: String) {
    try {
        val json = JSONObject(text)
        val fields = json.getJSONObject("fields")
        val array = fields.getJSONArray("args")

        val eventName = fields.getString("eventName")
        val roomId = eventName.substringBefore("/")
        val eventType = eventName.substringAfter("/")
        if (eventType == "typing") {
            launch(parent = parentJob) {
                typingStatusChannel.send(NotifyRoomTyping(roomId, array.getString(0), array.getBoolean(1)))
            }
        }
    } catch (ex: Exception) {
        ex.printStackTrace()
    }
}