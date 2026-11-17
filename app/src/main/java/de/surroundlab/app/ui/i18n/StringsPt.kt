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

val StringsPt = AppStrings(
    langCode = "pt",
    back = "Voltar",
    close = "Fechar",
    ok = "OK",
    apply = "Aplicar",
    cancel = "Cancelar",
    activeTag = "ATIVO",
    ready = "Pronto",
    testBtn = "Testar",
    stopBtn = "Parar",
    startBtn = "Iniciar",
    pauseBtn = "Pausar",
    deleteBtn = "Excluir",
    settingsLabel = "Configurações",
    homeHubBtn = "Hub Inicial",

    languageSectionTitle = "Idioma",
    languageAutoLabel = "Auto",
    languageAutoHint = { resolvedName -> "Automático ($resolvedName)" },

    setupSubtitle = { setup ->
        when (setup) {
            SpeakerSetup.S2_0 -> "Frente Esquerda & Direita"
            SpeakerSetup.S2_1 -> "Estéreo + Subwoofer"
            SpeakerSetup.S3_1 -> "Frente + Central + Sub"
            SpeakerSetup.S5_0 -> "5 Caixas Satélites"
            SpeakerSetup.S5_1 -> "6 Canais Discretos"
            SpeakerSetup.S7_1 -> "8 Canais Discretos"
        }
    },
    channelName = { ch -> ch.displayName },
    sourceLabel = { src ->
        when (src) {
            SignalSourceType.PINK_NOISE -> "Ruído rosa"
            SignalSourceType.SINE -> "Onda senoidal"
            SignalSourceType.CUSTOM_AUDIO -> "Áudio personalizado"
        }
    },
    sourceShortLabel = { src ->
        when (src) {
            SignalSourceType.PINK_NOISE -> "Ruído rosa"
            SignalSourceType.SINE -> "Senoidal"
            SignalSourceType.CUSTOM_AUDIO -> "Áudio"
        }
    },
    generatorPresetLabel = { preset ->
        when (preset) {
            GeneratorPreset.ALL_ACTIVE -> "Todos os canais ativos"
            GeneratorPreset.P2_1 -> "2.1 Estéreo + Sub"
            GeneratorPreset.P2_0 -> "2.0 Frontal"
            GeneratorPreset.P0_1_SUB -> "0.1 Apenas Subwoofer"
            GeneratorPreset.CUSTOM -> "Personalizado"
        }
    },
    panningModeLabel = { mode ->
        when (mode) {
            PanningMode.SWEET_SPOT_VBAP -> "Objeto virtual"
            PanningMode.ROOM_DISTANCE -> "Foco na caixa"
        }
    },
    panningModeHint = { mode ->
        when (mode) {
            PanningMode.SWEET_SPOT_VBAP -> "Distância e atenuação aérea"
            PanningMode.ROOM_DISTANCE -> "Proximidade das caixas"
        }
    },
    orbitSpeedLabel = { spd ->
        when (spd) {
            OrbitSpeed.SLOW -> "Lento"
            OrbitSpeed.MEDIUM -> "Médio"
            OrbitSpeed.FAST -> "Rápido"
        }
    },
    layoutPresetShort = { preset ->
        when (preset) {
            SpeakerLayoutPreset.COMPACT -> "Estreito (±22°)"
            SpeakerLayoutPreset.STANDARD -> "Padrão (±30°)"
            SpeakerLayoutPreset.WIDE -> "Amplo (±40°)"
        }
    },
    layoutPresetSummary = { preset ->
        when (preset) {
            SpeakerLayoutPreset.COMPACT -> "Frente ±22° • Surround ±100° • Traseira ±140°"
            SpeakerLayoutPreset.STANDARD -> "Frente ±30° • Surround ±110° • Traseira ±150°"
            SpeakerLayoutPreset.WIDE -> "Frente ±40° • Surround ±120° • Traseira ±155°"
        }
    },
    layoutPresetDetail = { preset ->
        when (preset) {
            SpeakerLayoutPreset.COMPACT -> "Ideal para racks estreitos ou audição próxima à tela."
            SpeakerLayoutPreset.STANDARD -> "Disposição clássica Dolby / ITU em triângulo equilátero."
            SpeakerLayoutPreset.WIDE -> "Para caixas frontais bem afastadas ou salas amplas."
        }
    },

    roomTvFront = "FRENTE TV",
    roomSweetSpotDesc = "Ponto ideal",
    roomSoundPuckDesc = "Fonte 2D",

    tvTopologyBadge = "TOPOLOGIA TV",
    setupTvSubtitle = "Escolher caixas de som & parear celular / navegador",
    connectBrowserIosBtn = "Conectar Navegador / iOS",
    goToHomeHubBtn = "Ir para o Hub",
    activeSpeakerConfigHeading = "Configuração de caixas ativa",
    legendNavigate = "Navegar",
    legendActivate = "Selecionar",
    activeBadgeUpper = "ATIVO",
    profileSpecUpper = "ESPECIFICAÇÕES DO PERFIL",
    setupPrefix = "CONFIG",
    setupStreamDesc = { ch ->
        "Abre um AudioTrack de $ch canais (PCM 48 kHz). Canais não usados ficam em silêncio digital (0.0f)."
    },
    specChannelsActive = "Canais ativos",
    specPcmStream = "Stream PCM",
    specPcmValue = { ch -> "$ch canais @ 48 kHz" },
    specSubwooferLfe = "Subwoofer LFE",
    specSubwooferLfeValue = "Passa-baixa até 80 Hz",
    hdmiMultichannel = { maxCh -> "HDMI multicanal ($maxCh ch)" },
    outputStereo = { maxCh -> "Saída: Estéreo ($maxCh ch)" },
    headroomProtectionTag = "Proteção -12 dBFS",
    mobileSetupTitle = "Configuração de Caixas",
    mobileSetupSubtitle = "Escolher topologia da sala",
    setupActiveTag = { code -> "$code ATIVO" },
    mobileSetupSelectPrompt = "Selecione seu sistema de som",
    mobileSetupConfirmBtn = { code -> "Confirmar $code & abrir Hub" },

    hubSetupChipLabel = "Config:",
    hubAudioBtn = { count -> "Áudio ($count)" },
    hubBrowserIosBtn = "Navegador / iOS",
    tvHubSectionTitle = "Suíte de Calibração Acústica",
    tvHubSectionSubtitle = "Escolha um modo com o controle remoto (Esquerda/Direita + OK).",
    tvHubChannelsBadge = { count, code -> "$count canais ($code)" },
    tvHostActiveLabel = { ip -> "Host TV ativo: $ip" },
    wlanCheckPrompt = "Verificar Wi-Fi",
    wlanNotDetected = "Wi-Fi não detectado",
    remoteAndroidAndBrowser = "Conectado: App celular & Navegador",
    remoteAndroidOnly = { count -> "Conectado: App celular" + if (count > 1) " ($count)" else "" },
    remoteBrowserOnly = { count -> "Conectado: Navegador" + if (count > 1) " ($count)" else "" },
    remoteNone = "Nenhum controle conectado",
    headroomStatusBar = { db -> "Headroom: $db dBFS • Soft-Limiter ativo" },
    mode1Badge = "MODO 01",
    mode1Title = "Teste por Caixa",
    mode1TvDesc = { chs -> "Diagnóstico em tela dividida. Isole cada canal ($chs) para checar cabos e volumes." },
    mode1MobileDesc = { chs -> "Planta 2D interativa. Isole canais ($chs) para verificar fiação e ganho." },
    mode1Footer = "Ruído rosa • Senoidal • Áudio",
    mode2Badge = "MODO 02",
    mode2Title = "Gerador de Frequências",
    mode2TvDesc = "Gerador senoidal contínuo via D-Pad, ruído rosa e predefinições de crossover do subwoofer.",
    mode2MobileDesc = "Roda tátil háptica com ondas senoidais, ruído rosa e cortes de subwoofer.",
    mode2Footer = "Espectro 20 Hz – 20 kHz",
    mode3Badge = "MODO 03",
    mode3Title = "Puck Espacial 2D",
    mode3TvDesc = "Panner espacial em tempo real. Mova a fonte virtual pelo ambiente com D-Pad ou Auto-Órbita.",
    mode3MobileDesc = "Panner em tempo real. Arraste a fonte virtual ou foque caixas pelo toque e Auto-Órbita.",
    mode3Footer = "Objeto virtual & Foco na caixa",
    mobileConnConnected = { host -> "Conectado: $host" },
    mobileConnConnecting = "Conectando à TV...",
    mobileConnLocal = "Modo local (2.0)",
    mobileRoleRemote = "Controle Remoto TV",
    mobileRoleLocal = "Calibrador de Áudio (Estéreo)",
    mobileCard1FooterLeft = { count -> "$count canais" },
    mobileCard2FooterLeft = "Espectro de frequências",
    mobileCard3FooterLeft = "Panning surround",
    mobileCard3FooterRight = "360° tempo real",
    mobileFooterTv = { ip -> "TV: $ip" },
    mobileFooterLocal = "Downmix local ativo",
    mobileFooterHeadroom = { db -> "Headroom $db dBFS" },

    singleTitle = "Teste por Caixa",
    singleTvSubtitle = { setupName -> "$setupName • Diagnóstico por canal em tela dividida" },
    singleMobileSubtitle = { code -> "DIAGNÓSTICO $code" },
    legendChannel = "Canal",
    legendStartStop = "Iniciar / Parar",
    legendSignalChoice = "Sinal",
    singleActiveBadge = { s -> "$s ATIVO" },
    pinkNoiseFreqRange = "Ruído rosa (20Hz–20kHz)",
    sineToneLabel = { hz -> "Senoidal $hz Hz" },
    noAudioSelected = "Nenhum arquivo selecionado",
    noAudioShort = "Sem áudio",
    configureSignalDesc = { name -> "Fonte de sinal para $name" },
    syncRoomPlanUpper = "PLANTA SINCRONIZADA",
    signalActiveUpper = "SINAL ATIVO",
    sourcePrefix = { lbl -> "Fonte: $lbl" },
    channelDialogTitle = { name, s -> "$name ($s) – Fonte de Sinal" },
    channelSheetSubtitle = "Escolha o sinal de teste para esta caixa",
    sineFreqLabel = { hz -> "Frequência senoidal: $hz Hz" },
    sineDesc = { hz -> "Tom puro discreto ($hz Hz)" },
    pinkNoiseDesc = "Energia igual por oitava (Padrão)",
    customAudioDesc = "Faixa de referência em loop",
    frequencyLabel = "Frequência",
    noCustomAudioTvHint = "Nenhuma faixa enviada ainda. Envie áudios pelo app no celular ou pelo navegador.",
    noCustomAudioMobileHint = "Sem faixas na biblioteca. Envie arquivos de áudio pela tela inicial.",
    applyToAllChannels = "Aplicar a todas as caixas",
    applySignalBtn = "Aplicar sinal",

    generatorTitle = "Gerador de Frequências",
    generatorMobileSubtitle = "SINTETIZADOR DSP",
    activeChannelsPrefix = { chs -> "Canais ativos: $chs" },
    noneText = "Nenhum",
    legendFreq = "Frequência",
    upStepHint = "CIMA (+1 / +5 / +25 Hz)",
    downStepHint = { playing -> "BAIXO (-1 / -5 / -25 Hz) • OK: ${if (playing) "Pausar" else "Tocar"}" },
    pauseAction = "Pausar",
    startAction = "Iniciar",
    pinkNoiseStartTv = "Iniciar ruído rosa [OK]",
    pinkNoisePauseTv = "Pausar ruído rosa [OK]",
    sineStartTv = { hz -> "Iniciar tom $hz Hz [OK]" },
    sinePauseTv = { hz -> "Pausar tom $hz Hz [OK]" },
    pinkNoiseStartMobile = "Iniciar ruído rosa",
    pinkNoisePauseMobile = "Pausar ruído rosa",
    sineStartMobile = { hz -> "Iniciar tom $hz Hz" },
    sinePauseMobile = { hz -> "Parar tom $hz Hz" },
    quickSelectUpper = "ATALHOS RÁPIDOS",
    referenceUpper = "REFERÊNCIA",
    quickPresets = listOf(
        QuickFreqPresetText(40, "40 Hz", "Modo de sala do sub"),
        QuickFreqPresetText(60, "60 Hz", "Base do subwoofer"),
        QuickFreqPresetText(80, "80 Hz", "Crossover THX"),
        QuickFreqPresetText(400, "400 Hz", "Médio padrão"),
        QuickFreqPresetText(1000, "1.000 Hz", "Referência 1 kHz"),
        QuickFreqPresetText(10000, "10.000 Hz", "Checagem de tweeter")
    ),
    digitalLevelLabel = "Nível digital:",
    signalUpper = "SINAL",
    freqUpper = "FREQUÊNCIA",
    tapToEnter = "Toque para digitar",
    freqDialogTitle = "Inserir frequência exata",
    freqDialogRangeLabel = "Frequência (20 – 20.000 Hz)",

    puckTitle = "Puck Espacial 2D",
    puckMobileSubtitle = "PANNER ESPACIAL EM TEMPO REAL",
    puckTvSubtitle = { mode, az, hint -> "$mode • Azimute ${az}° • $hint" },
    legendMovePuck = "Mover Puck",
    legendSideMenuActive = "Menu lateral ativo",
    legendBackForHub = "Voltar ao Hub",
    puckSourcePrefix = { lbl -> "Fonte: $lbl" },
    virtualDistance = { m -> "Distância virtual: $m m" },
    roomControlUpper = "CONTROLE ESPACIAL",
    puckBtnLabel = "Puck",
    orbitStateLabel = { on -> "Órbita: ${if (on) "Ligada" else "Desligada"}" },
    stateOn = "On",
    stateOff = "Off",
    orbitWithSpeed = { s -> "Órbita ($s)" },
    autoOrbitBtn = "Auto-Órbita",
    speedPrefix = { s -> "Velocidade: $s" },
    noAudioUploaded = "Nenhum áudio enviado",
    trackCycleLabel = { idx, total, name -> "Faixa ($idx/$total): $name" },
    puckDecodingMobile = "Decodificando áudio… (Pausa)",
    puckDecodingTv = "Carregando…",
    puckPauseMobile = "Pausar áudio espacial",
    puckStartMobile = "Iniciar áudio espacial",
    noAudioOnTvHint = "Sem áudios na TV – envie arquivos pela tela inicial.",

    qrTitle = "Parear Navegador / iOS",
    qrBody = "Este QR code só é necessário para controlar via navegador (ex. iPhone / notebook).\nO app Android SurroundLab encontra e sincroniza automaticamente na mesma rede Wi-Fi.",
    tvSettingsTitle = "Configurações do Host TV",
    tvSettingsSubtitle = "Headroom digital, proteção de tweeters & diagnóstico HDMI",
    masterLevelTitleTv = { def -> "Nível de saída geral (Padrão: $def dBFS)" },
    swap71Title = "Inverter 7.1 Surround & Traseira (HDMI-HAL)",
    swap71DescTv = "Ative se os canais 7.1 SL/SR e SBL/SBR estiverem trocados (ex. Chromecast / Amlogic).",
    swap71DescMobile = "Ajuste HDMI-HAL (ex. Chromecast / Amlogic) se SL/SR e SBL/SBR estiverem invertidos.",
    layoutAnglesTitle = "Ângulos de posicionamento (Objeto virtual)",
    tvUiScaleTitle = "Escala da interface TV (Zoom DPI)",
    scaleSmallerBtn = "−5 % (Menor)",
    scaleDefaultBtn = "Padrão (75 %)",
    scaleLargerBtn = "+5 % (Maior)",
    audioOutputDiag = { name, ch -> "Saída de áudio: $name (máx. $ch canais)" },
    hdmiDiagOk = "PCM multicanal detectado. Roteamento discreto 5.1 / 7.1 HDMI/eARC ativo.",
    hdmiDiagStereoWarn = "Aviso: apenas saída estéreo detectada agora. Defina o formato de áudio da TV/Chromecast como Auto / PCM e ligue direto ao receiver AVR se possível.",
    setupChangeBtn = { code -> "Alterar setup ($code)" },
    setupChangeFullBtn = { code -> "Alterar caixas ($code)" },
    checkNetworkHdmiBtn = "Reverificar rede / HDMI",
    guideBtnShort = "Guia rápido",
    guideShowBtn = "Mostrar guia rápido",
    tvAudioLibTitle = { count -> "Biblioteca de áudio ($count)" },
    tvAudioLibSubtitle = "Faixas enviadas do celular ou navegador via HTTP",
    tvAudioLibEmpty = "Nenhum arquivo de áudio ainda.\nEnvie faixas pelo app Android SurroundLab ou pelo navegador.",

    mobileLibTitle = "Biblioteca de Áudio",
    mobileLibSubtitle = "FAIXAS DE REFERÊNCIA",
    libCountBadge = { count, mb -> "$count faixas • $mb MB" },
    syncInProgressTitle = "Sincronizando com a TV…",
    syncAllDoneTitle = "Todas as faixas sincronizadas na TV",
    syncPendingTitle = { count -> "$count faixa(s) ainda não enviada(s) à TV" },
    syncLocalTitle = "Biblioteca local do celular",
    syncActiveSub = { count -> "$count transferência(s) via Wi-Fi" },
    syncAllDoneSub = "Todas as faixas prontas na TV e no celular",
    syncPendingSub = "Toque em “Sincronizar” para enviar os arquivos restantes à TV",
    syncOfflineSub = "Novos arquivos serão enviados automaticamente ao conectar na TV",
    syncNowBtn = "Sincronizar",
    libEmptyTitle = "Sem arquivos de áudio",
    libEmptyBody = "Selecione .wav, .mp3, .flac ou .ogg pelo seletor seguro do Android (máx. 30 MB).",
    statusSyncedOnTv = "Sincronizado na TV",
    statusSavedLocally = "Salvo localmente",
    statusUploading = "Enviando para a TV…",
    statusOnlyOnPhone = "Apenas no celular (não está na TV)",
    statusWaitingSync = "Aguardando envio para a TV",
    statusUploadFailed = "Falha no envio para a TV",
    statusDeletingOnTv = "Excluindo na TV…",
    sendToTvDesc = "Enviar à TV",
    uploadAudioBtn = "+ Enviar arquivo de áudio",
    mobileConnTitle = "Conexão TV & Nível",
    mobileConnSubtitle = "DESCOBERTA mDNS & HEADROOM",
    refreshSearchDesc = "Reiniciar busca",
    currentModeTitle = "Modo atual",
    connConnectedDetail = { ip, port -> "Conectado ao Host TV ($ip:$port)" },
    connConnectingDetail = "Conectando ao host TV...",
    connLocalDetail = "Modo local (downmix estéreo 2.0 no celular)",
    disconnectTvBtn = "Desconectar da TV (Modo local)",
    discoveredTvsTitle = "TVs encontradas no Wi-Fi (mDNS)",
    noTvsFoundText = "Nenhuma TV com SurroundLab encontrada. Confira se o app está aberto na TV.",
    connectBtn = "Conectar",
    manualConnTitle = "Conexão manual à TV",
    tvIpLabel = "Endereço IP da TV",
    portLabel = "Porta",
    sessionTokenLabel = "Código de sessão de 6 dígitos (da TV)",
    manualConnectBtn = "Conectar manualmente",
    masterLevelTitleMobile = "Nível Geral (Proteção Auditiva)",

    onboardingSlides = listOf(
        OnboardingSlide(
            badge = "PASSO 1 / 3 • VISÃO GERAL",
            title = "Bem-vindo ao SurroundLab",
            subtitle = "Seu estúdio de calibração multicanal de 2.0 a 7.1 via HDMI.",
            icon = Icons.Rounded.SurroundSound,
            bullets = listOf(
                "Teste por Caixa" to "Isole cada canal para conferir fiação e equilíbrio com ruído rosa, senoide ou áudios próprios.",
                "Gerador de Frequências" to "Varra de 20 Hz a 20 kHz, encontre ressonâncias da sala e ajuste o corte do subwoofer.",
                "Puck Espacial 2D" to "Mova uma fonte virtual ou foque caixas individuais em tempo real ao redor da sala."
            )
        ),
        OnboardingSlide(
            badge = "PASSO 2 / 3 • CONTROLE",
            title = "TV, App Android ou Navegador",
            subtitle = "Controle o SurroundLab da forma mais prática na sua sala.",
            icon = Icons.Rounded.Devices,
            bullets = listOf(
                "Diretamente na TV" to "Todos os modos e ajustes funcionam facilmente pelo controle remoto (D-Pad).",
                "App Android (Automático)" to "Instale o SurroundLab no celular: os aparelhos se acham no Wi-Fi e sincronizam sem QR code.",
                "Navegador / iOS (QR no canto superior)" to "Selecione “Navegador / iOS” no topo direito da TV para abrir o painel em qualquer navegador."
            )
        ),
        OnboardingSlide(
            badge = "PASSO 3 / 3 • DICAS PRO",
            title = "Dicas úteis de Home Theater",
            subtitle = "Três ajustes rápidos importantes antes de começar:",
            icon = Icons.Rounded.Tune,
            bullets = listOf(
                "7.1 Surround & Traseira trocados?" to "Alguns TV sticks (como Chromecast) trocam SL/SR com SBL/SBR em 7.1. Ative a inversão nas Configurações.",
                "Ângulos das caixas" to "Ajuste a geometria da sala nas configurações entre Estreito (±22°), Padrão (±30°) e Amplo (±40°).",
                "Proteção & modo do Receiver" to "A saída inicia em -12 dBFS com limitador suave. Use o receiver em “Multi Ch In” ou “Straight”."
            )
        )
    ),
    onboardingSkipToNext = "Voltar",
    onboardingNext = "Próximo →",
    onboardingFinish = "Começar",

    tvInstallMenuBtn = "Instalar app na TV (Guia)",
    tvInstallBadge = "INSTALAÇÃO NA TV • VIA “DOWNLOADER”",
    tvInstallTitle = "Instalar o SurroundLab na TV",
    tvInstallSubtitle = "Coloque o app no seu Chromecast / Android TV em 4 passos simples, sem precisar de PC:",
    tvInstallSteps = listOf(
        "1. Instale o “Downloader” na TV" to "Abra a Google Play Store na sua TV, procure por “Downloader by AFTVnews” (ícone laranja) e instale este app gratuito.",
        "2. Digite o link do GitHub no Downloader" to "Abra o “Downloader” na TV, digite o endereço do GitHub abaixo (dica: você pode colar usando o app de controle remoto do Google TV no celular) e toque em “Go”.",
        "3. Permita a instalação uma vez" to "Se a TV pedir permissão para o “Downloader” instalar apps, acesse Configurações → Permitir e depois toque em Instalar.",
        "4. Abra e conecte automaticamente" to "Inicie o SurroundLab na TV. Assim que a TV e o celular estiverem no mesmo Wi-Fi, eles se conectam sozinhos."
    ),
    tvInstallCopyUrlBtn = "Copiar link do GitHub",
    tvInstallUrlCopiedToast = "Link copiado para a área de transferência",
    tvInstallCloseBtn = "Fechar"
)
