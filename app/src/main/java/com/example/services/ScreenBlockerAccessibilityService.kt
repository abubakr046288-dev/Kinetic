package com.example.services

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import com.example.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

object DisciplineEngine {
    var isEnabled = true
    var isAppInForeground = false
    var blockedApps = mutableSetOf(
        "com.instagram.android",
        "com.zhiliaoapp.musically", // TikTok
        "com.google.android.youtube",
        "com.facebook.katana"
    )
    
    // Default pool so user can test and enjoy opening apps initially, reloadable via exercise
    var screenTimeRemainingSec = 180 
    
    var isOverlayLocked = false
    var currentViolatingAppPackage = ""
    var currentViolatingAppName = ""

    fun addScreenTime(seconds: Int) {
        screenTimeRemainingSec = (screenTimeRemainingSec + seconds).coerceAtLeast(0)
        if (screenTimeRemainingSec > 0) {
            isOverlayLocked = false
        }
    }

    fun getAppNameForPackage(pkg: String): String {
        return when {
            pkg.contains("instagram") -> "Instagram"
            pkg.contains("musically") || pkg.contains("tiktok") -> "TikTok"
            pkg.contains("youtube") -> "YouTube"
            pkg.contains("facebook") -> "Facebook"
            else -> "Social App"
        }
    }
}

class ScreenBlockerAccessibilityService : AccessibilityService() {
    private var monitorJob: Job? = null
    private val serviceScope = CoroutineScope(Dispatchers.Main)
    private var currentForegroundPackage = ""

    override fun onServiceConnected() {
        super.onServiceConnected()
        Log.d("BlockerService", "Discipline Blocker Accessibility Service Connected successfully!")
        
        // Background tick to decrement screen time and auto-lock if app is open
        monitorJob = serviceScope.launch {
            while (true) {
                delay(1000)
                if (DisciplineEngine.isAppInForeground || currentForegroundPackage == this@ScreenBlockerAccessibilityService.packageName) {
                    continue
                }
                
                if (DisciplineEngine.isEnabled && 
                    DisciplineEngine.blockedApps.contains(currentForegroundPackage)
                ) {
                    if (DisciplineEngine.screenTimeRemainingSec > 0) {
                        DisciplineEngine.screenTimeRemainingSec--
                        Log.d("BlockerService", "Decremented screen time. Remaining: ${DisciplineEngine.screenTimeRemainingSec}s")
                        
                        if (DisciplineEngine.screenTimeRemainingSec <= 0) {
                            // Out of time! Instant redirect
                            triggerOverlayRedirect(currentForegroundPackage)
                        }
                    } else {
                        // Time already depleted, enforce lock
                        triggerOverlayRedirect(currentForegroundPackage)
                    }
                }
            }
        }
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event?.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            val packageName = event.packageName?.toString() ?: return
            currentForegroundPackage = packageName
            
            if (DisciplineEngine.isAppInForeground || packageName == this@ScreenBlockerAccessibilityService.packageName) {
                return
            }
            
            if (DisciplineEngine.isEnabled && DisciplineEngine.blockedApps.contains(packageName)) {
                if (DisciplineEngine.screenTimeRemainingSec <= 0) {
                    triggerOverlayRedirect(packageName)
                }
            }
        }
    }

    private var lastRedirectTime = 0L

    private fun triggerOverlayRedirect(packageName: String) {
        if (DisciplineEngine.isAppInForeground || DisciplineEngine.isOverlayLocked) {
            // Already locked or our application is active, do not spam trigger overlay
            return
        }
        
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastRedirectTime < 3000) {
            // Do not spam launch intent, wait at least 3 seconds between launches
            return
        }
        lastRedirectTime = currentTime
        
        DisciplineEngine.isOverlayLocked = true
        DisciplineEngine.currentViolatingAppPackage = packageName
        DisciplineEngine.currentViolatingAppName = DisciplineEngine.getAppNameForPackage(packageName)
        
        Log.d("BlockerService", "REDIRECT REGULAR INTERACTION FOR BLOCKED APP: $packageName")
        
        // Force launch our main overlay blocking visual
        try {
            val launchIntent = Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                putExtra("trigger_lock_overlay", true)
                putExtra("violating_package", packageName)
            }
            startActivity(launchIntent)
            
            // Immediately set currentForegroundPackage to our own package to prevent next background tick from spamming
            currentForegroundPackage = this.packageName
        } catch (e: Exception) {
            Log.e("BlockerService", "Failed to start MainActivity: ${e.message}")
        }
    }

    override fun onInterrupt() {
        Log.d("BlockerService", "Discipline service interrupted.")
    }

    override fun onDestroy() {
        super.onDestroy()
        monitorJob?.cancel()
    }
}
