package com.example

import android.os.Bundle
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.model.TvInteractionMode
import com.example.ui.screens.TvMapScreen
import com.example.ui.theme.TvBackground
import com.example.ui.theme.TvGoogleMapTheme
import com.example.viewmodel.TvMapViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: TvMapViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            TvGoogleMapTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = TvBackground
                ) {
                    TvMapScreen(viewModel = viewModel)
                }
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        // Handle TV Remote Navigation
        val state = viewModel.uiState.value

        when (keyCode) {
            KeyEvent.KEYCODE_PAGE_UP, KeyEvent.KEYCODE_MEDIA_FAST_FORWARD -> {
                viewModel.zoomIn()
                return true
            }
            KeyEvent.KEYCODE_PAGE_DOWN, KeyEvent.KEYCODE_MEDIA_REWIND -> {
                viewModel.zoomOut()
                return true
            }
            KeyEvent.KEYCODE_DPAD_UP -> {
                if (state.interactionMode == TvInteractionMode.MAP_PAN) {
                    viewModel.panBy(0f, -0.6f)
                    return true
                }
            }
            KeyEvent.KEYCODE_DPAD_DOWN -> {
                if (state.interactionMode == TvInteractionMode.MAP_PAN) {
                    viewModel.panBy(0f, 0.6f)
                    return true
                }
            }
            KeyEvent.KEYCODE_DPAD_LEFT -> {
                if (state.interactionMode == TvInteractionMode.MAP_PAN) {
                    viewModel.panBy(-0.6f, 0f)
                    return true
                }
            }
            KeyEvent.KEYCODE_DPAD_RIGHT -> {
                if (state.interactionMode == TvInteractionMode.MAP_PAN) {
                    viewModel.panBy(0.6f, 0f)
                    return true
                }
            }
            KeyEvent.KEYCODE_DPAD_CENTER, KeyEvent.KEYCODE_ENTER -> {
                if (state.interactionMode == TvInteractionMode.MAP_PAN) {
                    if (state.selectedLocation != null) {
                        viewModel.setDetailsOpen(true)
                    } else {
                        viewModel.toggleInteractionMode()
                    }
                    return true
                }
            }
        }

        return super.onKeyDown(keyCode, event)
    }
}
