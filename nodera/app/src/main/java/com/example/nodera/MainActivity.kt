package com.example.nodera

import android.annotation.SuppressLint
import android.graphics.Color
import android.net.http.SslError
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import android.webkit.SslErrorHandler
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : ComponentActivity() {

    private lateinit var webView: WebView
    private lateinit var root: FrameLayout

    private val siteUrl = "http://www.barcelo.somee.com"

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        root = FrameLayout(this).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            setBackgroundColor(Color.rgb(5, 10, 25))
        }

        ViewCompat.setOnApplyWindowInsetsListener(root) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }

        setContentView(root)
        showSplashThenLoadWebView()
    }

    private fun showSplashThenLoadWebView() {
        val logo = ImageView(this).apply {
            setImageResource(R.mipmap.ic_launcher)
            adjustViewBounds = true
            scaleType = ImageView.ScaleType.FIT_CENTER
            layoutParams = FrameLayout.LayoutParams(260, 260, Gravity.CENTER)
        }

        val fade = AlphaAnimation(0.25f, 1f).apply {
            duration = 900
            repeatMode = Animation.REVERSE
            repeatCount = Animation.INFINITE
        }

        val pulse = ScaleAnimation(
            0.92f, 1.08f,
            0.92f, 1.08f,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        ).apply {
            duration = 900
            repeatMode = Animation.REVERSE
            repeatCount = Animation.INFINITE
        }

        logo.startAnimation(fade)
        logo.startAnimation(pulse)

        root.addView(logo)

        root.postDelayed({
            logo.clearAnimation()
            root.removeView(logo)
            setupWebView()
        }, 2200)
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView() {
        WebView.setWebContentsDebuggingEnabled(false)

        webView = WebView(this).apply {
            layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )

            setLayerType(WebView.LAYER_TYPE_HARDWARE, null)

            webChromeClient = WebChromeClient()

            webViewClient = object : WebViewClient() {

                override fun onReceivedError(
                    view: WebView?,
                    request: WebResourceRequest?,
                    error: WebResourceError?
                ) {
                    super.onReceivedError(view, request, error)

                    if (request?.isForMainFrame == true) {
                        showConnectionError()
                    }
                }

                @Suppress("DEPRECATION")
                override fun onReceivedError(
                    view: WebView?,
                    errorCode: Int,
                    description: String?,
                    failingUrl: String?
                ) {
                    super.onReceivedError(view, errorCode, description, failingUrl)
                    showConnectionError()
                }

                override fun onReceivedSslError(
                    view: WebView?,
                    handler: SslErrorHandler?,
                    error: SslError?
                ) {
                    handler?.cancel()
                    showConnectionError()
                }
            }

            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.useWideViewPort = true
            settings.loadWithOverviewMode = false
            settings.textZoom = 100

            settings.setSupportZoom(false)
            settings.builtInZoomControls = false
            settings.displayZoomControls = false

            settings.allowContentAccess = true
            settings.allowFileAccess = true
            settings.cacheMode = WebSettings.LOAD_DEFAULT
            settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.NORMAL

            isHorizontalScrollBarEnabled = false
            isVerticalScrollBarEnabled = true
            overScrollMode = WebView.OVER_SCROLL_NEVER
        }

        root.setBackgroundColor(Color.WHITE)
        root.addView(webView)

        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (::webView.isInitialized && webView.canGoBack()) {
                        webView.goBack()
                    } else {
                        isEnabled = false
                        onBackPressedDispatcher.onBackPressed()
                    }
                }
            }
        )

        webView.loadUrl(siteUrl)
    }

    private fun showConnectionError() {
        if (!::webView.isInitialized) return

        webView.loadDataWithBaseURL(
            null,
            """
            <!DOCTYPE html>
            <html>
            <head>
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <style>
                    body {
                        margin: 0;
                        height: 100vh;
                        background: #f0f4f8;
                        font-family: Arial, sans-serif;
                        display: flex;
                        align-items: center;
                        justify-content: center;
                        color: #1a3a5c;
                    }
                    .box {
                        text-align: center;
                        padding: 28px;
                    }
                    .icon {
                        width: 72px;
                        height: 72px;
                        margin: 0 auto 18px;
                        border-radius: 50%;
                        background: #1a3a5c;
                        color: white;
                        display: flex;
                        align-items: center;
                        justify-content: center;
                        font-size: 34px;
                        font-weight: bold;
                    }
                    h2 {
                        margin: 0 0 10px;
                        font-size: 22px;
                    }
                    p {
                        margin: 0 0 22px;
                        color: #555;
                        font-size: 15px;
                        line-height: 1.5;
                    }
                    button {
                        background: #1a3a5c;
                        color: white;
                        border: none;
                        padding: 12px 22px;
                        border-radius: 10px;
                        font-size: 15px;
                        font-weight: bold;
                    }
                </style>
            </head>
            <body>
                <div class="box">
                    <div class="icon">!</div>
                    <h2>Bağlantı kurulamadı</h2>
                    <p>İnternet bağlantınızı kontrol edip tekrar deneyin.</p>
                    <button onclick="location.href='$siteUrl'">Tekrar Dene</button>
                </div>
            </body>
            </html>
            """.trimIndent(),
            "text/html",
            "UTF-8",
            null
        )
    }

    override fun onDestroy() {
        if (::webView.isInitialized) {
            webView.stopLoading()
            webView.webChromeClient = null
            webView.webViewClient = WebViewClient()
            webView.destroy()
        }
        super.onDestroy()
    }
}