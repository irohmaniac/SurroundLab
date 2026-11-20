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
            GeneratorPreset.ALL_WITHOUT_SUB -> "Alle außer Subwoofer"
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
    roomSoundPuckDesc = "Klangbühne",

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
    mode2Title = "Frequenzgenerator",
    mode2TvDesc = "Kontinuierlicher Sinuston-Generator mit D-Pad Stepper, Pink Noise und Subwoofer-Raummoden-Presets.",
    mode2MobileDesc = "Taktiles Dreh-Jog-Wheel mit Haptik, Sinustönen, Rosa Rauschen und Subwoofer-Crossover-Presets.",
    mode2Footer = "20 Hz – 20 kHz Frequenzband",
    mode3Badge = "MODUS 03",
    mode3Title = "2D-Klangbühne",
    mode3TvDesc = "Echtzeit-Raumpanner. Bewege die Klangquelle mit D-Pad oder Auto-Orbit frei zwischen den Lautsprechern.",
    mode3MobileDesc = "Live-Raumpanner. Schiebe ein virtuelles Objekt oder fokussiere einzelne Lautsprecher direkt per Touch & Auto-Orbit.",
    mode3Footer = "Virtuelles Objekt & Lautsprecher-Fokus",
    mode4Badge = "MODUS 04",
    mode4Title = "Raummoden & Einmessung",
    mode4TvDesc = "Automatische Frequenzgang-Messung gegen Raummoden mit deinem Android-Smartphone als Messmikrofon.",
    mode4MobileDesc = "Automatischer Frequenz-Sweep über Handy-Mikrofon: Finde Raummoden, vergleiche Hörpositionen & exportiere DIN-A4-PDFs.",
    mode4Footer = "Mikrofon-Sweep & PDF-Bericht",
    mobileConnConnected = { host -> "Verbunden: $host" },
    mobileConnConnecting = "Verbinde mit TV...",
    mobileConnLocal = "Lokal-Modus (2.0) · Nicht verbunden",
    mobileRoleRemote = "TV-Fernsteuerung",
    mobileRoleLocal = "Raum-Kalibrator (Stereo)",
    mobileCard1FooterLeft = { count -> "$count Kanäle" },
    mobileCard2FooterLeft = "Frequenzspektrum",
    mobileCard3FooterLeft = "Surround-Panning",
    mobileCard3FooterRight = "360° Echtzeit",
    mobileCard4FooterLeft = "Mikrofon-Analyse",
    mobileCard4FooterRight = "Graph & PDF",
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

    generatorTitle = "Frequenzgenerator",
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

    puckTitle = "2D-Klangbühne",
    puckMobileSubtitle = "LIVE-RAUMPANNER",
    puckTvSubtitle = { mode, az, hint -> "$mode • Azimut ${az}° • $hint" },
    legendMovePuck = "Quelle bewegen",
    legendSideMenuActive = "Seitenmenü aktiv",
    legendBackForHub = "Zurück für Hub",
    puckSourcePrefix = { lbl -> "Quelle: $lbl" },
    virtualDistance = { m -> "Virtuelle Distanz: $m m" },
    roomControlUpper = "RAUM-STEUERUNG",
    puckBtnLabel = "Quelle",
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
    lfeLowPassTitle = "LFE-Tiefpass (Rauschen & Musik)",
    lfeLowPassDesc = "Begrenzt Rosa Rauschen und eigene Tracks auf dem Subwoofer (.1). Reine Sinustöne und Raum-Messungen laufen immer direkt bis zum AVR-Crossover.",
    lfeDirectLabel = "Direkt (Aus)",
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
                "Frequenzgenerator" to "Durchfahre 20 Hz – 20 kHz, finde dröhnende Raummoden und kontrolliere deinen Subwoofer.",
                "2D-Klangbühne" to "Schiebe ein virtuelles Sound-Objekt oder Fokuspunkt in Echtzeit durch den Raum.",
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
    ),

    room = RoomMeasurementStrings(
        screenTitle = "Raummoden & Einmessung",
        screenSubtitle = "AUTOMATISCHER FREQUENZ-SWEEP",
        guideBtn = "Anleitung",
        tabMeasure = "Messen",
        tabArchive = "Gespeichert",
        tabCompare = "Vergleich",
        tvInfoBadge = "SMARTPHONE-MIKROFON ERFORDERLICH",
        tvInfoTitle = "Einmessung über Android-Smartphone starten",
        tvInfoBody = "Für die automatische Raummoden-Messung wird das Mikrofon deines Android-Smartphones direkt am Hörplatz benötigt. Der Fernseher gibt währenddessen die exakten Testtöne über deine Anlage aus.",
        tvInfoSteps = listOf(
            "1. SurroundLab auf deinem Android-Handy installieren & öffnen (im selben WLAN verbindet es sich automatisch).",
            "2. Im Handy auf „Raummoden & Einmessung“ tippen und Mikrofonzugriff erlauben.",
            "3. Handy ruhig auf Ohrhöhe an den Hörplatz halten und Sweep starten – die Auswertung samt PDF-Export erfolgt direkt am Handy."
        ),
        tvActiveSweepStatus = { hz -> "Messung aktiv vom Smartphone • Aktueller Testton: $hz Hz" },
        tvShowQrBtn = "Verbindungs-Infos / QR zeigen",
        guideTitle = "So funktioniert die Raum-Einmessung",
        guideSubtitle = "Alles Wichtige für aussagekräftige Messergebnisse:",
        guideItems = listOf(
            "Wofür ist das Tool?" to "Die App spielt automatisch Sinustöne nacheinander ab und misst die Lautstärke an deinem Hörplatz. So erkennst du dröhnende Raummoden (Peaks) und Auslöschungen (Löcher) und kannst verschiedene Subwoofer-/Boxen-Aufstellungen vergleichen.",
            "Steuergerät nötig (Android-App)" to "Du kannst die Messung direkt mit deinem verbundenen TV-Host (5.1/7.1) oder im Standalone-Modus (Kabel/Bluetooth an Stereoanlage) über die Android-App durchführen. Mobile Browser blockieren leider das Mikrofon über lokales WLAN.",
            "Vorbereitung & Ruhe" to "Schalte Lüfter/Störquellen aus, bleib während des Durchlaufs ruhig sitzen, vermeide Rascheln und halte das Handy ruhig auf Ohrhöhe (Mikrofon frei).",
            "Lautstärke der Anlage" to "Stelle normale bis gehobene Zimmerlautstärke ein und nutze vorab den Pegel-Check („Optimal ✓“), bevor du den Sweep startest.",
            "Ehrliche Einordnung" to "Smartphone-Mikrofone sind nicht wie teure Messmikrofone kalibriert (besonders unter 40 Hz fallen viele Handys leicht ab). Die absoluten dB-Werte sind daher relativ – aber Peaks, Löcher und der direkte Vergleich verschiedener Positionen sind sehr aussagekräftig."
        ),
        guideGotIt = "Verstanden, los geht's",
        micPermissionTitle = "Mikrofon-Berechtigung benötigt",
        micPermissionDesc = "Für die automatische Lautstärkemessung am Hörplatz braucht die App einmalig Zugriff auf das Smartphone-Mikrofon (nur während der Messung aktiv).",
        micPermissionBtn = "Mikrofon freigeben",
        micErrorMsg = "Mikrofon konnte nicht initialisiert werden. Bitte Berechtigung prüfen.",
        levelCheckTitle = "Vorab-Pegelcheck",
        levelCheckDesc = "Prüfe kurz, ob deine Anlagen-Lautstärke für das Mikrofon sauber lesbar ist. Tipp: Ein kurzer Check weckt auch Subwoofer mit Abschaltautomatik vorab auf, sodass während der Messung keine Punkte verloren gehen.",
        levelCheckStartBtn = "Testton prüfen",
        levelCheckStopBtn = "Prüfung stoppen",
        levelAmbientLabel = { db -> "Raumrauschen: $db dBFS" },
        levelLiveLabel = { db, snr -> "Messpegel: $db dBFS (Abstand +$snr dB)" },
        verdictIdle = "Bereit – tippe auf „Testton prüfen“ oder starte direkt den Sweep.",
        verdictCalibrating = "Messe Umgebungsrauschen… bitte kurz still sein.",
        verdictTooQuiet = "Signal zu leise! Dreh die Anlage etwas lauter auf.",
        verdictOptimal = "Pegel optimal ✓ – sauber über dem Grundrauschen.",
        verdictClipping = "Mikrofon übersteuert! Dreh die Anlage etwas leiser.",
        speakersSectionTitle = "Lautsprecher-Auswahl",
        speakerPresetHint = { preset ->
            when (preset) {
                GeneratorPreset.P2_0 -> "Sinnvoll im reinen Bassbereich (20–200 Hz), um die gemeinsame Tiefton-Summierung oder L/R-Auslöschungen zu prüfen. Ab ca. 300 Hz entstehen zwischen zwei Boxen jedoch Kammfilter-Effekte am Mikrofon – für Vollspektrum-Messungen daher lieber nur einen einzelnen Lautsprecher (FL oder FR) messen."
                GeneratorPreset.P0_1_SUB -> "Misst ausschließlich den Subwoofer (.1 LFE) – ideal zum Finden reiner Bass-Raummoden (20–120 Hz) und zum Vergleichen neuer Sub-Standorte."
                GeneratorPreset.P2_1 -> "Sendet gleichzeitig an Fronts + LFE (gut für ‚Large‘-Fronts oder Anlagen ohne AVR-Bassmanagement; bei ‚Small‘-Boxen unter dem Crossover etwas lauter, im Hochton Kammfilter-Effekte möglich)."
                GeneratorPreset.ALL_WITHOUT_SUB -> "Prüft alle Satellitenkanäle ohne separaten LFE-Kanal (bei aktivem AVR-Bassmanagement spielt der Sub unter dem Crossover mit; zwischen mehreren Boxen entstehen im Hochton Kammfilter-Effekte)."
                GeneratorPreset.ALL_ACTIVE -> "Steuert alle aktiven Lautsprecher gleichzeitig an (nützlich für maximalen Gesamtpegel; zwischen mehreren Boxen entstehen ab ca. 200 Hz jedoch Kammfilter- und Phasen-Auslöschungen)."
                GeneratorPreset.CUSTOM -> "Empfohlener Mess-Standard (einzelner Lautsprecher, z. B. nur FL oder FR): Vermeidet Kammfilter-Auslöschungen im Mittel-/Hochton. Zeigt bei ‚Small‘-Boxen den realen Übergang zum Subwoofer (AVR leitet Bass um) und bei ‚Large‘-Boxen die reine Tiefton- & Vollbereichskurve samt Raummoden."
            }
        },
        presetSectionTitle = "Frequenzbereich & Schrittgröße",
        presetRoomModes = "Raummoden (20–200 Hz)",
        presetSubwoofer = "Subwoofer (20–120 Hz)",
        presetFullRange = "Vollspektrum (20–20k)",
        presetCustom = "Benutzerdefiniert",
        startHzLabel = "Start (Hz)",
        endHzLabel = "Ende (Hz)",
        stepModeLinear = "Linear (Hz)",
        stepModeOctave = "Logarithmisch (Oktave)",
        stepHzLabel = "Schrittweite (Hz)",
        stepOctaveLabel = { d -> "1/$d Oktave" },
        speedLabel = "Mess-Tempo",
        speedFast = "Schnell",
        speedStandard = "Standard",
        speedPrecise = "Präzise",
        estimatedTimeLabel = { pts, sec -> "$pts Messpunkte • ca. ${sec}s pro Hörposition" },
        positionNameLabel = "Name dieser Hörposition",
        defaultPositionName = { idx -> "Hörposition $idx" },
        startSweepBtn = "Messung starten",
        localPlaybackHint = "Wiedergabe erfolgt über dieses Handy (kein TV verbunden)",
        micSelectorLabel = "Mikrofon",
        micPhoneDefault = "Handy-Mikrofon (Standard)",
        addPositionSweepBtn = "Hörposition einmessen",
        countdownLabel = { sec -> "Bitte still halten… Start in $sec" },
        silenceCalibLabel = "Messe Grundrauschen im Raum…",
        sweepingLabel = { cur, tot, hz -> "Schritt $cur / $tot • $hz Hz" },
        cancelSweepBtn = "Messung abbrechen",
        activeSessionTitle = "Aktuelle Messsitzung",
        defaultSessionName = { d -> "Messung $d" },
        scaleLinearLabel = "Linear (Flat)",
        scaleDbaLabel = "dBA (Gehör)",
        meanCurveLabel = "Mittelwert",
        smoothLabel = "Glätten",
        addAnotherPositionBtn = "+ Weitere Hörposition",
        saveSessionBtn = "Durchlauf speichern",
        savedToast = "Messung gespeichert",
        renameSessionTitle = "Durchlauf benennen & Notiz",
        sessionNameHint = "z. B. Subwoofer Ecke links",
        sessionNoteHint = "Notiz (z. B. Crossover 80 Hz, Phase 0°)",
        renamePositionTitle = "Hörposition umbenennen",
        newPositionHint = "z. B. Sofa Mitte",
        archiveEmptyTitle = "Noch keine gespeicherten Messungen",
        archiveEmptyBody = "Starte im Tab „Messen“ deinen ersten Durchlauf und speichere ihn, um später Aufstellungen zu vergleichen oder PDFs zu exportieren.",
        positionsCountLabel = { c -> "$c Hörposition(en)" },
        loadInEditorBtn = "Öffnen / Ergänzen",
        exportPdfSingleBtn = "PDF",
        exportPdfSelectedBtn = { c -> "PDF exportieren ($c)" },
        selectForExportHint = "Tippe Checkboxen an, um mehrere Messungen auf eine DIN-A4-PDF zu packen.",
        compareHint = "Wähle 2 oder mehr gespeicherte Durchläufe, um deren Kurven direkt zu vergleichen:",
        compareMinTwoHint = "Mindestens 2 gespeicherte Messungen nötig.",
        pdfReportTitle = "SurroundLab — Raumakustik-Bericht",
        pdfGeneratedPrefix = "Erstellt",
        pdfPageLabel = { p, t -> "Seite $p / $t" },
        pdfChannelsPrefix = "Kanäle",
        pdfShareTitle = "Raummessung als PDF teilen",
        pdfErrorToast = "PDF-Export fehlgeschlagen.",
        pdfLayoutDialogTitle = "PDF-Seitenformat wählen",
        pdfLayoutDialogBody = "Für breite Messungen (z. B. 20 Hz – 20 kHz) ist das Querformat besonders übersichtlich.",
        pdfPortraitDefaultBtn = "Hochformat (Standard)",
        pdfLandscapeWideBtn = "Querformat (Breitbild)",
        pdfExportConfigTitle = "PDF-Export konfigurieren",
        pdfExportActionBtn = "PDF exportieren"
    )
)
