package com.example.webviewtest.features

import android.content.Context
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast

class CustomWebViewClient(private val context: Context) : WebViewClient() {
    /**
     * The custom feature to show toast on every click
     * */
    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
        request?.url?.let {
            Toast.makeText(
                context,
                "Link clicked: ${it}",
                Toast.LENGTH_SHORT
            ).show()
            view?.loadUrl(it.toString())
        }
        return false
    }
    override fun onReceivedError(
        view: WebView,
        request: WebResourceRequest,
        error: WebResourceError
    ) {
        Toast.makeText(context, "Got Error! $error", Toast.LENGTH_SHORT).show()
    }
}