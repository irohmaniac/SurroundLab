package de.surroundlab.app.ui.i18n

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Devices
import androidx.compose.material.icons.rounded.SurroundSound
import androidx.compose.material.icons.rounded.Tune
import de.surroundlab.app.domain.model.GeneratorPreset
import de.surroundlab.app.domain.model.OrbitSpeed
import de.surroundlab.app.domain.model.PanningMode
import de.surroundlab.app.domain.model.SignalSourceType
import de.surroundlab.app.domain.model.SpeakerChannel
import de.surroundlab.app.domain.model.SpeakerLayoutPreset
import de.surroundlab.app.domain.model.SpeakerSetup
import de.surroundlab.app.ui.components.OnboardingSlide

val StringsDe = AppStrings(
    langCode = "de",
    back = "Zurück",
    close = "Schließen",
    ok = "OK",
    apply = "Übernehmen",
    cancel = "Abbrechen",
    activeTag = "AKTIV",
    ready = "Bereit",
    testBtn = "Test",
    stopBtn = "Stop",
    startBtn = "Start",
    pauseBtn = "Pause",
    deleteBtn = "Löschen",
    settingsLabel = "Einstellungen",
    homeHubBtn = "Home Hub",

    languageSectionTitle = "Sprache / Language",
    languageAutoLabel = "Auto",
    languageAutoHint = { resolvedName -> "Automatisch ($resolvedName)" },

    setupSubtitle = { setup ->
        when (setup) {
            SpeakerSetup.S2_0 -> "Front Links & Rechts"
            SpeakerSetup.S2_1 -> "Stereo + Subwoofer"
            SpeakerSetup.S3_1 -> "Front + Center + Sub"
            SpeakerSetup.S5_0 -> "5 Satellitenkanäle"
            SpeakerSetup.S5_1 -> "6 Diskrete Kanäle"
            SpeakerSetup.S7_1 -> "8 Diskrete Kanäle"
        }
    },
    channelName = { ch -> ch.displayName },
    sourceLabel = { src ->
        when (src) {
            SignalSourceType.PINK_NOISE -> "Pink Noise"
            SignalSourceType.SINE -> "Sinuston"
            SignalSourceType.CUSTOM_AUDIO -> "Eigene Audiodatei"
        }
    },
    sourceShortLabel = { src ->
        when (src) {
            SignalSourceType.PINK_NOISE -> "Pink Noise"
            SignalSourceType.SINE -> "Sinus"
            SignalSourceType.CUSTOM_AUDIO -> "Audio"
        }
    },
    generatorPresetLabel = { preset ->
        when (preset) {
            GeneratorPreset.ALL_ACTIVE -> "Alle aktiven Kanäle"
            GeneratorPreset.P2_1 -> "2.1 Stereo + Sub"
            GeneratorPreset.P2_0 -> "2.0 Front"
            GeneratorPreset.P0_1_SUB -> "0.1 Nur Subwoofer"
            GeneratorPreset.CUSTOM -> "Benutzerdefiniert"
        }
    },
    panningModeLabel = { mode ->
        when (mode) {
            PanningMode.SWEET_SPOT_VBAP -> "Virtuelles Objekt"
            PanningMode.ROOM_DISTANCE -> "Lautsprecher-Fokus"
        }
    },
    panningModeHint = { mode ->
        when (mode) {
            PanningMode.SWEET_SPOT_VBAP -> "Distanz- & Luftdämpfung"
            PanningMode.ROOM_DISTANCE -> "Boxen-Nähe"
        }
    },
    orbitSpeedLabel = { spd ->
        when (spd) {
            OrbitSpeed.SLOW -> "Langsam"
            OrbitSpeed.MEDIUM -> "Mittel"
            OrbitSpeed.FAST -> "Schnell"
        }
    },
    layoutPresetShort = { preset ->
        when (preset) {
            SpeakerLayoutPreset.COMPACT -> "Eng (±22°)"
            SpeakerLayoutPreset.STANDARD -> "Standard (±30°)"
            SpeakerLayoutPreset.WIDE -> "Breit (±40°)"
        }
    },
    layoutPresetSummary = { preset ->
        when (preset) {
            SpeakerLayoutPreset.COMPACT -> "Front ±22° • Surround ±100° • Back ±140°"
            SpeakerLayoutPreset.STANDARD -> "Front ±30° • Surround ±110° • Back ±150°"
            SpeakerLayoutPreset.WIDE -> "Front ±40° • Surround ±120° • Back ±155°"
        }
    },
    layoutPresetDetail = { preset ->
        when (preset) {
            SpeakerLayoutPreset.COMPACT -> "Für schmale TV-Möbel oder einen kurzen Sitzabstand, bei dem die Frontlautsprecher nah am Fernseher stehen."
            SpeakerLayoutPreset.STANDARD -> "Klassisches gleichschenkliges Stereo-/Surround-Dreieck nach Dolby- & ITU-Empfehlung."
            SpeakerLayoutPreset.WIDE -> "Für weit außen aufgestellte Front-Boxen oder eine breite Sitzgruppe mit viel Abstand nach links/rechts."
        }
    },

    roomTvFront = "TV FRONT",
    roomSweetSpotDesc = "Hörplatz",
    roomSoundPuckDesc = "Sound Puck",

    tvTopologyBadge = "TV TOPOLOGIE",
    setupTvSubtitle = "Lautsprecher-Setup wählen & Smartphone / Browser koppeln",
    connectBrowserIosBtn = "Mit Browser / iOS verbinden",
    goToHomeHubBtn = "Zum Home Hub",
    activeSpeakerConfigHeading = "Aktive Lautsprecher-Konfiguration",
    legendNavigate = "Navigieren",
    legendActivate = "Aktivieren",
    activeBadgeUpper = "AKTIV",
    profileSpecUpper = "PROFIL-SPEZIFIKATION",
    setupPrefix = "SETUP",
    setupStreamDesc = { ch ->
        "Öffnet einen $ch-Kanal AudioTrack (48 kHz PCM). Nicht belegte Kanäle werden digital mit Stille (0.0f) aufgefüllt."
    },
    specChannelsActive = "Kanäle aktiv",
    specPcmStream = "PCM Stream",
    specPcmValue = { ch -> "$ch Kanäle @ 48 kHz" },
    specSubwooferLfe = "Subwoofer LFE",
    specSubwooferLfeValue = "Tiefpass bis 80 Hz",
    hdmiMultichannel = { maxCh -> "HDMI Mehrkanal (${maxCh}ch)" },
    outputStereo = { maxCh -> "Ausgang: Stereo (${maxCh}ch)" },
    headroomProtectionTag = "-12 dBFS Schutz",
    mobileSetupTitle = "Speaker Setup",
    mobileSetupSubtitle = "Raum-Topologie wählen",
    setupActiveTag = { code -> "$code AKTIV" },
    mobileSetupSelectPrompt = "Wähle dein reales Lautsprechersystem",
    mobileSetupConfirmBtn = { code -> "$code Bestätigen & zum Hub" },

    hubSetupChipLabel = "Setup:",
    hubAudioBtn = { count -> "Audio ($count)" },
    hubBrowserIosBtn = "Browser / iOS",
    tvHubSectionTitle = "Akustik-Testsuite",
    tvHubSectionSubtitle = "Wähle einen Kalibrierungsmodus mit der Fernbedienung (Links/Rechts + OK).",
    tvHubChannelsBadge = { count, code -> "$count Kanäle ($code)" },
    tvHostActiveLabel = { ip -> "TV Host aktiv: $ip" },
    wlanCheckPrompt = "WLAN prüfen",
    wlanNotDetected = "WLAN nicht erkannt",
    remoteAndroidAndBrowser = "Verbunden: Handy-App & Browser",
    remoteAndroidOnly = { count -> "Verbunden: Handy-App" + if (count > 1) " ($count)" else "" },
    remoteBrowserOnly = { count -> "Verbunden: Browser" + if (count > 1) " ($count)" else "" },
    remoteNone = "Keine Fernsteuerung",
    headroomStatusBar = { db -> "Headroom: $db dBFS • Soft-Limiter aktiv" },
    mode1Badge = "MODUS 01",
    mode1Title = "Einzellautsprecher-Test",
    mode1TvDesc = { chs -> "Split-View Diagnostik. Isoliere einzelne Kanäle ($chs), prüfe die reale Verkabelung und teste Pegel." },
    mode1MobileDesc = { chs -> "Interaktiver 2D-Raumplan. Isoliere einzelne Kanäle ($chs) für Pegel- & Kanalprüfung." },
    mode1Footer = "Pink Noise • Sinus • Audio",
    mode2Badge = "MODUS 02",
    mode2Title = "Frequenz- & Preset-Generator",
    mode2TvDesc = "Kontinuierlicher Sinuston-Generator mit D-Pad Stepper, Pink Noise und Subwoofer-Raummoden-Presets.",
    mode2MobileDesc = "Taktiles Dreh-Jog-Wheel mit Haptik, Sinustönen, Rosa Rauschen und Subwoofer-Crossover-Presets.",
    mode2Footer = "20 Hz – 20 kHz Frequenzband",
    mode3Badge = "MODUS 03",
    mode3Title = "2D-Raum-Puck",
    mode3TvDesc = "Echtzeit-Bühnenpanner. Bewege den Sound-Puck mit D-Pad oder Auto-Orbit frei zwischen den Lautsprechern.",
    mode3MobileDesc = "Live-Raumpanner. Schiebe ein virtuelles Objekt oder fokussiere einzelne Lautsprecher direkt per Touch & Auto-Orbit.",
    mode3Footer = "Virtuelles Objekt & Lautsprecher-Fokus",
    mobileConnConnected = { host -> "Verbunden: $host" },
    mobileConnConnecting = "Verbinde mit TV...",
    mobileConnLocal = "Lokal-Modus (2.0)",
    mobileRoleRemote = "TV-Fernsteuerung",
    mobileRoleLocal = "Raum-Kalibrator (Stereo)",
    mobileCard1FooterLeft = { count -> "$count Kanäle" },
    mobileCard2FooterLeft = "Frequenzspektrum",
    mobileCard3FooterLeft = "Surround-Panning",
    mobileCard3FooterRight = "360° Echtzeit",
    mobileFooterTv = { ip -> "TV: $ip" },
    mobileFooterLocal = "Lokaler Downmix aktiv",
    mobileFooterHeadroom = { db -> "Headroom $db dBFS" },

    singleTitle = "Einzellautsprecher-Test",
    singleTvSubtitle = { setupName -> "$setupName • Split-View Kanal-Diagnostik" },
    singleMobileSubtitle = { code -> "$code DIAGNOSTIK" },
    legendChannel = "Kanal",
    legendStartStop = "Start / Stop",
    legendSignalChoice = "Signalwahl",
    singleActiveBadge = { s -> "$s AKTIV" },
    pinkNoiseFreqRange = "Rosa Rauschen (20Hz–20kHz)",
    sineToneLabel = { hz -> "Sinuston $hz Hz" },
    noAudioSelected = "Keine Audiodatei gewählt",
    noAudioShort = "Keine Audiodatei",
    configureSignalDesc = { name -> "Quelle für $name" },
    syncRoomPlanUpper = "SYNCHRONISIERTER RAUMPLAN",
    signalActiveUpper = "SIGNAL ANLIEGEND",
    sourcePrefix = { lbl -> "Quelle: $lbl" },
    channelDialogTitle = { name, s -> "$name ($s) – Signalquelle" },
    channelSheetSubtitle = "Signalquelle für diesen Lautsprecher wählen",
    sineFreqLabel = { hz -> "Sinusfrequenz: $hz Hz" },
    sineDesc = { hz -> "Diskreter Reinton ($hz Hz)" },
    pinkNoiseDesc = "Gleichmäßige Energie pro Oktave (Standard)",
    customAudioDesc = "Eigener Referenz-Track (Endlosschleife)",
    frequencyLabel = "Frequenz",
    noCustomAudioTvHint = "Keine eigenen Audiodateien vorhanden. Bitte über Smartphone oder Browser hochladen.",
    noCustomAudioMobileHint = "Noch keine Audiodateien in der Bibliothek. Öffne die Audio-Bibliothek im Hub zum Hochladen.",
    applyToAllChannels = "Für alle Lautsprecher übernehmen",
    applySignalBtn = "Signal übernehmen",

    generatorTitle = "Frequenz- & Preset-Generator",
    generatorMobileSubtitle = "DSP SYNTHESIZER",
    activeChannelsPrefix = { chs -> "Aktive Kanäle: $chs" },
    noneText = "Keine",
    legendFreq = "Frequenz",
    upStepHint = "HOCH (+1 / +5 / +25 Hz)",
    downStepHint = { playing -> "RUNTER (-1 / -5 / -25 Hz) • OK: ${if (playing) "Pause" else "Start"}" },
    pauseAction = "Pause",
    startAction = "Start",
    pinkNoiseStartTv = "Rosa Rauschen starten [OK]",
    pinkNoisePauseTv = "Rosa Rauschen pausieren [OK]",
    sineStartTv = { hz -> "$hz Hz Dauerton starten [OK]" },
    sinePauseTv = { hz -> "$hz Hz Dauerton pausieren [OK]" },
    pinkNoiseStartMobile = "Pink Noise starten",
    pinkNoisePauseMobile = "Pink Noise anhalten",
    sineStartMobile = { hz -> "$hz Hz Dauerton starten" },
    sinePauseMobile = { hz -> "$hz Hz anhalten" },
    quickSelectUpper = "SCHNELLWAHL",
    referenceUpper = "REFERENZ",
    quickPresets = listOf(
        QuickFreqPresetText(40, "40 Hz", "Sub-Raummode"),
        QuickFreqPresetText(60, "60 Hz", "Subwoofer Basis"),
        QuickFreqPresetText(80, "80 Hz", "THX Trennfrequenz"),
        QuickFreqPresetText(400, "400 Hz", "Standard-Mittenfokus"),
        QuickFreqPresetText(1000, "1.000 Hz", "1 kHz Pegelreferenz"),
        QuickFreqPresetText(10000, "10.000 Hz", "Hochtöner-Check")
    ),
    digitalLevelLabel = "Digitalpegel:",
    signalUpper = "SIGNAL",
    freqUpper = "FREQUENZ",
    tapToEnter = "Tippen für Eingabe",
    freqDialogTitle = "Frequenz direkt eingeben",
    freqDialogRangeLabel = "Frequenz (20 – 20.000 Hz)",

    puckTitle = "2D-Raum-Puck",
    puckMobileSubtitle = "LIVE-RAUMPANNER",
    puckTvSubtitle = { mode, az, hint -> "$mode • Azimut ${az}° • $hint" },
    legendMovePuck = "Puck bewegen",
    legendSideMenuActive = "Seitenmenü aktiv",
    legendBackForHub = "Zurück für Hub",
    puckSourcePrefix = { lbl -> "Quelle: $lbl" },
    virtualDistance = { m -> "Virtuelle Distanz: $m m" },
    roomControlUpper = "RAUM-STEUERUNG",
    puckBtnLabel = "Puck",
    orbitStateLabel = { on -> "Orbit: ${if (on) "An" else "Aus"}" },
    stateOn = "An",
    stateOff = "Aus",
    orbitWithSpeed = { s -> "Orbit ($s)" },
    autoOrbitBtn = "Auto-Orbit",
    speedPrefix = { s -> "Tempo: $s" },
    noAudioUploaded = "Keine Audios hochgeladen",
    trackCycleLabel = { idx, total, name -> "Track ($idx/$total): $name" },
    puckDecodingMobile = "Audio wird dekodiert… (Pausieren)",
    puckDecodingTv = "Lädt…",
    puckPauseMobile = "Raum-Audio pausieren",
    puckStartMobile = "Raum-Audio starten",
    noAudioOnTvHint = "Noch keine Audiodateien auf TV vorhanden – im Hub hochladen.",

    qrTitle = "Browser / iOS koppeln",
    qrBody = "Dieser QR-Code ist nur für die Steuerung im Browser (z. B. iPhone / Laptop) nötig.\nDie SurroundLab Android-App verbindet sich im selben WLAN automatisch.",
    tvSettingsTitle = "TV Host Einstellungen",
    tvSettingsSubtitle = "Digitaler Gehör- & Hochtönerschutz + HDMI-Diagnose",
    masterLevelTitleTv = { def -> "Master-Ausgangspegel (Standard: $def dBFS)" },
    swap71Title = "7.1 Surround & Back tauschen (HDMI-HAL)",
    swap71DescTv = "Aktivieren, wenn bei 7.1 SL/SR und SBL/SBR vertauscht sind (z. B. Chromecast / Amlogic).",
    swap71DescMobile = "HDMI-HAL-Workaround (z. B. Chromecast / Amlogic), falls SL/SR und SBL/SBR vertauscht sind.",
    layoutAnglesTitle = "Aufstellungs-Winkel (Virtuelles Objekt)",
    tvUiScaleTitle = "TV-Oberflächen-Größe (DPI-Zoom)",
    scaleSmallerBtn = "−5 % (Kleiner)",
    scaleDefaultBtn = "Standard (75 %)",
    scaleLargerBtn = "+5 % (Größer)",
    audioOutputDiag = { name, ch -> "Audio-Ausgang: $name (max. $ch Kanäle)" },
    hdmiDiagOk = "Mehrkanal-PCM erkannt. Diskretes 5.1 / 7.1 Routing über HDMI/eARC aktiv.",
    hdmiDiagStereoWarn = "Hinweis: Aktuell nur Stereo erkannt. Prüfe am TV/Chromecast das Audioformat auf „Automatisch“ / „PCM“ und schließe idealerweise direkt am AV-Receiver an.",
    setupChangeBtn = { code -> "Setup ($code) ändern" },
    setupChangeFullBtn = { code -> "Lautsprecher-Setup ($code) ändern" },
    checkNetworkHdmiBtn = "Netzwerk / HDMI prüfen",
    guideBtnShort = "Kurzanleitung",
    guideShowBtn = "Kurzanleitung anzeigen",
    tvAudioLibTitle = { count -> "Audio-Bibliothek ($count)" },
    tvAudioLibSubtitle = "Dateien vom Smartphone oder Browser per HTTP-Upload",
    tvAudioLibEmpty = "Noch keine eigenen Audiodateien vorhanden.\nLade Tracks über die SurroundLab Smartphone-App oder die Browser-UI hoch.",

    mobileLibTitle = "Audio Library",
    mobileLibSubtitle = "REFERENZ-AUDIODATEIEN",
    libCountBadge = { count, mb -> "$count Titel • $mb MB" },
    syncInProgressTitle = "Synchronisiere mit TV…",
    syncAllDoneTitle = "Alle Titel auf TV synchron",
    syncPendingTitle = { count -> "$count Titel noch nicht auf TV" },
    syncLocalTitle = "Lokale Smartphone-Bibliothek",
    syncActiveSub = { count -> "$count Übertragung(en) laufen über WLAN" },
    syncAllDoneSub = "Alle Titel bereit auf TV & Handy",
    syncPendingSub = "Tippe auf „Jetzt syncen“, um fehlende Dateien an den TV zu senden",
    syncOfflineSub = "Neue Dateien werden beim nächsten TV-Connect automatisch gesendet",
    syncNowBtn = "Jetzt syncen",
    libEmptyTitle = "Noch keine Audiodateien",
    libEmptyBody = "Wähle .wav, .mp3, .flac oder .ogg über die sichere Android-Dateiauswahl (max. 30 MB).",
    statusSyncedOnTv = "Auf TV synchron",
    statusSavedLocally = "Lokal gespeichert",
    statusUploading = "Wird an TV übertragen…",
    statusOnlyOnPhone = "Nur auf Handy (nicht auf TV)",
    statusWaitingSync = "Wartet auf TV-Sync",
    statusUploadFailed = "TV-Upload fehlgeschlagen",
    statusDeletingOnTv = "Wird auf TV gelöscht…",
    sendToTvDesc = "An TV senden",
    uploadAudioBtn = "+ Audiodatei hochladen",
    mobileConnTitle = "TV-Verbindung & Pegel",
    mobileConnSubtitle = "mDNS DISCOVERY & HEADROOM",
    refreshSearchDesc = "Suche neu starten",
    currentModeTitle = "Aktueller Modus",
    connConnectedDetail = { ip, port -> "Verbunden mit TV-Host ($ip:$port)" },
    connConnectingDetail = "Verbindung zum TV wird hergestellt...",
    connLocalDetail = "Lokal-Modus (2.0 Stereo Downmix auf Smartphone)",
    disconnectTvBtn = "Vom TV trennen (Lokal-Modus)",
    discoveredTvsTitle = "Gefundene TVs im WLAN (mDNS)",
    noTvsFoundText = "Keine SurroundLab TV-Hosts gefunden. Prüfe, ob die App auf dem TV geöffnet ist.",
    connectBtn = "Verbinden",
    manualConnTitle = "Manuelle TV-Verbindung",
    tvIpLabel = "TV IP-Adresse",
    portLabel = "Port",
    sessionTokenLabel = "6-stelliger Session-Token (vom TV)",
    manualConnectBtn = "Manuell verbinden",
    masterLevelTitleMobile = "Master-Pegel (Gehörschutz)",

    onboardingSlides = listOf(
        OnboardingSlide(
            badge = "SCHRITT 1 / 3 • ÜBERBLICK",
            title = "Willkommen bei SurroundLab",
            subtitle = "Dein Mehrkanal-Teststudio für 2.0- bis 7.1-Heimkinos direkt über HDMI.",
            icon = Icons.Rounded.SurroundSound,
            bullets = listOf(
                "Einzellautsprecher-Test" to "Prüfe jeden Kanal isoliert auf korrekte Verkabelung mit Pink Noise, Sinuston oder eigenen Tracks.",
                "Frequenz- & Preset-Generator" to "Durchfahre 20 Hz – 20 kHz, finde dröhnende Raummoden und kontrolliere deinen Subwoofer.",
                "2D-Raum-Puck" to "Schiebe ein virtuelles Sound-Objekt oder Fokuspunkt in Echtzeit durch den Raum."
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
    ),
    onboardingSkipToNext = "Zurück",
    onboardingNext = "Weiter →",
    onboardingFinish = "Los geht's",

    tvInstall = TvInstallStrings(
        menuBtn = "App auf dem TV installieren (Anleitung)",
        badge = "TV-INSTALLATION • VIA „DOWNLOADER“",
        title = "SurroundLab auf dem TV installieren",
        subtitle = "So bringst du die App in 4 einfachen Schritten ohne PC auf deinen Chromecast / Android TV:",
        steps = listOf(
            "1. „Downloader“ auf dem TV laden" to "Öffne den Google Play Store auf deinem Fernseher, suche nach „Downloader by AFTVnews“ (oranges Symbol) und installiere die kostenlose App.",
            "2. Code 7281113 im Downloader eingeben" to "Öffne „Downloader“ am TV, tippe einfach den Zahlencode 7281113 ein (oder die GitHub-Adresse unten) und drücke auf „Go“.",
            "3. Installation einmalig erlauben" to "Falls dein Fernseher fragt, ob „Downloader“ Apps installieren darf, gehe kurz auf Einstellungen → Zulassen und tippe danach auf Installieren.",
            "4. Öffnen & automatisch verbinden" to "Starte SurroundLab auf dem Fernseher. Sobald TV und Handy im selben WLAN sind, finden sich beide ganz ohne QR-Code automatisch."
        ),
        copyUrlBtn = "GitHub-Link kopieren",
        urlCopiedToast = "Download-Link in die Zwischenablage kopiert",
        closeBtn = "Schließen"
    ),
    donatePaypalBtn = "Projekt unterstützen (PayPal)",

    update = UpdateStrings(
        sectionTitle = "App-Updates (GitHub)",
        currentVersion = { v -> "Installierte Version: v$v" },
        checkBtn = "Auf Updates prüfen",
        checkingText = "Suche auf GitHub nach neuer Version…",
        upToDateText = { v -> "Alles aktuell (v$v)." },
        availableText = { v -> "Neue Version v$v verfügbar!" },
        downloadInstallBtn = "Herunterladen & Installieren",
        downloadingText = { pct -> "Lade APK herunter… $pct%" },
        installNowBtn = "Jetzt installieren",
        permissionHint = "Bitte erlaube einmalig die Installation aus dieser Quelle und tippe danach erneut auf „Jetzt installieren“.",
        errorText = { err -> "Update-Prüfung fehlgeschlagen ($err)" }
    )
)
