package com.heyanle.easybangumi4.splash.step

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.heyanle.easybangumi4.R
import com.heyanle.easybangumi4.splash.SplashGuildController
import com.heyanle.easybangumi4.ui.common.OkImage
import com.heyanle.easybangumi4.ui.setting.AppearanceSetting
import com.heyanle.okkv2.core.okkv

/**
 * Created by heyanlin on 2024/7/4.
 */
class ThemeStep(
    private val splashGuildController: SplashGuildController
) : BaseStep {

    override fun need(first: Boolean): Boolean {
        return first
    }

    @Composable
    override fun Compose() {
        Column (
            modifier = Modifier.fillMaxSize()
        ) {
            SampleGuildHeader()
            HorizontalDivider()
            AppearanceSetting()
        }
    }
}