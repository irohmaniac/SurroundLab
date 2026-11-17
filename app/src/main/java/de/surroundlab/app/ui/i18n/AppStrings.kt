package de.surroundlab.app.ui.i18n

import androidx.compose.runtime.staticCompositionLocalOf
import de.surroundlab.app.domain.model.AppLanguage
import de.surroundlab.app.domain.model.GeneratorPreset
import de.surroundlab.app.domain.model.OrbitSpeed
import de.surroundlab.app.domain.model.PanningMode
import de.surroundlab.app.domain.model.SignalSourceType
import de.surroundlab.app.domain.model.SpeakerChannel
import de.surroundlab.app.domain.model.SpeakerLayoutPreset
import de.surroundlab.app.domain.model.SpeakerSetup
import de.surroundlab.app.ui.components.OnboardingSlide

data class QuickFreqPresetText(
    val hz: Int,
    val title: String,
    val desc: String
)

data class AppStrings(
    val langCode: String,
    // Common
    val back: String,
    val close: String,
    val ok: String,
    val apply: String,
    val cancel: String,
    val activeTag: String,
    val ready: String,
    val testBtn: String,
    val stopBtn: String,
    val startBtn: String,
    val pauseBtn: String,
    val deleteBtn: String,
    val settingsLabel: String,
    val homeHubBtn: String,

    // Language selector
    val languageSectionTitle: String,
    val languageAutoLabel: String,
    val languageAutoHint: (resolvedName: String) -> String,

    // Topologies / Channels / Domain Enums
    val setupSubtitle: (SpeakerSetup) -> String,
    val channelName: (SpeakerChannel) -> String,
    val sourceLabel: (SignalSourceType) -> String,
    val sourceShortLabel: (SignalSourceType) -> String,
    val generatorPresetLabel: (GeneratorPreset) -> String,
    val panningModeLabel: (PanningMode) -> String,
    val panningModeHint: (PanningMode) -> String,
    val orbitSpeedLabel: (OrbitSpeed) -> String,
    val layoutPresetShort: (SpeakerLayoutPreset) -> String,
    val layoutPresetSummary: (SpeakerLayoutPreset) -> String,
    val layoutPresetDetail: (SpeakerLayoutPreset) -> String,

    // Room stage
    val roomTvFront: String,
    val roomSweetSpotDesc: String,
    val roomSoundPuckDesc: String,

    // TV & Mobile Setup Screens
    val tvTopologyBadge: String,
    val setupTvSubtitle: String,
    val connectBrowserIosBtn: String,
    val goToHomeHubBtn: String,
    val activeSpeakerConfigHeading: String,
    val legendNavigate: String,
    val legendActivate: String,
    val activeBadgeUpper: String,
    val profileSpecUpper: String,
    val setupPrefix: String,
    val setupStreamDesc: (channels: Int) -> String,
    val specChannelsActive: String,
    val specPcmStream: String,
    val specPcmValue: (channels: Int) -> String,
    val specSubwooferLfe: String,
    val specSubwooferLfeValue: String,
    val hdmiMultichannel: (maxCh: Int) -> String,
    val outputStereo: (maxCh: Int) -> String,
    val headroomProtectionTag: String,
    val mobileSetupTitle: String,
    val mobileSetupSubtitle: String,
    val setupActiveTag: (code: String) -> String,
    val mobileSetupSelectPrompt: String,
    val mobileSetupConfirmBtn: (code: String) -> String,

    // Home Hub (TV & Mobile)
    val hubSetupChipLabel: String,
    val hubAudioBtn: (count: Int) -> String,
    val hubBrowserIosBtn: String,
    val tvHubSectionTitle: String,
    val tvHubSectionSubtitle: String,
    val tvHubChannelsBadge: (count: Int, code: String) -> String,
    val tvHostActiveLabel: (ipOrWarn: String) -> String,
    val wlanCheckPrompt: String,
    val wlanNotDetected: String,
    val remoteAndroidAndBrowser: String,
    val remoteAndroidOnly: (count: Int) -> String,
    val remoteBrowserOnly: (count: Int) -> String,
    val remoteNone: String,
    val headroomStatusBar: (db: Int) -> String,
    val mode1Badge: String,
    val mode1Title: String,
    val mode1TvDesc: (channelsList: String) -> String,
    val mode1MobileDesc: (channelsList: String) -> String,
    val mode1Footer: String,
    val mode2Badge: String,
    val mode2Title: String,
    val mode2TvDesc: String,
    val mode2MobileDesc: String,
    val mode2Footer: String,
    val mode3Badge: String,
    val mode3Title: String,
    val mode3TvDesc: String,
    val mode3MobileDesc: String,
    val mode3Footer: String,
    val mobileConnConnected: (hostName: String) -> String,
    val mobileConnConnecting: String,
    val mobileConnLocal: String,
    val mobileRoleRemote: String,
    val mobileRoleLocal: String,
    val mobileCard1FooterLeft: (count: Int) -> String,
    val mobileCard2FooterLeft: String,
    val mobileCard3FooterLeft: String,
    val mobileCard3FooterRight: String,
    val mobileFooterTv: (ipOrWlan: String) -> String,
    val mobileFooterLocal: String,
    val mobileFooterHeadroom: (db: Int) -> String,

    // Single Speaker Test (TV & Mobile)
    val singleTitle: String,
    val singleTvSubtitle: (setupName: String) -> String,
    val singleMobileSubtitle: (code: String) -> String,
    val legendChannel: String,
    val legendStartStop: String,
    val legendSignalChoice: String,
    val singleActiveBadge: (shortLabel: String) -> String,
    val pinkNoiseFreqRange: String,
    val sineToneLabel: (hz: Int) -> String,
    val noAudioSelected: String,
    val noAudioShort: String,
    val configureSignalDesc: (channelName: String) -> String,
    val syncRoomPlanUpper: String,
    val signalActiveUpper: String,
    val sourcePrefix: (label: String) -> String,
    val channelDialogTitle: (name: String, shortLabel: String) -> String,
    val channelSheetSubtitle: String,
    val sineFreqLabel: (hz: Int) -> String,
    val sineDesc: (hz: Int) -> String,
    val pinkNoiseDesc: String,
    val customAudioDesc: String,
    val frequencyLabel: String,
    val noCustomAudioTvHint: String,
    val noCustomAudioMobileHint: String,
    val applyToAllChannels: String,
    val applySignalBtn: String,

    // Generator (TV & Mobile)
    val generatorTitle: String,
    val generatorMobileSubtitle: String,
    val activeChannelsPrefix: (channels: String) -> String,
    val noneText: String,
    val legendFreq: String,
    val upStepHint: String,
    val downStepHint: (playing: Boolean) -> String,
    val pauseAction: String,
    val startAction: String,
    val pinkNoiseStartTv: String,
    val pinkNoisePauseTv: String,
    val sineStartTv: (hz: Int) -> String,
    val sinePauseTv: (hz: Int) -> String,
    val pinkNoiseStartMobile: String,
    val pinkNoisePauseMobile: String,
    val sineStartMobile: (hz: Int) -> String,
    val sinePauseMobile: (hz: Int) -> String,
    val quickSelectUpper: String,
    val referenceUpper: String,
    val quickPresets: List<QuickFreqPresetText>,
    val digitalLevelLabel: String,
    val signalUpper: String,
    val freqUpper: String,
    val tapToEnter: String,
    val freqDialogTitle: String,
    val freqDialogRangeLabel: String,

    // 2D Spatial Puck (TV & Mobile)
    val puckTitle: String,
    val puckMobileSubtitle: String,
    val puckTvSubtitle: (modeLabel: String, az: Int, hint: String) -> String,
    val legendMovePuck: String,
    val legendSideMenuActive: String,
    val legendBackForHub: String,
    val puckSourcePrefix: (label: String) -> String,
    val virtualDistance: (metersFormatted: String) -> String,
    val roomControlUpper: String,
    val puckBtnLabel: String,
    val orbitStateLabel: (active: Boolean) -> String,
    val stateOn: String,
    val stateOff: String,
    val orbitWithSpeed: (speed: String) -> String,
    val autoOrbitBtn: String,
    val speedPrefix: (speed: String) -> String,
    val noAudioUploaded: String,
    val trackCycleLabel: (idx: Int, total: Int, name: String) -> String,
    val puckDecodingMobile: String,
    val puckDecodingTv: String,
    val puckPauseMobile: String,
    val puckStartMobile: String,
    val noAudioOnTvHint: String,

    // Settings, QR Dialog, Audio Library (TV & Mobile)
    val qrTitle: String,
    val qrBody: String,
    val tvSettingsTitle: String,
    val tvSettingsSubtitle: String,
    val masterLevelTitleTv: (defaultDb: Int) -> String,
    val swap71Title: String,
    val swap71DescTv: String,
    val swap71DescMobile: String,
    val layoutAnglesTitle: String,
    val tvUiScaleTitle: String,
    val scaleSmallerBtn: String,
    val scaleDefaultBtn: String,
    val scaleLargerBtn: String,
    val audioOutputDiag: (name: String, maxCh: Int) -> String,
    val hdmiDiagOk: String,
    val hdmiDiagStereoWarn: String,
    val setupChangeBtn: (code: String) -> String,
    val setupChangeFullBtn: (code: String) -> String,
    val checkNetworkHdmiBtn: String,
    val guideBtnShort: String,
    val guideShowBtn: String,
    val tvAudioLibTitle: (count: Int) -> String,
    val tvAudioLibSubtitle: String,
    val tvAudioLibEmpty: String,

    // Mobile Connection Settings & Audio Library
    val mobileLibTitle: String,
    val mobileLibSubtitle: String,
    val libCountBadge: (count: Int, mbFormatted: String) -> String,
    val syncInProgressTitle: String,
    val syncAllDoneTitle: String,
    val syncPendingTitle: (count: Int) -> String,
    val syncLocalTitle: String,
    val syncActiveSub: (count: Int) -> String,
    val syncAllDoneSub: String,
    val syncPendingSub: String,
    val syncOfflineSub: String,
    val syncNowBtn: String,
    val libEmptyTitle: String,
    val libEmptyBody: String,
    val statusSyncedOnTv: String,
    val statusSavedLocally: String,
    val statusUploading: String,
    val statusOnlyOnPhone: String,
    val statusWaitingSync: String,
    val statusUploadFailed: String,
    val statusDeletingOnTv: String,
    val sendToTvDesc: String,
    val uploadAudioBtn: String,
    val mobileConnTitle: String,
    val mobileConnSubtitle: String,
    val refreshSearchDesc: String,
    val currentModeTitle: String,
    val connConnectedDetail: (ip: String, port: Int) -> String,
    val connConnectingDetail: String,
    val connLocalDetail: String,
    val disconnectTvBtn: String,
    val discoveredTvsTitle: String,
    val noTvsFoundText: String,
    val connectBtn: String,
    val manualConnTitle: String,
    val tvIpLabel: String,
    val portLabel: String,
    val sessionTokenLabel: String,
    val manualConnectBtn: String,
    val masterLevelTitleMobile: String,

    // Onboarding
    val onboardingSlides: List<OnboardingSlide>,
    val onboardingSkipToNext: String,
    val onboardingNext: String,
    val onboardingFinish: String,

    // TV Installation Guide (Downloader by AFTVnews + GitHub Release)
    val tvInstallMenuBtn: String,
    val tvInstallBadge: String,
    val tvInstallTitle: String,
    val tvInstallSubtitle: String,
    val tvInstallSteps: List<Pair<String, String>>,
    val tvInstallCopyUrlBtn: String,
    val tvInstallUrlCopiedToast: String,
    val tvInstallCloseBtn: String,

    // Donation
    val donatePaypalBtn: String,

    // In-App GitHub Updater
    val updateSectionTitle: String,
    val updateCurrentVersion: (version: String) -> String,
    val updateCheckBtn: String,
    val updateCheckingText: String,
    val updateUpToDateText: (version: String) -> String,
    val updateAvailableText: (version: String) -> String,
    val updateDownloadInstallBtn: String,
    val updateDownloadingText: (percent: Int) -> String,
    val updateInstallNowBtn: String,
    val updatePermissionHint: String,
    val updateErrorText: (detail: String) -> String
)

const val AFTVNEWS_DOWNLOADER_CODE = "7281113"
const val GITHUB_LATEST_APK_URL = "https://github.com/irohmaniac/SurroundLab/releases/latest/download/SurroundLab.apk"
const val PAYPAL_DONATE_URL = "https://www.paypal.me/ceddiemcleod"

val LocalAppStrings = staticCompositionLocalOf { StringsDe }

object AppStringsRegistry {
    fun forAppLanguage(appLanguage: AppLanguage): AppStrings {
        return when (appLanguage.resolve()) {
            AppLanguage.DE -> StringsDe
            AppLanguage.EN -> StringsEn
            AppLanguage.ES -> StringsEs
            AppLanguage.FR -> StringsFr
            AppLanguage.PT -> StringsPt
            AppLanguage.JA -> StringsJa
            AppLanguage.AUTO -> StringsEn
        }
    }
}
