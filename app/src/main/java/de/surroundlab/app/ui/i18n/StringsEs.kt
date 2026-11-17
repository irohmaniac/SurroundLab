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

val StringsEs = AppStrings(
    langCode = "es",
    back = "Atrás",
    close = "Cerrar",
    ok = "OK",
    apply = "Aplicar",
    cancel = "Cancelar",
    activeTag = "ACTIVO",
    ready = "Listo",
    testBtn = "Probar",
    stopBtn = "Detener",
    startBtn = "Iniciar",
    pauseBtn = "Pausa",
    deleteBtn = "Eliminar",
    settingsLabel = "Ajustes",
    homeHubBtn = "Inicio",

    languageSectionTitle = "Idioma / Language",
    languageAutoLabel = "Auto",
    languageAutoHint = { resolvedName -> "Automático ($resolvedName)" },

    setupSubtitle = { setup ->
        when (setup) {
            SpeakerSetup.S2_0 -> "Frontal Izq. y Der."
            SpeakerSetup.S2_1 -> "Estéreo + Subwoofer"
            SpeakerSetup.S3_1 -> "Frontal + Central + Sub"
            SpeakerSetup.S5_0 -> "5 canales satélite"
            SpeakerSetup.S5_1 -> "6 canales discretos"
            SpeakerSetup.S7_1 -> "8 canales discretos"
        }
    },
    channelName = { ch -> ch.displayName },
    sourceLabel = { src ->
        when (src) {
            SignalSourceType.PINK_NOISE -> "Ruido rosa"
            SignalSourceType.SINE -> "Onda senoidal"
            SignalSourceType.CUSTOM_AUDIO -> "Audio personalizado"
        }
    },
    sourceShortLabel = { src ->
        when (src) {
            SignalSourceType.PINK_NOISE -> "Ruido rosa"
            SignalSourceType.SINE -> "Senoidal"
            SignalSourceType.CUSTOM_AUDIO -> "Audio"
        }
    },
    generatorPresetLabel = { preset ->
        when (preset) {
            GeneratorPreset.ALL_ACTIVE -> "Todos los canales activos"
            GeneratorPreset.P2_1 -> "2.1 Estéreo + Sub"
            GeneratorPreset.P2_0 -> "2.0 Frontal"
            GeneratorPreset.P0_1_SUB -> "0.1 Solo Subwoofer"
            GeneratorPreset.CUSTOM -> "Personalizado"
        }
    },
    panningModeLabel = { mode ->
        when (mode) {
            PanningMode.SWEET_SPOT_VBAP -> "Objeto virtual"
            PanningMode.ROOM_DISTANCE -> "Enfoque de altavoz"
        }
    },
    panningModeHint = { mode ->
        when (mode) {
            PanningMode.SWEET_SPOT_VBAP -> "Atenuación por distancia y aire"
            PanningMode.ROOM_DISTANCE -> "Proximidad al altavoz"
        }
    },
    orbitSpeedLabel = { spd ->
        when (spd) {
            OrbitSpeed.SLOW -> "Lento"
            OrbitSpeed.MEDIUM -> "Medio"
            OrbitSpeed.FAST -> "Rápido"
        }
    },
    layoutPresetShort = { preset ->
        when (preset) {
            SpeakerLayoutPreset.COMPACT -> "Estrecho (±22°)"
            SpeakerLayoutPreset.STANDARD -> "Estándar (±30°)"
            SpeakerLayoutPreset.WIDE -> "Amplio (±40°)"
        }
    },
    layoutPresetSummary = { preset ->
        when (preset) {
            SpeakerLayoutPreset.COMPACT -> "Frontal ±22° • Surround ±100° • Trasero ±140°"
            SpeakerLayoutPreset.STANDARD -> "Frontal ±30° • Surround ±110° • Trasero ±150°"
            SpeakerLayoutPreset.WIDE -> "Frontal ±40° • Surround ±120° • Trasero ±155°"
        }
    },
    layoutPresetDetail = { preset ->
        when (preset) {
            SpeakerLayoutPreset.COMPACT -> "Ideal para muebles de TV estrechos o poca distancia de escucha."
            SpeakerLayoutPreset.STANDARD -> "Disposición clásica Dolby / ITU en triángulo equilátero."
            SpeakerLayoutPreset.WIDE -> "Para altavoces frontales muy separados o salas amplias."
        }
    },

    roomTvFront = "FRENTE TV",
    roomSweetSpotDesc = "Punto de escucha",
    roomSoundPuckDesc = "Puck espacial",

    tvTopologyBadge = "TOPOLOGÍA TV",
    setupTvSubtitle = "Elige la configuración de altavoces y vincula móvil / navegador",
    connectBrowserIosBtn = "Conectar Navegador / iOS",
    goToHomeHubBtn = "Ir al menú principal",
    activeSpeakerConfigHeading = "Configuración de altavoces activa",
    legendNavigate = "Navegar",
    legendActivate = "Activar",
    activeBadgeUpper = "ACTIVO",
    profileSpecUpper = "ESPECIFICACIÓN DEL PERFIL",
    setupPrefix = "CONFIG",
    setupStreamDesc = { ch ->
        "Abre un AudioTrack de $ch canales (48 kHz PCM). Los canales sin uso se rellenan con silencio digital (0.0f)."
    },
    specChannelsActive = "Canales activos",
    specPcmStream = "Flujo PCM",
    specPcmValue = { ch -> "$ch canales @ 48 kHz" },
    specSubwooferLfe = "Subwoofer LFE",
    specSubwooferLfeValue = "Paso bajo hasta 80 Hz",
    hdmiMultichannel = { maxCh -> "HDMI Multicanal (${maxCh}ch)" },
    outputStereo = { maxCh -> "Salida: Estéreo (${maxCh}ch)" },
    headroomProtectionTag = "Protección -12 dBFS",
    mobileSetupTitle = "Altavoces",
    mobileSetupSubtitle = "Elegir topología de sala",
    setupActiveTag = { code -> "$code ACTIVO" },
    mobileSetupSelectPrompt = "Selecciona tu sistema real de altavoces",
    mobileSetupConfirmBtn = { code -> "Confirmar $code e ir al Hub" },

    hubSetupChipLabel = "Setup:",
    hubAudioBtn = { count -> "Audio ($count)" },
    hubBrowserIosBtn = "Navegador / iOS",
    tvHubSectionTitle = "Suite de calibración acústica",
    tvHubSectionSubtitle = "Elige un modo de prueba con el mando (Izquierda/Derecha + OK).",
    tvHubChannelsBadge = { count, code -> "$count canales ($code)" },
    tvHostActiveLabel = { ip -> "TV Host activo: $ip" },
    wlanCheckPrompt = "Revisar Wi-Fi",
    wlanNotDetected = "Wi-Fi no detectado",
    remoteAndroidAndBrowser = "Conectado: App móvil y Navegador",
    remoteAndroidOnly = { count -> "Conectado: App móvil" + if (count > 1) " ($count)" else "" },
    remoteBrowserOnly = { count -> "Conectado: Navegador" + if (count > 1) " ($count)" else "" },
    remoteNone = "Sin control remoto",
    headroomStatusBar = { db -> "Margen: $db dBFS • Limitador suave activo" },
    mode1Badge = "MODO 01",
    mode1Title = "Prueba de altavoz individual",
    mode1TvDesc = { chs -> "Diagnóstico por canal. Aísla cada altavoz ($chs) para comprobar cableado y niveles." },
    mode1MobileDesc = { chs -> "Plano 2D interactivo. Aísla cada canal ($chs) para verificar cableado y volumen." },
    mode1Footer = "Ruido rosa • Senoidal • Audio",
    mode2Badge = "MODO 02",
    mode2Title = "Generador de frecuencias",
    mode2TvDesc = "Generador continuo de onda senoidal con control por cruceta, ruido rosa y presets de sala.",
    mode2MobileDesc = "Rueda táctil con háptica, tonos senoidales, ruido rosa y presets de corte para subwoofer.",
    mode2Footer = "Banda 20 Hz – 20 kHz",
    mode3Badge = "MODO 03",
    mode3Title = "Puck espacial 2D",
    mode3TvDesc = "Paneador en tiempo real. Mueve la fuente sonora entre altavoces con cruceta u Órbita automática.",
    mode3MobileDesc = "Paneador de sala en vivo. Desliza un objeto virtual o enfoca altavoces con toque y Órbita automática.",
    mode3Footer = "Objeto virtual y Enfoque de altavoz",
    mobileConnConnected = { host -> "Conectado: $host" },
    mobileConnConnecting = "Conectando al TV...",
    mobileConnLocal = "Modo local (2.0)",
    mobileRoleRemote = "Control remoto de TV",
    mobileRoleLocal = "Calibrador acústico (Estéreo)",
    mobileCard1FooterLeft = { count -> "$count canales" },
    mobileCard2FooterLeft = "Espectro de frecuencias",
    mobileCard3FooterLeft = "Paneo envolvente",
    mobileCard3FooterRight = "360° en vivo",
    mobileFooterTv = { ip -> "TV: $ip" },
    mobileFooterLocal = "Mezcla local 2.0 activa",
    mobileFooterHeadroom = { db -> "Margen $db dBFS" },

    singleTitle = "Prueba de altavoz individual",
    singleTvSubtitle = { setupName -> "$setupName • Diagnóstico de canales" },
    singleMobileSubtitle = { code -> "DIAGNÓSTICO $code" },
    legendChannel = "Canal",
    legendStartStop = "Iniciar / Parar",
    legendSignalChoice = "Señal",
    singleActiveBadge = { s -> "$s ACTIVO" },
    pinkNoiseFreqRange = "Ruido rosa (20Hz–20kHz)",
    sineToneLabel = { hz -> "Onda senoidal $hz Hz" },
    noAudioSelected = "Sin archivo de audio",
    noAudioShort = "Sin audio",
    configureSignalDesc = { name -> "Fuente para $name" },
    syncRoomPlanUpper = "PLANO DE SALA SINCRONIZADO",
    signalActiveUpper = "SEÑAL ACTIVA",
    sourcePrefix = { lbl -> "Fuente: $lbl" },
    channelDialogTitle = { name, s -> "$name ($s) – Fuente de señal" },
    channelSheetSubtitle = "Elige la fuente de señal para este altavoz",
    sineFreqLabel = { hz -> "Frecuencia senoidal: $hz Hz" },
    sineDesc = { hz -> "Tono puro discreto ($hz Hz)" },
    pinkNoiseDesc = "Energía uniforme por octava (Estándar)",
    customAudioDesc = "Pista de referencia propia (bucle)",
    frequencyLabel = "Frecuencia",
    noCustomAudioTvHint = "No hay audios cargados. Sube pistas desde la app móvil o el navegador.",
    noCustomAudioMobileHint = "Aún no hay audios en la biblioteca. Abre la Biblioteca de Audio en Inicio para subir.",
    applyToAllChannels = "Aplicar a todos los altavoces",
    applySignalBtn = "Aplicar señal",

    generatorTitle = "Generador de frecuencias",
    generatorMobileSubtitle = "SINTETIZADOR DSP",
    activeChannelsPrefix = { chs -> "Canales activos: $chs" },
    noneText = "Ninguno",
    legendFreq = "Frecuencia",
    upStepHint = "SUBIR (+1 / +5 / +25 Hz)",
    downStepHint = { playing -> "BAJAR (-1 / -5 / -25 Hz) • OK: ${if (playing) "Pausa" else "Iniciar"}" },
    pauseAction = "Pausa",
    startAction = "Iniciar",
    pinkNoiseStartTv = "Iniciar ruido rosa [OK]",
    pinkNoisePauseTv = "Pausar ruido rosa [OK]",
    sineStartTv = { hz -> "Iniciar tono $hz Hz [OK]" },
    sinePauseTv = { hz -> "Pausar tono $hz Hz [OK]" },
    pinkNoiseStartMobile = "Iniciar ruido rosa",
    pinkNoisePauseMobile = "Pausar ruido rosa",
    sineStartMobile = { hz -> "Iniciar tono $hz Hz" },
    sinePauseMobile = { hz -> "Detener tono $hz Hz" },
    quickSelectUpper = "ACCESO RÁPIDO",
    referenceUpper = "REFERENCIA",
    quickPresets = listOf(
        QuickFreqPresetText(40, "40 Hz", "Resonancia sub"),
        QuickFreqPresetText(60, "60 Hz", "Base subwoofer"),
        QuickFreqPresetText(80, "80 Hz", "Cruce THX"),
        QuickFreqPresetText(400, "400 Hz", "Referencia media"),
        QuickFreqPresetText(1000, "1.000 Hz", "Ref. nivel 1 kHz"),
        QuickFreqPresetText(10000, "10.000 Hz", "Prueba agudos")
    ),
    digitalLevelLabel = "Nivel digital:",
    signalUpper = "SEÑAL",
    freqUpper = "FRECUENCIA",
    tapToEnter = "Toca para escribir",
    freqDialogTitle = "Introducir frecuencia",
    freqDialogRangeLabel = "Frecuencia (20 – 20.000 Hz)",

    puckTitle = "Puck espacial 2D",
    puckMobileSubtitle = "PANEADOR EN VIVO",
    puckTvSubtitle = { mode, az, hint -> "$mode • Azimut ${az}° • $hint" },
    legendMovePuck = "Mover Puck",
    legendSideMenuActive = "Menú lateral activo",
    legendBackForHub = "Atrás al inicio",
    puckSourcePrefix = { lbl -> "Fuente: $lbl" },
    virtualDistance = { m -> "Distancia virtual: $m m" },
    roomControlUpper = "CONTROL ESPACIAL",
    puckBtnLabel = "Puck",
    orbitStateLabel = { on -> "Órbita: ${if (on) "Sí" else "No"}" },
    stateOn = "Sí",
    stateOff = "No",
    orbitWithSpeed = { s -> "Órbita ($s)" },
    autoOrbitBtn = "Órbita auto",
    speedPrefix = { s -> "Velocidad: $s" },
    noAudioUploaded = "Sin pistas cargadas",
    trackCycleLabel = { idx, total, name -> "Pista ($idx/$total): $name" },
    puckDecodingMobile = "Decodificando audio… (Pausar)",
    puckDecodingTv = "Cargando…",
    puckPauseMobile = "Pausar audio espacial",
    puckStartMobile = "Iniciar audio espacial",
    noAudioOnTvHint = "Aún no hay audios en el TV – súbelos desde Inicio.",

    qrTitle = "Vincular Navegador / iOS",
    qrBody = "Este código QR solo es necesario para controlar desde el navegador (p. ej. iPhone / portátil).\nLa app Android SurroundLab se conecta automáticamente en la misma red Wi-Fi.",
    tvSettingsTitle = "Ajustes del TV Host",
    tvSettingsSubtitle = "Protección auditiva digital + Diagnóstico HDMI",
    masterLevelTitleTv = { def -> "Nivel de salida maestro (Predeterminado: $def dBFS)" },
    swap71Title = "Intercambiar 7.1 Surround y Back (HDMI-HAL)",
    swap71DescTv = "Activar si SL/SR y SBL/SBR están invertidos en 7.1 (p. ej. Chromecast / Amlogic).",
    swap71DescMobile = "Solución HDMI-HAL (p. ej. Chromecast / Amlogic) si SL/SR y SBL/SBR están invertidos.",
    layoutAnglesTitle = "Ángulos de ubicación (Objeto virtual)",
    tvUiScaleTitle = "Tamaño de interfaz TV (Zoom DPI)",
    scaleSmallerBtn = "−5 % (Menor)",
    scaleDefaultBtn = "Estándar (75 %)",
    scaleLargerBtn = "+5 % (Mayor)",
    audioOutputDiag = { name, ch -> "Salida de audio: $name (máx. $ch canales)" },
    hdmiDiagOk = "PCM multicanal detectado. Enrutamiento discreto 5.1 / 7.1 por HDMI/eARC activo.",
    hdmiDiagStereoWarn = "Aviso: Solo se detecta estéreo. Configura la salida de audio del TV en Automático / PCM y conecta directo al receptor AV.",
    setupChangeBtn = { code -> "Cambiar Setup ($code)" },
    setupChangeFullBtn = { code -> "Cambiar altavoces ($code)" },
    checkNetworkHdmiBtn = "Comprobar Red / HDMI",
    guideBtnShort = "Guía rápida",
    guideShowBtn = "Mostrar guía rápida",
    tvAudioLibTitle = { count -> "Biblioteca de Audio ($count)" },
    tvAudioLibSubtitle = "Archivos subidos desde móvil o navegador por HTTP",
    tvAudioLibEmpty = "Aún no hay archivos de audio propios.\nSube pistas desde la app móvil SurroundLab o el navegador.",

    mobileLibTitle = "Biblioteca de Audio",
    mobileLibSubtitle = "PISTAS DE REFERENCIA",
    libCountBadge = { count, mb -> "$count pistas • $mb MB" },
    syncInProgressTitle = "Sincronizando con el TV…",
    syncAllDoneTitle = "Todo sincronizado con el TV",
    syncPendingTitle = { count -> "$count pista(s) aún no en el TV" },
    syncLocalTitle = "Biblioteca local del móvil",
    syncActiveSub = { count -> "$count transferencia(s) en curso por Wi-Fi" },
    syncAllDoneSub = "Todas las pistas listas en TV y móvil",
    syncPendingSub = "Toca «Sincronizar» para enviar los archivos faltantes al TV",
    syncOfflineSub = "Los archivos nuevos se enviarán al conectar con el TV",
    syncNowBtn = "Sincronizar",
    libEmptyTitle = "Sin archivos de audio",
    libEmptyBody = "Elige .wav, .mp3, .flac u .ogg mediante el selector seguro de Android (máx. 30 MB).",
    statusSyncedOnTv = "Sincronizado en TV",
    statusSavedLocally = "Guardado localmente",
    statusUploading = "Subiendo al TV…",
    statusOnlyOnPhone = "Solo en móvil (falta en TV)",
    statusWaitingSync = "Esperando sincronización TV",
    statusUploadFailed = "Error al subir al TV",
    statusDeletingOnTv = "Eliminando en TV…",
    sendToTvDesc = "Enviar al TV",
    uploadAudioBtn = "+ Subir archivo de audio",
    mobileConnTitle = "Conexión TV y Nivel",
    mobileConnSubtitle = "DESCUBRIMIENTO mDNS Y MARGEN",
    refreshSearchDesc = "Buscar de nuevo",
    currentModeTitle = "Modo actual",
    connConnectedDetail = { ip, port -> "Conectado al TV Host ($ip:$port)" },
    connConnectingDetail = "Conectando al TV Host...",
    connLocalDetail = "Modo local (mezcla estéreo 2.0 en el móvil)",
    disconnectTvBtn = "Desconectar del TV (Modo local)",
    discoveredTvsTitle = "TVs encontrados en Wi-Fi (mDNS)",
    noTvsFoundText = "No se encontraron TVs SurroundLab. Verifica que la app esté abierta en el televisor.",
    connectBtn = "Conectar",
    manualConnTitle = "Conexión manual al TV",
    tvIpLabel = "Dirección IP del TV",
    portLabel = "Puerto",
    sessionTokenLabel = "Token de sesión de 6 dígitos (del TV)",
    manualConnectBtn = "Conectar manualmente",
    masterLevelTitleMobile = "Nivel maestro (Protección auditiva)",

    onboardingSlides = listOf(
        OnboardingSlide(
            badge = "PASO 1 / 3 • RESUMEN",
            title = "Bienvenido a SurroundLab",
            subtitle = "Tu estudio de calibración multicanal para cine en casa 2.0 a 7.1 por HDMI.",
            icon = Icons.Rounded.SurroundSound,
            bullets = listOf(
                "Prueba de altavoz individual" to "Aísla cada canal para verificar el cableado con ruido rosa, onda senoidal o tus propios audios.",
                "Generador de frecuencias" to "Recorre de 20 Hz a 20 kHz, detecta resonancias de la sala y ajusta el subwoofer.",
                "Puck espacial 2D" to "Desplaza un objeto sonoro virtual o enfoca altavoces en tiempo real por la sala."
            )
        ),
        OnboardingSlide(
            badge = "PASO 2 / 3 • CONTROL",
            title = "TV, App Android o Navegador",
            subtitle = "Controla SurroundLab como te resulte más cómodo.",
            icon = Icons.Rounded.Devices,
            bullets = listOf(
                "Directamente en el TV" to "Todos los modos, ajustes y pruebas funcionan por completo con el mando del televisor.",
                "App Android (Automática)" to "Instala SurroundLab también en tu móvil: ambos dispositivos se detectan en el mismo Wi-Fi y sincronizan ajustes y audios (sin código QR).",
                "Navegador / iOS (QR arriba a la derecha)" to "Pulsa «Navegador / iOS» arriba a la derecha en el TV para mostrar el QR y abrir el control en cualquier navegador."
            )
        ),
        OnboardingSlide(
            badge = "PASO 3 / 3 • CONSEJOS",
            title = "Consejos útiles de configuración",
            subtitle = "Tres detalles importantes antes de empezar:",
            icon = Icons.Rounded.Tune,
            bullets = listOf(
                "¿7.1 Surround y Back invertidos?" to "Algunos dispositivos TV (como Chromecast) invierten SL/SR y SBL/SBR por hardware. En Ajustes (engranaje) puedes activar «Intercambiar 7.1 Surround y Back».",
                "Ángulos de ubicación" to "En Ajustes puedes elegir entre Estrecho (±22°), Estándar (±30°) y Amplio (±40°) según tu sala.",
                "Protección y modo del AVR" to "El volumen inicia seguro en -12 dBFS con limitador suave. Usa «Multi Ch In» / «Straight» en tu receptor AV (sin upmixer)."
            )
        )
    ),
    onboardingSkipToNext = "Atrás",
    onboardingNext = "Siguiente →",
    onboardingFinish = "Empezar",

    tvInstall = TvInstallStrings(
        menuBtn = "Instalar app en tu TV (Guía)",
        badge = "INSTALACIÓN EN TV • VÍA «DOWNLOADER»",
        title = "Instalar SurroundLab en tu TV",
        subtitle = "Instala la app en tu Chromecast / Android TV en 4 pasos sencillos, sin usar PC:",
        steps = listOf(
            "1. Instala «Downloader» en tu TV" to "Abre Google Play Store en tu televisor, busca «Downloader by AFTVnews» (icono naranja) e instala esta app gratuita.",
            "2. Introduce el código 7281113 en Downloader" to "Abre «Downloader» en tu TV, escribe simplemente el código 7281113 (o la dirección de GitHub de abajo) y pulsa «Go».",
            "3. Permite la instalación una vez" to "Si tu TV pide permiso para que «Downloader» instale apps, entra en Ajustes → Permitir y después pulsa Instalar.",
            "4. Abre y conecta automáticamente" to "Inicia SurroundLab en tu TV. En cuanto el TV y el móvil estén en el mismo Wi-Fi, se conectarán solos."
        ),
        copyUrlBtn = "Copiar enlace de GitHub",
        urlCopiedToast = "Enlace copiado al portapapeles",
        closeBtn = "Cerrar"
    ),
    donatePaypalBtn = "Apoyar el proyecto (PayPal)",

    update = UpdateStrings(
        sectionTitle = "Actualizaciones (GitHub)",
        currentVersion = { v -> "Versión instalada: v$v" },
        checkBtn = "Buscar actualizaciones",
        checkingText = "Buscando nuevas versiones en GitHub…",
        upToDateText = { v -> "Todo actualizado (v$v)." },
        availableText = { v -> "¡Nueva versión v$v disponible!" },
        downloadInstallBtn = "Descargar e instalar",
        downloadingText = { pct -> "Descargando APK… $pct%" },
        installNowBtn = "Instalar ahora",
        permissionHint = "Permite instalar apps desde esta fuente una vez y vuelve a pulsar «Instalar ahora».",
        errorText = { err -> "Error al comprobar ($err)" }
    )
)
