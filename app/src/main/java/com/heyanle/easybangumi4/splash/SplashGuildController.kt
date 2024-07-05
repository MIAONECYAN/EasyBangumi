package com.heyanle.easybangumi4.splash

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.heyanle.easybangumi4.base.preferences.android.AndroidPreferenceStore

/**
 * Created by heyanlin on 2024/7/4.
 */
class SplashGuildController(
    private val androidPreferenceStore: AndroidPreferenceStore
) {

    var canNextStep by mutableStateOf(false)

    fun stepCompletely(step: Int){

    }



}