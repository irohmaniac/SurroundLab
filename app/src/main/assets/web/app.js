(() => {
  const NATIVE_NAMES = {
    DE: 'Deutsch',
    EN: 'English',
    ES: 'Español',
    FR: 'Français',
    PT: 'Português',
    JA: '日本語'
  };

  const I18N = {
    DE: {
      connectingHost: 'Verbinde mit TV Host...',
      connectedTo: (h) => `Verbunden mit ${h}`,
      offline: 'Offline',
      tokenNeeded: 'Token nötig',
      connected: 'Verbunden',
      disconnected: 'Getrennt',
      tokenPrompt: 'Session-Token eingeben (vom TV-Bildschirm):',
      connect: 'Verbinden',
      mode1Title: 'Einzellautsprecher-Test',
      mode1Desc: 'Isoliere einzelne Kanäle, prüfe die Verkabelung und wähle Rauschen, Sinuston oder eigene Tracks.',
      mode2Title: 'Frequenzgenerator',
      mode2Desc: '20 Hz – 20 kHz Sinuston oder Rosa Rauschen auf gewählten Kanälen & Subwoofer-Presets.',
      mode3Title: '2D-Klangbühne',
      mode3Desc: 'Live-Raumpanner per Touch/Maus mit Virtuellem Objekt, Lautsprecher-Fokus und Auto-Orbit.',
      mode4Title: 'Raummoden & Einmessung',
      mode4DisabledDesc: 'Nur in der Android-App verfügbar – Browser blockieren Mikrofonzugriff über lokales HTTP im WLAN.',
      masterLevelTitle: 'Master-Pegel (Gehörschutz)',
      lfeLowPassLabel: 'LFE-Tiefpass (Rauschen/Musik)',
      lfeDirect: 'Direkt (Aus)',
      swap71Label: '7.1 Surround & Back tauschen (HDMI-HAL)',
      layoutAnglesLabel: 'Aufstellungs-Winkel',
      presetCompact: 'Eng (±22°)',
      presetStandard: 'Standard (±30°)',
      presetWide: 'Breit (±40°)',
      presetCompactDesc: 'Front ±22° • Surround ±100° – Für schmale TV-Möbel / kurzen Sitzabstand.',
      presetStandardDesc: 'Front ±30° • Surround ±110° – Klassische Dolby-/ITU-Aufstellung.',
      presetWideDesc: 'Front ±40° • Surround ±120° – Für weit außen stehende Boxen.',
      languageLabel: 'Sprache',
      autoLang: (n) => `Automatisch (${n})`,
      audioLibHeading: 'Audio-Bibliothek',
      uploadBtn: '+ Datei hochladen',
      uploadHint: 'Unterstützt .wav, .mp3, .flac, .ogg bis max. 30 MB.',
      noFiles: 'Noch keine Dateien hochgeladen.',
      deleteBtn: 'Löschen',
      backHub: '← Hub',
      sine: 'Sinuston',
      pinkNoise: 'Rosa Rauschen',
      customAudio: 'Eigene Audiodatei',
      audioNoFile: 'Audio (keine Datei)',
      noAudios: 'Keine Audios',
      testBtn: 'Test',
      stopBtn: 'Stop',
      genShortTitle: 'Frequenz-Generator',
      genPresets: {
        ALL_ACTIVE: 'Alle aktiven Kanäle',
        ALL_WITHOUT_SUB: 'Alle außer Subwoofer',
        P2_1: '2.1 Stereo + Sub',
        P2_0: '2.0 Front',
        P0_1_SUB: '0.1 Nur Sub',
        CUSTOM: 'Benutzerdefiniert'
      },
      pauseBtn: 'Pausieren',
      startHzBtn: (hz) => `${hz} Hz Starten`,
      startPinkBtn: 'Rosa Rauschen starten',
      panVirtual: 'Virtuelles Objekt',
      panSpeaker: 'Lautsprecher-Fokus',
      autoOrbit: 'Auto-Orbit',
      speedSlow: 'Tempo: Langsam',
      speedMed: 'Tempo: Mittel',
      speedFast: 'Tempo: Schnell',
      puckDecoding: 'Audio wird dekodiert… (Pausieren)',
      puckPause: 'Raum-Audio pausieren',
      puckStart: 'Raum-Audio starten',
      errSize: 'Fehler: Maximale Dateigröße beträgt 30 MB.',
      uploading: (f) => `Lade hoch: ${f}...`,
      uploadOk: (f) => `Erfolgreich synchronisiert: ${f}`,
      uploadFail: (e) => `Upload fehlgeschlagen: ${e}`,
      netErr: (e) => `Netzwerkfehler: ${e}`
    },
    EN: {
      connectingHost: 'Connecting to TV host...',
      connectedTo: (h) => `Connected to ${h}`,
      offline: 'Offline',
      tokenNeeded: 'Token required',
      connected: 'Connected',
      disconnected: 'Disconnected',
      tokenPrompt: 'Enter Session Token (shown on TV):',
      connect: 'Connect',
      mode1Title: 'Single Speaker Test',
      mode1Desc: 'Isolate individual channels, verify wiring, and choose pink noise, sine wave, or custom tracks.',
      mode2Title: 'Frequency Generator',
      mode2Desc: '20 Hz – 20 kHz sine wave or pink noise on selected channels & subwoofer presets.',
      mode3Title: '2D Spatial Sound Stage',
      mode3Desc: 'Real-time spatial panner via touch/mouse with Virtual Object, Speaker Focus, and Auto-Orbit.',
      mode4Title: 'Room Modes & Sweep',
      mode4DisabledDesc: 'Android app only — browsers block microphone access over local Wi-Fi HTTP.',
      masterLevelTitle: 'Master Level (Hearing Protection)',
      lfeLowPassLabel: 'LFE Low-Pass (Noise/Audio)',
      lfeDirect: 'Direct (Off)',
      swap71Label: 'Swap 7.1 Surround & Back (HDMI-HAL)',
      layoutAnglesLabel: 'Placement Angles',
      presetCompact: 'Narrow (±22°)',
      presetStandard: 'Standard (±30°)',
      presetWide: 'Wide (±40°)',
      presetCompactDesc: 'Front ±22° • Surround ±100° – For narrow TV stands or close seating.',
      presetStandardDesc: 'Front ±30° • Surround ±110° – Classic Dolby / ITU equilateral layout.',
      presetWideDesc: 'Front ±40° • Surround ±120° – For wide-spaced front speakers.',
      languageLabel: 'Language',
      autoLang: (n) => `Auto (${n})`,
      audioLibHeading: 'Audio Library',
      uploadBtn: '+ Upload Audio',
      uploadHint: 'Supports .wav, .mp3, .flac, .ogg up to 30 MB.',
      noFiles: 'No audio files uploaded yet.',
      deleteBtn: 'Delete',
      backHub: '← Hub',
      sine: 'Sine Wave',
      pinkNoise: 'Pink Noise',
      customAudio: 'Custom Audio',
      audioNoFile: 'Audio (no file)',
      noAudios: 'No audio files',
      testBtn: 'Test',
      stopBtn: 'Stop',
      genShortTitle: 'Frequency Generator',
      genPresets: {
        ALL_ACTIVE: 'All Active Channels',
        ALL_WITHOUT_SUB: 'All Except Subwoofer',
        P2_1: '2.1 Stereo + Sub',
        P2_0: '2.0 Front',
        P0_1_SUB: '0.1 Subwoofer Only',
        CUSTOM: 'Custom'
      },
      pauseBtn: 'Pause',
      startHzBtn: (hz) => `Start ${hz} Hz`,
      startPinkBtn: 'Start Pink Noise',
      panVirtual: 'Virtual Object',
      panSpeaker: 'Speaker Focus',
      autoOrbit: 'Auto-Orbit',
      speedSlow: 'Speed: Slow',
      speedMed: 'Speed: Medium',
      speedFast: 'Speed: Fast',
      puckDecoding: 'Decoding audio… (Pause)',
      puckPause: 'Pause Spatial Audio',
      puckStart: 'Start Spatial Audio',
      errSize: 'Error: Maximum file size is 30 MB.',
      uploading: (f) => `Uploading: ${f}...`,
      uploadOk: (f) => `Synced successfully: ${f}`,
      uploadFail: (e) => `Upload failed: ${e}`,
      netErr: (e) => `Network error: ${e}`
    },
    ES: {
      connectingHost: 'Conectando al host TV...',
      connectedTo: (h) => `Conectado a ${h}`,
      offline: 'Desconectado',
      tokenNeeded: 'Token requerido',
      connected: 'Conectado',
      disconnected: 'Sin conexión',
      tokenPrompt: 'Introduce el token de sesión (mostrado en la TV):',
      connect: 'Conectar',
      mode1Title: 'Prueba por altavoz',
      mode1Desc: 'Aísla cada canal, comprueba el cableado y elige ruido rosa, onda senoidal o pistas propias.',
      mode2Title: 'Generador de frecuencias',
      mode2Desc: 'Onda senoidal 20 Hz – 20 kHz o ruido rosa en canales elegidos y corte de subwoofer.',
      mode3Title: 'Escena sonora 2D',
      mode3Desc: 'Panner espacial en tiempo real táctil/ratón con Objeto virtual, Foco en altavoz y Auto-Órbita.',
      mode4Title: 'Modos de sala y Barrido',
      mode4DisabledDesc: 'Solo disponible en la app Android: los navegadores bloquean el micrófono por HTTP en red local.',
      masterLevelTitle: 'Nivel maestro (Protección auditiva)',
      lfeLowPassLabel: 'Paso bajo LFE (Ruido/Audio)',
      lfeDirect: 'Directo (No)',
      swap71Label: 'Invertir 7.1 Surround y Traseros (HDMI-HAL)',
      layoutAnglesLabel: 'Ángulos de ubicación',
      presetCompact: 'Estrecho (±22°)',
      presetStandard: 'Estándar (±30°)',
      presetWide: 'Amplio (±40°)',
      presetCompactDesc: 'Frontal ±22° • Surround ±100° – Para muebles estrechos o escucha cercana.',
      presetStandardDesc: 'Frontal ±30° • Surround ±110° – Disposición clásica Dolby / ITU.',
      presetWideDesc: 'Frontal ±40° • Surround ±120° – Para frontales muy separados.',
      languageLabel: 'Idioma',
      autoLang: (n) => `Automático (${n})`,
      audioLibHeading: 'Biblioteca de audio',
      uploadBtn: '+ Subir archivo',
      uploadHint: 'Compatible con .wav, .mp3, .flac, .ogg hasta 30 MB.',
      noFiles: 'Aún no hay pistas subidas.',
      deleteBtn: 'Eliminar',
      backHub: '← Hub',
      sine: 'Onda senoidal',
      pinkNoise: 'Ruido rosa',
      customAudio: 'Audio propio',
      audioNoFile: 'Audio (sin archivo)',
      noAudios: 'Sin audios',
      testBtn: 'Probar',
      stopBtn: 'Parar',
      genShortTitle: 'Generador de frecuencias',
      genPresets: {
        ALL_ACTIVE: 'Todos los canales activos',
        ALL_WITHOUT_SUB: 'Todos menos Subwoofer',
        P2_1: '2.1 Estéreo + Sub',
        P2_0: '2.0 Frontal',
        P0_1_SUB: '0.1 Solo Subwoofer',
        CUSTOM: 'Personalizado'
      },
      pauseBtn: 'Pausar',
      startHzBtn: (hz) => `Iniciar ${hz} Hz`,
      startPinkBtn: 'Iniciar ruido rosa',
      panVirtual: 'Objeto virtual',
      panSpeaker: 'Foco en altavoz',
      autoOrbit: 'Auto-Órbita',
      speedSlow: 'Velocidad: Lenta',
      speedMed: 'Velocidad: Media',
      speedFast: 'Velocidad: Rápida',
      puckDecoding: 'Decodificando audio… (Pausa)',
      puckPause: 'Pausar audio espacial',
      puckStart: 'Iniciar audio espacial',
      errSize: 'Error: El tamaño máximo es de 30 MB.',
      uploading: (f) => `Subiendo: ${f}...`,
      uploadOk: (f) => `Sincronizado correctamente: ${f}`,
      uploadFail: (e) => `Error al subir: ${e}`,
      netErr: (e) => `Error de red: ${e}`
    },
    FR: {
      connectingHost: 'Connexion à l’hôte TV...',
      connectedTo: (h) => `Connecté à ${h}`,
      offline: 'Hors ligne',
      tokenNeeded: 'Code requis',
      connected: 'Connecté',
      disconnected: 'Déconnecté',
      tokenPrompt: 'Saisissez le code session (affiché sur la TV) :',
      connect: 'Connecter',
      mode1Title: 'Test par enceinte',
      mode1Desc: 'Isolez chaque canal, vérifiez le câblage et choisissez bruit rose, sinus ou vos propres pistes.',
      mode2Title: 'Générateur de fréquences',
      mode2Desc: 'Sinusoïde 20 Hz – 20 kHz ou bruit rose sur les canaux choisis & préréglages caisson.',
      mode3Title: 'Scène sonore 2D',
      mode3Desc: 'Panner spatial temps réel tactile/souris avec Objet virtuel, Focus enceinte et Auto-Orbite.',
      mode4Title: 'Modes de pièce & Mesure',
      mode4DisabledDesc: 'Uniquement dans l’app Android : les navigateurs bloquent le micro sur HTTP en Wi-Fi local.',
      masterLevelTitle: 'Niveau général (Protection auditive)',
      lfeLowPassLabel: 'Passe-bas LFE (Bruit/Audio)',
      lfeDirect: 'Direct (Off)',
      swap71Label: 'Inverser 7.1 Surround & Arrière (HDMI-HAL)',
      layoutAnglesLabel: 'Angles de placement',
      presetCompact: 'Resserré (±22°)',
      presetStandard: 'Standard (±30°)',
      presetWide: 'Large (±40°)',
      presetCompactDesc: 'Avant ±22° • Surround ±100° – Meubles TV étroits ou écoute rapprochée.',
      presetStandardDesc: 'Avant ±30° • Surround ±110° – Disposition classique Dolby / ITU.',
      presetWideDesc: 'Avant ±40° • Surround ±120° – Enceintes frontales très espacées.',
      languageLabel: 'Langue',
      autoLang: (n) => `Automatique (${n})`,
      audioLibHeading: 'Bibliothèque audio',
      uploadBtn: '+ Envoyer fichier',
      uploadHint: 'Prend en charge .wav, .mp3, .flac, .ogg jusqu’à 30 Mo.',
      noFiles: 'Aucun fichier envoyé pour le moment.',
      deleteBtn: 'Supprimer',
      backHub: '← Hub',
      sine: 'Sinus',
      pinkNoise: 'Bruit rose',
      customAudio: 'Audio personnalisé',
      audioNoFile: 'Audio (aucun fichier)',
      noAudios: 'Aucune piste',
      testBtn: 'Tester',
      stopBtn: 'Arrêter',
      genShortTitle: 'Générateur de fréquences',
      genPresets: {
        ALL_ACTIVE: 'Tous les canaux actifs',
        ALL_WITHOUT_SUB: 'Tous sauf caisson',
        P2_1: '2.1 Stéréo + Caisson',
        P2_0: '2.0 Avant',
        P0_1_SUB: '0.1 Caisson seul',
        CUSTOM: 'Personnalisé'
      },
      pauseBtn: 'Pause',
      startHzBtn: (hz) => `Démarrer ${hz} Hz`,
      startPinkBtn: 'Démarrer bruit rose',
      panVirtual: 'Objet virtuel',
      panSpeaker: 'Focus enceinte',
      autoOrbit: 'Auto-Orbite',
      speedSlow: 'Vitesse : Lente',
      speedMed: 'Vitesse : Moyenne',
      speedFast: 'Vitesse : Rapide',
      puckDecoding: 'Décodage audio… (Pause)',
      puckPause: 'Mettre en pause',
      puckStart: 'Démarrer le son spatial',
      errSize: 'Erreur : Taille maximale 30 Mo.',
      uploading: (f) => `Envoi en cours : ${f}...`,
      uploadOk: (f) => `Synchronisé : ${f}`,
      uploadFail: (e) => `Échec de l’envoi : ${e}`,
      netErr: (e) => `Erreur réseau : ${e}`
    },
    PT: {
      connectingHost: 'Conectando ao host TV...',
      connectedTo: (h) => `Conectado a ${h}`,
      offline: 'Offline',
      tokenNeeded: 'Token necessário',
      connected: 'Conectado',
      disconnected: 'Desconectado',
      tokenPrompt: 'Digite o código da sessão (mostrado na TV):',
      connect: 'Conectar',
      mode1Title: 'Teste por Caixa',
      mode1Desc: 'Isole cada canal, confira a fiação e escolha ruído rosa, senoide ou faixas próprias.',
      mode2Title: 'Gerador de Frequências',
      mode2Desc: 'Senoide 20 Hz – 20 kHz ou ruído rosa nos canais selecionados e cortes de subwoofer.',
      mode3Title: 'Palco sonoro 2D',
      mode3Desc: 'Panner espacial em tempo real por toque/mouse com Objeto virtual, Foco na caixa e Auto-Órbita.',
      mode4Title: 'Modos de Sala & Varredura',
      mode4DisabledDesc: 'Apenas no app Android — navegadores bloqueiam o microfone via HTTP na rede local.',
      masterLevelTitle: 'Nível Geral (Proteção Auditiva)',
      lfeLowPassLabel: 'Filtro LFE (Ruído/Áudio)',
      lfeDirect: 'Direto (Off)',
      swap71Label: 'Inverter 7.1 Surround & Traseira (HDMI-HAL)',
      layoutAnglesLabel: 'Ângulos de posicionamento',
      presetCompact: 'Estreito (±22°)',
      presetStandard: 'Padrão (±30°)',
      presetWide: 'Amplo (±40°)',
      presetCompactDesc: 'Frente ±22° • Surround ±100° – Ideal para racks estreitos.',
      presetStandardDesc: 'Frente ±30° • Surround ±110° – Disposição clássica Dolby / ITU.',
      presetWideDesc: 'Frente ±40° • Surround ±120° – Para caixas frontais bem afastadas.',
      languageLabel: 'Idioma',
      autoLang: (n) => `Automático (${n})`,
      audioLibHeading: 'Biblioteca de áudio',
      uploadBtn: '+ Enviar arquivo',
      uploadHint: 'Aceita .wav, .mp3, .flac, .ogg até 30 MB.',
      noFiles: 'Nenhum arquivo enviado ainda.',
      deleteBtn: 'Excluir',
      backHub: '← Hub',
      sine: 'Onda senoidal',
      pinkNoise: 'Ruído rosa',
      customAudio: 'Áudio personalizado',
      audioNoFile: 'Áudio (sem arquivo)',
      noAudios: 'Sem áudios',
      testBtn: 'Testar',
      stopBtn: 'Parar',
      genShortTitle: 'Gerador de Frequências',
      genPresets: {
        ALL_ACTIVE: 'Todos os canais ativos',
        ALL_WITHOUT_SUB: 'Todos exceto Subwoofer',
        P2_1: '2.1 Estéreo + Sub',
        P2_0: '2.0 Frontal',
        P0_1_SUB: '0.1 Apenas Subwoofer',
        CUSTOM: 'Personalizado'
      },
      pauseBtn: 'Pausar',
      startHzBtn: (hz) => `Iniciar ${hz} Hz`,
      startPinkBtn: 'Iniciar ruído rosa',
      panVirtual: 'Objeto virtual',
      panSpeaker: 'Foco na caixa',
      autoOrbit: 'Auto-Órbita',
      speedSlow: 'Velocidade: Lenta',
      speedMed: 'Velocidade: Média',
      speedFast: 'Velocidade: Rápida',
      puckDecoding: 'Decodificando áudio… (Pausa)',
      puckPause: 'Pausar áudio espacial',
      puckStart: 'Iniciar áudio espacial',
      errSize: 'Erro: Tamanho máximo é 30 MB.',
      uploading: (f) => `Enviando: ${f}...`,
      uploadOk: (f) => `Sincronizado com sucesso: ${f}`,
      uploadFail: (e) => `Falha no envio: ${e}`,
      netErr: (e) => `Erro de rede: ${e}`
    },
    JA: {
      connectingHost: 'TV ホストに接続中...',
      connectedTo: (h) => `${h} に接続中`,
      offline: 'オフライン',
      tokenNeeded: 'トークン必要',
      connected: '接続済み',
      disconnected: '切断されました',
      tokenPrompt: 'セッショントークンを入力（TV画面に表示）:',
      connect: '接続',
      mode1Title: '単体スピーカーテスト',
      mode1Desc: '各チャンネルを個別に鳴らし、配線チェックやピンクノイズ・サイン波・音声切替を行えます。',
      mode2Title: '周波数ジェネレーター',
      mode2Desc: '20 Hz 〜 20 kHz のサイン波・ピンクノイズおよびサブウーファープリセット。',
      mode3Title: '2D 空間サウンドステージ',
      mode3Desc: 'タッチ・マウス操作対応のリアルタイム空間パンナー＆自動オービット。',
      mode4Title: 'ルーム音響・自動測定',
      mode4DisabledDesc: 'Android アプリ専用機能です（ブラウザはローカル HTTP 環境でマイク利用をブロックします）。',
      masterLevelTitle: 'マスター出力レベル（聴覚保護）',
      lfeLowPassLabel: 'LFE ローパス（ノイズ/音楽）',
      lfeDirect: 'スルー (無効)',
      swap71Label: '7.1 サラウンドとバックを入替 (HDMI-HAL)',
      layoutAnglesLabel: 'スピーカー配置角度',
      presetCompact: '狭角 (±22°)',
      presetStandard: '標準 (±30°)',
      presetWide: '広角 (±40°)',
      presetCompactDesc: 'フロント ±22° • サラウンド ±100° – コンパクト配置向け。',
      presetStandardDesc: 'フロント ±30° • サラウンド ±110° – Dolby / ITU 標準配置。',
      presetWideDesc: 'フロント ±40° • サラウンド ±120° – 広角・大部屋向け。',
      languageLabel: '言語 / Language',
      autoLang: (n) => `自動 (${n})`,
      audioLibHeading: '音声ライブラリ',
      uploadBtn: '+ 音声を追加',
      uploadHint: '.wav / .mp3 / .flac / .ogg 対応（最大 30 MB）',
      noFiles: 'まだ音声トラックがありません。',
      deleteBtn: '削除',
      backHub: '← ハブ',
      sine: 'サイン波',
      pinkNoise: 'ピンクノイズ',
      customAudio: 'カスタム音声',
      audioNoFile: '音声（ファイル未選択）',
      noAudios: '音声ファイルなし',
      testBtn: 'テスト',
      stopBtn: '停止',
      genShortTitle: '周波数発信器',
      genPresets: {
        ALL_ACTIVE: '有効な全チャンネル',
        ALL_WITHOUT_SUB: 'サブウーファー以外の全て',
        P2_1: '2.1 ステレオ + サブ',
        P2_0: '2.0 フロント',
        P0_1_SUB: '0.1 サブ単体',
        CUSTOM: 'カスタム'
      },
      pauseBtn: '一時停止',
      startHzBtn: (hz) => `${hz} Hz を再生`,
      startPinkBtn: 'ピンクノイズを再生',
      panVirtual: 'バーチャル音源',
      panSpeaker: 'スピーカーフォーカス',
      autoOrbit: '自動オービット',
      speedSlow: '速度: 低速',
      speedMed: '速度: 標準',
      speedFast: '速度: 高速',
      puckDecoding: 'デコード中… (一時停止)',
      puckPause: '空間オーディオを一時停止',
      puckStart: '空間オーディオを再生',
      errSize: 'エラー: 最大ファイルサイズは 30 MB です。',
      uploading: (f) => `送信中: ${f}...`,
      uploadOk: (f) => `同期完了: ${f}`,
      uploadFail: (e) => `送信失敗: ${e}`,
      netErr: (e) => `ネットワークエラー: ${e}`
    }
  };

  function resolveBrowserLang() {
    const code = (navigator.language || 'en').slice(0, 2).toUpperCase();
    return I18N[code] ? code : 'EN';
  }

  function getActiveLangCode() {
    const raw = (appState && appState.appLanguage) || 'AUTO';
    if (raw === 'AUTO') return resolveBrowserLang();
    return I18N[raw] ? raw : 'EN';
  }

  function t() {
    return I18N[getActiveLangCode()] || I18N.EN;
  }

  function applyStaticI18n() {
    const tr = t();
    document.documentElement.lang = getActiveLangCode().toLowerCase();
    document.querySelectorAll('[data-i18n]').forEach((node) => {
      const key = node.getAttribute('data-i18n');
      if (key && typeof tr[key] === 'string') {
        node.textContent = tr[key];
      }
    });
    const autoOpt = el('langAutoOption');
    if (autoOpt) {
      const resolved = resolveBrowserLang();
      autoOpt.textContent = tr.autoLang(NATIVE_NAMES[resolved] || 'English');
    }
    const langSel = el('langSelect');
    if (langSel && appState) {
      langSel.value = appState.appLanguage || 'AUTO';
    }
    const uploadStatus = el('uploadStatus');
    if (uploadStatus && !uploadStatus.dataset.customStatus) {
      uploadStatus.textContent = tr.uploadHint;
    }
  }

  const LAYOUT_ANGLES = {
    COMPACT: { FL: -22, C: 0, FR: 22, SL: -100, SR: 100, SBL: -140, SBR: 140 },
    STANDARD: { FL: -30, C: 0, FR: 30, SL: -110, SR: 110, SBL: -150, SBR: 150 },
    WIDE: { FL: -40, C: 0, FR: 40, SL: -120, SR: 120, SBL: -155, SBR: 155 }
  };

  function getChannelPos(ch) {
    if (ch === 'LFE') return { label: 'SUB', x: -0.30, y: 0.46, lfe: true };
    const labels = { FL: 'FL', C: 'C', FR: 'FR', SL: 'SL', SR: 'SR', SBL: 'SBL', SBR: 'SBR' };
    const presetKey = (appState && appState.speakerLayoutPreset) || 'STANDARD';
    const angles = LAYOUT_ANGLES[presetKey] || LAYOUT_ANGLES.STANDARD;
    const rad = ((angles[ch] || 0) * Math.PI) / 180;
    return { label: labels[ch] || ch, x: 0.86 * Math.sin(rad), y: 0.86 * Math.cos(rad) };
  }

  const CHANNEL_POSITIONS = {
    FL: { label: 'FL', x: -0.43, y: 0.745 },
    C: { label: 'C', x: 0.0, y: 0.86 },
    FR: { label: 'FR', x: 0.43, y: 0.745 },
    SL: { label: 'SL', x: -0.808, y: -0.294 },
    SR: { label: 'SR', x: 0.808, y: -0.294 },
    SBL: { label: 'SBL', x: -0.43, y: -0.745 },
    SBR: { label: 'SBR', x: 0.43, y: -0.745 },
    LFE: { label: 'SUB', x: -0.30, y: 0.46, lfe: true }
  };

  const SETUP_CHANNELS = {
    '2.0': ['FL', 'FR'],
    '2.1': ['FL', 'FR', 'LFE'],
    '3.1': ['FL', 'C', 'FR', 'LFE'],
    '5.0': ['FL', 'C', 'FR', 'SL', 'SR'],
    '5.1': ['FL', 'C', 'FR', 'LFE', 'SL', 'SR'],
    '7.1': ['FL', 'C', 'FR', 'LFE', 'SL', 'SR', 'SBL', 'SBR']
  };

  const SETUP_FROM_ENUM = {
    S2_0: '2.0',
    S2_1: '2.1',
    S3_1: '3.1',
    S5_0: '5.0',
    S5_1: '5.1',
    S7_1: '7.1'
  };

  const PRESET_DEFS = [
    { id: 'ALL_ACTIVE' },
    { id: 'ALL_WITHOUT_SUB', needSub: true },
    { id: 'P2_1', needSub: true },
    { id: 'P2_0' },
    { id: 'P0_1_SUB', needSub: true },
    { id: 'CUSTOM' }
  ];

  const params = new URLSearchParams(window.location.search);
  let sessionToken = (params.get('token') || localStorage.getItem('sl_token') || '').trim().toUpperCase();
  let ws = null;
  let appState = null;
  let draggingPuck = false;
  let lastDragSent = 0;

  const el = (id) => document.getElementById(id);

  function sendWs(payload) {
    if (ws && ws.readyState === WebSocket.OPEN) {
      ws.send(JSON.stringify(payload));
    }
  }

  function updateConnectionHeader(connected) {
    const tr = t();
    if (connected) {
      el('connBadge').classList.add('online');
      el('connText').textContent = tr.connected;
      el('connSubtitle').textContent = tr.connectedTo(window.location.host);
    } else if (!sessionToken) {
      el('connBadge').classList.remove('online');
      el('connText').textContent = tr.tokenNeeded;
      el('connSubtitle').textContent = tr.connectingHost;
    } else {
      el('connBadge').classList.remove('online');
      el('connText').textContent = tr.offline;
      el('connSubtitle').textContent = tr.connectingHost;
    }
  }

  function connect() {
    applyStaticI18n();
    if (!sessionToken) {
      el('tokenBanner').classList.remove('hidden');
      updateConnectionHeader(false);
      return;
    }
    localStorage.setItem('sl_token', sessionToken);
    el('tokenBanner').classList.add('hidden');
    const proto = window.location.protocol === 'https:' ? 'wss:' : 'ws:';
    const wsUrl = `${proto}//${window.location.host}/ws?token=${encodeURIComponent(sessionToken)}&client=browser`;
    ws = new WebSocket(wsUrl);

    ws.onopen = () => {
      updateConnectionHeader(true);
    };

    ws.onclose = () => {
      updateConnectionHeader(false);
      el('connText').textContent = t().disconnected;
      setTimeout(connect, 2000);
    };

    ws.onmessage = (evt) => {
      try {
        const msg = JSON.parse(evt.data);
        if (msg.type === 'STATE_SYNC' && msg.state) {
          appState = msg.state;
          render();
        } else if (msg.type === 'LIBRARY_UPDATED' && msg.audioLibrary && appState) {
          appState.audioLibrary = msg.audioLibrary;
          renderLibrary();
        }
      } catch (_) {}
    };
  }

  function render() {
    if (!appState) return;

    applyStaticI18n();
    updateConnectionHeader(ws && ws.readyState === WebSocket.OPEN);

    const tr = t();
    const setupCode = SETUP_FROM_ENUM[appState.setup] || '5.1';
    el('setupSelect').value = setupCode;

    // Switch Hub-and-Spoke View (ROOM_MEASUREMENT stays on Hub in browser)
    const rawMode = appState.activeMode || 'HUB';
    const mode = rawMode === 'ROOM_MEASUREMENT' ? 'HUB' : rawMode;
    el('viewHub').classList.toggle('hidden', mode !== 'HUB');
    el('viewSingle').classList.toggle('hidden', mode !== 'SINGLE_SPEAKER');
    el('viewGenerator').classList.toggle('hidden', mode !== 'GENERATOR');
    el('viewPuck').classList.toggle('hidden', mode !== 'PUCK_2D');

    // Master Gain, LFE Low-Pass, 7.1 Swap & Layout Angle Preset
    const gainDb = Math.round(appState.masterGainDb ?? -12);
    el('masterGainSlider').value = gainDb;
    el('masterGainLabel').textContent = `${gainDb} dBFS`;
    const lfeSel = el('lfeLowPassSelect');
    if (lfeSel) {
      lfeSel.value = String(appState.lfeLowPassHz ?? 120);
    }
    el('swap71Toggle').checked = !!appState.swap71SurroundBack;
    const presetKey = appState.speakerLayoutPreset || 'STANDARD';
    el('layoutPresetSelect').value = presetKey;
    const descMap = {
      COMPACT: tr.presetCompactDesc,
      STANDARD: tr.presetStandardDesc,
      WIDE: tr.presetWideDesc
    };
    el('layoutPresetDesc').textContent = descMap[presetKey] || tr.presetStandardDesc;

    renderLibrary();
    renderSingleSpeaker(setupCode);
    renderGenerator(setupCode);
    renderPuck(setupCode);
  }

  function renderLibrary() {
    const tr = t();
    const tracks = appState.audioLibrary || [];
    el('libCount').textContent = tracks.length;
    const list = el('audioLibraryList');
    list.innerHTML = '';
    if (tracks.length === 0) {
      list.innerHTML = `<div class="muted">${escapeHtml(tr.noFiles)}</div>`;
      return;
    }
    tracks.forEach((track) => {
      const row = document.createElement('div');
      row.className = 'track-row';
      const sec = Math.max(0, Math.round((track.durationMs || 0) / 1000));
      const mb = ((track.sizeBytes || 0) / (1024 * 1024)).toFixed(1);
      row.innerHTML = `
        <div>
          <strong>${escapeHtml(track.name)}</strong>
          <div class="muted mono">${sec}s • ${track.sampleRate} Hz • ${mb} MB</div>
        </div>
        <button class="btn-tonal" data-del="${track.id}">${escapeHtml(tr.deleteBtn)}</button>
      `;
      row.querySelector('[data-del]').onclick = async () => {
        await fetch(`/api/audio/${encodeURIComponent(track.id)}?token=${encodeURIComponent(sessionToken)}`, {
          method: 'DELETE'
        });
      };
      list.appendChild(row);
    });
  }

  function renderSingleSpeaker(setupCode) {
    const tr = t();
    const channels = SETUP_CHANNELS[setupCode] || SETUP_CHANNELS['5.1'];
    const ss = appState.singleSpeaker || {};
    const list = el('singleSpeakerList');
    list.innerHTML = '';

    const tracks = appState.audioLibrary || [];
    channels.forEach((ch) => {
      const isPlaying = ss.playing && ss.activeChannel === ch;
      const cfg = (ss.channelConfigs && ss.channelConfigs[ch]) || { sourceType: 'PINK_NOISE', frequencyHz: 400 };
      const activeAudioId = cfg.audioId || (tracks[0] && tracks[0].id) || '';
      const activeTrack = tracks.find((item) => item.id === activeAudioId);
      const srcSub = cfg.sourceType === 'SINE'
        ? `${tr.sine} ${cfg.frequencyHz} Hz`
        : cfg.sourceType === 'CUSTOM_AUDIO'
          ? (activeTrack ? activeTrack.name : tr.audioNoFile)
          : tr.pinkNoise;
      const trackOptions = tracks.map((item) =>
        `<option value="${escapeHtml(item.id)}" ${item.id === activeAudioId ? 'selected' : ''}>${escapeHtml(item.name)}</option>`
      ).join('');
      const row = document.createElement('div');
      row.className = `speaker-row${isPlaying ? ' active' : ''}`;
      row.innerHTML = `
        <div class="speaker-left">
          <div class="speaker-code">${getChannelPos(ch).label}</div>
          <div>
            <strong>${ch}</strong>
            <div class="muted mono">${escapeHtml(srcSub)}</div>
          </div>
        </div>
        <div style="display:flex;gap:8px;align-items:center;flex-wrap:wrap;justify-content:flex-end;">
          <select class="pill-select src-select">
            <option value="PINK_NOISE" ${cfg.sourceType === 'PINK_NOISE' ? 'selected' : ''}>${escapeHtml(tr.pinkNoise)}</option>
            <option value="SINE" ${cfg.sourceType === 'SINE' ? 'selected' : ''}>${escapeHtml(tr.sine)}</option>
            <option value="CUSTOM_AUDIO" ${cfg.sourceType === 'CUSTOM_AUDIO' ? 'selected' : ''}>${escapeHtml(tr.customAudio)}</option>
          </select>
          ${cfg.sourceType === 'CUSTOM_AUDIO' && tracks.length > 0 ? `<select class="pill-select audio-select">${trackOptions}</select>` : ''}
          <button class="btn-primary ${isPlaying ? 'playing' : ''}">${escapeHtml(isPlaying ? tr.stopBtn : tr.testBtn)}</button>
        </div>
      `;
      row.querySelector('.src-select').onchange = (e) => {
        sendWs({
          type: 'UPDATE_CHANNEL_CONFIG',
          channelId: ch,
          sourceType: e.target.value,
          frequencyHz: cfg.frequencyHz || 400,
          audioId: e.target.value === 'CUSTOM_AUDIO' ? activeAudioId : undefined
        });
      };
      const audioSel = row.querySelector('.audio-select');
      if (audioSel) {
        audioSel.onchange = (e) => {
          sendWs({
            type: 'UPDATE_CHANNEL_CONFIG',
            channelId: ch,
            sourceType: 'CUSTOM_AUDIO',
            frequencyHz: cfg.frequencyHz || 400,
            audioId: e.target.value
          });
        };
      }
      row.querySelector('button').onclick = () => {
        sendWs({
          type: 'PLAY_SINGLE_SPEAKER',
          channelId: ch,
          playing: !isPlaying,
          sourceType: cfg.sourceType,
          frequencyHz: cfg.frequencyHz || 400
        });
      };
      list.appendChild(row);
    });
  }

  function renderGenerator(setupCode) {
    const tr = t();
    const channels = SETUP_CHANNELS[setupCode] || SETUP_CHANNELS['5.1'];
    const hasSub = channels.includes('LFE');
    const gen = appState.generator || {};

    const presetsEl = el('genPresets');
    presetsEl.innerHTML = '';
    PRESET_DEFS.filter((p) => !p.needSub || hasSub).forEach((p) => {
      const b = document.createElement('button');
      b.className = `chip-btn${gen.preset === p.id ? ' active' : ''}`;
      b.textContent = tr.genPresets[p.id] || p.id;
      b.onclick = () => sendWs({ type: 'UPDATE_GENERATOR', preset: p.id });
      presetsEl.appendChild(b);
    });

    const customEl = el('genCustomChannels');
    customEl.innerHTML = '';
    if (gen.preset === 'CUSTOM') {
      customEl.classList.remove('hidden');
      const selected = new Set(gen.customChannels || []);
      channels.forEach((ch) => {
        const b = document.createElement('button');
        const active = selected.has(ch);
        b.className = `chip-btn${active ? ' active' : ''}`;
        b.textContent = CHANNEL_POSITIONS[ch].label;
        b.onclick = () => {
          const next = new Set(selected);
          if (active) next.delete(ch);
          else next.add(ch);
          sendWs({ type: 'UPDATE_GENERATOR', customChannels: Array.from(next) });
        };
        customEl.appendChild(b);
      });
    } else {
      customEl.classList.add('hidden');
    }

    document.querySelectorAll('#genSignals .seg-btn').forEach((btn) => {
      btn.classList.toggle('active', btn.dataset.sig === (gen.signalType || 'SINE'));
    });

    const isPink = (gen.signalType || 'SINE') === 'PINK_NOISE';
    const freqBox = document.querySelector('#viewGenerator .freq-box');
    if (freqBox) freqBox.classList.toggle('hidden', isPink);

    const hz = gen.frequencyHz || 400;
    el('genFreqSlider').value = hz;
    el('genFreqDisplay').textContent = isPink ? 'PINK' : `${hz} Hz`;

    const playBtn = el('genPlayBtn');
    playBtn.textContent = gen.playing
      ? tr.pauseBtn
      : (gen.signalType === 'PINK_NOISE' ? tr.startPinkBtn : tr.startHzBtn(hz));
    playBtn.classList.toggle('playing', !!gen.playing);
  }

  function renderPuck(setupCode) {
    const tr = t();
    const puck = appState.puck || {};
    const channels = SETUP_CHANNELS[setupCode] || SETUP_CHANNELS['5.1'];
    const hasSub = channels.includes('LFE');

    el('puckCoords').textContent = `X:${Math.round((puck.x || 0) * 100)} Y:${Math.round((puck.y || 0) * 100)}`;
    el('puckSubToggle').parentElement.style.display = hasSub ? 'flex' : 'none';
    el('puckSubToggle').checked = !!puck.subwooferEnabled;
    el('puckOrbitBtn').classList.toggle('active', !!puck.autoOrbit);
    const orbitSpeedEl = el('puckOrbitSpeeds');
    orbitSpeedEl.classList.toggle('hidden', !puck.autoOrbit);
    const curSpeed = puck.orbitSpeed || 'MEDIUM';
    orbitSpeedEl.querySelectorAll('.seg-btn').forEach((btn) => {
      btn.classList.toggle('active', btn.dataset.speed === curSpeed);
    });
    el('puckSourceSelect').value = puck.sourceType || 'PINK_NOISE';

    const tracks = appState.audioLibrary || [];
    const puckAudioSel = el('puckAudioSelect');
    if (puck.sourceType === 'CUSTOM_AUDIO') {
      puckAudioSel.classList.remove('hidden');
      if (tracks.length === 0) {
        puckAudioSel.innerHTML = `<option value="">${escapeHtml(tr.noAudios)}</option>`;
      } else {
        const curId = puck.audioId || tracks[0].id;
        puckAudioSel.innerHTML = tracks.map((item) =>
          `<option value="${escapeHtml(item.id)}" ${item.id === curId ? 'selected' : ''}>${escapeHtml(item.name)}</option>`
        ).join('');
        puckAudioSel.value = curId;
      }
    } else {
      puckAudioSel.classList.add('hidden');
    }

    document.querySelectorAll('#puckPanning .seg-btn').forEach((btn) => {
      btn.classList.toggle('active', btn.dataset.pan === (puck.panningMode || 'SWEET_SPOT_VBAP'));
    });

    const playBtn = el('puckPlayBtn');
    playBtn.textContent = puck.playing
      ? (appState.loadingAudioTrackId ? tr.puckDecoding : tr.puckPause)
      : tr.puckStart;
    playBtn.classList.toggle('playing', !!puck.playing);

    // Live meters
    const metersEl = el('puckMeters');
    metersEl.innerHTML = '';
    const gains = puck.channelGains || {};
    channels.forEach((ch) => {
      const g = ch === 'LFE' && !puck.subwooferEnabled ? 0 : (gains[ch] || 0);
      const db = g <= 0.001 ? '-∞' : (20 * Math.log10(g)).toFixed(1);
      const item = document.createElement('div');
      item.className = 'meter-item';
      item.innerHTML = `<div class="muted mono">${CHANNEL_POSITIONS[ch].label}</div><strong class="mono" style="color:var(--primary)">${db} dB</strong>`;
      metersEl.appendChild(item);
    });

    drawPuckCanvas(channels, puck);
  }

  function drawPuckCanvas(channels, puck) {
    const canvas = el('puckCanvas');
    const ctx = canvas.getContext('2d');
    const w = canvas.width;
    const h = canvas.height;
    const cx = w / 2;
    const cy = h / 2;
    const half = w * 0.38;

    ctx.clearRect(0, 0, w, h);

    [0.42, 0.29, 0.16].forEach((r, idx) => {
      ctx.beginPath();
      ctx.arc(cx, cy, w * r, 0, Math.PI * 2);
      ctx.strokeStyle = idx === 0 ? 'rgba(62,73,72,0.55)' : 'rgba(77,218,213,0.22)';
      ctx.lineWidth = 2;
      ctx.stroke();
    });

    ctx.beginPath();
    ctx.arc(cx, cy, 14, 0, Math.PI * 2);
    ctx.fillStyle = '#2E3635';
    ctx.fill();
    ctx.beginPath();
    ctx.arc(cx, cy, 5, 0, Math.PI * 2);
    ctx.fillStyle = '#4DDAD5';
    ctx.fill();

    const gains = puck.channelGains || {};
    channels.forEach((ch) => {
      const info = getChannelPos(ch);
      const sx = cx + info.x * half;
      const sy = cy - info.y * half;
      const g = ch === 'LFE' && !puck.subwooferEnabled ? 0 : (gains[ch] || 0);
      const pct = Math.round(g * 100);

      ctx.fillStyle = info.lfe
        ? (puck.subwooferEnabled ? '#FFB77B' : '#2E3635')
        : (g > 0.35 ? '#004F4F' : '#2E3635');
      roundRect(ctx, sx - 26, sy - 12, 52, 24, 8);
      ctx.fill();

      ctx.fillStyle = info.lfe ? '#4C2700' : '#DEE4E3';
      ctx.font = 'bold 11px monospace';
      ctx.textAlign = 'center';
      ctx.textBaseline = 'middle';
      ctx.fillText(`${info.label} ${pct}%`, sx, sy);
    });

    const px = cx + (puck.x || 0) * half;
    const py = cy - (puck.y || 0) * half;

    if (puck.playing) {
      ctx.beginPath();
      ctx.arc(px, py, 28, 0, Math.PI * 2);
      ctx.fillStyle = 'rgba(77,218,213,0.22)';
      ctx.fill();
    }
    ctx.beginPath();
    ctx.arc(px, py, 17, 0, Math.PI * 2);
    ctx.fillStyle = puck.playing ? '#4DDAD5' : '#2E3635';
    ctx.fill();
    ctx.lineWidth = 4;
    ctx.strokeStyle = '#004F4F';
    ctx.stroke();
  }

  function roundRect(ctx, x, y, w, h, r) {
    ctx.beginPath();
    ctx.moveTo(x + r, y);
    ctx.arcTo(x + w, y, x + w, y + h, r);
    ctx.arcTo(x + w, y + h, x, y + h, r);
    ctx.arcTo(x, y + h, x, y, r);
    ctx.arcTo(x, y, x + w, y, r);
    ctx.closePath();
  }

  function escapeHtml(str) {
    return String(str).replace(/[&<>"']/g, (c) => ({
      '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;'
    }[c]));
  }

  // Event bindings
  el('tokenConnectBtn').onclick = () => {
    sessionToken = el('tokenInput').value.trim().toUpperCase();
    connect();
  };

  el('setupSelect').onchange = (e) => {
    sendWs({ type: 'SET_SETUP', setup: e.target.value });
  };

  el('langSelect').onchange = (e) => {
    const lang = e.target.value;
    if (appState) {
      appState.appLanguage = lang;
      render();
    }
    sendWs({ type: 'SET_LANGUAGE', appLanguage: lang });
  };

  document.querySelectorAll('.mode-card[data-mode]').forEach((btn) => {
    btn.onclick = () => sendWs({ type: 'SET_MODE', mode: btn.dataset.mode });
  });

  document.querySelectorAll('.back-hub').forEach((btn) => {
    btn.onclick = () => sendWs({ type: 'SET_MODE', mode: 'HUB' });
  });

  el('masterGainSlider').oninput = (e) => {
    const db = Number(e.target.value);
    el('masterGainLabel').textContent = `${db} dBFS`;
    sendWs({ type: 'SET_MASTER_GAIN', masterGainDb: db });
  };

  const lfeSelEl = el('lfeLowPassSelect');
  if (lfeSelEl) {
    lfeSelEl.onchange = (e) => {
      sendWs({ type: 'SET_LFE_LOW_PASS', lfeLowPassHz: Number(e.target.value) });
    };
  }

  el('swap71Toggle').onchange = (e) => {
    sendWs({ type: 'SET_SWAP_71_SURROUND_BACK', swap71SurroundBack: e.target.checked });
  };

  el('layoutPresetSelect').onchange = (e) => {
    sendWs({ type: 'SET_SPEAKER_LAYOUT_PRESET', speakerLayoutPreset: e.target.value });
  };

  el('audioUploadInput').onchange = async (e) => {
    const tr = t();
    const file = e.target.files[0];
    if (!file) return;
    const statusEl = el('uploadStatus');
    statusEl.dataset.customStatus = '1';
    if (file.size > 30 * 1024 * 1024) {
      statusEl.textContent = tr.errSize;
      return;
    }
    statusEl.textContent = tr.uploading(file.name);
    const formData = new FormData();
    formData.append('file', file, file.name);
    try {
      const resp = await fetch(`/api/audio/upload?token=${encodeURIComponent(sessionToken)}`, {
        method: 'POST',
        body: formData
      });
      if (resp.ok) {
        statusEl.textContent = tr.uploadOk(file.name);
      } else {
        const err = await resp.text();
        statusEl.textContent = tr.uploadFail(err);
      }
    } catch (err) {
      statusEl.textContent = tr.netErr(err.message);
    }
    e.target.value = '';
  };

  document.querySelectorAll('#genSignals .seg-btn').forEach((btn) => {
    btn.onclick = () => sendWs({ type: 'UPDATE_GENERATOR', signalType: btn.dataset.sig });
  });

  el('genFreqSlider').oninput = (e) => {
    sendWs({ type: 'UPDATE_GENERATOR', signalType: 'SINE', frequencyHz: Number(e.target.value) });
  };

  document.querySelectorAll('.gen-step').forEach((btn) => {
    btn.onclick = () => {
      const cur = (appState && appState.generator && appState.generator.frequencyHz) || 400;
      const next = Math.max(20, Math.min(20000, cur + Number(btn.dataset.delta)));
      sendWs({ type: 'UPDATE_GENERATOR', signalType: 'SINE', frequencyHz: next });
    };
  });

  document.querySelectorAll('.gen-preset-hz').forEach((btn) => {
    btn.onclick = () => sendWs({ type: 'UPDATE_GENERATOR', signalType: 'SINE', frequencyHz: Number(btn.dataset.hz) });
  });

  el('genPlayBtn').onclick = () => {
    const curPlay = !!(appState && appState.generator && appState.generator.playing);
    sendWs({ type: 'UPDATE_GENERATOR', playing: !curPlay });
  };

  document.querySelectorAll('#puckPanning .seg-btn').forEach((btn) => {
    btn.onclick = () => sendWs({ type: 'UPDATE_PUCK', panningMode: btn.dataset.pan });
  });

  el('puckSubToggle').onchange = (e) => {
    sendWs({ type: 'UPDATE_PUCK', subwooferEnabled: e.target.checked });
  };

  el('puckOrbitBtn').onclick = () => {
    const curOrbit = !!(appState && appState.puck && appState.puck.autoOrbit);
    sendWs({ type: 'UPDATE_PUCK', autoOrbit: !curOrbit, playing: !curOrbit ? true : undefined });
  };

  document.querySelectorAll('#puckOrbitSpeeds .seg-btn').forEach((btn) => {
    btn.onclick = () => sendWs({ type: 'UPDATE_PUCK', autoOrbit: true, orbitSpeed: btn.dataset.speed });
  });

  el('puckSourceSelect').onchange = (e) => {
    const src = e.target.value;
    const curAudio = appState && appState.puck && appState.puck.audioId;
    const firstId = appState && appState.audioLibrary && appState.audioLibrary[0] && appState.audioLibrary[0].id;
    sendWs({ type: 'UPDATE_PUCK', sourceType: src, audioId: src === 'CUSTOM_AUDIO' ? (curAudio || firstId) : undefined });
  };

  el('puckAudioSelect').onchange = (e) => {
    const id = e.target.value;
    if (id) {
      sendWs({ type: 'UPDATE_PUCK', sourceType: 'CUSTOM_AUDIO', audioId: id });
    }
  };

  el('puckPlayBtn').onclick = () => {
    const curPlay = !!(appState && appState.puck && appState.puck.playing);
    sendWs({ type: 'UPDATE_PUCK', playing: !curPlay });
  };

  // Interactive 2D Puck Canvas pointer handling (throttled ~40 Hz)
  const canvas = el('puckCanvas');
  function sendPointerPos(clientX, clientY) {
    const now = performance.now();
    if (now - lastDragSent < 25) return;
    lastDragSent = now;
    const rect = canvas.getBoundingClientRect();
    const half = rect.width * 0.38;
    const nx = Math.max(-1, Math.min(1, ((clientX - rect.left) - rect.width / 2) / half));
    const ny = Math.max(-1, Math.min(1, (rect.height / 2 - (clientY - rect.top)) / half));
    sendWs({ type: 'UPDATE_PUCK', x: nx, y: ny, autoOrbit: false });
  }

  canvas.addEventListener('pointerdown', (e) => {
    draggingPuck = true;
    canvas.setPointerCapture(e.pointerId);
    sendPointerPos(e.clientX, e.clientY);
  });
  canvas.addEventListener('pointermove', (e) => {
    if (draggingPuck) sendPointerPos(e.clientX, e.clientY);
  });
  canvas.addEventListener('pointerup', () => { draggingPuck = false; });
  canvas.addEventListener('pointercancel', () => { draggingPuck = false; });

  connect();
})();
