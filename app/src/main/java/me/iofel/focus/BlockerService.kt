package me.iofel.focus

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Intent
import android.graphics.PixelFormat
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.Gravity
import android.view.LayoutInflater
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED
import android.widget.TextView
import android.widget.Toast
import androidx.compose.ui.semantics.text

class BlockerService : AccessibilityService() {
    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        if (event.eventType == TYPE_WINDOW_CONTENT_CHANGED) {
            val packageName = event.packageName?.toString()
            if (packageName == "com.twitter.android") {
                performGlobalAction(GLOBAL_ACTION_BACK)

                showToast()
            }
        }
    }

    override fun onInterrupt() {
        // Do nothing
    }

    override fun onServiceConnected() {
        val info = AccessibilityServiceInfo()
        info.eventTypes = TYPE_WINDOW_CONTENT_CHANGED
        info.packageNames = arrayOf("com.twitter.android")
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC
        serviceInfo = info
    }

    private fun showToast() {
        val toastView = LayoutInflater.from(this).inflate(R.layout.overlay, null)
        val textView = toastView.findViewById<TextView>(R.id.toast_text)
        textView.text = "Twitter has been blocked"

        val windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT)

        params.gravity = Gravity.CENTER
        windowManager.addView(toastView, params)

        // Remove Toast after a delay
        Handler(Looper.getMainLooper()).postDelayed({
            windowManager.removeView(toastView)
        }, 2000) // 2 seconds
    }
}