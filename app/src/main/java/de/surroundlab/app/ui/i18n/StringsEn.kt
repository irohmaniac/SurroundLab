package de.surroundlab.app.ui.i18n

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Devices
import androidx.compose.material.icons.rounded.SurroundSound
import androidx.compose.material.icons.rounded.Tune
import de.surroundlab.app.domain.model.GeneratorPreset
import de.surroundlab.app.domain.model.OrbitSpeed
import de.surroundlab.app.domain.model.PanningMode
import de.surroundlab.app.domain.model.SignalSourceType
import de.surroundlab.app.domain.model.SpeakerLayoutPreset
import de.surroundlab.app.domain.model.SpeakerSetup
import de.surroundlab.app.ui.components.OnboardingSlide

val StringsEn = AppStrings(
    langCode = "en",
    back = "Back",
    close = "Close",
    ok = "OK",
    apply = "Apply",
    cancel = "Cancel",
    activeTag = "ACTIVE",
    ready = "Ready",
    testBtn = "Test",
    stopBtn = "Stop",
    startBtn = "Start",
    pauseBtn = "Pause",
    deleteBtn = "Delete",
    settingsLabel = "Settings",
    homeHubBtn = "Home Hub",

    languageSectionTitle = "Language",
    languageAutoLabel = "Auto",
    languageAutoHint = { resolvedName -> "Auto ($resolvedName)" },

    setupSubtitle = { setup ->
        when (setup) {
            SpeakerSetup.S2_0 -> "Front Left & Right"
            SpeakerSetup.S2_1 -> "Stereo + Subwoofer"
            SpeakerSetup.S3_1 -> "Front + Center + Sub"
            SpeakerSetup.S5_0 -> "5 Satellite Channels"
            SpeakerSetup.S5_1 -> "6 Discrete Channels"
            SpeakerSetup.S7_1 -> "8 Discrete Channels"
        }
    },
    channelName = { ch -> ch.displayName },
    sourceLabel = { src ->
        when (src) {
            SignalSourceType.PINK_NOISE -> "Pink Noise"
            SignalSourceType.SINE -> "Sine Wave"
            SignalSourceType.CUSTOM_AUDIO -> "Custom Audio"
        }
    },
    sourceShortLabel = { src ->
        when (src) {
            SignalSourceType.PINK_NOISE -> "Pink Noise"
            SignalSourceType.SINE -> "Sine"
            SignalSourceType.CUSTOM_AUDIO -> "Audio"
        }
    },
    generatorPresetLabel = { preset ->
        when (preset) {
            GeneratorPreset.ALL_ACTIVE -> "All Active Channels"
            GeneratorPreset.P2_1 -> "2.1 Stereo + Sub"
            GeneratorPreset.P2_0 -> "2.0 Front"
            GeneratorPreset.P0_1_SUB -> "0.1 Subwoofer Only"
            GeneratorPreset.CUSTOM -> "Custom"
        }
    },
    panningModeLabel = { mode ->
        when (mode) {
            PanningMode.SWEET_SPOT_VBAP -> "Virtual Object"
            PanningMode.ROOM_DISTANCE -> "Speaker Focus"
        }
    },
    panningModeHint = { mode ->
        when (mode) {
            PanningMode.SWEET_SPOT_VBAP -> "Distance & Air Attenuation"
            PanningMode.ROOM_DISTANCE -> "Speaker Proximity"
        }
    },
    orbitSpeedLabel = { spd ->
        when (spd) {
            OrbitSpeed.SLOW -> "Slow"
            OrbitSpeed.MEDIUM -> "Medium"
            OrbitSpeed.FAST -> "Fast"
        }
    },
    layoutPresetShort = { preset ->
        when (preset) {
            SpeakerLayoutPreset.COMPACT -> "Narrow (±22°)"
            SpeakerLayoutPreset.STANDARD -> "Standard (±30°)"
            SpeakerLayoutPreset.WIDE -> "Wide (±40°)"
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
            SpeakerLayoutPreset.COMPACT -> "Best for narrow TV stands or close seating where front speakers sit near the screen."
            SpeakerLayoutPreset.STANDARD -> "Classic Dolby / ITU equilateral triangle speaker layout."
            SpeakerLayoutPreset.WIDE -> "For wide-spaced front speakers or large living room seating areas."
        }
    },

    roomTvFront = "TV FRONT",
    roomSweetSpotDesc = "Sweet Spot",
    roomSoundPuckDesc = "Sound Puck",

    tvTopologyBadge = "TV TOPOLOGY",
    setupTvSubtitle = "Choose speaker setup & pair smartphone / browser",
    connectBrowserIosBtn = "Connect Browser / iOS",
    goToHomeHubBtn = "Go to Home Hub",
    activeSpeakerConfigHeading = "Active Speaker Configuration",
    legendNavigate = "Navigate",
    legendActivate = "Select",
    activeBadgeUpper = "ACTIVE",
    profileSpecUpper = "PROFILE SPECIFICATION",
    setupPrefix = "SETUP",
    setupStreamDesc = { ch ->
        "Opens a $ch-channel AudioTrack (48 kHz PCM). Unused channels are padded with digital silence (0.0f)."
    },
    specChannelsActive = "Active channels",
    specPcmStream = "PCM stream",
    specPcmValue = { ch -> "$ch channels @ 48 kHz" },
    specSubwooferLfe = "Subwoofer LFE",
    specSubwooferLfeValue = "Low-pass up to 80 Hz",
    hdmiMultichannel = { maxCh -> "HDMI Multichannel (${maxCh}ch)" },
    outputStereo = { maxCh -> "Output: Stereo (${maxCh}ch)" },
    headroomProtectionTag = "-12 dBFS Protection",
    mobileSetupTitle = "Speaker Setup",
    mobileSetupSubtitle = "Select Room Topology",
    setupActiveTag = { code -> "$code ACTIVE" },
    mobileSetupSelectPrompt = "Select your physical speaker system",
    mobileSetupConfirmBtn = { code -> "Confirm $code & Open Hub" },

    hubSetupChipLabel = "Setup:",
    hubAudioBtn = { count -> "Audio ($count)" },
    hubBrowserIosBtn = "Browser / iOS",
    tvHubSectionTitle = "Acoustic Calibration Suite",
    tvHubSectionSubtitle = "Select a calibration mode using your remote (Left/Right + OK).",
    tvHubChannelsBadge = { count, code -> "$count Channels ($code)" },
    tvHostActiveLabel = { ip -> "TV Host active: $ip" },
    wlanCheckPrompt = "Check Wi-Fi",
    wlanNotDetected = "Wi-Fi not detected",
    remoteAndroidAndBrowser = "Connected: Mobile App & Browser",
    remoteAndroidOnly = { count -> "Connected: Mobile App" + if (count > 1) " ($count)" else "" },
    remoteBrowserOnly = { count -> "Connected: Browser" + if (count > 1) " ($count)" else "" },
    remoteNone = "No remote connected",
    headroomStatusBar = { db -> "Headroom: $db dBFS • Soft-Limiter active" },
    mode1Badge = "MODE 01",
    mode1Title = "Single Speaker Test",
    mode1TvDesc = { chs -> "Split-view diagnostics. Isolate individual channels ($chs) to verify wiring and trim levels." },
    mode1MobileDesc = { chs -> "Interactive 2D floorplan. Isolate channels ($chs) to verify wiring and levels." },
    mode1Footer = "Pink Noise • Sine • Audio",
    mode2Badge = "MODE 02",
    mode2Title = "Frequency & Preset Generator",
    mode2TvDesc = "Continuous sine wave generator with D-Pad stepper, pink noise, and subwoofer room-mode presets.",
    mode2MobileDesc = "Tactile haptic jog wheel with sine waves, pink noise, and subwoofer crossover presets.",
    mode2Footer = "20 Hz – 20 kHz Spectrum",
    mode3Badge = "MODE 03",
    mode3Title = "2D Spatial Sound Puck",
    mode3TvDesc = "Real-time spatial panner. Move the sound puck smoothly around speakers with D-Pad or Auto-Orbit.",
    mode3MobileDesc = "Live room panner. Drag a virtual sound object or focus individual speakers via touch & Auto-Orbit.",
    mode3Footer = "Virtual Object & Speaker Focus",
    mobileConnConnected = { host -> "Connected: $host" },
    mobileConnConnecting = "Connecting to TV...",
    mobileConnLocal = "Local Mode (2.0)",
    mobileRoleRemote = "TV Remote Controller",
    mobileRoleLocal = "Soundstage Calibrator (Stereo)",
    mobileCard1FooterLeft = { count -> "$count Channels" },
    mobileCard2FooterLeft = "Frequency Spectrum",
    mobileCard3FooterLeft = "Surround Panning",
    mobileCard3FooterRight = "360° Real-time",
    mobileFooterTv = { ip -> "TV: $ip" },
    mobileFooterLocal = "Local downmix active",
    mobileFooterHeadroom = { db -> "Headroom $db dBFS" },

    singleTitle = "Single Speaker Test",
    singleTvSubtitle = { setupName -> "$setupName • Split-View Channel Diagnostics" },
    singleMobileSubtitle = { code -> "$code DIAGNOSTICS" },
    legendChannel = "Channel",
    legendStartStop = "Start / Stop",
    legendSignalChoice = "Signal",
    singleActiveBadge = { s -> "$s ACTIVE" },
    pinkNoiseFreqRange = "Pink Noise (20Hz–20kHz)",
    sineToneLabel = { hz -> "Sine Wave $hz Hz" },
    noAudioSelected = "No audio file selected",
    noAudioShort = "No audio file",
    configureSignalDesc = { name -> "Signal source for $name" },
    syncRoomPlanUpper = "SYNCHRONIZED FLOORPLAN",
    signalActiveUpper = "SIGNAL ACTIVE",
    sourcePrefix = { lbl -> "Source: $lbl" },
    channelDialogTitle = { name, s -> "$name ($s) – Signal Source" },
    channelSheetSubtitle = "Select signal source for this speaker",
    sineFreqLabel = { hz -> "Sine frequency: $hz Hz" },
    sineDesc = { hz -> "Discrete pure tone ($hz Hz)" },
    pinkNoiseDesc = "Equal energy per octave (Standard)",
    customAudioDesc = "Custom reference track (looped)",
    frequencyLabel = "Frequency",
    noCustomAudioTvHint = "No custom audio tracks uploaded yet. Upload tracks via the mobile app or browser.",
    noCustomAudioMobileHint = "No audio tracks in your library yet. Open Audio Library from the Hub to upload.",
    applyToAllChannels = "Apply to all speakers",
    applySignalBtn = "Apply Signal",

    generatorTitle = "Frequency & Preset Generator",
    generatorMobileSubtitle = "DSP SYNTHESIZER",
    activeChannelsPrefix = { chs -> "Active channels: $chs" },
    noneText = "None",
    legendFreq = "Frequency",
    upStepHint = "UP (+1 / +5 / +25 Hz)",
    downStepHint = { playing -> "DOWN (-1 / -5 / -25 Hz) • OK: ${if (playing) "Pause" else "Start"}" },
    pauseAction = "Pause",
    startAction = "Start",
    pinkNoiseStartTv = "Start Pink Noise [OK]",
    pinkNoisePauseTv = "Pause Pink Noise [OK]",
    sineStartTv = { hz -> "Start $hz Hz Tone [OK]" },
    sinePauseTv = { hz -> "Pause $hz Hz Tone [OK]" },
    pinkNoiseStartMobile = "Start Pink Noise",
    pinkNoisePauseMobile = "Pause Pink Noise",
    sineStartMobile = { hz -> "Start $hz Hz Tone" },
    sinePauseMobile = { hz -> "Stop $hz Hz Tone" },
    quickSelectUpper = "QUICK PRESETS",
    referenceUpper = "REFERENCE",
    quickPresets = listOf(
        QuickFreqPresetText(40, "40 Hz", "Sub room mode"),
        QuickFreqPresetText(60, "60 Hz", "Subwoofer base"),
        QuickFreqPresetText(80, "80 Hz", "THX crossover"),
        QuickFreqPresetText(400, "400 Hz", "Standard midrange"),
        QuickFreqPresetText(1000, "1,000 Hz", "1 kHz level ref"),
        QuickFreqPresetText(10000, "10,000 Hz", "Tweeter check")
    ),
    digitalLevelLabel = "Digital level:",
    signalUpper = "SIGNAL",
    freqUpper = "FREQUENCY",
    tapToEnter = "Tap to type",
    freqDialogTitle = "Enter frequency directly",
    freqDialogRangeLabel = "Frequency (20 – 20,000 Hz)",

    puckTitle = "2D Sound Puck",
    puckMobileSubtitle = "LIVE SPATIAL PANNER",
    puckTvSubtitle = { mode, az, hint -> "$mode • Azimuth ${az}° • $hint" },
    legendMovePuck = "Move Puck",
    legendSideMenuActive = "Side menu active",
    legendBackForHub = "Back for Hub",
    puckSourcePrefix = { lbl -> "Source: $lbl" },
    virtualDistance = { m -> "Virtual distance: $m m" },
    roomControlUpper = "SPATIAL CONTROLS",
    puckBtnLabel = "Puck",
    orbitStateLabel = { on -> "Orbit: ${if (on) "On" else "Off"}" },
    stateOn = "On",
    stateOff = "Off",
    orbitWithSpeed = { s -> "Orbit ($s)" },
    autoOrbitBtn = "Auto-Orbit",
    speedPrefix = { s -> "Speed: $s" },
    noAudioUploaded = "No audio tracks uploaded",
    trackCycleLabel = { idx, total, name -> "Track ($idx/$total): $name" },
    puckDecodingMobile = "Decoding audio… (Pause)",
    puckDecodingTv = "Loading…",
    puckPauseMobile = "Pause Spatial Audio",
    puckStartMobile = "Start Spatial Audio",
    noAudioOnTvHint = "No audio tracks on TV yet – upload from the Hub.",

    qrTitle = "Pair Browser / iOS",
    qrBody = "This QR code is only needed for browser control (e.g. iPhone / laptop).\nThe SurroundLab Android app discovers and connects automatically on the same Wi-Fi.",
    tvSettingsTitle = "TV Host Settings",
    tvSettingsSubtitle = "Digital Headroom & Tweeter Protection + HDMI Diagnostics",
    masterLevelTitleTv = { def -> "Master Output Level (Default: $def dBFS)" },
    swap71Title = "Swap 7.1 Surround & Back (HDMI-HAL)",
    swap71DescTv = "Enable if 7.1 SL/SR and SBL/SBR channels are swapped (e.g. Chromecast / Amlogic).",
    swap71DescMobile = "HDMI-HAL workaround (e.g. Chromecast / Amlogic) if SL/SR and SBL/SBR are swapped.",
    layoutAnglesTitle = "Placement Angles (Virtual Object)",
    tvUiScaleTitle = "TV Interface Scale (DPI Zoom)",
    scaleSmallerBtn = "−5 % (Smaller)",
    scaleDefaultBtn = "Default (75 %)",
    scaleLargerBtn = "+5 % (Larger)",
    audioOutputDiag = { name, ch -> "Audio Output: $name (max $ch channels)" },
    hdmiDiagOk = "Multichannel PCM detected. Discrete 5.1 / 7.1 HDMI/eARC routing active.",
    hdmiDiagStereoWarn = "Notice: Only stereo detected right now. Set TV/Chromecast audio format to Auto / PCM and connect directly to your AVR if possible.",
    setupChangeBtn = { code -> "Change Setup ($code)" },
    setupChangeFullBtn = { code -> "Change Speaker Setup ($code)" },
    checkNetworkHdmiBtn = "Re-check Network / HDMI",
    guideBtnShort = "Quick Guide",
    guideShowBtn = "Show Quick Guide",
    tvAudioLibTitle = { count -> "Audio Library ($count)" },
    tvAudioLibSubtitle = "Tracks uploaded from smartphone or browser via HTTP",
    tvAudioLibEmpty = "No custom audio files yet.\nUpload tracks via the SurroundLab smartphone app or web browser.",

    mobileLibTitle = "Audio Library",
    mobileLibSubtitle = "REFERENCE AUDIO TRACKS",
    libCountBadge = { count, mb -> "$count tracks • $mb MB" },
    syncInProgressTitle = "Syncing with TV…",
    syncAllDoneTitle = "All tracks synced on TV",
    syncPendingTitle = { count -> "$count track(s) not on TV yet" },
    syncLocalTitle = "Local Smartphone Library",
    syncActiveSub = { count -> "$count transfer(s) running over Wi-Fi" },
    syncAllDoneSub = "All tracks ready on TV & phone",
    syncPendingSub = "Tap “Sync now” to send missing files to the TV",
    syncOfflineSub = "New files will auto-sync the next time you connect to the TV",
    syncNowBtn = "Sync now",
    libEmptyTitle = "No audio files yet",
    libEmptyBody = "Pick .wav, .mp3, .flac or .ogg via Android's secure file picker (max 30 MB).",
    statusSyncedOnTv = "Synced on TV",
    statusSavedLocally = "Saved locally",
    statusUploading = "Uploading to TV…",
    statusOnlyOnPhone = "Phone only (not on TV)",
    statusWaitingSync = "Waiting for TV sync",
    statusUploadFailed = "TV upload failed",
    statusDeletingOnTv = "Deleting on TV…",
    sendToTvDesc = "Send to TV",
    uploadAudioBtn = "+ Upload Audio File",
    mobileConnTitle = "TV Connection & Level",
    mobileConnSubtitle = "mDNS DISCOVERY & HEADROOM",
    refreshSearchDesc = "Restart search",
    currentModeTitle = "Current Mode",
    connConnectedDetail = { ip, port -> "Connected to TV Host ($ip:$port)" },
    connConnectingDetail = "Connecting to TV host...",
    connLocalDetail = "Local Mode (2.0 stereo downmix on smartphone)",
    disconnectTvBtn = "Disconnect from TV (Local Mode)",
    discoveredTvsTitle = "Discovered TVs on Wi-Fi (mDNS)",
    noTvsFoundText = "No SurroundLab TV hosts found. Make sure the app is open on your TV.",
    connectBtn = "Connect",
    manualConnTitle = "Manual TV Connection",
    tvIpLabel = "TV IP Address",
    portLabel = "Port",
    sessionTokenLabel = "6-character Session Token (from TV)",
    manualConnectBtn = "Connect Manually",
    masterLevelTitleMobile = "Master Level (Hearing Protection)",

    onboardingSlides = listOf(
        OnboardingSlide(
            badge = "STEP 1 / 3 • OVERVIEW",
            title = "Welcome to SurroundLab",
            subtitle = "Your multichannel calibration studio for 2.0 to 7.1 home theaters over HDMI.",
            icon = Icons.Rounded.SurroundSound,
            bullets = listOf(
                "Single Speaker Test" to "Isolate every channel to verify real speaker wiring using Pink Noise, Sine Wave, or custom tracks.",
                "Frequency & Preset Generator" to "Sweep 20 Hz – 20 kHz, locate room resonances, and verify your subwoofer crossover.",
                "2D Spatial Sound Puck" to "Move a virtual sound object or speaker focus smoothly around your room in real time."
            )
        ),
        OnboardingSlide(
            badge = "STEP 2 / 3 • CONTROL",
            title = "TV, Android App, or Browser",
            subtitle = "Control SurroundLab however works best in your living room.",
            icon = Icons.Rounded.Devices,
            bullets = listOf(
                "Directly on your TV" to "Every mode, setting, and audio test is fully navigable using your TV remote (D-Pad).",
                "Android App (Automatic)" to "Install SurroundLab on your phone too — both devices auto-discover on the same Wi-Fi and sync setups & audio files (no QR code needed).",
                "Browser / iOS (QR code top-right)" to "Select “Browser / iOS” at the top-right on your TV to show a QR code and open the controls in any phone or laptop browser."
            )
        ),
        OnboardingSlide(
            badge = "STEP 3 / 3 • PRO TIPS",
            title = "Helpful Home Theater Tips",
            subtitle = "Three quick settings worth knowing before you start:",
            icon = Icons.Rounded.Tune,
            bullets = listOf(
                "7.1 Surround & Back swapped?" to "Some TV sticks (such as Chromecast) swap SL/SR and SBL/SBR in 7.1 hardware output. Toggle “Swap 7.1 Surround & Back” in Settings (gear icon).",
                "Speaker Placement Angles" to "In Settings you can match your room geometry between Narrow (±22°), Standard (±30°), and Wide (±40°).",
                "Speaker Protection & AVR Mode" to "Output starts safely at -12 dBFS with a soft-limiter. Set your AVR to “Multi Ch In” or “Straight” (no upmixer) for accurate channel separation."
            )
        )
    ),
    onboardingSkipToNext = "Back",
    onboardingNext = "Next →",
    onboardingFinish = "Get Started",

    tvInstall = TvInstallStrings(
        menuBtn = "Install App on Your TV (Guide)",
        badge = "TV SETUP • VIA “DOWNLOADER”",
        title = "Install SurroundLab on Your TV",
        subtitle = "Get the app onto your Chromecast / Android TV in 4 easy steps — no PC required:",
        steps = listOf(
            "1. Install “Downloader” on your TV" to "Open the Google Play Store on your TV, search for “Downloader by AFTVnews” (orange icon), and install the free app.",
            "2. Enter code 7281113 in Downloader" to "Open “Downloader” on your TV, simply type the short code 7281113 (or the GitHub URL below), and press “Go”.",
            "3. Allow installation once" to "If your TV asks for permission to install apps from “Downloader”, open Settings → Allow, then tap Install.",
            "4. Open & connect automatically" to "Launch SurroundLab on your TV. As soon as your TV and phone share the same Wi-Fi, they discover each other automatically."
        ),
        copyUrlBtn = "Copy GitHub Link",
        urlCopiedToast = "Download link copied to clipboard",
        closeBtn = "Close"
    ),
    donatePaypalBtn = "Support Project (PayPal)",

    update = UpdateStrings(
        sectionTitle = "App Updates (GitHub)",
        currentVersion = { v -> "Installed version: v$v" },
        checkBtn = "Check for Updates",
        checkingText = "Checking GitHub for a newer release…",
        upToDateText = { v -> "You're up to date (v$v)." },
        availableText = { v -> "New version v$v available!" },
        downloadInstallBtn = "Download & Install",
        downloadingText = { pct -> "Downloading APK… $pct%" },
        installNowBtn = "Install Now",
        permissionHint = "Please allow app installs from this source once, then tap “Install Now” again.",
        errorText = { err -> "Update check failed ($err)" }
    )
)
