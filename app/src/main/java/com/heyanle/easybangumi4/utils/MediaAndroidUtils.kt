package com.heyanle.easybangumi4.utils

import android.content.ContentValues
import android.content.Context
import android.media.MediaScannerConnection
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import com.heyanle.easybangumi4.APP
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * Created by heyanlin on 2024/7/4.
 */
object MediaAndroidUtils {

    suspend fun mediaScan(context: Context, path: String){
        suspendCoroutine<Unit> {
            MediaScannerConnection.scanFile(context, arrayOf(path), null
            ) { p0, p1 -> it.resume(Unit) }
        }

    }


    suspend fun saveToDownload(file: File, type: String, displayName: String = file.name) {
        withContext(Dispatchers.IO) {
            kotlin.runCatching {
                val picturesFile =
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                        ?: File(APP.getFilePath())
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                    val targetRoot = File(picturesFile, "EasyBangumi/${type}")
                    val target = File(targetRoot, file.name)
                    file.copyTo(target, true)
                } else {
                    val values = ContentValues().apply {
                        put(MediaStore.MediaColumns.DISPLAY_NAME, displayName)
                        put(
                            MediaStore.MediaColumns.RELATIVE_PATH, "${
                                Environment.getExternalStoragePublicDirectory(
                                    Environment.DIRECTORY_DOWNLOADS)}/EasyBangumi/${type}")
                    }
                    APP.contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
                        ?.let { uri ->
                            APP.contentResolver.openOutputStream(uri)
                        }?.use {
                            file.inputStream().copyTo(it)
                        }
                }
            }.onFailure {
                it.printStackTrace()
            }
        }

    }

}