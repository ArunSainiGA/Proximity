package com.asp.proximitylabs.data.client

import com.asp.proximitylabs.model.AirQualityModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.launch
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import org.json.JSONArray
import java.net.URI


class AirQualitySocketClient(private val uri: String): WebSocketClient(URI(uri)) {
    private val context = Dispatchers.IO

    private val channel = ConflatedBroadcastChannel<ClientState<ArrayList<AirQualityModel>>>()

    override fun onOpen(handshakedata: ServerHandshake?) {
        CoroutineScope(context).launch {
            channel.send(ClientState.ConnectionOpen)
        }
    }

    override fun onMessage(message: String?) {
        CoroutineScope(context).launch {
            channel.send(ClientState.DataReceived(parseData(message)))
        }
    }

    override fun onClose(code: Int, reason: String?, remote: Boolean) {
        CoroutineScope(context).launch {
            channel.send(ClientState.ConnectionClosed)
        }
    }

    override fun onError(ex: Exception?) {
        CoroutineScope(context).launch {
            channel.send(ClientState.Error(ex))
        }
    }

    fun getChannel() = channel

    override fun connect() {
        if(!isOpen)
            super.connect()
    }

    private fun parseData(response: String?): ArrayList<AirQualityModel>?{
        return response?.let {
            val result = ArrayList<AirQualityModel>()
            val arr = JSONArray(it)
            for(i in 0 until arr.length()){
                val obj = arr.getJSONObject(i)
                val city = obj.getString("city")
                val aqi = obj.getString("aqi")
                result.add(AirQualityModel(city, aqi))
            }
            result
        }
    }

    sealed class ClientState<out D>{
        object ConnectionOpen: ClientState<Nothing>()
        object ConnectionClosed: ClientState<Nothing>()
        data class Error(val exception: Throwable?): ClientState<Nothing>()
        data class DataReceived<out D>(val data: D?): ClientState<D>()
    }
}