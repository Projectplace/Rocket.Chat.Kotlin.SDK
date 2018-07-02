package chat.rocket.core.internal.realtime.message

import chat.rocket.common.model.UserStatus

/**
 * Method calls are used to trigger actions based on the passed data. The response to any method call is completely
 * asynchronous and there’s no way to guarantee the order on the fullfilment of the calls. Because of that, it is really
 * important that a unique-id is used on the method call since the same ID will be used on the response so the client
 * may know the result of a call.
 *
 * See https://rocket.chat/docs/developer-guides/realtime-api/method-calls/
 */

private fun newMessage(method: String, id: String, params: String): String =
        "{\"msg\":\"method\",\"id\":\"$id\",\"method\":\"$method\",\"params\":[$params]}"

internal fun loginMethod(id: String, token: String): String =
        newMessage("login", id, "{\"resume\":\"$token\"}")

internal fun defaultStatusMessage(id: String, status: UserStatus): String =
        newMessage("UserPresence:setDefaultStatus", id, "\"$status\"")

internal fun temporaryStatusMessage(id: String, status: UserStatus): String =
        newMessage("UserPresence:$status", id, "")

internal fun typingMessage(id: String, roomId: String, username: String, isTyping: Boolean) =
        newMessage("stream-notify-room", id, "\"$roomId/typing\",\"$username\",$isTyping")

internal fun createDirectMessage(id: String, username: String): String =
        newMessage("createDirectMessage", id, "\"$username\"")

internal fun subscriptionsMethod(id: String): String =
        newMessage("subscriptions/get", id, "")

internal fun roomsMethod(id: String): String =
        newMessage("rooms/get", id, "")

internal fun roomHistoryMethod(id: String, roomId: String, limit: Int): String =
        newMessage("loadHistory", id, "\"$roomId\", null, $limit")

internal fun sendMessageMethod(id: String, messageId: String, roomId: String, text: String): String =
        newMessage("sendMessage", id, "{\"_id\":\"$messageId\", \"rid\":\"$roomId\", \"msg\":\"$text\"}")

internal fun markRoomAsReadMethod(id: String, roomId: String): String =
        newMessage("readMessages", id, "\"$roomId\"")
