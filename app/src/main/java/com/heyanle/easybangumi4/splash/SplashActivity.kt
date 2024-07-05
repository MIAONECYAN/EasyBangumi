package com.heyanle.easybangumi4.splash

import androidx.activity.ComponentActivity
import com.heyanle.easybangumi4.LauncherBus

/**
 * 闪屏页
 * Created by heyanlin on 2024/7/4.
 */
class SplashActivity : ComponentActivity() {

    private val launcherBus = LauncherBus(this)
    override fun onResume() {
        super.onResume()
        LauncherBus.onResume(launcherBus)
    }
}