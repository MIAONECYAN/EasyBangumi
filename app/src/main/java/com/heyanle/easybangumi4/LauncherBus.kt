package com.heyanle.easybangumi4

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.heyanle.easybangumi4.utils.CoroutineProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.launch
import java.lang.ref.SoftReference
import java.lang.ref.WeakReference
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicReference
import kotlin.coroutines.suspendCoroutine

/**
 * Created by heyanlin on 2024/5/21.
 */
class LauncherBus(
    act: ComponentActivity
) {

    companion object {
        private var _current: WeakReference<LauncherBus>? = null
        val current: LauncherBus?
            get() = _current?.get()
        fun onResume(launcherBus: LauncherBus) {
            _current = WeakReference(launcherBus)
        }
    }

    private val dispatcher = Executors.newSingleThreadExecutor().asCoroutineDispatcher()
    private val scope = CoroutineScope(SupervisorJob() + dispatcher)

    // 1. 选择备份文件
    private var getBackupZipCallback: SoftReference<(Uri?)->Unit>? = null
    private val getBackupZipLauncher = act.registerForActivityResult(ActivityResultContracts.GetContent()){
        scope.launch {
            getBackupZipCallback?.get()?.invoke(it)
            getBackupZipCallback = null
        }
    }
    fun getBackupZip(callback: (Uri?)->Unit){
        scope.launch {
            if (getBackupZipCallback != null){
                getBackupZipCallback?.get()?.invoke(null)
            }
            getBackupZipCallback = SoftReference(callback)
            getBackupZipLauncher.launch("application/zip")
        }
    }

    // 2. 选择文件夹
    private var getDocumentTreeCallback: SoftReference<(Uri?)->Unit>? = null
    private val getDocumentTreeLauncher = act.registerForActivityResult(ActivityResultContracts.OpenDocumentTree()) {
        scope.launch {
            if (it != null) {
                act.contentResolver.takePersistableUriPermission(
                    it,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                )
            }
            getDocumentTreeCallback?.get()?.invoke(it)
            getDocumentTreeCallback = null
        }
    }
    fun getDocumentTree(default: Uri? = null,callback: (Uri?)->Unit){
        scope.launch {
            if (getDocumentTreeCallback != null){
                getDocumentTreeCallback?.get()?.invoke(null)
            }
            getDocumentTreeCallback = SoftReference(callback)
            getDocumentTreeLauncher.launch(default)
        }
    }


}