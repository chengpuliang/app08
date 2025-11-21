    package com.example.myapp01

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel


    class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val viewModel by viewModels<MainViewModel>()
        setContent {
            // 轉場動畫
            Crossfade(viewModel.screen, label = "") { screen ->
                // 沒有screen了就關閉程式
                screen?.invoke() ?: finish()
            }
            // 處理返回鍵
            BackHandler(onBack = viewModel::pop)
        }
    }
}
class MainViewModel : ViewModel() {
    private val screens = mutableStateListOf<@Composable () -> Unit>({ ListScreen(this) })
    // 用getter保持最新
    val screen get() = screens.lastOrNull()

    fun push(targetScreen: @Composable () -> Unit) {
        screens += targetScreen
    }

    fun pop() {
        screens.removeLastOrNull()
    }
}
