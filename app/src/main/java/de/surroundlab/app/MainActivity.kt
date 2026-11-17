package de.surroundlab.app

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import de.surroundlab.app.service.SurroundHostService
import de.surroundlab.app.ui.SurroundViewModel
import de.surroundlab.app.ui.i18n.AppStringsRegistry
import de.surroundlab.app.ui.i18n.LocalAppStrings
import de.surroundlab.app.ui.mobile.MobileAppRoot
import de.surroundlab.app.ui.theme.SurroundLabMobileTheme
import de.surroundlab.app.ui.theme.SurroundLabTvTheme
import de.surroundlab.app.ui.tv.TvAppRoot

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val repo = (application as SurroundLabApp).repository
        if (repo.isTvDevice) {
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            SurroundHostService.start(this)
        }

        setContent {
            val vm: SurroundViewModel = viewModel()
            val state by vm.uiState.collectAsStateWithLifecycle()
            val connectionStatus by vm.connectionStatus.collectAsStateWithLifecycle()
            val discoveredHosts by vm.discoveredHosts.collectAsStateWithLifecycle()
            val networkError by vm.networkError.collectAsStateWithLifecycle()

            val isPlayingAudio = state.singleSpeaker.playing ||
                state.generator.playing ||
                state.puck.playing

            LaunchedEffect(vm.isTvDevice, isPlayingAudio) {
                if (vm.isTvDevice || isPlayingAudio) {
                    window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                } else {
                    window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                }
            }

            val appStrings = remember(state.appLanguage) {
                AppStringsRegistry.forAppLanguage(state.appLanguage)
            }

            if (vm.isTvDevice) {
                val baseDensity = LocalDensity.current
                val tvScale = state.tvUiScale.coerceIn(0.60f, 1.15f)
                CompositionLocalProvider(
                    LocalAppStrings provides appStrings,
                    LocalDensity provides Density(
                        density = baseDensity.density * tvScale,
                        fontScale = baseDensity.fontScale
                    )
                ) {
                    SurroundLabTvTheme {
                        TvAppRoot(vm = vm, state = state)
                    }
                }
            } else {
                CompositionLocalProvider(LocalAppStrings provides appStrings) {
                    SurroundLabMobileTheme {
                        MobileAppRoot(
                            vm = vm,
                            state = state,
                            connectionStatus = connectionStatus,
                            discoveredHosts = discoveredHosts,
                            networkError = networkError
                        )
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        super.onDestroy()
    }
}
