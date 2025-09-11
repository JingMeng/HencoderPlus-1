package com.hsicen.hellocompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
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
                Box {
                    Home(mViewModel)
                    ChatPage()
                }
            }
        }
    }

    override fun onBackPressed() {
        if (mViewModel.endChat().not()) {
            super.onBackPressed()
        }
    }
}