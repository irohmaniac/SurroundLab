package de.surroundlab.app.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import de.surroundlab.app.domain.model.AppLanguage
import de.surroundlab.app.domain.model.SpeakerLayoutPreset
import de.surroundlab.app.domain.model.SpeakerSetup
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.surroundDataStore: DataStore<Preferences> by preferencesDataStore(name = "surroundlab_prefs")

data class PersistedSettings(
    val setup: SpeakerSetup = SpeakerSetup.S5_1,
    val setupCompleted: Boolean = false,
    val onboardingCompleted: Boolean = false,
    val masterGainDb: Float = -12.0f,
    val swap71SurroundBack: Boolean? = null,
    val speakerLayoutPreset: SpeakerLayoutPreset = SpeakerLayoutPreset.STANDARD,
    val tvUiScale: Float = 0.75f,
    val appLanguage: AppLanguage = AppLanguage.AUTO,
    val lastHostIp: String = "",
    val lastHostPort: Int = 8080,
    val lastSessionToken: String = ""
)

class PreferencesRepository(private val context: Context) {

    private object Keys {
        val SETUP_CODE = stringPreferencesKey("setup_code")
        val SETUP_COMPLETED = booleanPreferencesKey("setup_completed")
        val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        val MASTER_GAIN_DB = floatPreferencesKey("master_gain_db")
        val SWAP_71_SURROUND_BACK = booleanPreferencesKey("swap_71_surround_back")
        val SPEAKER_LAYOUT_PRESET = stringPreferencesKey("speaker_layout_preset")
        val TV_UI_SCALE = floatPreferencesKey("tv_ui_scale")
        val APP_LANGUAGE = stringPreferencesKey("app_language")
        val LAST_HOST_IP = stringPreferencesKey("last_host_ip")
        val LAST_HOST_PORT = intPreferencesKey("last_host_port")
        val LAST_SESSION_TOKEN = stringPreferencesKey("last_session_token")
    }

    val settingsFlow: Flow<PersistedSettings> = context.surroundDataStore.data.map { prefs ->
        PersistedSettings(
            setup = SpeakerSetup.fromCode(prefs[Keys.SETUP_CODE] ?: SpeakerSetup.S5_1.code),
            setupCompleted = prefs[Keys.SETUP_COMPLETED] ?: false,
            onboardingCompleted = prefs[Keys.ONBOARDING_COMPLETED] ?: false,
            masterGainDb = (prefs[Keys.MASTER_GAIN_DB] ?: -12.0f).coerceIn(-30.0f, 0.0f),
            swap71SurroundBack = prefs[Keys.SWAP_71_SURROUND_BACK],
            speakerLayoutPreset = prefs[Keys.SPEAKER_LAYOUT_PRESET]?.let { raw ->
                runCatching { SpeakerLayoutPreset.valueOf(raw) }.getOrNull()
            } ?: SpeakerLayoutPreset.STANDARD,
            tvUiScale = (prefs[Keys.TV_UI_SCALE] ?: 0.75f).coerceIn(0.60f, 1.15f),
            appLanguage = AppLanguage.fromCode(prefs[Keys.APP_LANGUAGE]),
            lastHostIp = prefs[Keys.LAST_HOST_IP] ?: "",
            lastHostPort = prefs[Keys.LAST_HOST_PORT] ?: 8080,
            lastSessionToken = prefs[Keys.LAST_SESSION_TOKEN] ?: ""
        )
    }

    suspend fun getSnapshot(): PersistedSettings = settingsFlow.first()

    suspend fun saveSetup(setup: SpeakerSetup, completed: Boolean = true) {
        context.surroundDataStore.edit { prefs ->
            prefs[Keys.SETUP_CODE] = setup.code
            prefs[Keys.SETUP_COMPLETED] = completed
        }
    }

    suspend fun saveMasterGainDb(gainDb: Float) {
        context.surroundDataStore.edit { prefs ->
            prefs[Keys.MASTER_GAIN_DB] = gainDb.coerceIn(-30.0f, 0.0f)
        }
    }

    suspend fun saveSwap71SurroundBack(enabled: Boolean) {
        context.surroundDataStore.edit { prefs ->
            prefs[Keys.SWAP_71_SURROUND_BACK] = enabled
        }
    }

    suspend fun saveSpeakerLayoutPreset(preset: SpeakerLayoutPreset) {
        context.surroundDataStore.edit { prefs ->
            prefs[Keys.SPEAKER_LAYOUT_PRESET] = preset.name
        }
    }

    suspend fun saveTvUiScale(scale: Float) {
        context.surroundDataStore.edit { prefs ->
            prefs[Keys.TV_UI_SCALE] = scale.coerceIn(0.60f, 1.15f)
        }
    }

    suspend fun saveAppLanguage(language: AppLanguage) {
        context.surroundDataStore.edit { prefs ->
            prefs[Keys.APP_LANGUAGE] = language.code
        }
    }

    suspend fun saveOnboardingCompleted(completed: Boolean) {
        context.surroundDataStore.edit { prefs ->
            prefs[Keys.ONBOARDING_COMPLETED] = completed
        }
    }

    suspend fun saveLastConnection(hostIp: String, port: Int, token: String) {
        context.surroundDataStore.edit { prefs ->
            prefs[Keys.LAST_HOST_IP] = hostIp
            prefs[Keys.LAST_HOST_PORT] = port
            prefs[Keys.LAST_SESSION_TOKEN] = token
        }
    }
}
