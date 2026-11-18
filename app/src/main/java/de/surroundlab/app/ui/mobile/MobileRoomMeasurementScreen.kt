package de.surroundlab.app.ui.mobile

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.HelpOutline
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.CheckBox
import androidx.compose.material.icons.rounded.CheckBoxOutlineBlank
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material.icons.rounded.PictureAsPdf
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material.icons.rounded.Stop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import de.surroundlab.app.data.RoomPdfExporter
import de.surroundlab.app.domain.model.DbScaleMode
import de.surroundlab.app.domain.model.FrequencyMeasurementPoint
import de.surroundlab.app.domain.model.GeneratorPreset
import de.surroundlab.app.domain.model.LevelCheckVerdict
import de.surroundlab.app.domain.model.ListeningPositionTrace
import de.surroundlab.app.domain.model.MeasurementRunProgress
import de.surroundlab.app.domain.model.MeasurementSpeed
import de.surroundlab.app.domain.model.RoomGraphUtils
import de.surroundlab.app.domain.model.RoomLevelCheckState
import de.surroundlab.app.domain.model.RoomMeasurementSession
import de.surroundlab.app.domain.model.RoomPresetType
import de.surroundlab.app.domain.model.SpeakerChannel
import de.surroundlab.app.domain.model.SurroundAppState
import de.surroundlab.app.domain.model.SweepPhase
import de.surroundlab.app.domain.model.SweepStepMode
import de.surroundlab.app.ui.components.GraphSeries
import de.surroundlab.app.ui.components.RoomFrequencyGraph
import de.surroundlab.app.ui.i18n.LocalAppStrings
import de.surroundlab.app.ui.theme.MonoFontFamily
import de.surroundlab.app.ui.theme.SurroundColors
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import kotlin.math.roundToInt

private enum class RoomTab {
    MEASURE,
    ARCHIVE,
    COMPARE
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MobileRoomMeasurementScreen(
    state: SurroundAppState,
    roomGuideSeen: Boolean,
    savedSessions: List<RoomMeasurementSession>,
    levelCheckState: RoomLevelCheckState,
    runProgress: MeasurementRunProgress,
    onMarkGuideSeen: () -> Unit,
    onStartLevelCheck: (GeneratorPreset, Set<SpeakerChannel>, Int, Boolean) -> Unit,
    onStopLevelCheck: () -> Unit,
    onStartSweep: (
        preset: GeneratorPreset,
        customChannels: Set<SpeakerChannel>,
        startHz: Int,
        endHz: Int,
        stepMode: SweepStepMode,
        stepValue: Int,
        speed: MeasurementSpeed,
        micErrorLabel: String,
        onCompleted: (List<FrequencyMeasurementPoint>) -> Unit
    ) -> Unit,
    onCancelSweep: () -> Unit,
    onResetSweepProgress: () -> Unit,
    onSaveSession: (RoomMeasurementSession) -> Unit,
    onUpdateSessionMeta: (sessionId: String, name: String, note: String) -> Unit,
    onRenamePosition: (sessionId: String, positionId: String, newName: String) -> Unit,
    onDeletePosition: (sessionId: String, positionId: String) -> Unit,
    onDeleteSession: (sessionId: String) -> Unit,
    onBack: () -> Unit
) {
    val s = LocalAppStrings.current
    val r = s.room
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    BackHandler {
        onStopLevelCheck()
        onCancelSweep()
        onBack()
    }

    DisposableEffect(Unit) {
        onDispose {
            onStopLevelCheck()
            onCancelSweep()
        }
    }

    var hasMicPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) ==
                PackageManager.PERMISSION_GRANTED
        )
    }
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasMicPermission = granted
    }

    var showGuideDialog by remember { mutableStateOf(!roomGuideSeen) }
    var activeTab by remember { mutableStateOf(RoomTab.MEASURE) }

    // Sweep configuration state (Default: 2.0 Front for realistic AVR bass-management without LFE doubling)
    var selectedPreset by remember { mutableStateOf(GeneratorPreset.P2_0) }
    LaunchedEffect(state.setup) {
        if (!selectedPreset.isAvailableFor(state.setup)) {
            selectedPreset = GeneratorPreset.P2_0
        }
    }
    var customChannels by remember(state.setup) {
        mutableStateOf(state.setup.channels.toSet())
    }
    var rangePreset by remember { mutableStateOf(RoomPresetType.ROOM_MODES) }
    var startHzText by remember { mutableStateOf("20") }
    var endHzText by remember { mutableStateOf("200") }
    var stepMode by remember { mutableStateOf(SweepStepMode.LINEAR_HZ) }
    var stepValueText by remember { mutableStateOf("5") }
    var speed by remember { mutableStateOf(MeasurementSpeed.STANDARD) }

    // Active working session (draft or loaded from archive)
    var workingSession by remember { mutableStateOf<RoomMeasurementSession?>(null) }
    var nextPositionName by remember { mutableStateOf(r.defaultPositionName(1)) }
    var showConfigForNextPosition by remember { mutableStateOf(true) }

    // Graph display options
    var scaleMode by remember { mutableStateOf(DbScaleMode.LINEAR_FLAT) }
    var showMeanCurve by remember { mutableStateOf(true) }
    var smoothCurves by remember { mutableStateOf(false) }
    var hiddenPositionIds by remember(workingSession?.id) { mutableStateOf<Set<String>>(emptySet()) }

    // Dialog states
    var renameSessionTarget by remember { mutableStateOf<RoomMeasurementSession?>(null) }
    var saveDraftDialogOpen by remember { mutableStateOf(false) }
    var renamePositionTarget by remember { mutableStateOf<Pair<String, ListeningPositionTrace>?>(null) }
    var pendingSinglePdfSessions by remember { mutableStateOf<List<RoomMeasurementSession>?>(null) }

    // Archive / Compare selection state
    var selectedPdfSessionIds by remember { mutableStateOf<Set<String>>(emptySet()) }
    var selectedCompareIds by remember { mutableStateOf<Set<String>>(emptySet()) }

    val parsedStartHz = (startHzText.toIntOrNull() ?: 20).coerceIn(20, 19999)
    val parsedEndHz = (endHzText.toIntOrNull() ?: 200).coerceIn(parsedStartHz, 20000)
    val parsedStepValue = when (stepMode) {
        SweepStepMode.LINEAR_HZ -> (stepValueText.toIntOrNull() ?: 5).coerceIn(1, 5000)
        SweepStepMode.OCTAVE_FRACTION -> (stepValueText.toIntOrNull() ?: 6).coerceIn(1, 24)
    }

    val isSweepRunning = runProgress.phase == SweepPhase.COUNTDOWN ||
        runProgress.phase == SweepPhase.MEASURING_SILENCE ||
        runProgress.phase == SweepPhase.SWEEPING

    fun launchPdfShare(
        sessionsToExport: List<RoomMeasurementSession>,
        landscapeSinglePerPage: Boolean = false
    ) {
        if (sessionsToExport.isEmpty()) return
        coroutineScope.launch {
            val result = withContext(Dispatchers.IO) {
                RoomPdfExporter.exportToPdfUri(
                    context = context,
                    sessions = sessionsToExport,
                    scaleMode = scaleMode,
                    showMeanCurve = showMeanCurve,
                    smoothCurves = smoothCurves,
                    landscapeSinglePerPage = landscapeSinglePerPage,
                    labels = RoomPdfExporter.ExportLabels(
                        reportTitle = r.pdfReportTitle,
                        generatedPrefix = r.pdfGeneratedPrefix,
                        pageLabel = r.pdfPageLabel,
                        scaleLabel = if (scaleMode == DbScaleMode.DBA) r.scaleDbaLabel else r.scaleLinearLabel,
                        channelsPrefix = r.pdfChannelsPrefix,
                        meanCurveLabel = r.meanCurveLabel
                    )
                )
            }
            result.onSuccess { uri ->
                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "application/pdf"
                    putExtra(Intent.EXTRA_STREAM, uri)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                context.startActivity(Intent.createChooser(shareIntent, r.pdfShareTitle))
            }.onFailure {
                Toast.makeText(context, r.pdfErrorToast, Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun requestPdfExport(sessionsToExport: List<RoomMeasurementSession>) {
        if (sessionsToExport.isEmpty()) return
        if (sessionsToExport.size == 1) {
            pendingSinglePdfSessions = sessionsToExport
        } else {
            launchPdfShare(sessionsToExport, landscapeSinglePerPage = false)
        }
    }

    LaunchedEffect(savedSessions) {
        val currentId = workingSession?.id
        if (currentId != null) {
            val persisted = savedSessions.firstOrNull { it.id == currentId }
            if (persisted != null) {
                workingSession = persisted
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SurroundColors.Background)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // Top Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                IconButton(
                    onClick = {
                        onStopLevelCheck()
                        onCancelSweep()
                        onBack()
                    },
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = SurroundColors.SurfaceContainerHigh,
                        contentColor = SurroundColors.OnSurface
                    )
                ) {
                    Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = s.back)
                }
                Column {
                    Text(
                        text = r.screenTitle,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = SurroundColors.OnBackground
                    )
                    Text(
                        text = r.screenSubtitle,
                        fontFamily = MonoFontFamily,
                        fontSize = 10.sp,
                        color = SurroundColors.Primary
                    )
                }
            }

            IconButton(
                onClick = { showGuideDialog = true },
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = SurroundColors.SurfaceContainerHigh,
                    contentColor = SurroundColors.Primary
                )
            ) {
                Icon(Icons.AutoMirrored.Rounded.HelpOutline, contentDescription = r.guideBtn)
            }
        }

        // 3-Tab Switcher
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 4.dp)
                .clip(CircleShape)
                .background(SurroundColors.SurfaceContainer)
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            listOf(
                RoomTab.MEASURE to r.tabMeasure,
                RoomTab.ARCHIVE to "${r.tabArchive} (${savedSessions.size})",
                RoomTab.COMPARE to r.tabCompare
            ).forEach { (tab, label) ->
                val selected = activeTab == tab
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(CircleShape)
                        .background(if (selected) SurroundColors.PrimaryContainer else Color.Transparent)
                        .clickable(enabled = !isSweepRunning) { activeTab = tab }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (selected) SurroundColors.OnPrimaryContainer else SurroundColors.OnSurfaceVariant
                    )
                }
            }
        }

        when (activeTab) {
            RoomTab.MEASURE -> {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    if (!hasMicPermission) {
                        MicPermissionCard(
                            title = r.micPermissionTitle,
                            desc = r.micPermissionDesc,
                            buttonLabel = r.micPermissionBtn,
                            onRequest = { permissionLauncher.launch(Manifest.permission.RECORD_AUDIO) }
                        )
                    }

                    if (isSweepRunning) {
                        ActiveSweepProgressCard(
                            progress = runProgress,
                            startHz = workingSession?.startHz ?: parsedStartHz,
                            endHz = workingSession?.endHz ?: parsedEndHz,
                            stepMode = workingSession?.stepMode ?: stepMode,
                            scaleMode = scaleMode,
                            onCancel = onCancelSweep
                        )
                    } else {
                        // Active Session Graph & Positions
                        val activeSession = workingSession
                        if (activeSession != null && activeSession.positions.isNotEmpty()) {
                            ActiveSessionResultCard(
                                session = activeSession,
                                hiddenPositionIds = hiddenPositionIds,
                                scaleMode = scaleMode,
                                showMeanCurve = showMeanCurve,
                                smoothCurves = smoothCurves,
                                onScaleChange = { scaleMode = it },
                                onToggleMean = { showMeanCurve = !showMeanCurve },
                                onToggleSmooth = { smoothCurves = !smoothCurves },
                                onTogglePositionVisible = { pos ->
                                    hiddenPositionIds = if (pos.id in hiddenPositionIds) {
                                        hiddenPositionIds - pos.id
                                    } else {
                                        hiddenPositionIds + pos.id
                                    }
                                },
                                onRenameSession = { renameSessionTarget = activeSession },
                                onRenamePosition = { pos -> renamePositionTarget = activeSession.id to pos },
                                onDeletePosition = { pos ->
                                    hiddenPositionIds = hiddenPositionIds - pos.id
                                    val updatedPositions = activeSession.positions.filterNot { it.id == pos.id }
                                    workingSession = if (updatedPositions.isEmpty()) null
                                    else activeSession.copy(positions = updatedPositions)
                                    if (savedSessions.any { it.id == activeSession.id }) {
                                        onDeletePosition(activeSession.id, pos.id)
                                    }
                                },
                                onAddAnotherPosition = {
                                    val nextIdx = activeSession.positions.size + 1
                                    nextPositionName = r.defaultPositionName(nextIdx)
                                    showConfigForNextPosition = true
                                },
                                onSaveSession = {
                                    saveDraftDialogOpen = true
                                },
                                onNewMeasurement = {
                                    workingSession = null
                                    hiddenPositionIds = emptySet()
                                    nextPositionName = r.defaultPositionName(1)
                                    showConfigForNextPosition = true
                                    onResetSweepProgress()
                                },
                                onExportPdf = {
                                    val visible = activeSession.positions.filterNot { it.id in hiddenPositionIds }
                                    val exportSession = if (visible.isNotEmpty()) {
                                        activeSession.copy(positions = visible)
                                    } else activeSession
                                    requestPdfExport(listOf(exportSession))
                                }
                            )
                        }

                        if (activeSession == null || showConfigForNextPosition) {
                            val lockedToSession = activeSession != null
                            val activePreset = activeSession?.speakerPreset ?: selectedPreset
                            val activeCustom = activeSession?.channels
                                ?.mapNotNull { runCatching { SpeakerChannel.valueOf(it) }.getOrNull() }
                                ?.toSet()
                                ?: customChannels
                            val activeStartHz = activeSession?.startHz ?: parsedStartHz
                            val activeEndHz = activeSession?.endHz ?: parsedEndHz
                            val activeStepMode = activeSession?.stepMode ?: stepMode
                            val activeStepValue = activeSession?.stepValue ?: parsedStepValue

                            val sweepFreqs = remember(activeStartHz, activeEndHz, activeStepMode, activeStepValue) {
                                RoomGraphUtils.buildSweepFrequencies(
                                    activeStartHz,
                                    activeEndHz,
                                    activeStepMode,
                                    activeStepValue
                                )
                            }
                            val stepDurationMs = speed.settleMs + speed.measureMs + speed.pauseMs
                            val estSeconds = ((sweepFreqs.size * stepDurationMs) / 1000f).roundToInt().coerceAtLeast(1)

                            // Listening Position Name
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(22.dp))
                                    .background(SurroundColors.SurfaceContainer)
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                OutlinedTextField(
                                    value = nextPositionName,
                                    onValueChange = { nextPositionName = it },
                                    label = { Text(r.positionNameLabel) },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth()
                                )

                                if (!lockedToSession) {
                                    // Speaker selection
                                    Text(
                                        text = r.speakersSectionTitle,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = SurroundColors.OnSurface
                                    )
                                    FlowRow(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        GeneratorPreset.entries.filter { it.isAvailableFor(state.setup) }.forEach { preset ->
                                            val sel = selectedPreset == preset
                                            RoomChip(
                                                text = s.generatorPresetLabel(preset),
                                                selected = sel,
                                                onClick = { selectedPreset = preset }
                                            )
                                        }
                                    }

                                    if (selectedPreset == GeneratorPreset.CUSTOM) {
                                        FlowRow(
                                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                                            verticalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            state.setup.channels.forEach { ch ->
                                                val sel = ch in customChannels
                                                RoomChip(
                                                    text = ch.shortLabel,
                                                    selected = sel,
                                                    onClick = {
                                                        val next = if (sel) customChannels - ch else customChannels + ch
                                                        if (next.isNotEmpty()) customChannels = next
                                                    }
                                                )
                                            }
                                        }
                                    }

                                    Text(
                                        text = r.speakerPresetHint(selectedPreset),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = SurroundColors.OnSurfaceVariant,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(SurroundColors.SurfaceContainerHigh.copy(alpha = 0.7f))
                                            .padding(horizontal = 12.dp, vertical = 8.dp)
                                    )

                                    Spacer(Modifier.height(4.dp))

                                    // Frequency Preset & Range
                                    Text(
                                        text = r.presetSectionTitle,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = SurroundColors.OnSurface
                                    )
                                    FlowRow(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        listOf(
                                            RoomPresetType.ROOM_MODES to r.presetRoomModes,
                                            RoomPresetType.SUBWOOFER to r.presetSubwoofer,
                                            RoomPresetType.FULL_RANGE to r.presetFullRange,
                                            RoomPresetType.CUSTOM to r.presetCustom
                                        ).forEach { (rp, label) ->
                                            RoomChip(
                                                text = label,
                                                selected = rangePreset == rp,
                                                onClick = {
                                                    rangePreset = rp
                                                    if (rp != RoomPresetType.CUSTOM) {
                                                        startHzText = rp.startHz.toString()
                                                        endHzText = rp.endHz.toString()
                                                        stepMode = rp.stepMode
                                                        stepValueText = rp.stepValue.toString()
                                                    }
                                                }
                                            )
                                        }
                                    }

                                    // Start / End / Step controls
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        OutlinedTextField(
                                            value = startHzText,
                                            onValueChange = {
                                                rangePreset = RoomPresetType.CUSTOM
                                                startHzText = it.filter(Char::isDigit).take(5)
                                            },
                                            label = { Text(r.startHzLabel) },
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                            singleLine = true,
                                            modifier = Modifier.weight(1f)
                                        )
                                        OutlinedTextField(
                                            value = endHzText,
                                            onValueChange = {
                                                rangePreset = RoomPresetType.CUSTOM
                                                endHzText = it.filter(Char::isDigit).take(5)
                                            },
                                            label = { Text(r.endHzLabel) },
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                            singleLine = true,
                                            modifier = Modifier.weight(1f)
                                        )
                                    }

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        RoomChip(
                                            text = r.stepModeLinear,
                                            selected = stepMode == SweepStepMode.LINEAR_HZ,
                                            onClick = {
                                                rangePreset = RoomPresetType.CUSTOM
                                                stepMode = SweepStepMode.LINEAR_HZ
                                                if ((stepValueText.toIntOrNull() ?: 0) > 200 || stepValueText == "6" || stepValueText == "12" || stepValueText == "3") {
                                                    stepValueText = "5"
                                                }
                                            }
                                        )
                                        RoomChip(
                                            text = r.stepModeOctave,
                                            selected = stepMode == SweepStepMode.OCTAVE_FRACTION,
                                            onClick = {
                                                rangePreset = RoomPresetType.CUSTOM
                                                stepMode = SweepStepMode.OCTAVE_FRACTION
                                                stepValueText = "6"
                                            }
                                        )
                                    }

                                    if (stepMode == SweepStepMode.LINEAR_HZ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            OutlinedTextField(
                                                value = stepValueText,
                                                onValueChange = {
                                                    rangePreset = RoomPresetType.CUSTOM
                                                    stepValueText = it.filter(Char::isDigit).take(4)
                                                },
                                                label = { Text(r.stepHzLabel) },
                                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                                singleLine = true,
                                                modifier = Modifier.width(120.dp)
                                            )
                                            Row(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .horizontalScroll(rememberScrollState()),
                                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                                            ) {
                                                listOf(2, 5, 10, 20, 50).forEach { st ->
                                                    RoomChip(
                                                        text = "$st Hz",
                                                        selected = parsedStepValue == st,
                                                        onClick = {
                                                            rangePreset = RoomPresetType.CUSTOM
                                                            stepValueText = st.toString()
                                                        }
                                                    )
                                                }
                                            }
                                        }
                                    } else {
                                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                            listOf(3, 6, 12).forEach { denom ->
                                                RoomChip(
                                                    text = r.stepOctaveLabel(denom),
                                                    selected = parsedStepValue == denom,
                                                    onClick = {
                                                        rangePreset = RoomPresetType.CUSTOM
                                                        stepValueText = denom.toString()
                                                    }
                                                )
                                            }
                                        }
                                    }
                                }

                                // Measurement Speed
                                Text(
                                    text = r.speedLabel,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = SurroundColors.OnSurface
                                )
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    listOf(
                                        MeasurementSpeed.FAST to r.speedFast,
                                        MeasurementSpeed.STANDARD to r.speedStandard,
                                        MeasurementSpeed.PRECISE to r.speedPrecise
                                    ).forEach { (spd, label) ->
                                        RoomChip(
                                            text = label,
                                            selected = speed == spd,
                                            onClick = { speed = spd }
                                        )
                                    }
                                }

                                Text(
                                    text = r.estimatedTimeLabel(sweepFreqs.size, estSeconds),
                                    fontFamily = MonoFontFamily,
                                    fontSize = 12.sp,
                                    color = SurroundColors.Primary
                                )
                            }

                            // Pre-sweep Level Check Card
                            val testFreq = if (activePreset == GeneratorPreset.P0_1_SUB) 60 else 100
                            LevelCheckCard(
                                state = levelCheckState,
                                hasPermission = hasMicPermission,
                                onToggleCheck = {
                                    if (!hasMicPermission) {
                                        permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                    } else if (levelCheckState.running) {
                                        onStopLevelCheck()
                                    } else {
                                        onStartLevelCheck(activePreset, activeCustom, testFreq, true)
                                    }
                                }
                            )

                            // Start Sweep CTA
                            Button(
                                onClick = {
                                    if (!hasMicPermission) {
                                        permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                        return@Button
                                    }
                                    onStopLevelCheck()
                                    val resolvedChannels = activePreset.resolveChannels(state.setup, activeCustom)
                                    val posIndex = (activeSession?.positions?.size ?: 0) + 1
                                    val chosenPosName = nextPositionName.trim()
                                        .ifBlank { r.defaultPositionName(posIndex) }

                                    onStartSweep(
                                        activePreset,
                                        activeCustom,
                                        activeStartHz,
                                        activeEndHz,
                                        activeStepMode,
                                        activeStepValue,
                                        speed,
                                        r.micErrorMsg
                                    ) { measuredPoints ->
                                        val trace = ListeningPositionTrace(
                                            id = UUID.randomUUID().toString(),
                                            name = chosenPosName,
                                            colorArgb = RoomGraphUtils.colorForIndex(posIndex - 1),
                                            points = measuredPoints,
                                            createdAtMs = System.currentTimeMillis()
                                        )
                                        val existing = workingSession
                                        if (existing == null) {
                                            val stamp = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()).format(Date())
                                            val newSession = RoomMeasurementSession(
                                                id = UUID.randomUUID().toString(),
                                                name = r.defaultSessionName(stamp),
                                                note = "",
                                                createdAtMs = System.currentTimeMillis(),
                                                setupCode = state.setup.code,
                                                speakerPreset = activePreset,
                                                channels = resolvedChannels.map { it.shortLabel },
                                                startHz = activeStartHz,
                                                endHz = activeEndHz,
                                                stepMode = activeStepMode,
                                                stepValue = activeStepValue,
                                                isRemoteTv = state.hostIp != null,
                                                positions = listOf(trace)
                                            )
                                            workingSession = newSession
                                            onSaveSession(newSession)
                                        } else {
                                            val updated = existing.copy(positions = existing.positions + trace)
                                            workingSession = updated
                                            onSaveSession(updated)
                                        }
                                        showConfigForNextPosition = false
                                        nextPositionName = r.defaultPositionName(posIndex + 1)
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp),
                                shape = CircleShape,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = SurroundColors.Primary,
                                    contentColor = SurroundColors.OnPrimary
                                )
                            ) {
                                Icon(Icons.Rounded.Mic, contentDescription = null)
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = if (activeSession == null) r.startSweepBtn else r.addPositionSweepBtn,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(12.dp))
                }
            }

            RoomTab.ARCHIVE -> {
                ArchiveTabContent(
                    savedSessions = savedSessions,
                    selectedPdfIds = selectedPdfSessionIds,
                    scaleMode = scaleMode,
                    showMeanCurve = showMeanCurve,
                    smoothCurves = smoothCurves,
                    onToggleSelectPdf = { id ->
                        selectedPdfSessionIds = if (id in selectedPdfSessionIds) {
                            selectedPdfSessionIds - id
                        } else {
                            selectedPdfSessionIds + id
                        }
                    },
                    onExportSelectedPdf = {
                        val list = savedSessions.filter { it.id in selectedPdfSessionIds }
                        requestPdfExport(list)
                    },
                    onExportSinglePdf = { session -> requestPdfExport(listOf(session)) },
                    onOpenSession = { session ->
                        workingSession = session
                        showConfigForNextPosition = false
                        nextPositionName = r.defaultPositionName(session.positions.size + 1)
                        activeTab = RoomTab.MEASURE
                    },
                    onRenameSession = { session -> renameSessionTarget = session },
                    onDeleteSession = { session ->
                        selectedPdfSessionIds = selectedPdfSessionIds - session.id
                        selectedCompareIds = selectedCompareIds - session.id
                        if (workingSession?.id == session.id) workingSession = null
                        onDeleteSession(session.id)
                    }
                )
            }

            RoomTab.COMPARE -> {
                CompareTabContent(
                    savedSessions = savedSessions,
                    selectedIds = selectedCompareIds,
                    scaleMode = scaleMode,
                    smoothCurves = smoothCurves,
                    onToggleSelect = { id ->
                        selectedCompareIds = if (id in selectedCompareIds) {
                            selectedCompareIds - id
                        } else {
                            selectedCompareIds + id
                        }
                    },
                    onScaleChange = { scaleMode = it },
                    onToggleSmooth = { smoothCurves = !smoothCurves }
                )
            }
        }
    }

    if (showGuideDialog) {
        RoomGuideDialog(
            onDismiss = {
                showGuideDialog = false
                onMarkGuideSeen()
            }
        )
    }

    renameSessionTarget?.let { target ->
        RenameSessionDialog(
            initialName = target.name,
            initialNote = target.note,
            onConfirm = { newName, newNote ->
                onUpdateSessionMeta(target.id, newName, newNote)
                if (workingSession?.id == target.id) {
                    workingSession = workingSession?.copy(name = newName, note = newNote)
                }
                renameSessionTarget = null
            },
            onDismiss = { renameSessionTarget = null }
        )
    }

    if (saveDraftDialogOpen && workingSession != null) {
        val ws = workingSession!!
        RenameSessionDialog(
            initialName = ws.name,
            initialNote = ws.note,
            onConfirm = { newName, newNote ->
                val updated = ws.copy(name = newName, note = newNote)
                workingSession = updated
                onSaveSession(updated)
                saveDraftDialogOpen = false
                Toast.makeText(context, r.savedToast, Toast.LENGTH_SHORT).show()
            },
            onDismiss = { saveDraftDialogOpen = false }
        )
    }

    renamePositionTarget?.let { (sessionId, pos) ->
        RenamePositionDialog(
            initialName = pos.name,
            onConfirm = { newName ->
                onRenamePosition(sessionId, pos.id, newName)
                if (workingSession?.id == sessionId) {
                    workingSession = workingSession?.copy(
                        positions = workingSession!!.positions.map {
                            if (it.id == pos.id) it.copy(name = newName) else it
                        }
                    )
                }
                renamePositionTarget = null
            },
            onDismiss = { renamePositionTarget = null }
        )
    }

    pendingSinglePdfSessions?.let { sessionsToExport ->
        PdfLayoutChoiceDialog(
            onSelectPortrait = {
                pendingSinglePdfSessions = null
                launchPdfShare(sessionsToExport, landscapeSinglePerPage = false)
            },
            onSelectLandscape = {
                pendingSinglePdfSessions = null
                launchPdfShare(sessionsToExport, landscapeSinglePerPage = true)
            },
            onDismiss = { pendingSinglePdfSessions = null }
        )
    }
}

@Composable
private fun MicPermissionCard(
    title: String,
    desc: String,
    buttonLabel: String,
    onRequest: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(SurroundColors.SecondaryContainer.copy(alpha = 0.7f))
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Icon(Icons.Rounded.Mic, contentDescription = null, tint = SurroundColors.Secondary)
            Text(title, fontWeight = FontWeight.Bold, color = SurroundColors.OnSurface)
        }
        Text(desc, style = MaterialTheme.typography.bodyMedium, color = SurroundColors.OnSurfaceVariant)
        Button(
            onClick = onRequest,
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = SurroundColors.Secondary,
                contentColor = SurroundColors.OnSecondary
            )
        ) {
            Text(buttonLabel, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun LevelCheckCard(
    state: RoomLevelCheckState,
    hasPermission: Boolean,
    onToggleCheck: () -> Unit
) {
    val r = LocalAppStrings.current.room
    val (verdictText, verdictColor) = when (state.verdict) {
        LevelCheckVerdict.IDLE -> r.verdictIdle to SurroundColors.OnSurfaceVariant
        LevelCheckVerdict.CALIBRATING_SILENCE -> r.verdictCalibrating to SurroundColors.Tertiary
        LevelCheckVerdict.TOO_QUIET -> r.verdictTooQuiet to SurroundColors.Secondary
        LevelCheckVerdict.OPTIMAL -> r.verdictOptimal to SurroundColors.Success
        LevelCheckVerdict.CLIPPING -> r.verdictClipping to SurroundColors.Error
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(SurroundColors.SurfaceContainer)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = r.levelCheckTitle,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = SurroundColors.OnSurface
                )
                Text(
                    text = r.levelCheckDesc,
                    style = MaterialTheme.typography.bodySmall,
                    color = SurroundColors.OnSurfaceVariant
                )
            }
            Spacer(Modifier.width(8.dp))
            OutlinedButton(
                onClick = onToggleCheck,
                shape = CircleShape
            ) {
                Icon(
                    imageVector = if (state.running) Icons.Rounded.Stop else Icons.Rounded.PlayArrow,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = if (state.running) r.levelCheckStopBtn else r.levelCheckStartBtn,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        if (state.running) {
            val meterProgress = ((state.liveToneDbFs + 80f) / 80f).coerceIn(0.02f, 1f)
            LinearProgressIndicator(
                progress = { meterProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(CircleShape),
                color = verdictColor,
                trackColor = SurroundColors.SurfaceContainerHighest
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = r.levelAmbientLabel(state.ambientNoiseDbFs.roundToInt()),
                    fontFamily = MonoFontFamily,
                    fontSize = 11.sp,
                    color = SurroundColors.OnSurfaceVariant
                )
                Text(
                    text = r.levelLiveLabel(state.liveToneDbFs.roundToInt(), state.snrDb.roundToInt()),
                    fontFamily = MonoFontFamily,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = verdictColor
                )
            }
        }

        Text(
            text = if (!hasPermission) r.micPermissionTitle else verdictText,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = verdictColor
        )
    }
}

@Composable
private fun ActiveSweepProgressCard(
    progress: MeasurementRunProgress,
    startHz: Int,
    endHz: Int,
    stepMode: SweepStepMode,
    scaleMode: DbScaleMode,
    onCancel: () -> Unit
) {
    val r = LocalAppStrings.current.room
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(SurroundColors.SurfaceContainer)
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        val statusText = when (progress.phase) {
            SweepPhase.COUNTDOWN -> r.countdownLabel(progress.countdownSeconds)
            SweepPhase.MEASURING_SILENCE -> r.silenceCalibLabel
            SweepPhase.SWEEPING -> r.sweepingLabel(
                progress.currentStepIndex,
                progress.totalSteps,
                progress.currentFrequencyHz
            )
            else -> ""
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(Icons.Rounded.GraphicEq, contentDescription = null, tint = SurroundColors.Primary)
            Text(
                text = statusText,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = SurroundColors.OnSurface
            )
        }

        val frac = if (progress.totalSteps > 0) {
            (progress.currentStepIndex.toFloat() / progress.totalSteps.toFloat()).coerceIn(0f, 1f)
        } else 0f

        LinearProgressIndicator(
            progress = { frac },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(CircleShape),
            color = SurroundColors.Primary,
            trackColor = SurroundColors.SurfaceContainerHighest
        )

        if (progress.livePoints.isNotEmpty()) {
            val liveSeries = listOf(
                GraphSeries(
                    id = "live",
                    label = "Live",
                    color = SurroundColors.Primary,
                    points = progress.livePoints.map {
                        it.frequencyHz to RoomGraphUtils.valueForScale(it, scaleMode)
                    }
                )
            )
            RoomFrequencyGraph(
                series = liveSeries,
                startHz = startHz,
                endHz = endHz,
                stepMode = stepMode,
                scaleMode = scaleMode,
                height = 200.dp,
                interactive = false
            )
        }

        OutlinedButton(
            onClick = onCancel,
            modifier = Modifier.fillMaxWidth(),
            shape = CircleShape
        ) {
            Icon(Icons.Rounded.Close, contentDescription = null)
            Spacer(Modifier.width(6.dp))
            Text(r.cancelSweepBtn, fontWeight = FontWeight.Bold)
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ActiveSessionResultCard(
    session: RoomMeasurementSession,
    hiddenPositionIds: Set<String>,
    scaleMode: DbScaleMode,
    showMeanCurve: Boolean,
    smoothCurves: Boolean,
    onScaleChange: (DbScaleMode) -> Unit,
    onToggleMean: () -> Unit,
    onToggleSmooth: () -> Unit,
    onTogglePositionVisible: (ListeningPositionTrace) -> Unit,
    onRenameSession: () -> Unit,
    onRenamePosition: (ListeningPositionTrace) -> Unit,
    onDeletePosition: (ListeningPositionTrace) -> Unit,
    onAddAnotherPosition: () -> Unit,
    onSaveSession: () -> Unit,
    onNewMeasurement: () -> Unit,
    onExportPdf: () -> Unit
) {
    val r = LocalAppStrings.current.room

    val series = remember(session, hiddenPositionIds, scaleMode, showMeanCurve, smoothCurves) {
        val traces = session.positions.mapIndexedNotNull { idx, pos ->
            if (pos.id in hiddenPositionIds) return@mapIndexedNotNull null
            val raw = pos.points.map { it.frequencyHz to RoomGraphUtils.valueForScale(it, scaleMode) }
            GraphSeries(
                id = pos.id,
                label = pos.name,
                color = Color(RoomGraphUtils.colorForIndex(idx)),
                points = if (smoothCurves) RoomGraphUtils.smoothCurve(raw) else raw
            )
        }.toMutableList()

        if (showMeanCurve && session.positions.size >= 2) {
            val rawMean = RoomGraphUtils.computeMeanCurve(session.positions, scaleMode)
            traces.add(
                GraphSeries(
                    id = "mean",
                    label = r.meanCurveLabel,
                    color = SurroundColors.OnSurface,
                    points = if (smoothCurves) RoomGraphUtils.smoothCurve(rawMean) else rawMean,
                    dashed = true
                )
            )
        }
        traces
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(SurroundColors.SurfaceContainer)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = session.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = SurroundColors.OnSurface
                )
                Text(
                    text = "${session.setupCode} (${session.channels.joinToString("+")}) • ${session.startHz}–${session.endHz} Hz",
                    fontFamily = MonoFontFamily,
                    fontSize = 11.sp,
                    color = SurroundColors.Primary
                )
            }
            IconButton(onClick = onRenameSession) {
                Icon(Icons.Rounded.Edit, contentDescription = null, tint = SurroundColors.OnSurfaceVariant)
            }
            IconButton(onClick = onExportPdf) {
                Icon(Icons.Rounded.PictureAsPdf, contentDescription = r.exportPdfSingleBtn, tint = SurroundColors.Secondary)
            }
        }

        if (session.note.isNotBlank()) {
            Text(
                text = session.note,
                style = MaterialTheme.typography.bodySmall,
                color = SurroundColors.OnSurfaceVariant
            )
        }

        // Graph
        RoomFrequencyGraph(
            series = series,
            startHz = session.startHz,
            endHz = session.endHz,
            stepMode = session.stepMode,
            scaleMode = scaleMode,
            height = 220.dp
        )

        // Graph Toggle Controls (Linear vs dBA, Mean Curve, Smooth)
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            RoomChip(
                text = r.scaleLinearLabel,
                selected = scaleMode == DbScaleMode.LINEAR_FLAT,
                onClick = { onScaleChange(DbScaleMode.LINEAR_FLAT) }
            )
            RoomChip(
                text = r.scaleDbaLabel,
                selected = scaleMode == DbScaleMode.DBA,
                onClick = { onScaleChange(DbScaleMode.DBA) }
            )
            if (session.positions.size >= 2) {
                RoomChip(
                    text = r.meanCurveLabel,
                    selected = showMeanCurve,
                    onClick = onToggleMean
                )
            }
            RoomChip(
                text = r.smoothLabel,
                selected = smoothCurves,
                onClick = onToggleSmooth
            )
        }

        // Colored Position Legend Chips (Checkbox toggles visibility; pencil renames; X deletes)
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            session.positions.forEachIndexed { idx, pos ->
                val visible = pos.id !in hiddenPositionIds
                val col = Color(RoomGraphUtils.colorForIndex(idx))
                Row(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(
                            if (visible) SurroundColors.SurfaceContainerHigh
                            else SurroundColors.SurfaceContainerHigh.copy(alpha = 0.45f)
                        )
                        .clickable { onTogglePositionVisible(pos) }
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = if (visible) Icons.Rounded.CheckBox else Icons.Rounded.CheckBoxOutlineBlank,
                        contentDescription = null,
                        tint = if (visible) col else SurroundColors.OnSurfaceVariant.copy(alpha = 0.6f),
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = pos.name,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (visible) SurroundColors.OnSurface else SurroundColors.OnSurfaceVariant.copy(alpha = 0.65f)
                    )
                    Icon(
                        imageVector = Icons.Rounded.Edit,
                        contentDescription = null,
                        tint = SurroundColors.OnSurfaceVariant,
                        modifier = Modifier
                            .size(14.dp)
                            .clickable { onRenamePosition(pos) }
                    )
                    if (session.positions.size > 1) {
                        Icon(
                            imageVector = Icons.Rounded.Close,
                            contentDescription = null,
                            tint = SurroundColors.OnSurfaceVariant,
                            modifier = Modifier
                                .size(14.dp)
                                .clickable { onDeletePosition(pos) }
                        )
                    }
                }
            }
        }

        // Actions
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = onAddAnotherPosition,
                modifier = Modifier.weight(1f),
                shape = CircleShape
            ) {
                Icon(Icons.Rounded.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text(r.addAnotherPositionBtn, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
            Button(
                onClick = onSaveSession,
                modifier = Modifier.weight(1f),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = SurroundColors.PrimaryContainer,
                    contentColor = SurroundColors.OnPrimaryContainer
                )
            ) {
                Icon(Icons.Rounded.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text(r.saveSessionBtn, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        TextButton(
            onClick = onNewMeasurement,
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("+ Neue Messsitzung / Reset", fontSize = 12.sp, color = SurroundColors.OnSurfaceVariant)
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ArchiveTabContent(
    savedSessions: List<RoomMeasurementSession>,
    selectedPdfIds: Set<String>,
    scaleMode: DbScaleMode,
    showMeanCurve: Boolean,
    smoothCurves: Boolean,
    onToggleSelectPdf: (String) -> Unit,
    onExportSelectedPdf: () -> Unit,
    onExportSinglePdf: (RoomMeasurementSession) -> Unit,
    onOpenSession: (RoomMeasurementSession) -> Unit,
    onRenameSession: (RoomMeasurementSession) -> Unit,
    onDeleteSession: (RoomMeasurementSession) -> Unit
) {
    val r = LocalAppStrings.current.room
    val dateFmt = remember { SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (savedSessions.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(SurroundColors.SurfaceContainer)
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(r.archiveEmptyTitle, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = SurroundColors.OnSurface)
                Text(r.archiveEmptyBody, style = MaterialTheme.typography.bodyMedium, color = SurroundColors.OnSurfaceVariant)
            }
            return@Column
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = r.selectForExportHint,
                style = MaterialTheme.typography.bodySmall,
                color = SurroundColors.OnSurfaceVariant,
                modifier = Modifier.weight(1f)
            )
            if (selectedPdfIds.isNotEmpty()) {
                Spacer(Modifier.width(8.dp))
                Button(
                    onClick = onExportSelectedPdf,
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SurroundColors.Secondary,
                        contentColor = SurroundColors.OnSecondary
                    )
                ) {
                    Icon(Icons.Rounded.PictureAsPdf, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(r.exportPdfSelectedBtn(selectedPdfIds.size), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }

        savedSessions.forEach { session ->
            val checked = session.id in selectedPdfIds
            val previewSeries = remember(session, scaleMode, showMeanCurve, smoothCurves) {
                val list = session.positions.mapIndexed { idx, pos ->
                    val raw = pos.points.map { it.frequencyHz to RoomGraphUtils.valueForScale(it, scaleMode) }
                    GraphSeries(
                        id = pos.id,
                        label = pos.name,
                        color = Color(RoomGraphUtils.colorForIndex(idx)),
                        points = if (smoothCurves) RoomGraphUtils.smoothCurve(raw) else raw
                    )
                }.toMutableList()
                if (showMeanCurve && session.positions.size >= 2) {
                    val rawMean = RoomGraphUtils.computeMeanCurve(session.positions, scaleMode)
                    list.add(
                        GraphSeries(
                            id = "mean",
                            label = r.meanCurveLabel,
                            color = SurroundColors.OnSurface,
                            points = if (smoothCurves) RoomGraphUtils.smoothCurve(rawMean) else rawMean,
                            dashed = true
                        )
                    )
                }
                list
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(SurroundColors.SurfaceContainer)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = checked,
                        onCheckedChange = { onToggleSelectPdf(session.id) },
                        colors = CheckboxDefaults.colors(checkedColor = SurroundColors.Primary)
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = session.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = SurroundColors.OnSurface
                        )
                        Text(
                            text = "${dateFmt.format(Date(session.createdAtMs))} • ${session.setupCode} (${session.channels.joinToString("+")}) • ${r.positionsCountLabel(session.positions.size)}",
                            fontFamily = MonoFontFamily,
                            fontSize = 11.sp,
                            color = SurroundColors.OnSurfaceVariant
                        )
                    }
                    IconButton(onClick = { onRenameSession(session) }) {
                        Icon(Icons.Rounded.Edit, contentDescription = null, tint = SurroundColors.OnSurfaceVariant)
                    }
                    IconButton(onClick = { onDeleteSession(session) }) {
                        Icon(Icons.Rounded.Delete, contentDescription = null, tint = SurroundColors.Error)
                    }
                }

                if (session.note.isNotBlank()) {
                    Text(
                        text = session.note,
                        style = MaterialTheme.typography.bodySmall,
                        color = SurroundColors.Primary
                    )
                }

                RoomFrequencyGraph(
                    series = previewSeries,
                    startHz = session.startHz,
                    endHz = session.endHz,
                    stepMode = session.stepMode,
                    scaleMode = scaleMode,
                    height = 140.dp,
                    interactive = false
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { onOpenSession(session) },
                        modifier = Modifier.weight(1f),
                        shape = CircleShape
                    ) {
                        Text(r.loadInEditorBtn, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Button(
                        onClick = { onExportSinglePdf(session) },
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SurroundColors.SecondaryContainer,
                            contentColor = SurroundColors.OnSecondaryContainer
                        )
                    ) {
                        Icon(Icons.Rounded.PictureAsPdf, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text(r.exportPdfSingleBtn, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun CompareTabContent(
    savedSessions: List<RoomMeasurementSession>,
    selectedIds: Set<String>,
    scaleMode: DbScaleMode,
    smoothCurves: Boolean,
    onToggleSelect: (String) -> Unit,
    onScaleChange: (DbScaleMode) -> Unit,
    onToggleSmooth: () -> Unit
) {
    val r = LocalAppStrings.current.room

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (savedSessions.size < 2) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(SurroundColors.SurfaceContainer)
                    .padding(24.dp)
            ) {
                Text(r.compareMinTwoHint, style = MaterialTheme.typography.bodyMedium, color = SurroundColors.OnSurfaceVariant)
            }
            return@Column
        }

        Text(r.compareHint, style = MaterialTheme.typography.bodySmall, color = SurroundColors.OnSurfaceVariant)

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            savedSessions.forEach { session ->
                RoomChip(
                    text = session.name,
                    selected = session.id in selectedIds,
                    onClick = { onToggleSelect(session.id) }
                )
            }
        }

        val chosen = savedSessions.filter { it.id in selectedIds }
        if (chosen.size >= 2) {
            val minHz = chosen.minOf { it.startHz }
            val maxHz = chosen.maxOf { it.endHz }
            val anyLog = chosen.any { RoomGraphUtils.shouldUseLogXAxis(it.startHz, it.endHz, it.stepMode) }
            val stepModeForGraph = if (anyLog) SweepStepMode.OCTAVE_FRACTION else SweepStepMode.LINEAR_HZ

            val compareSeries = chosen.mapIndexed { idx, session ->
                val raw = if (session.positions.size >= 2) {
                    RoomGraphUtils.computeMeanCurve(session.positions, scaleMode)
                } else {
                    session.positions.firstOrNull()?.points?.map {
                        it.frequencyHz to RoomGraphUtils.valueForScale(it, scaleMode)
                    } ?: emptyList()
                }
                GraphSeries(
                    id = session.id,
                    label = session.name,
                    color = Color(RoomGraphUtils.colorForIndex(idx)),
                    points = if (smoothCurves) RoomGraphUtils.smoothCurve(raw) else raw
                )
            }

            RoomFrequencyGraph(
                series = compareSeries,
                startHz = minHz,
                endHz = maxHz,
                stepMode = stepModeForGraph,
                scaleMode = scaleMode,
                height = 260.dp
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                RoomChip(
                    text = r.scaleLinearLabel,
                    selected = scaleMode == DbScaleMode.LINEAR_FLAT,
                    onClick = { onScaleChange(DbScaleMode.LINEAR_FLAT) }
                )
                RoomChip(
                    text = r.scaleDbaLabel,
                    selected = scaleMode == DbScaleMode.DBA,
                    onClick = { onScaleChange(DbScaleMode.DBA) }
                )
                RoomChip(
                    text = r.smoothLabel,
                    selected = smoothCurves,
                    onClick = onToggleSmooth
                )
            }

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                compareSeries.forEach { cs ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(cs.color)
                        )
                        Text(cs.label, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = SurroundColors.OnSurface)
                    }
                }
            }
        }
    }
}

@Composable
private fun RoomChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .background(if (selected) SurroundColors.PrimaryContainer else SurroundColors.SurfaceContainerHigh)
            .border(
                width = 1.dp,
                color = if (selected) SurroundColors.Primary else SurroundColors.Outline.copy(alpha = 0.35f),
                shape = RoundedCornerShape(14.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Text(
            text = text,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = if (selected) SurroundColors.OnPrimaryContainer else SurroundColors.OnSurfaceVariant
        )
    }
}

@Composable
private fun RoomGuideDialog(onDismiss: () -> Unit) {
    val r = LocalAppStrings.current.room
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurroundColors.SurfaceContainerHigh,
        title = {
            Text(r.guideTitle, fontWeight = FontWeight.Bold, color = SurroundColors.OnSurface)
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(r.guideSubtitle, style = MaterialTheme.typography.bodySmall, color = SurroundColors.Primary)
                r.guideItems.forEach { (heading, body) ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(SurroundColors.SurfaceContainer)
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.Rounded.CheckCircle, contentDescription = null, tint = SurroundColors.Primary, modifier = Modifier.size(16.dp))
                            Text(heading, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = SurroundColors.OnSurface)
                        }
                        Text(body, fontSize = 12.sp, lineHeight = 17.sp, color = SurroundColors.OnSurfaceVariant)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                    containerColor = SurroundColors.Primary,
                    contentColor = SurroundColors.OnPrimary
                )
            ) {
                Text(r.guideGotIt, fontWeight = FontWeight.Bold)
            }
        }
    )
}

@Composable
private fun RenameSessionDialog(
    initialName: String,
    initialNote: String,
    onConfirm: (String, String) -> Unit,
    onDismiss: () -> Unit
) {
    val s = LocalAppStrings.current
    val r = s.room
    var name by remember { mutableStateOf(initialName) }
    var note by remember { mutableStateOf(initialNote) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurroundColors.SurfaceContainerHigh,
        title = { Text(r.renameSessionTitle, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(r.sessionNameHint) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text(r.sessionNoteHint) },
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(name.trim().ifBlank { initialName }, note.trim()) }) {
                Text(s.apply, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(s.cancel)
            }
        }
    )
}

@Composable
private fun RenamePositionDialog(
    initialName: String,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val s = LocalAppStrings.current
    val r = s.room
    var name by remember { mutableStateOf(initialName) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurroundColors.SurfaceContainerHigh,
        title = { Text(r.renamePositionTitle, fontWeight = FontWeight.Bold) },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text(r.newPositionHint) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(name.trim().ifBlank { initialName }) }) {
                Text(s.apply, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(s.cancel)
            }
        }
    )
}

@Composable
private fun PdfLayoutChoiceDialog(
    onSelectPortrait: () -> Unit,
    onSelectLandscape: () -> Unit,
    onDismiss: () -> Unit
) {
    val s = LocalAppStrings.current
    val r = s.room
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurroundColors.SurfaceContainerHigh,
        title = {
            Text(r.pdfLayoutDialogTitle, fontWeight = FontWeight.Bold, color = SurroundColors.OnSurface)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = r.pdfLayoutDialogBody,
                    style = MaterialTheme.typography.bodyMedium,
                    color = SurroundColors.OnSurfaceVariant
                )
                Button(
                    onClick = onSelectPortrait,
                    modifier = Modifier.fillMaxWidth(),
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SurroundColors.Primary,
                        contentColor = SurroundColors.OnPrimary
                    )
                ) {
                    Icon(Icons.Rounded.PictureAsPdf, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(r.pdfPortraitDefaultBtn, fontWeight = FontWeight.Bold)
                }
                OutlinedButton(
                    onClick = onSelectLandscape,
                    modifier = Modifier.fillMaxWidth(),
                    shape = CircleShape
                ) {
                    Icon(Icons.Rounded.PictureAsPdf, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(r.pdfLandscapeWideBtn, fontWeight = FontWeight.Bold)
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(s.cancel)
            }
        }
    )
}
