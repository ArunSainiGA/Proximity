package com.asp.proximitylabs.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.asp.proximitylabs.model.AirQualityModel
import com.asp.proximitylabs.data.client.AirQualitySocketClient
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch

class ViewModel(private val client: AirQualitySocketClient) : ViewModel() {

    private val viewState: MutableLiveData<ViewState> = MutableLiveData()

    val viewStateObservable: LiveData<ViewState> = viewState

    private val coroutineExceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        viewState.value = ViewState.Error(throwable?.message ?: "There was some error")
    }

    private val context = coroutineExceptionHandler

    init {
        viewModelScope.launch(context){
            client.connect()
            val channel = client.getChannel()
            channel.consumeEach {
                when(val res = it){
                    is AirQualitySocketClient.ClientState.DataReceived ->{
                        viewState.value = ViewState.DataFetched(res.data)
                    }
                }
            }
        }
    }

    sealed class ViewState{
        data class Loading(val isLoading: Boolean = false): ViewState()
        data class DataFetched(val data: ArrayList<AirQualityModel>?): ViewState()
        data class Error(val message: String): ViewState()
    }
}