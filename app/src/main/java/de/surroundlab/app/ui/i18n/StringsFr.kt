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

val StringsFr = AppStrings(
    langCode = "fr",
    back = "Retour",
    close = "Fermer",
    ok = "OK",
    apply = "Appliquer",
    cancel = "Annuler",
    activeTag = "ACTIF",
    ready = "Prêt",
    testBtn = "Tester",
    stopBtn = "Arrêter",
    startBtn = "Démarrer",
    pauseBtn = "Pause",
    deleteBtn = "Supprimer",
    settingsLabel = "Paramètres",
    homeHubBtn = "Accueil Hub",

    languageSectionTitle = "Langue",
    languageAutoLabel = "Auto",
    languageAutoHint = { resolvedName -> "Automatique ($resolvedName)" },

    setupSubtitle = { setup ->
        when (setup) {
            SpeakerSetup.S2_0 -> "Avant gauche & droite"
            SpeakerSetup.S2_1 -> "Stéréo + Caisson"
            SpeakerSetup.S3_1 -> "Avant + Centrale + Caisson"
            SpeakerSetup.S5_0 -> "5 enceintes satellites"
            SpeakerSetup.S5_1 -> "6 canaux discrets"
            SpeakerSetup.S7_1 -> "8 canaux discrets"
        }
    },
    channelName = { ch -> ch.displayName },
    sourceLabel = { src ->
        when (src) {
            SignalSourceType.PINK_NOISE -> "Bruit rose"
            SignalSourceType.SINE -> "Onde sinusoïdale"
            SignalSourceType.CUSTOM_AUDIO -> "Audio personnalisé"
        }
    },
    sourceShortLabel = { src ->
        when (src) {
            SignalSourceType.PINK_NOISE -> "Bruit rose"
            SignalSourceType.SINE -> "Sinus"
            SignalSourceType.CUSTOM_AUDIO -> "Audio"
        }
    },
    generatorPresetLabel = { preset ->
        when (preset) {
            GeneratorPreset.ALL_ACTIVE -> "Tous les canaux actifs"
            GeneratorPreset.ALL_WITHOUT_SUB -> "Tous sauf caisson"
            GeneratorPreset.P2_1 -> "2.1 Stéréo + Caisson"
            GeneratorPreset.P2_0 -> "2.0 Avant"
            GeneratorPreset.P0_1_SUB -> "0.1 Caisson seul"
            GeneratorPreset.CUSTOM -> "Personnalisé"
        }
    },
    panningModeLabel = { mode ->
        when (mode) {
            PanningMode.SWEET_SPOT_VBAP -> "Objet virtuel"
            PanningMode.ROOM_DISTANCE -> "Focus enceinte"
        }
    },
    panningModeHint = { mode ->
        when (mode) {
            PanningMode.SWEET_SPOT_VBAP -> "Distance & atténuation d'air"
            PanningMode.ROOM_DISTANCE -> "Proximité des enceintes"
        }
    },
    orbitSpeedLabel = { spd ->
        when (spd) {
            OrbitSpeed.SLOW -> "Lent"
            OrbitSpeed.MEDIUM -> "Moyen"
            OrbitSpeed.FAST -> "Rapide"
        }
    },
    layoutPresetShort = { preset ->
        when (preset) {
            SpeakerLayoutPreset.COMPACT -> "Resserré (±22°)"
            SpeakerLayoutPreset.STANDARD -> "Standard (±30°)"
            SpeakerLayoutPreset.WIDE -> "Large (±40°)"
        }
    },
    layoutPresetSummary = { preset ->
        when (preset) {
            SpeakerLayoutPreset.COMPACT -> "Avant ±22° • Surround ±100° • Arrière ±140°"
            SpeakerLayoutPreset.STANDARD -> "Avant ±30° • Surround ±110° • Arrière ±150°"
            SpeakerLayoutPreset.WIDE -> "Avant ±40° • Surround ±120° • Arrière ±155°"
        }
    },
    layoutPresetDetail = { preset ->
        when (preset) {
            SpeakerLayoutPreset.COMPACT -> "Idéal pour les meubles TV étroits ou une écoute rapprochée."
            SpeakerLayoutPreset.STANDARD -> "Disposition classique Dolby / ITU en triangle équilatéral."
            SpeakerLayoutPreset.WIDE -> "Pour les enceintes frontales espacées ou un grand salon."
        }
    },

    roomTvFront = "FAÇADE TV",
    roomSweetSpotDesc = "Point d'écoute",
    roomSoundPuckDesc = "Source 2D",

    tvTopologyBadge = "TOPOLOGIE TV",
    setupTvSubtitle = "Choisir la configuration d'enceintes & associer mobile / navigateur",
    connectBrowserIosBtn = "Connecter Navigateur / iOS",
    goToHomeHubBtn = "Accéder au Hub",
    activeSpeakerConfigHeading = "Configuration d'enceintes active",
    legendNavigate = "Naviguer",
    legendActivate = "Valider",
    activeBadgeUpper = "ACTIF",
    profileSpecUpper = "SPÉCIFICATIONS DU PROFIL",
    setupPrefix = "CONFIG",
    setupStreamDesc = { ch ->
        "Ouvre un AudioTrack à $ch canaux (PCM 48 kHz). Les canaux inutilisés restent en silence numérique (0.0f)."
    },
    specChannelsActive = "Canaux actifs",
    specPcmStream = "Flux PCM",
    specPcmValue = { ch -> "$ch canaux @ 48 kHz" },
    specSubwooferLfe = "Caisson LFE",
    specSubwooferLfeValue = "Passe-bas jusqu'à 80 Hz",
    hdmiMultichannel = { maxCh -> "HDMI multicanal ($maxCh can.)" },
    outputStereo = { maxCh -> "Sortie : Stéréo ($maxCh can.)" },
    headroomProtectionTag = "Protection -12 dBFS",
    mobileSetupTitle = "Configuration d'enceintes",
    mobileSetupSubtitle = "Choisir la topologie de la pièce",
    setupActiveTag = { code -> "$code ACTIF" },
    mobileSetupSelectPrompt = "Sélectionnez votre système d'enceintes",
    mobileSetupConfirmBtn = { code -> "Confirmer $code & ouvrir le Hub" },

    hubSetupChipLabel = "Config :",
    hubAudioBtn = { count -> "Audio ($count)" },
    hubBrowserIosBtn = "Navigateur / iOS",
    tvHubSectionTitle = "Suite de Calibration Acoustique",
    tvHubSectionSubtitle = "Sélectionnez un mode de test avec la télécommande (Gauche/Droite + OK).",
    tvHubChannelsBadge = { count, code -> "$count canaux ($code)" },
    tvHostActiveLabel = { ip -> "Hôte TV actif : $ip" },
    wlanCheckPrompt = "Vérifier le Wi-Fi",
    wlanNotDetected = "Wi-Fi non détecté",
    remoteAndroidAndBrowser = "Connecté : App mobile & Navigateur",
    remoteAndroidOnly = { count -> "Connecté : App mobile" + if (count > 1) " ($count)" else "" },
    remoteBrowserOnly = { count -> "Connecté : Navigateur" + if (count > 1) " ($count)" else "" },
    remoteNone = "Aucun contrôleur connecté",
    headroomStatusBar = { db -> "Réserve (Headroom) : $db dBFS • Limiteur doux actif" },
    mode1Badge = "MODE 01",
    mode1Title = "Test par enceinte",
    mode1TvDesc = { chs -> "Diagnostic en vue partagée. Isolez chaque canal ($chs) pour vérifier le câblage et les niveaux." },
    mode1MobileDesc = { chs -> "Plan 2D interactif. Isolez chaque canal ($chs) pour vérifier câblage et niveaux." },
    mode1Footer = "Bruit rose • Sinus • Audio",
    mode2Badge = "MODE 02",
    mode2Title = "Générateur de fréquences",
    mode2TvDesc = "Générateur sinusoïdal continu avec réglage D-Pad, bruit rose et préréglages de coupure caisson.",
    mode2MobileDesc = "Molette tactile haptique avec ondes sinusoïdales, bruit rose et préréglages de fréquences caisson.",
    mode2Footer = "Spectre 20 Hz – 20 kHz",
    mode3Badge = "MODE 03",
    mode3Title = "Scène sonore 2D",
    mode3TvDesc = "Panner spatial temps réel. Déplacez la source autour de la pièce au D-Pad ou en Auto-Orbite.",
    mode3MobileDesc = "Panner temps réel. Glissez la source ou ciblez chaque enceinte au doigt et en Auto-Orbite.",
    mode3Footer = "Objet virtuel & Focus enceinte",
    mode4Badge = "MODE 04",
    mode4Title = "Modes de pièce & Mesure",
    mode4TvDesc = "Mesure automatique de la réponse en fréquence avec votre smartphone Android comme microphone de mesure.",
    mode4MobileDesc = "Balayage micro automatique : détectez les résonances, comparez les places d'écoute et exportez en PDF A4.",
    mode4Footer = "Balayage micro & Rapport PDF",
    mobileConnConnected = { host -> "Connecté : $host" },
    mobileConnConnecting = "Connexion au téléviseur...",
    mobileConnLocal = "Mode local (2.0) · Non connecté",
    mobileRoleRemote = "Télécommande TV",
    mobileRoleLocal = "Calibrateur sonore (Stéréo)",
    mobileCard1FooterLeft = { count -> "$count canaux" },
    mobileCard2FooterLeft = "Spectre de fréquences",
    mobileCard3FooterLeft = "Spatialisation surround",
    mobileCard3FooterRight = "360° temps réel",
    mobileCard4FooterLeft = "Analyse micro",
    mobileCard4FooterRight = "Courbe & PDF",
    mobileFooterTv = { ip -> "TV : $ip" },
    mobileFooterLocal = "Downmix local actif",
    mobileFooterHeadroom = { db -> "Headroom $db dBFS" },

    singleTitle = "Test par enceinte",
    singleTvSubtitle = { setupName -> "$setupName • Diagnostic par canal en vue partagée" },
    singleMobileSubtitle = { code -> "DIAGNOSTIC $code" },
    legendChannel = "Canal",
    legendStartStop = "Démarrer / Stop",
    legendSignalChoice = "Signal",
    singleActiveBadge = { s -> "$s ACTIF" },
    pinkNoiseFreqRange = "Bruit rose (20Hz–20kHz)",
    sineToneLabel = { hz -> "Sinus $hz Hz" },
    noAudioSelected = "Aucun fichier audio sélectionné",
    noAudioShort = "Aucun audio",
    configureSignalDesc = { name -> "Source de signal pour $name" },
    syncRoomPlanUpper = "PLAN SYNCHRONISÉ",
    signalActiveUpper = "SIGNAL ACTIF",
    sourcePrefix = { lbl -> "Source : $lbl" },
    channelDialogTitle = { name, s -> "$name ($s) – Source de signal" },
    channelSheetSubtitle = "Choisir le signal de test pour cette enceinte",
    sineFreqLabel = { hz -> "Fréquence sinusoïdale : $hz Hz" },
    sineDesc = { hz -> "Tonalité pure discrète ($hz Hz)" },
    pinkNoiseDesc = "Énergie égale par octave (Standard)",
    customAudioDesc = "Piste de référence en boucle",
    frequencyLabel = "Fréquence",
    noCustomAudioTvHint = "Aucune piste importée pour l'instant. Envoyez des fichiers via l'app mobile ou le navigateur.",
    noCustomAudioMobileHint = "Aucune piste audio dans la bibliothèque. Importez-en depuis l'écran d'accueil.",
    applyToAllChannels = "Appliquer à toutes les enceintes",
    applySignalBtn = "Appliquer le signal",

    generatorTitle = "Générateur de fréquences",
    generatorMobileSubtitle = "SYNTHÉTISEUR DSP",
    activeChannelsPrefix = { chs -> "Canaux actifs : $chs" },
    noneText = "Aucun",
    legendFreq = "Fréquence",
    upStepHint = "HAUT (+1 / +5 / +25 Hz)",
    downStepHint = { playing -> "BAS (-1 / -5 / -25 Hz) • OK : ${if (playing) "Pause" else "Lire"}" },
    pauseAction = "Pause",
    startAction = "Démarrer",
    pinkNoiseStartTv = "Démarrer bruit rose [OK]",
    pinkNoisePauseTv = "Mettre en pause le bruit rose [OK]",
    sineStartTv = { hz -> "Démarrer tonalité $hz Hz [OK]" },
    sinePauseTv = { hz -> "Pause tonalité $hz Hz [OK]" },
    pinkNoiseStartMobile = "Démarrer bruit rose",
    pinkNoisePauseMobile = "Pause bruit rose",
    sineStartMobile = { hz -> "Démarrer tonalité $hz Hz" },
    sinePauseMobile = { hz -> "Arrêter tonalité $hz Hz" },
    quickSelectUpper = "PRÉRÉGLAGES RAPIDES",
    referenceUpper = "RÉFÉRENCE",
    quickPresets = listOf(
        QuickFreqPresetText(40, "40 Hz", "Mode de pièce caisson"),
        QuickFreqPresetText(60, "60 Hz", "Assise grave caisson"),
        QuickFreqPresetText(80, "80 Hz", "Coupure THX (Crossover)"),
        QuickFreqPresetText(400, "400 Hz", "Médium de référence"),
        QuickFreqPresetText(1000, "1 000 Hz", "Référence niveau 1 kHz"),
        QuickFreqPresetText(10000, "10 000 Hz", "Test tweeter aigus")
    ),
    digitalLevelLabel = "Niveau numérique :",
    signalUpper = "SIGNAL",
    freqUpper = "FRÉQUENCE",
    tapToEnter = "Toucher pour saisir",
    freqDialogTitle = "Saisir la fréquence directement",
    freqDialogRangeLabel = "Fréquence (20 – 20 000 Hz)",

    puckTitle = "Scène sonore 2D",
    puckMobileSubtitle = "PANNER SPATIAL TEMPS RÉEL",
    puckTvSubtitle = { mode, az, hint -> "$mode • Azimut ${az}° • $hint" },
    legendMovePuck = "Déplacer",
    legendSideMenuActive = "Menu latéral actif",
    legendBackForHub = "Retour pour Hub",
    puckSourcePrefix = { lbl -> "Source : $lbl" },
    virtualDistance = { m -> "Distance virtuelle : $m m" },
    roomControlUpper = "CONTRÔLE SPATIAL",
    puckBtnLabel = "Curseur",
    orbitStateLabel = { on -> "Orbite : ${if (on) "Activée" else "Désactivée"}" },
    stateOn = "Oui",
    stateOff = "Non",
    orbitWithSpeed = { s -> "Orbite ($s)" },
    autoOrbitBtn = "Auto-Orbite",
    speedPrefix = { s -> "Vitesse : $s" },
    noAudioUploaded = "Aucune piste importée",
    trackCycleLabel = { idx, total, name -> "Piste ($idx/$total) : $name" },
    puckDecodingMobile = "Décodage audio… (Pause)",
    puckDecodingTv = "Chargement…",
    puckPauseMobile = "Mettre en pause",
    puckStartMobile = "Démarrer le son spatial",
    noAudioOnTvHint = "Aucune piste sur le téléviseur – importez-en depuis l'accueil.",

    qrTitle = "Associer Navigateur / iOS",
    qrBody = "Ce QR code sert uniquement au contrôle via navigateur (ex. iPhone / ordinateur).\nL'app Android SurroundLab détecte et synchronise le téléviseur automatiquement sur le même Wi-Fi.",
    tvSettingsTitle = "Paramètres de l'hôte TV",
    tvSettingsSubtitle = "Réserve numérique, protection tweeters & diagnostic HDMI",
    masterLevelTitleTv = { def -> "Niveau de sortie général (Défaut : $def dBFS)" },
    lfeLowPassTitle = "Passe-bas LFE (Bruit & Musique)",
    lfeLowPassDesc = "Limite le bruit rose et vos pistes sur le caisson (.1). Les sinus purs et mesures acoustiques passent toujours directement vers le crossover de l'ampli.",
    lfeDirectLabel = "Direct (Off)",
    swap71Title = "Inverser 7.1 Surround & Arrière (HDMI-HAL)",
    swap71DescTv = "À activer si les canaux 7.1 SL/SR et SBL/SBR sont inversés (ex. Chromecast / Amlogic).",
    swap71DescMobile = "Correctif HDMI-HAL (ex. Chromecast / Amlogic) si SL/SR et SBL/SBR sont inversés.",
    layoutAnglesTitle = "Angles de placement (Objet virtuel)",
    tvUiScaleTitle = "Échelle d'interface TV (Zoom DPI)",
    scaleSmallerBtn = "−5 % (Plus petit)",
    scaleDefaultBtn = "Défaut (75 %)",
    scaleLargerBtn = "+5 % (Plus grand)",
    audioOutputDiag = { name, ch -> "Sortie audio : $name (max $ch canaux)" },
    hdmiDiagOk = "PCM multicanal détecté. Routage discret 5.1 / 7.1 HDMI/eARC actif.",
    hdmiDiagStereoWarn = "Attention : sortie stéréo uniquement détectée. Réglez le format audio TV/Chromecast sur Auto / PCM et branchez directement sur l'ampli AV si possible.",
    setupChangeBtn = { code -> "Changer config ($code)" },
    setupChangeFullBtn = { code -> "Changer d'enceintes ($code)" },
    checkNetworkHdmiBtn = "Revérifier réseau / HDMI",
    guideBtnShort = "Guide rapide",
    guideShowBtn = "Afficher le guide",
    tvAudioLibTitle = { count -> "Bibliothèque audio ($count)" },
    tvAudioLibSubtitle = "Pistes envoyées depuis le mobile ou le navigateur via HTTP",
    tvAudioLibEmpty = "Aucun fichier audio importé.\nEnvoyez des pistes depuis l'app Android SurroundLab ou un navigateur.",

    mobileLibTitle = "Bibliothèque audio",
    mobileLibSubtitle = "PISTES DE RÉFÉRENCE",
    libCountBadge = { count, mb -> "$count pistes • $mb Mo" },
    syncInProgressTitle = "Synchronisation avec la TV…",
    syncAllDoneTitle = "Toutes les pistes sont sur la TV",
    syncPendingTitle = { count -> "$count piste(s) manquante(s) sur la TV" },
    syncLocalTitle = "Bibliothèque locale mobile",
    syncActiveSub = { count -> "$count transfert(s) Wi-Fi en cours" },
    syncAllDoneSub = "Toutes les pistes sont prêtes sur TV & mobile",
    syncPendingSub = "Appuyez sur « Synchroniser » pour envoyer les fichiers manquants à la TV",
    syncOfflineSub = "Les nouveaux fichiers seront envoyés automatiquement à la prochaine connexion TV",
    syncNowBtn = "Synchroniser",
    libEmptyTitle = "Aucun fichier audio",
    libEmptyBody = "Sélectionnez un fichier .wav, .mp3, .flac ou .ogg via le sélecteur sécurisé Android (max 30 Mo).",
    statusSyncedOnTv = "Synchronisé sur la TV",
    statusSavedLocally = "Enregistré localement",
    statusUploading = "Envoi vers la TV…",
    statusOnlyOnPhone = "Uniquement sur mobile (pas sur la TV)",
    statusWaitingSync = "En attente de synchro TV",
    statusUploadFailed = "Échec de l'envoi vers la TV",
    statusDeletingOnTv = "Suppression sur la TV…",
    sendToTvDesc = "Envoyer vers la TV",
    uploadAudioBtn = "+ Importer un fichier audio",
    mobileConnTitle = "Connexion TV & Niveau",
    mobileConnSubtitle = "DÉCOUVERTE mDNS & HEADROOM",
    refreshSearchDesc = "Relancer la recherche",
    currentModeTitle = "Mode actuel",
    connConnectedDetail = { ip, port -> "Connecté à l'hôte TV ($ip:$port)" },
    connConnectingDetail = "Connexion à l'hôte TV en cours...",
    connLocalDetail = "Mode local (downmix stéréo 2.0 sur smartphone)",
    disconnectTvBtn = "Déconnecter de la TV (mode local)",
    discoveredTvsTitle = "Téléviseurs détectés en Wi-Fi (mDNS)",
    noTvsFoundText = "Aucun hôte SurroundLab trouvé. Vérifiez que l'application est ouverte sur la TV.",
    connectBtn = "Connecter",
    manualConnTitle = "Connexion TV manuelle",
    tvIpLabel = "Adresse IP de la TV",
    portLabel = "Port",
    sessionTokenLabel = "Code session à 6 caractères (affiché sur TV)",
    manualConnectBtn = "Connexion manuelle",
    masterLevelTitleMobile = "Niveau général (Protection auditive)",

    onboardingSlides = listOf(
        OnboardingSlide(
            badge = "ÉTAPE 1 / 3 • APERÇU",
            title = "Bienvenue dans SurroundLab",
            subtitle = "Votre studio de calibration multicanal de 2.0 à 7.1 sur HDMI.",
            icon = Icons.Rounded.SurroundSound,
            bullets = listOf(
                "Test par enceinte" to "Isolez chaque canal pour vérifier le câblage réel avec bruit rose, sinus ou pistes audio.",
                "Générateur de fréquences" to "Explorez de 20 Hz à 20 kHz, repérez les résonances et réglez le raccord du caisson.",
                "Scène sonore 2D" to "Déplacez une source virtuelle ou ciblez chaque enceinte en temps réel autour du salon.",
                "Modes de pièce & Mesure" to "Mesurez la réponse en fréquence avec le micro de votre mobile Android sur plusieurs places, comparez vos placements et exportez en PDF A4."
            )
        ),
        OnboardingSlide(
            badge = "ÉTAPE 2 / 3 • CONTRÔLE",
            title = "TV, App Android ou Navigateur",
            subtitle = "Pilotez SurroundLab comme vous préférez depuis votre canapé.",
            icon = Icons.Rounded.Devices,
            bullets = listOf(
                "Directement sur la TV" to "Tous les modes, réglages et tests sont accessibles avec la télécommande (D-Pad).",
                "App Android (Automatique)" to "Installez SurroundLab sur votre smartphone : les appareils se trouvent en Wi-Fi et se synchronisent tout seuls.",
                "Navigateur / iOS (QR code en haut à droite)" to "Sélectionnez « Navigateur / iOS » en haut à droite sur la TV pour scanner le QR code depuis n'importe quel appareil."
            )
        ),
        OnboardingSlide(
            badge = "ÉTAPE 3 / 3 • CONSEILS PRO",
            title = "Réglages utiles Home Cinéma",
            subtitle = "Trois points rapides à connaître avant de commencer :",
            icon = Icons.Rounded.Tune,
            bullets = listOf(
                "Surround & Arrière 7.1 inversés ?" to "Certains boîtiers TV (ex. Chromecast) inversent SL/SR et SBL/SBR en 7.1. Activez l'option d'inversion dans Paramètres.",
                "Angles d'enceintes" to "Adaptez la géométrie de votre pièce dans les paramètres : Resserré (±22°), Standard (±30°) ou Large (±40°).",
                "Protection & mode Ampli AV" to "Le niveau démarre à -12 dBFS avec limiteur doux. Réglez votre ampli sur « Multi Ch In » ou « Direct / Straight »."
            )
        )
    ),
    onboardingSkipToNext = "Retour",
    onboardingNext = "Suivant →",
    onboardingFinish = "C'est parti",

    tvInstall = TvInstallStrings(
        menuBtn = "Installer l'app sur la TV (Guide)",
        badge = "INSTALLATION TV • VIA « DOWNLOADER »",
        title = "Installer SurroundLab sur la TV",
        subtitle = "Installez l'application sur votre Chromecast / Android TV en 4 étapes simples, sans PC :",
        steps = listOf(
            "1. Installer « Downloader » sur la TV" to "Ouvrez le Google Play Store sur votre TV, cherchez « Downloader by AFTVnews » (icône orange) et installez cette appli gratuite.",
            "2. Saisir le code 7281113 dans Downloader" to "Ouvrez « Downloader » sur la TV, saisissez simplement le code 7281113 (ou l'adresse GitHub ci-dessous) puis validez sur « Go ».",
            "3. Autoriser l'installation une fois" to "Si la TV demande l'autorisation pour « Downloader », ouvrez Paramètres → Autoriser, puis cliquez sur Installer.",
            "4. Ouvrir & connexion automatique" to "Lancez SurroundLab sur la TV. Dès que la TV et le mobile sont sur le même Wi-Fi, ils se détectent automatiquement."
        ),
        copyUrlBtn = "Copier le lien GitHub",
        urlCopiedToast = "Lien de téléchargement copié",
        closeBtn = "Fermer"
    ),

    update = UpdateStrings(
        sectionTitle = "Mises à jour (GitHub)",
        currentVersion = { v -> "Version installée : v$v" },
        checkBtn = "Rechercher des mises à jour",
        checkingText = "Vérification sur GitHub en cours…",
        upToDateText = { v -> "Tout est à jour (v$v)." },
        availableText = { v -> "Nouvelle version v$v disponible !" },
        downloadInstallBtn = "Télécharger & Installer",
        downloadingText = { pct -> "Téléchargement APK… $pct%" },
        installNowBtn = "Installer maintenant",
        permissionHint = "Autorisez l'installation depuis cette source une fois, puis appuyez de nouveau sur « Installer maintenant ».",
        errorText = { err -> "Échec de la vérification ($err)" }
    ),

    room = RoomMeasurementStrings(
        screenTitle = "Modes de pièce & Mesure",
        screenSubtitle = "BALAYAGE FRÉQUENTIEL AUTOMATIQUE",
        guideBtn = "Guide",
        tabMeasure = "Mesurer",
        tabArchive = "Enregistrés",
        tabCompare = "Comparer",
        tvInfoBadge = "MICROPHONE SMARTPHONE REQUIS",
        tvInfoTitle = "Lancer la mesure depuis votre smartphone Android",
        tvInfoBody = "La mesure automatique utilise le micro de votre téléphone Android à votre position d'écoute pendant que la TV diffuse les tonalités sur votre installation.",
        tvInfoSteps = listOf(
            "1. Installez et ouvrez SurroundLab sur votre smartphone Android (connexion auto sur le même Wi-Fi).",
            "2. Touchez « Modes de pièce & Mesure » sur le mobile et autorisez le micro.",
            "3. Tenez le téléphone immobile à hauteur d'oreille et lancez le balayage : graphiques et PDF restent sur le mobile."
        ),
        tvActiveSweepStatus = { hz -> "Mesure en cours depuis le mobile • Tonalité : $hz Hz" },
        tvShowQrBtn = "Infos connexion / QR",
        guideTitle = "Comment fonctionne la mesure",
        guideSubtitle = "Conseils clés pour des courbes exploitables :",
        guideItems = listOf(
            "À quoi sert cet outil ?" to "L'app diffuse automatiquement une suite de sinus et mesure le niveau à votre place d'écoute pour repérer les résonances (pics) et creux, et comparer vos placements.",
            "App Android requise" to "Fonctionne reliée à la TV (5.1/7.1) ou en mode autonome (câble/Bluetooth sur chaîne stéréo). Les navigateurs bloquent le micro sur HTTP local.",
            "Calme & immobilité" to "Coupez ventilateurs et sources de bruit, restez immobile pendant le balayage et tenez le téléphone à hauteur d'oreille micro dégagé.",
            "Volume de l'ampli" to "Réglez un volume d'écoute modéré et utilisez le test de niveau préalable (« Optimal ✓ »).",
            "Limites réalistes" to "Les micros de smartphone ne sont pas calibrés (surtout sous 40 Hz). Les dB absolus sont relatifs, mais les pics, creux et comparaisons restent très fiables."
        ),
        guideGotIt = "Compris, c'est parti",
        micPermissionTitle = "Autorisation microphone requise",
        micPermissionDesc = "Pour mesurer l'acoustique à votre place, SurroundLab doit accéder au micro du téléphone (actif uniquement pendant les tests et mesures).",
        micPermissionBtn = "Autoriser le micro",
        micErrorMsg = "Impossible d'ouvrir le microphone. Vérifiez l'autorisation.",
        levelCheckTitle = "Test de niveau préalable",
        levelCheckDesc = "Vérifiez que le volume est bien supérieur au bruit ambiant. Astuce : ce test rapide réveille aussi les caissons en veille automatique afin de ne rater aucune mesure.",
        levelCheckStartBtn = "Tester le niveau",
        levelCheckStopBtn = "Arrêter le test",
        levelAmbientLabel = { db -> "Bruit ambiant : $db dBFS" },
        levelLiveLabel = { db, snr -> "Signal : $db dBFS (+$snr dB SNR)" },
        verdictIdle = "Prêt — touchez « Tester le niveau » ou lancez directement le balayage.",
        verdictCalibrating = "Mesure du bruit ambiant… restez silencieux.",
        verdictTooQuiet = "Signal trop faible ! Montez légèrement le volume.",
        verdictOptimal = "Niveau optimal ✓ — bien au-dessus du bruit de fond.",
        verdictClipping = "Micro saturé ! Baissez légèrement le volume.",
        speakersSectionTitle = "Enceintes mesurées",
        speakerPresetHint = { preset ->
            when (preset) {
                GeneratorPreset.P2_0 -> "Utile dans les graves (20–200 Hz) pour vérifier la somme G+D ou les annulations de phase. Au-dessus de ~300 Hz, deux enceintes créent un filtrage en peigne au micro : pour le spectre complet, mesurez une seule enceinte (FL ou FR)."
                GeneratorPreset.P0_1_SUB -> "Mesure uniquement le caisson (.1 LFE) — idéal pour repérer les résonances graves (20–120 Hz) et comparer ses emplacements."
                GeneratorPreset.P2_1 -> "Envoie aux enceintes avant + LFE ensemble (utile sans bass-management ou en Large ; renforce les graves sous la coupure en Small)."
                GeneratorPreset.ALL_WITHOUT_SUB -> "Teste les satellites sans LFE séparé (le caisson joue si le bass-management est actif ; filtrage en peigne possible dans les médiums/aigus)."
                GeneratorPreset.ALL_ACTIVE -> "Active toutes les enceintes simultanément (niveau global élevé, mais des annulations de phase apparaissent au-dessus de ~200 Hz)."
                GeneratorPreset.CUSTOM -> "Standard acoustique recommandé (une seule enceinte, ex. FL ou FR) : évite le filtrage en peigne. Révèle le raccord réel avec le caisson en « Small » ou la courbe complète et les modes en « Large »."
            }
        },
        presetSectionTitle = "Plage & pas de fréquence",
        presetRoomModes = "Modes de pièce (20–200 Hz)",
        presetSubwoofer = "Caisson (20–120 Hz)",
        presetFullRange = "Spectre complet (20–20k)",
        presetCustom = "Personnalisé",
        startHzLabel = "Début (Hz)",
        endHzLabel = "Fin (Hz)",
        stepModeLinear = "Linéaire (Hz)",
        stepModeOctave = "Logarithmique (Octave)",
        stepHzLabel = "Pas (Hz)",
        stepOctaveLabel = { d -> "1/$d octave" },
        speedLabel = "Vitesse",
        speedFast = "Rapide",
        speedStandard = "Standard",
        speedPrecise = "Précise",
        estimatedTimeLabel = { pts, sec -> "$pts points • ~${sec}s par position" },
        positionNameLabel = "Nom de cette position",
        defaultPositionName = { idx -> "Position $idx" },
        startSweepBtn = "Lancer la mesure",
        localPlaybackHint = "Lecture audio via ce téléphone (aucun TV connecté)",
        micSelectorLabel = "Microphone",
        micPhoneDefault = "Micro du téléphone",
        addPositionSweepBtn = "Mesurer la position",
        countdownLabel = { sec -> "Restez calme et immobile… début dans $sec" },
        silenceCalibLabel = "Mesure du bruit de fond…",
        sweepingLabel = { cur, tot, hz -> "Étape $cur / $tot • $hz Hz" },
        cancelSweepBtn = "Annuler la mesure",
        activeSessionTitle = "Session en cours",
        defaultSessionName = { d -> "Mesure $d" },
        scaleLinearLabel = "Linéaire (Plate)",
        scaleDbaLabel = "dBA (Audition)",
        meanCurveLabel = "Moyenne",
        smoothLabel = "Lisser",
        addAnotherPositionBtn = "+ Autre position d'écoute",
        saveSessionBtn = "Enregistrer",
        savedToast = "Mesure enregistrée",
        renameSessionTitle = "Nommer & annoter",
        sessionNameHint = "ex. Caisson angle gauche",
        sessionNoteHint = "Note (ex. Coupure 80 Hz, Phase 0°)",
        renamePositionTitle = "Renommer la position",
        newPositionHint = "ex. Canapé centre",
        archiveEmptyTitle = "Aucune mesure enregistrée",
        archiveEmptyBody = "Lancez votre premier balayage dans « Mesurer » puis enregistrez-le pour comparer ou exporter en PDF A4.",
        positionsCountLabel = { c -> "$c position(s)" },
        loadInEditorBtn = "Ouvrir / Compléter",
        exportPdfSingleBtn = "PDF",
        exportPdfSelectedBtn = { c -> "Exporter PDF ($c)" },
        selectForExportHint = "Cochez plusieurs mesures pour les regrouper dans un PDF A4.",
        compareHint = "Sélectionnez au moins 2 mesures enregistrées pour superposer leurs courbes :",
        compareMinTwoHint = "Enregistrez au moins 2 mesures pour comparer.",
        pdfReportTitle = "SurroundLab — Rapport acoustique",
        pdfGeneratedPrefix = "Généré le",
        pdfPageLabel = { p, t -> "Page $p / $t" },
        pdfChannelsPrefix = "Canaux",
        pdfShareTitle = "Partager le rapport PDF",
        pdfErrorToast = "Échec de l'export PDF.",
        pdfLayoutDialogTitle = "Format de page PDF",
        pdfLayoutDialogBody = "Le mode paysage offre plus de lisibilité sur les balayages larges (ex. 20 Hz – 20 kHz).",
        pdfPortraitDefaultBtn = "Portrait (Défaut)",
        pdfLandscapeWideBtn = "Paysage (Large)",
        pdfExportConfigTitle = "Configurer l’export PDF",
        pdfExportActionBtn = "Exporter en PDF"
    )
)
