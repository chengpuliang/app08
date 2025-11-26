package com.example.myapp01

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.lifecycle.ViewModel


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val viewModel by viewModels<MainViewModel>()
        setContent {
            Crossfade(viewModel.screen, label = "") { screen ->
                screen?.invoke() ?: finish()
            }
            // 處理返回鍵
            BackHandler(onBack = viewModel::pop)
        }
    }
}
class MainViewModel : ViewModel() {
    private val screens = mutableStateListOf<@Composable () -> Unit>({ ListScreen(this) })
    val screen get() = screens.lastOrNull()

    fun push(targetScreen: @Composable () -> Unit) {
        screens += targetScreen
    }

    fun pop() {
        screens.removeLastOrNull()
    }
}
@Composable
fun VSpacer(height: Dp) {
    Spacer(modifier = Modifier.height(height))
}
@Composable
fun HSpacer(width: Dp) {
    Spacer(modifier = Modifier.width(width))
}
