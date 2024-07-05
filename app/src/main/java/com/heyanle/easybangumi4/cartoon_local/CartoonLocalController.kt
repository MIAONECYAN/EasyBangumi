package com.heyanle.easybangumi4.cartoon_local

import com.heyanle.easybangumi4.base.preferences.android.AndroidPreferenceStore
import com.heyanle.easybangumi4.utils.CoroutineProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

/**
 * 本地番源
 * Created by heyanlin on 2024/7/4.
 */
class CartoonLocalController(
    private val androidPreferenceStore: AndroidPreferenceStore
) {

    val dispatcher = CoroutineProvider.SINGLE
    private val scope = CoroutineScope(SupervisorJob() + dispatcher)

    val localUsePrivatePref = androidPreferenceStore.getBoolean("local_use_private", true)
    val localUsePrivate = localUsePrivatePref.stateIn(scope)


    val localUriPref = androidPreferenceStore.getString("local_folder_uri", "")
    val localUri = localUriPref.stateIn(scope)

    val localPathPref = androidPreferenceStore.getString("local_folder_path", "")
    val localPath = localPathPref.stateIn(scope)


    fun usePrivate(b: Boolean){
        localUsePrivatePref.set(b)
    }


}