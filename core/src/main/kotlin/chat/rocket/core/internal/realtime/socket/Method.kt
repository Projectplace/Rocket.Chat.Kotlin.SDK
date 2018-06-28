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
    val id: String
    val result: String
    try {
        val json = JSONObject(text)
        id = json.getString("id")
        result = json.getString("result")
    } catch (ex: Exception) {
        ex.printStackTrace()
        return
    }

    methodsMap.remove(id)?.success(result)
}

internal fun <T> RocketChatClient.callMethod(id: String, method: String, resultAdapter: JsonAdapter<T>, callback: MethodCallback<T>) {
    with(socket) {
        send(method)
        methodsMap[id] = object : MethodCallback<String> {
            override fun success(result: String) {
                try {
                    val jsonResult = resultAdapter.fromJson(result)
                    jsonResult?.apply {
                        launch(parent = parentJob) {
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
        }
    }
}
