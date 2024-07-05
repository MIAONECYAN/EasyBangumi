package com.heyanle.easybangumi4.splash.step

import android.content.UriPermission
import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.heyanle.easybangumi4.APP
import com.heyanle.easybangumi4.LauncherBus
import com.heyanle.easybangumi4.cartoon_local.CartoonLocalController
import com.heyanle.easybangumi4.splash.SplashGuildController
import com.heyanle.easybangumi4.ui.common.moeSnackBar
import com.heyanle.easybangumi4.utils.FileUtils
import com.heyanle.easybangumi4.utils.stringRes


/**
 * Created by heyanlin on 2024/7/4.
 */
class LocalStep(
    private val splashGuildController: SplashGuildController,
    private val localController: CartoonLocalController,
) : BaseStep {


    override fun need(first: Boolean): Boolean {
        if (first) return true
        val usePrivate = localController.localUsePrivate.value
        if (usePrivate) return true

        val uri = localController.localUriPref.get()
        if (uri.isEmpty()) return true
        return APP.contentResolver.persistedUriPermissions.find {
            it.uri.toString() == uri && it.isReadPermission && it.isWritePermission && it.persistedTime != UriPermission.INVALID_TIME
        } != null
    }

    @Composable
    override fun Compose() {

        val usePrivate = localController.localUsePrivate.collectAsState()
        val path = localController.localPath.collectAsState()

        LaunchedEffect(Unit) {
            splashGuildController.canNextStep = true
        }

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            SampleGuildHeader()
            HorizontalDivider()
            ListItem(
                headlineContent = {
                    Text(
                        text = stringResource(id = com.heyanle.easy_i18n.R.string.choose_folder_title),
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                supportingContent = {
                    Text(
                        text = stringResource(id = com.heyanle.easy_i18n.R.string.choose_folder_msg),
                    )
                }
            )

            if (usePrivate.value) {
                ListItem(
                    headlineContent = {
                        Text(
                            text = stringResource(id = com.heyanle.easy_i18n.R.string.current_choose_folder, stringResource(com.heyanle.easy_i18n.R.string.private_folder)),
                            color = MaterialTheme.colorScheme.primary
                        )
                    },
                    supportingContent = {
                        Text(
                            text = stringResource(id = com.heyanle.easy_i18n.R.string.folder_use_private_msg),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                )
            } else {
                Text(
                    text = stringResource(id = com.heyanle.easy_i18n.R.string.current_choose_folder, path.value),
                    color = MaterialTheme.colorScheme.primary)
            }

            TextButton(
                onClick = {
                    localController.usePrivate(true)
                }
            ) {
                Text(text = stringResource(id = com.heyanle.easy_i18n.R.string.folder_use_private))
            }


            TextButton(
                onClick = {
                    chooseFolder()
                }
            ) {
                Text(text = stringResource(id = com.heyanle.easy_i18n.R.string.choose_folder))
            }
        }

    }

    private fun chooseFolder(){
        val currUri = localController.localUriPref.get()
        LauncherBus.current?.getDocumentTree(Uri.parse(currUri)){ uri ->
            var completely = false
            if(uri != null){
                val path = FileUtils.getFullPathFromTreeUri(uri, APP)
                if (path != null) {
                    localController.usePrivate(false)
                    localController.localUriPref.set(uri.toString())
                    localController.localPathPref.set(path)
                    completely = true
                }
            }
            if (!completely) {
                localController.usePrivate(true)
                stringRes(com.heyanle.easy_i18n.R.string.choose_folder_failed).moeSnackBar()

            }
        }
    }
}