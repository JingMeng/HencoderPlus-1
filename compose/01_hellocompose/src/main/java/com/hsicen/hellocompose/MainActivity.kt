package com.hsicen.hellocompose

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.hsicen.hellocompose.ui.ChatPage
import com.hsicen.hellocompose.ui.Home
import com.hsicen.hellocompose.ui.theme.WeComposeTheme

/**
 * 作者：hsicen  12/8/21 22:59
 * 邮箱：codinghuang@163.com
 * 功能：
 * 描述：微信主页
 */
class MainActivity : ComponentActivity() {
    private val mViewModel by viewModels<WeViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            WeComposeTheme(mViewModel.theme) {
                changeTheme()
                Box(
                    Modifier
                        .background(WeComposeTheme.colors.background)
                        .systemBarsPadding()
                ) {
                    Home(mViewModel)
                    ChatPage()
                }
            }
        }
    }

    @Composable
    fun changeTheme() {
        val view = LocalView.current
        val light = WeComposeTheme.colors.light
        val controller = WindowCompat.getInsetsController(window, view)
        controller.isAppearanceLightStatusBars = light
    }

    override fun onBackPressed() {
        if (mViewModel.endChat().not()) {
            super.onBackPressed()
        }
    }
}