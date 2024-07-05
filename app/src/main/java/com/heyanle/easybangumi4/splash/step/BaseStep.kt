package com.heyanle.easybangumi4.splash.step

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.heyanle.easybangumi4.R
import com.heyanle.easybangumi4.ui.common.OkImage

/**
 * Created by heyanlin on 2024/7/4.
 */
interface BaseStep {

    fun need(first: Boolean): Boolean

    @Composable
    fun Compose()

}

@Composable
fun ColumnScope.SampleGuildHeader() {
    Spacer(modifier = Modifier.height(32.dp))
    OkImage(
        modifier = Modifier
            .size(64.dp)
            .align(Alignment.Start),
        image = R.mipmap.logo_new,
        contentDescription = stringResource(com.heyanle.easy_i18n.R.string.app_name)
    )

    ListItem(
        headlineContent = {
            Text(
                text = stringResource(id = com.heyanle.easy_i18n.R.string.welcome),
                color = MaterialTheme.colorScheme.primary
            )
        },
        supportingContent = {
            Text(
                text = stringResource(id = com.heyanle.easy_i18n.R.string.setting_first),
            )
        }
    )
}