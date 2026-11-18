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
            GeneratorPreset.ALL_WITHOUT_SUB -> "サブウーファー以外の全て"
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
    mode4Badge = "モード 04",
    mode4Title = "ルーム音響・自動測定",
    mode4TvDesc = "Android スマホのマイクを使い、部屋の定在波や周波数特性を自動測定します。",
    mode4MobileDesc = "マイク自動スイープ測定：定在波ピークを可視化し、複数の試聴位置を比較して A4 PDF 出力できます。",
    mode4Footer = "マイク測定 & PDF レポート",
    mobileConnConnected = { host -> "接続中: $host" },
    mobileConnConnecting = "TV に接続中...",
    mobileConnLocal = "ローカルモード (2.0)",
    mobileRoleRemote = "TV リモートコントローラー",
    mobileRoleLocal = "ステレオキャリブレーター",
    mobileCard1FooterLeft = { count -> "$count チャンネル" },
    mobileCard2FooterLeft = "周波数スイープ",
    mobileCard3FooterLeft = "サラウンドパンニング",
    mobileCard3FooterRight = "360° リアルタイム",
    mobileCard4FooterLeft = "マイク音響解析",
    mobileCard4FooterRight = "グラフ & PDF",
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
    lfeLowPassTitle = "LFE ローパス（ノイズ & 音楽）",
    lfeLowPassDesc = "サブウーファー (.1) 向けのピンクノイズ・独自音声の上限周波数を制限します。純粋なサイン波と自動測定は常に AV アンプ側へ直接出力されます。",
    lfeDirectLabel = "スルー (無効)",
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
                "2D 空間サウンドパック" to "仮想音源やスピーカーフォーカスを部屋の周囲で滑らかに回転・移動させて定位をチェックします。",
                "ルーム音響・自動測定" to "Android スマホのマイクで複数の試聴位置を自動スイープ測定し、設置比較や A4 PDF レポート出力まで行えます。"
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

    tvInstall = TvInstallStrings(
        menuBtn = "TV へのインストール手順を見る",
        badge = "TV 導入ガイド •「DOWNLOADER」経由",
        title = "SurroundLab を TV にインストール",
        subtitle = "PC なしで Chromecast / Android TV にアプリを入れる簡単な 4 ステップです:",
        steps = listOf(
            "1. TV に「Downloader」を入れる" to "テレビの Google Play ストアを開き、「Downloader by AFTVnews」（オレンジ色のアイコン）を検索してインストールします。",
            "2. Downloader でコード 7281113 を入力" to "テレビで「Downloader」を開き、番号コード 7281113（または下の GitHub URL）を入力して「Go」を押します。",
            "3. インストール許可を一度だけオン" to "「Downloader」からのインストール確認が出たら、「設定」→「許可」をオンにして「インストール」を押します。",
            "4. 起動して自動接続" to "テレビで SurroundLab を開きます。テレビとスマホが同じ Wi-Fi にあれば、QR コードなしで自動的に接続されます。"
        ),
        copyUrlBtn = "GitHub URL をコピー",
        urlCopiedToast = "ダウンロード URL をコピーしました",
        closeBtn = "閉じる"
    ),
    donatePaypalBtn = "開発を支援する (PayPal)",

    update = UpdateStrings(
        sectionTitle = "アップデート確認 (GitHub)",
        currentVersion = { v -> "現在のバージョン: v$v" },
        checkBtn = "アップデートを確認",
        checkingText = "GitHub で最新バージョンを確認中…",
        upToDateText = { v -> "最新バージョンです (v$v)。" },
        availableText = { v -> "新しいバージョン v$v が利用可能です！" },
        downloadInstallBtn = "ダウンロードしてインストール",
        downloadingText = { pct -> "APK をダウンロード中… $pct%" },
        installNowBtn = "今すぐインストール",
        permissionHint = "提供元不明アプリのインストール許可を一度オンにしてから、もう一度「今すぐインストール」を押してください。",
        errorText = { err -> "確認に失敗しました ($err)" }
    ),

    room = RoomMeasurementStrings(
        screenTitle = "ルーム音響・自動測定",
        screenSubtitle = "自動周波数スイープ解析",
        guideBtn = "ガイド",
        tabMeasure = "測定",
        tabArchive = "保存済み",
        tabCompare = "比較",
        tvInfoBadge = "スマホのマイクが必要です",
        tvInfoTitle = "Android スマホから測定を開始してください",
        tvInfoBody = "自動ルーム測定では、試聴位置に置いた Android スマホのマイクで録音しながら、TV から正確なテストトーンを再生します。",
        tvInfoSteps = listOf(
            "1. Android スマホに SurroundLab を入れて起動します（同じ Wi-Fi なら自動接続されます）。",
            "2. スマホで「ルーム音響・自動測定」を開き、マイク権限を許可します。",
            "3. 耳の高さでスマホを静止させてスイープを開始します。グラフと PDF 出力はスマホ上で行えます。"
        ),
        tvActiveSweepStatus = { hz -> "スマホから測定実行中 • 再生中: $hz Hz" },
        tvShowQrBtn = "接続情報 / QR を表示",
        guideTitle = "ルーム音響測定の使い方",
        guideSubtitle = "正確なグラフを得るためのポイント:",
        guideItems = listOf(
            "この機能でできること" to "周波数を順番に自動再生して試聴位置の音量を記録し、定在波のピークやディップ（谷）、設置変更による違いをグラフで確認できます。",
            "Android アプリが必要" to "TV 接続時（5.1/7.1）でもスマホ単体（ケーブル/Bluetooth ステレオ）でも測定できます。ブラウザ版はローカル HTTP の仕様上マイクを使用できません。",
            "静かな環境と姿勢" to "換気扇などを止め、測定中は動かず静かに、マイク穴を塞がないよう耳の高さでスマホを静止させてください。",
            "アンプの音量設定" to "普段の視聴音量〜やや大きめに設定し、事前にレベルチェック（「最適 ✓」）を行ってください。",
            "スマホマイクの特性" to "スマホのマイクは測定用マイクのように校正されていません（特に 40 Hz 以下は減衰しやすいです）。絶対 dB 値ではなく相対的な山・谷や配置比較にご活用ください。"
        ),
        guideGotIt = "理解して始める",
        micPermissionTitle = "マイクの権限が必要です",
        micPermissionDesc = "試聴位置の音響を測定するため、測定および事前チェック中のみマイクを使用します。",
        micPermissionBtn = "マイクを許可",
        micErrorMsg = "マイクを初期化できませんでした。権限を確認してください。",
        levelCheckTitle = "事前レベルチェック",
        levelCheckDesc = "アンプの音量が部屋の雑音より十分大きいか確認します:",
        levelCheckStartBtn = "テスト音で確認",
        levelCheckStopBtn = "確認を停止",
        levelAmbientLabel = { db -> "暗騒音: $db dBFS" },
        levelLiveLabel = { db, snr -> "信号: $db dBFS (S/N比 +$snr dB)" },
        verdictIdle = "準備完了 — 「テスト音で確認」またはそのまま測定を開始してください。",
        verdictCalibrating = "環境ノイズを測定中…少し静かにしてください。",
        verdictTooQuiet = "音が小さすぎます！アンプの音量を少し上げてください。",
        verdictOptimal = "音量バランス最適 ✓ — 雑音と明確に分離されています。",
        verdictClipping = "マイクが音割れしています！音量を少し下げてください。",
        speakersSectionTitle = "測定スピーカー",
        speakerPresetHint = { preset ->
            when (preset) {
                GeneratorPreset.P2_0 -> "AV アンプでスピーカー設定が「Small」の場合に最適（低域管理が有効）：低音の二重加算がなく、音楽再生やクロスオーバー確認に最も現実的です。「Large」設定でサブウーファーが鳴らない場合は 0.1 または 2.1 を選択してください。"
                GeneratorPreset.P0_1_SUB -> "サブウーファー (.1 LFE) のみを測定します。低域の定在波や設置場所の比較に最適です。"
                GeneratorPreset.P2_1 -> "フロント + LFE を同時に出力します（AV アンプの低域管理なしや Large 設定向け。Small 設定ではクロスオーバー以下が加算されます）。"
                GeneratorPreset.ALL_WITHOUT_SUB -> "LFE を除いた全スピーカーを鳴らします（AV アンプ側で低域管理が有効な場合はサブウーファーも動作します）。"
                GeneratorPreset.ALL_ACTIVE -> "有効な全チャンネルを同時に鳴らします（複数スピーカー間の位相干渉・打ち消し合いが起きる場合があります）。"
                GeneratorPreset.CUSTOM -> "センターのみなど、特定のチャンネルを自由に選んで測定できます。"
            }
        },
        presetSectionTitle = "周波数範囲 & ステップ幅",
        presetRoomModes = "定在波 (20–200 Hz)",
        presetSubwoofer = "サブウーファー (20–120 Hz)",
        presetFullRange = "全帯域 (20–20k)",
        presetCustom = "カスタム",
        startHzLabel = "開始 (Hz)",
        endHzLabel = "終了 (Hz)",
        stepModeLinear = "リニア (Hz)",
        stepModeOctave = "対数 (オクターブ)",
        stepHzLabel = "刻み幅 (Hz)",
        stepOctaveLabel = { d -> "1/$d オクターブ" },
        speedLabel = "測定速度",
        speedFast = "高速",
        speedStandard = "標準",
        speedPrecise = "精密",
        estimatedTimeLabel = { pts, sec -> "$pts 点 • 1 位置あたり約 ${sec}秒" },
        positionNameLabel = "この試聴位置の名前",
        defaultPositionName = { idx -> "試聴位置 $idx" },
        startSweepBtn = "測定を開始",
        addPositionSweepBtn = "この位置を測定",
        countdownLabel = { sec -> "動かず静かに… $sec 秒後に開始" },
        silenceCalibLabel = "部屋の環境ノイズを測定中…",
        sweepingLabel = { cur, tot, hz -> "ステップ $cur / $tot • $hz Hz" },
        cancelSweepBtn = "測定を中止",
        activeSessionTitle = "現在の測定セッション",
        defaultSessionName = { d -> "測定 $d" },
        scaleLinearLabel = "リニア (フラット)",
        scaleDbaLabel = "dBA (聴感補正)",
        meanCurveLabel = "平均値",
        smoothLabel = "平滑化",
        addAnotherPositionBtn = "+ 別の試聴位置を追加",
        saveSessionBtn = "測定を保存",
        savedToast = "測定結果を保存しました",
        renameSessionTitle = "測定名とメモ",
        sessionNameHint = "例: サブウーファー左角配置",
        sessionNoteHint = "メモ (例: クロスオーバー 80 Hz, 位相 0°)",
        renamePositionTitle = "試聴位置の名前変更",
        newPositionHint = "例: ソファ中央",
        archiveEmptyTitle = "保存された測定はまだありません",
        archiveEmptyBody = "「測定」タブで最初のスイープを実行して保存すると、比較や A4 PDF 出力ができます。",
        positionsCountLabel = { c -> "$c 箇所" },
        loadInEditorBtn = "開く / 追加測定",
        exportPdfSingleBtn = "PDF",
        exportPdfSelectedBtn = { c -> "PDF 出力 ($c)" },
        selectForExportHint = "チェックを入れて複数の測定を 1 つの A4 PDF にまとめられます。",
        compareHint = "2 つ以上の保存済み測定を選んでグラフを重ねて比較:",
        compareMinTwoHint = "比較するには 2 つ以上保存してください。",
        pdfReportTitle = "SurroundLab — ルーム音響測定レポート",
        pdfGeneratedPrefix = "作成日時",
        pdfPageLabel = { p, t -> "ページ $p / $t" },
        pdfChannelsPrefix = "チャンネル",
        pdfShareTitle = "測定レポート PDF を共有",
        pdfErrorToast = "PDF の作成に失敗しました。",
        pdfLayoutDialogTitle = "PDF の向きを選択",
        pdfLayoutDialogBody = "全帯域スイープ（20 Hz – 20 kHz など）では横向きにするとグラフがより見やすくなります。",
        pdfPortraitDefaultBtn = "縦向き（標準）",
        pdfLandscapeWideBtn = "横向き（ワイド）"
    )
)
