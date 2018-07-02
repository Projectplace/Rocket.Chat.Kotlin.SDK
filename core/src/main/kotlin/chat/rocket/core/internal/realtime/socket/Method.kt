package chat.rocket.core.internal.realtime.socket

import chat.rocket.core.RocketChatClient
import com.squareup.moshi.JsonAdapter
import kotlinx.coroutines.experimental.launch
import org.json.JSONObject

interface MethodCallback<T> {
    fun success(result: T) {}
    fun error(reason: String) {}
}

internal fun Socket.processMethodResult(text: String) {
    try {
        val json = JSONObject(text)
        val id = json.getString("id")
        if (json.has("result")) {
            methodsMap.remove(id)?.success(json.getString("result"))
        } else if (json.has("error")) {
            methodsMap.remove(id)?.error(json.getString("error"))
        }
    } catch (ex: Exception) {
        ex.printStackTrace()
    }
}

internal fun <T> RocketChatClient.callTypedMethod(id: String, method: String, resultAdapter: JsonAdapter<T>, callback: MethodCallback<T>) {
    callMethod(id, method, object : MethodCallback<String> {
        override fun success(result: String) {
            try {
                val jsonResult = resultAdapter.fromJson(result)
                jsonResult?.apply {
                    launch(parent = socket.parentJob) {
                        callback.success(jsonResult)
                    }
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
                callback.error(ex.localizedMessage)
            }
        }

        override fun error(reason: String) {
            callback.error(reason)
        }
    })
}

internal fun RocketChatClient.callMethod(id: String, method: String, callback: MethodCallback<String>? = null) {
    with(socket) {
        send(method)
        callback?.apply {
            methodsMap[id] = this
        }
    }
}
