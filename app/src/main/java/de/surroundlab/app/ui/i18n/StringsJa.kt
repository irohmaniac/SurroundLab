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

val StringsJa = AppStrings(
    langCode = "ja",
    back = "戻る",
    close = "閉じる",
    ok = "OK",
    apply = "適用",
    cancel = "キャンセル",
    activeTag = "有効",
    ready = "準備完了",
    testBtn = "テスト",
    stopBtn = "停止",
    startBtn = "再生",
    pauseBtn = "一時停止",
    deleteBtn = "削除",
    settingsLabel = "設定",
    homeHubBtn = "ホームハブ",

    languageSectionTitle = "言語 / Language",
    languageAutoLabel = "自動",
    languageAutoHint = { resolvedName -> "自動 ($resolvedName)" },

    setupSubtitle = { setup ->
        when (setup) {
            SpeakerSetup.S2_0 -> "フロント L / R"
            SpeakerSetup.S2_1 -> "ステレオ + サブウーファー"
            SpeakerSetup.S3_1 -> "フロント + センター + サブ"
            SpeakerSetup.S5_0 -> "5 サテライトチャンネル"
            SpeakerSetup.S5_1 -> "6 ディスクリートチャンネル"
            SpeakerSetup.S7_1 -> "8 ディスクリートチャンネル"
        }
    },
    channelName = { ch -> ch.displayName },
    sourceLabel = { src ->
        when (src) {
            SignalSourceType.PINK_NOISE -> "ピンクノイズ"
            SignalSourceType.SINE -> "サイン波"
            SignalSourceType.CUSTOM_AUDIO -> "カスタム音声"
        }
    },
    sourceShortLabel = { src ->
        when (src) {
            SignalSourceType.PINK_NOISE -> "ピンクノイズ"
            SignalSourceType.SINE -> "サイン波"
            SignalSourceType.CUSTOM_AUDIO -> "音声ファイル"
        }
    },
    generatorPresetLabel = { preset ->
        when (preset) {
            GeneratorPreset.ALL_ACTIVE -> "有効な全チャンネル"
            GeneratorPreset.P2_1 -> "2.1 ステレオ + サブ"
            GeneratorPreset.P2_0 -> "2.0 フロント"
            GeneratorPreset.P0_1_SUB -> "0.1 サブウーファー単体"
            GeneratorPreset.CUSTOM -> "カスタム"
        }
    },
    panningModeLabel = { mode ->
        when (mode) {
            PanningMode.SWEET_SPOT_VBAP -> "バーチャル音源"
            PanningMode.ROOM_DISTANCE -> "スピーカーフォーカス"
        }
    },
    panningModeHint = { mode ->
        when (mode) {
            PanningMode.SWEET_SPOT_VBAP -> "距離・空気減衰シミュレーション"
            PanningMode.ROOM_DISTANCE -> "スピーカー近接定位"
        }
    },
    orbitSpeedLabel = { spd ->
        when (spd) {
            OrbitSpeed.SLOW -> "低速"
            OrbitSpeed.MEDIUM -> "標準"
            OrbitSpeed.FAST -> "高速"
        }
    },
    layoutPresetShort = { preset ->
        when (preset) {
            SpeakerLayoutPreset.COMPACT -> "狭角 (±22°)"
            SpeakerLayoutPreset.STANDARD -> "標準 (±30°)"
            SpeakerLayoutPreset.WIDE -> "広角 (±40°)"
        }
    },
    layoutPresetSummary = { preset ->
        when (preset) {
            SpeakerLayoutPreset.COMPACT -> "フロント ±22° • サラウンド ±100° • バック ±140°"
            SpeakerLayoutPreset.STANDARD -> "フロント ±30° • サラウンド ±110° • バック ±150°"
            SpeakerLayoutPreset.WIDE -> "フロント ±40° • サラウンド ±120° • バック ±155°"
        }
    },
    layoutPresetDetail = { preset ->
        when (preset) {
            SpeakerLayoutPreset.COMPACT -> "画面の左右にフロントスピーカーが近接するコンパクト配置向け。"
            SpeakerLayoutPreset.STANDARD -> "Dolby / ITU 標準の正三角形リファレンス配置。"
            SpeakerLayoutPreset.WIDE -> "フロント幅が広いリビングや大型スクリーン向け。"
        }
    },

    roomTvFront = "TV 前面",
    roomSweetSpotDesc = "視聴位置",
    roomSoundPuckDesc = "2D パック",

    tvTopologyBadge = "TV スピーカー構成",
    setupTvSubtitle = "スピーカー構成を選択 & スマホ / ブラウザと連携",
    connectBrowserIosBtn = "ブラウザ / iOS を接続",
    goToHomeHubBtn = "ホームハブへ",
    activeSpeakerConfigHeading = "使用中のスピーカー構成",
    legendNavigate = "移動",
    legendActivate = "決定",
    activeBadgeUpper = "選択中",
    profileSpecUpper = "プロファイル仕様",
    setupPrefix = "構成",
    setupStreamDesc = { ch ->
        "${ch}ch AudioTrack（48 kHz PCM）を出力します。未使用チャンネルは無音データ（0.0f）になります。"
    },
    specChannelsActive = "有効チャンネル",
    specPcmStream = "PCM ストリーム",
    specPcmValue = { ch -> "$ch チャンネル @ 48 kHz" },
    specSubwooferLfe = "サブウーファー LFE",
    specSubwooferLfeValue = "80 Hz ローパスフィルター",
    hdmiMultichannel = { maxCh -> "HDMI マルチチャンネル (最大 ${maxCh}ch)" },
    outputStereo = { maxCh -> "出力: ステレオ (${maxCh}ch)" },
    headroomProtectionTag = "-12 dBFS スピーカー保護",
    mobileSetupTitle = "スピーカー構成",
    mobileSetupSubtitle = "ルームトポロジーを選択",
    setupActiveTag = { code -> "$code 有効" },
    mobileSetupSelectPrompt = "スピーカー構成を選択してください",
    mobileSetupConfirmBtn = { code -> "$code を確定してハブを開く" },

    hubSetupChipLabel = "構成:",
    hubAudioBtn = { count -> "音声 ($count)" },
    hubBrowserIosBtn = "ブラウザ / iOS",
    tvHubSectionTitle = "音響キャリブレーションスイート",
    tvHubSectionSubtitle = "リモコンの左右キーと決定ボタンで測定モードを選択してください。",
    tvHubChannelsBadge = { count, code -> "$count チャンネル ($code)" },
    tvHostActiveLabel = { ip -> "TV ホスト稼働中: $ip" },
    wlanCheckPrompt = "Wi-Fi を確認",
    wlanNotDetected = "Wi-Fi 未検出",
    remoteAndroidAndBrowser = "接続中: スマホアプリ & ブラウザ",
    remoteAndroidOnly = { count -> "接続中: スマホアプリ" + if (count > 1) " ($count)" else "" },
    remoteBrowserOnly = { count -> "接続中: ブラウザ" + if (count > 1) " ($count)" else "" },
    remoteNone = "リモート未接続",
    headroomStatusBar = { db -> "ヘッドルーム: $db dBFS • ソフトリミッター有効" },
    mode1Badge = "モード 01",
    mode1Title = "単体スピーカーテスト",
    mode1TvDesc = { chs -> "分割ビュー診断。各チャンネル（$chs）を個別に鳴らして配線と音量バランスを確認します。" },
    mode1MobileDesc = { chs -> "対話型 2D ルーム図。各チャンネル（$chs）を個別に鳴らして配線と音量を確認します。" },
    mode1Footer = "ピンクノイズ • サイン波 • 音声",
    mode2Badge = "モード 02",
    mode2Title = "周波数・プリセット発信器",
    mode2TvDesc = "リモコンで調整できる連続サイン波・ピンクノイズ発信器とサブウーファー帯域プリセット。",
    mode2MobileDesc = "ハプティックダイヤル対応のサイン波・ピンクノイズ・クロスオーバー確認ツール。",
    mode2Footer = "20 Hz – 20 kHz 帯域",
    mode3Badge = "モード 03",
    mode3Title = "2D 空間サウンドパック",
    mode3TvDesc = "リアルタイム空間パンナー。方向キーや自動オービットで音像を部屋中で滑らかに移動できます。",
    mode3MobileDesc = "リアルタイム空間パンナー。タッチ操作や自動オービットで音像を自在に定位できます。",
    mode3Footer = "バーチャル音源 & スピーカーフォーカス",
    mobileConnConnected = { host -> "接続中: $host" },
    mobileConnConnecting = "TV に接続中...",
    mobileConnLocal = "ローカルモード (2.0)",
    mobileRoleRemote = "TV リモートコントローラー",
    mobileRoleLocal = "ステレオキャリブレーター",
    mobileCard1FooterLeft = { count -> "$count チャンネル" },
    mobileCard2FooterLeft = "周波数スイープ",
    mobileCard3FooterLeft = "サラウンドパンニング",
    mobileCard3FooterRight = "360° リアルタイム",
    mobileFooterTv = { ip -> "TV: $ip" },
    mobileFooterLocal = "ローカルダウンミックス有効",
    mobileFooterHeadroom = { db -> "ヘッドルーム $db dBFS" },

    singleTitle = "単体スピーカーテスト",
    singleTvSubtitle = { setupName -> "$setupName • 分割ビュー チャンネル診断" },
    singleMobileSubtitle = { code -> "$code チャンネル診断" },
    legendChannel = "チャンネル",
    legendStartStop = "再生 / 停止",
    legendSignalChoice = "信号切替",
    singleActiveBadge = { s -> "$s 再生中" },
    pinkNoiseFreqRange = "ピンクノイズ (20Hz–20kHz)",
    sineToneLabel = { hz -> "サイン波 $hz Hz" },
    noAudioSelected = "音声ファイル未選択",
    noAudioShort = "未選択",
    configureSignalDesc = { name -> "$name のテスト信号設定" },
    syncRoomPlanUpper = "同期ルームマップ",
    signalActiveUpper = "出力中",
    sourcePrefix = { lbl -> "信号: $lbl" },
    channelDialogTitle = { name, s -> "$name ($s) – テスト信号選択" },
    channelSheetSubtitle = "このスピーカーから出力する信号を選択",
    sineFreqLabel = { hz -> "サイン波周波数: $hz Hz" },
    sineDesc = { hz -> "単一周波数の純音 ($hz Hz)" },
    pinkNoiseDesc = "オクターブ均等エネルギー（標準テスト）",
    customAudioDesc = "アップロード音声をループ再生",
    frequencyLabel = "周波数",
    noCustomAudioTvHint = "音声トラックがありません。スマホアプリまたはブラウザからアップロードしてください。",
    noCustomAudioMobileHint = "ライブラリに音声ファイルがありません。ホームハブの音声ライブラリから追加できます。",
    applyToAllChannels = "全スピーカーに適用",
    applySignalBtn = "信号を適用",

    generatorTitle = "周波数・プリセット発信器",
    generatorMobileSubtitle = "DSP シンセサイザー",
    activeChannelsPrefix = { chs -> "出力チャンネル: $chs" },
    noneText = "なし",
    legendFreq = "周波数",
    upStepHint = "上 (+1 / +5 / +25 Hz)",
    downStepHint = { playing -> "下 (-1 / -5 / -25 Hz) • OK: ${if (playing) "一時停止" else "再生"}" },
    pauseAction = "一時停止",
    startAction = "再生",
    pinkNoiseStartTv = "ピンクノイズを再生 [OK]",
    pinkNoisePauseTv = "ピンクノイズを一時停止 [OK]",
    sineStartTv = { hz -> "$hz Hz サイン波を再生 [OK]" },
    sinePauseTv = { hz -> "$hz Hz サイン波を一時停止 [OK]" },
    pinkNoiseStartMobile = "ピンクノイズを再生",
    pinkNoisePauseMobile = "ピンクノイズを停止",
    sineStartMobile = { hz -> "$hz Hz を再生" },
    sinePauseMobile = { hz -> "$hz Hz を停止" },
    quickSelectUpper = "クイックプリセット",
    referenceUpper = "リファレンス",
    quickPresets = listOf(
        QuickFreqPresetText(40, "40 Hz", "ルーム定在波確認"),
        QuickFreqPresetText(60, "60 Hz", "サブウーファー低域"),
        QuickFreqPresetText(80, "80 Hz", "THX クロスオーバー"),
        QuickFreqPresetText(400, "400 Hz", "標準中域トーン"),
        QuickFreqPresetText(1000, "1,000 Hz", "1 kHz 基準レベル"),
        QuickFreqPresetText(10000, "10,000 Hz", "高域ツイーター確認")
    ),
    digitalLevelLabel = "デジタル出力:",
    signalUpper = "信号",
    freqUpper = "周波数",
    tapToEnter = "タップして入力",
    freqDialogTitle = "周波数を直接入力",
    freqDialogRangeLabel = "周波数 (20 – 20,000 Hz)",

    puckTitle = "2D 空間サウンドパック",
    puckMobileSubtitle = "リアルタイム空間パンナー",
    puckTvSubtitle = { mode, az, hint -> "$mode • 方位角 ${az}° • $hint" },
    legendMovePuck = "パック移動",
    legendSideMenuActive = "右メニュー操作中",
    legendBackForHub = "戻るでハブへ",
    puckSourcePrefix = { lbl -> "音源: $lbl" },
    virtualDistance = { m -> "仮想距離: $m m" },
    roomControlUpper = "空間コントロール",
    puckBtnLabel = "パック",
    orbitStateLabel = { on -> "自動回転: ${if (on) "オン" else "オフ"}" },
    stateOn = "オン",
    stateOff = "オフ",
    orbitWithSpeed = { s -> "回転 ($s)" },
    autoOrbitBtn = "自動オービット",
    speedPrefix = { s -> "速度: $s" },
    noAudioUploaded = "音声ファイル未登録",
    trackCycleLabel = { idx, total, name -> "トラック ($idx/$total): $name" },
    puckDecodingMobile = "デコード中… (一時停止)",
    puckDecodingTv = "読み込み中…",
    puckPauseMobile = "空間オーディオを一時停止",
    puckStartMobile = "空間オーディオを再生",
    noAudioOnTvHint = "TV に音声がありません – ハブからアップロードしてください。",

    qrTitle = "ブラウザ / iOS と連携",
    qrBody = "この QR コードはブラウザ操作用（iPhone や PC など）です。\nAndroid 版 SurroundLab アプリは同じ Wi-Fi 内で自動検出・同期するため、QR コードは不要です。",
    tvSettingsTitle = "TV ホスト設定",
    tvSettingsSubtitle = "ヘッドルーム・高域保護設定 & HDMI 診断",
    masterLevelTitleTv = { def -> "マスター出力レベル（初期値: $def dBFS）" },
    swap71Title = "7.1 サラウンドとバックを入替 (HDMI-HAL)",
    swap71DescTv = "7.1ch で SL/SR と SBL/SBR が逆に鳴る機器（Chromecast / Amlogic 等）で有効にします。",
    swap71DescMobile = "7.1ch の SL/SR と SBL/SBR が入れ替わる場合の HDMI-HAL 補正。",
    layoutAnglesTitle = "スピーカー配置角度（バーチャル音源）",
    tvUiScaleTitle = "TV 画面スケール (DPI ズーム)",
    scaleSmallerBtn = "−5 % (縮小)",
    scaleDefaultBtn = "標準 (75 %)",
    scaleLargerBtn = "+5 % (拡大)",
    audioOutputDiag = { name, ch -> "音声出力先: $name (最大 $ch ch)" },
    hdmiDiagOk = "マルチチャンネル PCM を検出しました。HDMI / eARC 経由の 5.1 / 7.1 分離出力が有効です。",
    hdmiDiagStereoWarn = "注意: 現在はステレオ出力のみ認識されています。TV / Chromecast の音声設定を Auto / PCM にし、可能であれば AV アンプに直接 HDMI 接続してください。",
    setupChangeBtn = { code -> "構成を変更 ($code)" },
    setupChangeFullBtn = { code -> "スピーカー構成を変更 ($code)" },
    checkNetworkHdmiBtn = "ネットワーク / HDMI を再確認",
    guideBtnShort = "ガイド",
    guideShowBtn = "クイックガイドを表示",
    tvAudioLibTitle = { count -> "音声ライブラリ ($count)" },
    tvAudioLibSubtitle = "スマホまたはブラウザから HTTP 送信されたトラック",
    tvAudioLibEmpty = "音声トラックはまだありません。\nスマホ版 SurroundLab またはブラウザからアップロードしてください。",

    mobileLibTitle = "音声ライブラリ",
    mobileLibSubtitle = "リファレンス音源",
    libCountBadge = { count, mb -> "$count 曲 • $mb MB" },
    syncInProgressTitle = "TV と同期中…",
    syncAllDoneTitle = "全トラックを TV と同期済み",
    syncPendingTitle = { count -> "TV 未送信のトラックが $count 件あります" },
    syncLocalTitle = "ローカル音声ライブラリ",
    syncActiveSub = { count -> "Wi-Fi 経由で $count 件を転送中" },
    syncAllDoneSub = "TV とスマホの両方で再生可能です",
    syncPendingSub = "「今すぐ同期」で未送信ファイルを TV へ送信します",
    syncOfflineSub = "次回 TV に接続した際に自動同期されます",
    syncNowBtn = "今すぐ同期",
    libEmptyTitle = "音声ファイルがありません",
    libEmptyBody = "Android のファイルピッカーから .wav / .mp3 / .flac / .ogg を追加できます（最大 30 MB）。",
    statusSyncedOnTv = "TV に同期済み",
    statusSavedLocally = "ローカル保存済み",
    statusUploading = "TV へ送信中…",
    statusOnlyOnPhone = "スマホのみ（TV 未送信）",
    statusWaitingSync = "TV 同期待機中",
    statusUploadFailed = "TV への送信に失敗",
    statusDeletingOnTv = "TV から削除中…",
    sendToTvDesc = "TV へ送信",
    uploadAudioBtn = "+ 音声ファイルをアップロード",
    mobileConnTitle = "TV 接続 & 出力レベル",
    mobileConnSubtitle = "mDNS 自動検出 & ヘッドルーム",
    refreshSearchDesc = "再検索",
    currentModeTitle = "現在の接続モード",
    connConnectedDetail = { ip, port -> "TV ホストに接続中 ($ip:$port)" },
    connConnectingDetail = "TV ホストへ接続中...",
    connLocalDetail = "ローカルモード（スマホ 2.0 ステレオ出力）",
    disconnectTvBtn = "TV から切断（ローカルモードへ）",
    discoveredTvsTitle = "Wi-Fi 内で見つかった TV (mDNS)",
    noTvsFoundText = "SurroundLab ホストが見つかりません。TV 側でアプリが起動しているか確認してください。",
    connectBtn = "接続",
    manualConnTitle = "IP アドレスで手動接続",
    tvIpLabel = "TV の IP アドレス",
    portLabel = "ポート",
    sessionTokenLabel = "6 桁のセッショントークン（TV 画面に表示）",
    manualConnectBtn = "手動接続",
    masterLevelTitleMobile = "マスター出力レベル（聴覚保護）",

    onboardingSlides = listOf(
        OnboardingSlide(
            badge = "ステップ 1 / 3 • 概要",
            title = "SurroundLab へようこそ",
            subtitle = "HDMI 経由で 2.0 〜 7.1ch ホームシアターを精密測定できるキャリブレーションスタジオ。",
            icon = Icons.Rounded.SurroundSound,
            bullets = listOf(
                "単体スピーカーテスト" to "ピンクノイズ・サイン波・任意音源で各チャンネルを個別に鳴らし、配線や音量を検証します。",
                "周波数・プリセット発信器" to "20 Hz 〜 20 kHz をスイープし、定在波やサブウーファーのクロスオーバーを確認できます。",
                "2D 空間サウンドパック" to "仮想音源やスピーカーフォーカスを部屋の周囲で滑らかに回転・移動させて定位をチェックします。"
            )
        ),
        OnboardingSlide(
            badge = "ステップ 2 / 3 • 操作方法",
            title = "TV・Android アプリ・ブラウザで操作",
            subtitle = "リビングの環境に合わせてお好みの方法で操作できます。",
            icon = Icons.Rounded.Devices,
            bullets = listOf(
                "TV リモコンで直接操作" to "すべての測定モードと設定を TV の方向キーで完結できます。",
                "Android アプリ（自動連携）" to "スマホにも SurroundLab を入れると、同一 Wi-Fi 上で自動検出・音声同期が行われます。",
                "ブラウザ / iOS（右上の QR コード）" to "TV 右上の「ブラウザ / iOS」から QR コードを表示し、iPhone や PC のブラウザから操作できます。"
            )
        ),
        OnboardingSlide(
            badge = "ステップ 3 / 3 • ヒント",
            title = "ホームシアター調整のポイント",
            subtitle = "測定前に知っておくと便利な 3 つの設定:",
            icon = Icons.Rounded.Tune,
            bullets = listOf(
                "7.1ch の側方と後方が逆になる場合" to "一部の TV デバイス（Chromecast 等）は SL/SR と SBL/SBR が逆に出力されます。設定の「7.1 入替」をオンにしてください。",
                "スピーカー配置角度" to "設定から部屋のレイアウトに合わせて 狭角 (±22°) / 標準 (±30°) / 広角 (±40°) を選べます。",
                "保護レベル & AV アンプ設定" to "安全のため -12 dBFS＋リミッターで開始します。AV アンプは「Multi Ch In」や「Direct / Straight」推奨です。"
            )
        )
    ),
    onboardingSkipToNext = "戻る",
    onboardingNext = "次へ →",
    onboardingFinish = "はじめる",

    tvInstallMenuBtn = "TV へのインストール手順を見る",
    tvInstallBadge = "TV 導入ガイド •「DOWNLOADER」経由",
    tvInstallTitle = "SurroundLab を TV にインストール",
    tvInstallSubtitle = "PC なしで Chromecast / Android TV にアプリを入れる簡単な 4 ステップです:",
    tvInstallSteps = listOf(
        "1. TV に「Downloader」を入れる" to "テレビの Google Play ストアを開き、「Downloader by AFTVnews」（オレンジ色のアイコン）を検索してインストールします。",
        "2. Downloader でコード 7281113 を入力" to "テレビで「Downloader」を開き、番号コード 7281113（または下の GitHub URL）を入力して「Go」を押します。",
        "3. インストール許可を一度だけオン" to "「Downloader」からのインストール確認が出たら、「設定」→「許可」をオンにして「インストール」を押します。",
        "4. 起動して自動接続" to "テレビで SurroundLab を開きます。テレビとスマホが同じ Wi-Fi にあれば、QR コードなしで自動的に接続されます。"
    ),
    tvInstallCopyUrlBtn = "GitHub URL をコピー",
    tvInstallUrlCopiedToast = "ダウンロード URL をコピーしました",
    tvInstallCloseBtn = "閉じる",
    donatePaypalBtn = "開発を支援する (PayPal)",

    updateSectionTitle = "アップデート確認 (GitHub)",
    updateCurrentVersion = { v -> "現在のバージョン: v$v" },
    updateCheckBtn = "アップデートを確認",
    updateCheckingText = "GitHub で最新バージョンを確認中…",
    updateUpToDateText = { v -> "最新バージョンです (v$v)。" },
    updateAvailableText = { v -> "新しいバージョン v$v が利用可能です！" },
    updateDownloadInstallBtn = "ダウンロードしてインストール",
    updateDownloadingText = { pct -> "APK をダウンロード中… $pct%" },
    updateInstallNowBtn = "今すぐインストール",
    updatePermissionHint = "提供元不明アプリのインストール許可を一度オンにしてから、もう一度「今すぐインストール」を押してください。",
    updateErrorText = { err -> "確認に失敗しました ($err)" }
)
