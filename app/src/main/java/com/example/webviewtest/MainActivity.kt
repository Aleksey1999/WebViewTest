package com.example.webviewtest

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.webviewtest.databinding.ActivityMainBinding
import com.example.webviewtest.features.CustomTabsHelper
import com.example.webviewtest.features.CustomWebViewClient
import com.example.webviewtest.models.Events
import com.example.webviewtest.models.WebViewType
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {
    private val TAG = javaClass.simpleName
    val binding: ActivityMainBinding by lazy(LazyThreadSafetyMode.NONE) {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private val viewModel: MainViewModel = MainViewModel()
    val tabsHelper = CustomTabsHelper()
    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        with(binding) {
            setupButtons()
            setupObserver()
           webView.settings.javaScriptEnabled = true
        }
        onBackPressedHandle()
    }

    private fun ActivityMainBinding.setupObserver() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.events.collect {
                    onEvent(it)
                }
            }
        }
    }

    private fun onEvent(event: Events) {
        when (event) {
            is Events.LoadUrl -> {
                loadUrl(event)
            }
            is Events.Error -> {
                Toast.makeText(this, event.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadUrl(event: Events.LoadUrl) {
        when (event.type) {
            WebViewType.ANDROID -> {
                binding.webView.webViewClient = WebViewClient()
                binding.webView.loadUrl(event.url)
            }
            WebViewType.CUSTOM -> {
                binding.webView.webViewClient = CustomWebViewClient(applicationContext)
                binding.webView.loadUrl(event.url)
            }
            WebViewType.TABS -> {
                val builder = CustomTabsIntent.Builder()
                builder.apply {
                    setInitialActivityHeightPx(400)
                    setCloseButtonPosition(CustomTabsIntent.CLOSE_BUTTON_POSITION_END)
                    setToolbarCornerRadiusDp(10)
                }
                val customTabsIntent = builder.build()
                val pName = tabsHelper.getPackageNameToUse(this@MainActivity)
                if (pName == null) {
                    viewModel.sendError("Chrome doesn't exist")
                } else {
                    Log.d(TAG, "start tabs")
                    customTabsIntent.intent.setPackage(pName)
                    customTabsIntent.launchUrl(this@MainActivity, Uri.parse(event.url))
                }
            }
        }
    }

    private fun onBackPressedHandle() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(
            true
        ) {
            override fun handleOnBackPressed() {
                if (binding.webView.canGoBack()) {
                    binding.webView.goBack()
                } else {
                    finish()
                }
            }
        }
        )
    }


    private fun ActivityMainBinding.setupButtons() {
        androidView.setOnClickListener {
            viewModel.loadAndroid(binding.url.text.toString().trim())
        }
        customView.setOnClickListener {
            viewModel.loadCustom(binding.url.text.toString().trim())
        }
        chromeTabs.setOnClickListener {
            viewModel.loadTabs(binding.url.text.toString().trim())
        }
    }


}




