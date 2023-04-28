package com.example.webviewtest.models

sealed class Events {
    data class LoadUrl(val url: String, val type: WebViewType) : Events()
    data class Error(val message: String) : Events()
}
