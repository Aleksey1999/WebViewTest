package com.example.webviewtest

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.webviewtest.models.Events
import com.example.webviewtest.models.WebViewType
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import java.net.URL

class MainViewModel : ViewModel() {

    private val mutableEvents = MutableSharedFlow<Events>()
    val events: SharedFlow<Events> get() = mutableEvents

    fun loadAndroid(url: String) {
        if (httpsChecker(url)) emitEvent(Events.LoadUrl(url = url, type = WebViewType.ANDROID))
    }

    fun loadCustom(url: String) {
        if (httpsChecker(url)) emitEvent(Events.LoadUrl(url = url, type = WebViewType.CUSTOM))
    }

    fun loadTabs(url: String) {
        if (httpsChecker(url)) emitEvent(Events.LoadUrl(url = url, type = WebViewType.TABS))
    }

    fun sendError(message: String) {
        emitEvent(Events.Error(message))
    }

    private fun emitEvent(event: Events) {
        viewModelScope.launch {
            mutableEvents.emit(event)
        }
    }

    private fun httpsChecker(url: String): Boolean {
        return try {
            URL(url).toURI()
            true
        } catch (e: Exception) {
            emitEvent(Events.Error("Incorrect URL"))
            false
        }
    }
}