package de.surroundlab.app.domain.model

import kotlinx.serialization.Serializable
import java.util.Locale

@Serializable
enum class AppLanguage(val code: String, val nativeName: String) {
    AUTO("auto", "Auto"),
    DE("de", "Deutsch"),
    EN("en", "English"),
    ES("es", "Español"),
    FR("fr", "Français"),
    PT("pt", "Português"),
    JA("ja", "日本語");

    fun resolve(deviceLocale: Locale = Locale.getDefault()): AppLanguage {
        if (this != AUTO) return this
        return when (deviceLocale.language.lowercase()) {
            "de" -> DE
            "es" -> ES
            "fr" -> FR
            "pt" -> PT
            "ja" -> JA
            else -> EN
        }
    }

    companion object {
        fun fromCode(raw: String?): AppLanguage {
            val normalized = raw?.trim()?.lowercase() ?: return AUTO
            return entries.firstOrNull {
                it.code.lowercase() == normalized || it.name.lowercase() == normalized
            } ?: AUTO
        }
    }
}
