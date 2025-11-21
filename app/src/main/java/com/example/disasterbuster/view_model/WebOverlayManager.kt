package com.example.disasterbuster.view_model

import android.app.Activity
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageButton
import android.webkit.WebView
import com.example.disasterbuster.R

class WebOverlayManager(
    activity: Activity,
    root: FrameLayout
) {
    private val overlayView = activity.layoutInflater.inflate(R.layout.disaster_description, root, false)
    private val overlay: FrameLayout = overlayView.findViewById(R.id.webview_overlay)
    private val closeButton: ImageButton = overlayView.findViewById(R.id.close_button)
    private val webView: WebView = overlayView.findViewById(R.id.disaster_webview)

    init {
        root.addView(overlayView)
        webView.settings.javaScriptEnabled = true
        closeButton.setOnClickListener {
            overlay.visibility = View.GONE
            webView.stopLoading()
            webView.loadData("", "text/html", "UTF-8")
        }
    }

    fun show(url: String) {
        webView.loadUrl(url)
        overlay.visibility = View.VISIBLE
    }
}
