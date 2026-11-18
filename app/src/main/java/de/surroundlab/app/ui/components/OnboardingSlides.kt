package de.surroundlab.app.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Devices
import androidx.compose.material.icons.rounded.SurroundSound
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.ui.graphics.vector.ImageVector

data class OnboardingSlide(
    val badge: String,
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val bullets: List<Pair<String, String>>
)

object OnboardingContent {
    val slides: List<OnboardingSlide> = listOf(
        OnboardingSlide(
            badge = "SCHRITT 1 / 3 • ÜBERBLICK",
            title = "Willkommen bei SurroundLab",
            subtitle = "Dein Mehrkanal-Teststudio für 2.0- bis 7.1-Heimkinos direkt über HDMI.",
            icon = Icons.Rounded.SurroundSound,
            bullets = listOf(
                "Einzellautsprecher-Test" to "Prüfe jeden Kanal isoliert auf korrekte Verkabelung mit Pink Noise, Sinuston oder eigenen Tracks.",
                "Frequenz- & Preset-Generator" to "Durchfahre 20 Hz – 20 kHz, finde dröhnende Raummoden und kontrolliere deinen Subwoofer.",
                "2D-Raum-Puck" to "Schiebe ein virtuelles Sound-Objekt oder Fokuspunkt in Echtzeit durch den Raum.",
                "Raummoden & Einmessung" to "Miss per Handy-Mikrofon automatisch den Frequenzgang an mehreren Hörpositionen, vergleiche Aufstellungen und exportiere DIN-A4-PDFs."
            )
        ),
        OnboardingSlide(
            badge = "SCHRITT 2 / 3 • STEUERUNG",
            title = "TV, Smartphone-App oder Browser",
            subtitle = "Steuere SurroundLab genau so, wie es für dich am bequemsten ist.",
            icon = Icons.Rounded.Devices,
            bullets = listOf(
                "Direkt am Fernseher" to "Alle Modi, Einstellungen und Audio-Tests sind vollständig mit der TV-Fernbedienung (D-Pad) bedienbar.",
                "Android-App (Automatisch)" to "Installiere SurroundLab einfach auch auf deinem Handy – beide Geräte finden sich im selben WLAN automatisch und synchronisieren Setups & Audiodateien (kein QR-Code nötig).",
                "Browser / iOS (QR-Code oben rechts)" to "Über „Browser / iOS“ oben rechts auf dem TV öffnest du einen QR-Code, um die Steuerung sofort im Smartphone- oder Laptop-Browser zu laden."
            )
        ),
        OnboardingSlide(
            badge = "SCHRITT 3 / 3 • PRAXIS-TIPPS",
            title = "Wichtige Tipps für dein Heimkino",
            subtitle = "Kurz vor dem Start noch drei Einstellungen, die oft hilfreich sind:",
            icon = Icons.Rounded.Tune,
            bullets = listOf(
                "7.1 Surround & Back vertauscht?" to "Manche TV-Sticks (z. B. Chromecast) vertauschen hardwareseitig SL/SR und SBL/SBR. In den Einstellungen (Zahnrad) gibt es dafür den Schalter „7.1 Surround & Back tauschen“.",
                "Aufstellungs-Winkel wählen" to "Ebenfalls in den Einstellungen kannst du zwischen Eng (±22°), Standard (±30°) und Breit (±40°) passend zu deinem Raum wählen.",
                "Gehörschutz & AVR-Modus" to "Die App startet geschützt bei -12 dBFS mit Soft-Limiter. Stelle am AV-Receiver idealerweise „Multi Ch In“ / „Straight“ (ohne Upmixer) ein."
            )
        )
    )
}
