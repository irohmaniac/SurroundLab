# Projekt-Spezifikation: SurroundLab – Universal Android TV & Mobile Surround-Sound-Test-Suite

## 1. Projektziel & Systemarchitektur
Entwickle eine **Universal-Android-App** (eine gemeinsame APK/Codebasis in **Kotlin** mit **Jetpack Compose**), die sowohl auf **Android TV / Chromecast mit Google TV** als auch auf **Android-Smartphones** läuft. Ergänzend stellt die TV-App eine eingebettete **Web-App (Browser/iOS-Fallback)** bereit.

### 1.1 Dual-Device-Rollen & Automatische Erkennung
Beim App-Start prüft die Anwendung den Gerätetyp über `UiModeManager.currentModeType == Configuration.UI_MODE_TYPE_TELEVISION`:
1. **TV-Modus (Host & Mehrkanal-Audio-Engine):**
   - Startet die D-Pad-optimierte 10-Foot-TV-Oberfläche (Compose for TV / Material 3).
   - Initialisiert die native Mehrkanal-PCM-Audio-Engine (`AudioTrack` bis 7.1).
   - Startet einen eingebetteten **Ktor-Server** (HTTP REST + WebSockets, Standard-Port `8080`).
   - Ermittelt die eigene lokale WLAN-IPv4-Adresse (`ConnectivityManager` / `NetworkInterface`) und registriert sich per **mDNS / Android NSD** (`NsdManager`) unter dem Service-Typ `_surroundtest._tcp.`.
2. **Smartphone-Modus (Controller & Lokaler Stereo-Modus):**
   - Startet die Touch-optimierte Smartphone-UI.
   - Sucht per **mDNS / Android NSD** automatisch nach aktiven TV-Hosts im WLAN und verbindet sich per WebSocket (inkl. Fallback für manuelle IP-Eingabe).
   - **Standalone / Lokal-Modus (ohne TV):** Nutzer können die App auch ohne verbundenen TV auf dem Smartphone nutzen (z. B. für Kopfhörer-Tests oder zur Verwaltung eigener Audiodateien). In diesem Modus läuft die Audio-Engine lokal auf dem Smartphone in **2.0 Stereo** mit automatischem Downmix aller Mehrkanal-Presets.
3. **Browser- / iOS-Fallback (Embedded Web App):**
   - Der Ktor-Server auf dem TV liefert unter `http://<TV-IP>:8080` eine im `assets/`-Ordner gebündelte Single-Page-Web-App (HTML5, CSS3, Vanilla JS, HTML5 Canvas, WebSockets) aus.
   - Auf dem TV gibt es eine Aktion **„Mit Browser / iOS verbinden“**, die mithilfe von **ZXing** einen **QR-Code** mit der URL `http://<TV-IP>:8080` samt Klartext-IP auf dem Bildschirm anzeigt.

### 1.2 Echtzeit-Synchronisation (Single Source of Truth)
- Ist ein Smartphone/Browser mit dem TV verbunden, liegt die **Single Source of Truth** beim TV-Host.
- Alle Zustandsänderungen (aktiver Modus, Lautsprecher-Setup, aktiver Kanal, Frequenz, Signalart, Puck-Koordinaten, Panning-Modus, Subwoofer-Toggle, Play/Pause, Audio-Bibliothek) werden bidirektional per **WebSocket (JSON-Nachrichten)** synchronisiert, sodass TV-Bildschirm und Controller-UI jederzeit exakt denselben Zustand anzeigen.

---

## 2. Tech-Stack & Abhängigkeiten
- **Sprache:** Kotlin 2.0+, Coroutines & Flow (`StateFlow` für UI-State).
- **UI:** Jetpack Compose (Material 3 + `androidx.tv.material3` für TV-Komponenten), Navigation Compose.
- **Audio / DSP:** Android `android.media.AudioTrack` (`ENCODING_PCM_FLOAT`, `48000 Hz`), `MediaExtractor` + `MediaCodec` (Decodierung eigener Audio-Dateien zu 48 kHz Float-PCM).
- **Netzwerk & Server:** Ktor Embedded Server (`ktor-server-cio` oder `netty`, `ktor-server-websockets`, `ktor-server-content-negotiation`), Android `NsdManager` (mDNS).
- **Persistenz:** Jetpack `DataStore` (Preferences) für Einstellungen; interner App-Speicher (`context.filesDir/custom_audio/`) + JSON-Metadatenindex für hochgeladene Audiodateien.
- **QR-Code:** `com.google.zxing:core` zur lokalen Generierung des QR-Codes auf dem TV.

---

## 3. Lautsprecher-Setups & Android-Kanal-Mapping

### 3.1 Unterstützte Setups
Beim **ersten App-Start** erscheint ein Setup-Assistent zur Auswahl der vorhandenen Lautsprecherkonfiguration (jederzeit später über *Einstellungen* änderbar, gespeichert in DataStore):
- `2.0` (FL, FR)
- `2.1` (FL, FR, LFE)
- `3.1` (FL, C, FR, LFE)
- `5.0` (FL, C, FR, SL, SR)
- `5.1` (FL, C, FR, LFE, SL, SR)
- `7.1` (FL, C, FR, LFE, SL, SR, SBL, SBR)

### 3.2 `AudioTrack`-Kanalmasken & Stille-Padding (Hardware-Kompatibilität)
Da Android TV / HDMI-Schnittstellen nicht-standardisierte Kanalmasken (wie 3.1 oder 5.0) oft verwerfen oder auf 2.0 Stereo herabstufen, gilt folgende Mapping-Regel:
- **Setup `2.0`:** Öffne `AudioTrack` mit `AudioFormat.CHANNEL_OUT_STEREO` (2 Kanäle: `[FL, FR]`).
- **Setups `2.1`, `3.1`, `5.0`, `5.1`:** Öffne `AudioTrack` immer als vollwertigen 6-Kanal-Stream mit `AudioFormat.CHANNEL_OUT_5POINT1` (Reihenfolge nach Android/WAVE-Standard: `0: FL`, `1: FR`, `2: FC/Center`, `3: LFE/Sub`, `4: BL/SL`, `5: BR/SR`). Kanäle, die im gewählten Setup nicht existieren (z. B. `FC`, `SL`, `SR` bei `2.1` oder `LFE` bei `5.0`), werden im DSP-Loop kontinuierlich mit digitaler Stille (`0.0f`) beschrieben. Die UI zeigt jedoch ausschließlich die im Setup gewählten Lautsprecher an.
- **Setup `7.1`:** Öffne `AudioTrack` mit `AudioFormat.CHANNEL_OUT_7POINT1_SURROUND` (8 Kanäle: `0: FL`, `1: FR`, `2: FC`, `3: LFE`, `4: BL/SBL`, `5: BR/SBR`, `6: SL`, `7: SR`).
- **Smartphone-Standalone-Modus (2.0 Stereo):** Ausgabe erfolgt immer über `CHANNEL_OUT_STEREO` mit ITU-R BS.775 Downmix:
  - `L_out = FL + 0.707f * FC + 0.707f * SL + 0.707f * SBL + 0.5f * LFE`
  - `R_out = FR + 0.707f * FC + 0.707f * SR + 0.707f * SBR + 0.5f * LFE`
- **HDMI-Diagnose:** Beim Start auf dem TV prüft die App über `AudioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS)`, ob ein HDMI-Ausgang mit Mehrkanal-Unterstützung erkannt wird, und zeigt bei reiner Stereo-Erkennung einen dezenten Hinweis in den Einstellungen (z. B. Chromecast-Audioformat auf „Automatisch“ prüfen / direkt an AV-Receiver anschließen).

---

## 4. Navigationskonzept (Hub-and-Spoke)
- **Keine horizontalen Swipe-Tabs:** Um versehentliche Tab-Wechsel bei D-Pad-Navigation auf dem TV zu verhindern, nutzt die App eine strikte **Hub-and-Spoke-Struktur**.
- **Startbildschirm (Hub):**
  - Kopfzeile: Verbindungsstatus-Badge (`TV Host aktiv: 192.168.x.x` / `Verbunden mit TV` / `Lokal-Modus 2.0`), aktuelles Setup-Badge (z. B. `5.1`), Button für **Audio-Bibliothek**, Button für **QR-Code** (auf TV) und **Einstellungen-Zahnrad**.
  - Hauptbereich: **3 große Modus-Kacheln**:
    1. *Einzellautsprecher-Test*
    2. *Frequenz- & Preset-Generator*
    3. *2D-Raum-Puck*
- **Zurück-Verhalten (`BackHandler`):**
  - Die Hardware-/Fernbedienungs-Zurücktaste sowie der sichtbare **Zurück-Pfeil oben links** auf dem Smartphone schließen zuerst geöffnete Dialoge/Bottom-Sheets und kehren andernfalls immer sauber zum Startbildschirm (Hub) zurück.
  - Beim Verlassen eines Modus wird die laufende Audio-Wiedergabe sauber mit einer 15-ms-Ausblendung gestoppt.

---

## 5. Funktionale Spezifikation der 3 Kern-Modi

### 5.1 Modus 1: Einzellautsprecher-Test (Single Speaker Check)
- **Zweck:** Gezieltes Ansteuern einzelner Lautsprecher zur Kanalzuordnung und Klangprüfung.
- **Konfigurierbare Tonquelle pro Lautsprecher (oder global für alle übernehmbar):**
  1. *Rosa Rauschen (Pink Noise)* – Standardwert.
  2. *Sinuston (Sine Wave)* – frei wählbare Frequenz (`20 Hz – 20.000 Hz`, Standard `400 Hz`).
  3. *Eigene Audiodatei* – Auswahl aus der lokalen Audio-Bibliothek (Endlosschleife).
- **Smartphone-UI:**
  - Visueller 2D-Raumgrundriss mit den aktiven Lautsprechern des Setups an ihren räumlichen Positionen um den Hörplatz (FL, C, FR oben; SL, SR seitlich; SBL, SBR hinten; LFE vorne).
  - Jeder Lautsprecher ist eine interaktive Kachel/Node mit:
    - **Hauptfläche / Play-Icon:** Einmal Antippen startet die Wiedergabe auf genau diesem Kanal (mit animiertem Puls-Ring); erneutes Antippen stoppt (Antippen eines anderen Lautsprechers wechselt die Wiedergabe nahtlos).
    - **Kleines Zahnrad-Icon am Lautsprecher:** Öffnet ein Bottom-Sheet zur Auswahl der Tonquelle/Frequenz für diesen Kanal.
- **TV-UI (D-Pad-optimiert):**
  - Zweispaltiges Layout: Links (~55 %) eine vertikale Liste (`LazyColumn`) der aktiven Lautsprecher; rechts (~45 %) eine visuelle Raumgrafik, die den aktuell fokussierten/spielenden Lautsprecher farblich hervorhebt.
  - **Steuerung:**
    - `Hoch / Runter`: Navigiert vertikal durch die Lautsprecherliste.
    - `OK / Enter` (Fokus auf der Zeilen-Hauptfläche): Startet/Stoppt den Testton auf diesem Lautsprecher.
    - `Rechts`: Verschiebt den Fokus auf das **Zahnrad-Icon** rechts in der jeweiligen Zeile -> `OK` öffnet das Tonauswahl-Dialogfenster. `Links` kehrt zur Hauptzeile zurück.

### 5.2 Modus 2: Frequenz- & Preset-Generator
- **Zweck:** Abspielen von Sinustönen oder Rosa Rauschen auf definierten Lautsprechergruppen (z. B. Subwoofer-Raummoden, Trennfrequenz-Check, Stereo-Imaging).
- **Kanal-Presets:**
  1. `Alle aktiven Kanäle` (alle im Setup konfigurierten Lautsprecher gleichzeitig).
  2. `2.1` (FL + FR + LFE; falls kein LFE im Setup, ausgeblendet oder deaktiviert).
  3. `2.0` (FL + FR).
  4. `0.1 (Nur Subwoofer)` (nur LFE; sichtbar, wenn Setup `.1` enthält).
  5. `Benutzerdefiniert` (zeigt eine Checkbox-Leiste aller im Setup vorhandenen Lautsprecher zum individuellen Zu-/Abschalten).
- **Signalarten & Frequenz:**
  - Signalarten: `Sinus` (Standard) und `Rosa Rauschen`.
  - Frequenzbereich: `20 Hz` bis `20.000 Hz` (ganzzahlig in Hz).
  - **Intelligente Startfrequenz:** Standard beim Öffnen ist **400 Hz**. Wechselt der Nutzer auf das Preset `0.1 (Nur Subwoofer)`, springt die Frequenz automatisch auf **60 Hz** (sofern sie vorher über 150 Hz lag); beim Zurückwechseln auf ein Satelliten-Preset springt sie wieder auf **400 Hz**.
- **Smartphone-UI:**
  - Preset-Auswahlchips + (bei *Benutzerdefiniert*) Lautsprecher-Toggles.
  - Großes **taktiles Drehrad (Rotary Jog-Wheel)** in Compose Canvas mit Skalenstrichen, feiner Beschleunigung und **haptischem Feedback** (`HapticFeedbackType.TextHandleMove` / `CLOCK_TICK`) bei jedem Frequenzschritt.
  - Antippen der großen Frequenz-Anzeige öffnet ein numerisches Eingabefeld zur direkten Hz-Eingabe; dazu `-1 Hz` / `+1 Hz` Feintuning-Buttons und ein großer Play/Pause-Button.
- **TV-UI (D-Pad-optimiert):**
  - Initialer Fokus liegt direkt auf dem zentralen **Frequenz-Regler-Block**.
  - `Hoch / Runter`: Ändert die Frequenz (`±1 Hz` bei kurzem Druck; stufenweise Beschleunigung auf `±5 Hz` / `±25 Hz` bei gedrückter Taste).
  - `OK / Enter`: Play / Pause.
  - `Links / Rechts`: Wechselt zu den Preset-Chips / Custom-Lautsprechern / Signalart-Umschalter.

### 5.3 Modus 3: 2D-Raum-Puck (Live Surround Panner)
- **Zweck:** Echtzeit-Positionierung und Bewegung einer Schallquelle durch den Surround-Raum.
- **Koordinatensystem & Panning-Modi (umschaltbar):**
  - Normierte 2D-Bühne `x ∈ [-1.0, +1.0]` (Links bis Rechts), `y ∈ [-1.0, +1.0]` (Hinten bis Vorne), Hörposition im Zentrum `(0.0, 0.0)`.
  - **Modus A: „Sweet-Spot-Fokus“ (Standard – Winkel-Panning / Constant-Power VBAP):**
    - Lautsprecher liegen auf virtuellen Azimut-Winkeln um den eingemessenen Hörplatz (`C: 0°`, `FL: -30°`, `FR: +30°`, `SL: -110°`, `SR: +110°`, `SBL: -150°`, `SBR: +150°`).
    - Aus der Puck-Position `(x, y)` wird der Winkel `θ = atan2(x, y)` berechnet. Zwischen den beiden benachbarten aktiven Lautsprechern wird nach dem **Constant-Power-Gesetz** (`gain_1 = cos(α)`, `gain_2 = sin(α)`) überblendet, sodass die Gesamtlautstärke beim Kreisen um den Zuhörer konstant bleibt. Im Zentrum `(0, 0)` verteilen sich die Pegel gleichmäßig auf die aktiven Satelliten.
    - *Hinweis:* Es werden bewusst **keine** künstlichen Laufzeitverzögerungen (Delays) berechnet, um die Einmessung des AV-Receivers (z. B. Audyssey) nicht zu verfälschen.
  - **Modus B: „Raum-Position“ (Abstands-Dämpfung):**
    - Berechnet die euklidische Distanz `d_i` zwischen Puck `(x, y)` und jedem Lautsprecher `(x_i, y_i)` mit inverser Abstandsgewichtung (`w_i = 1.0f / (d_i^2.5f + 0.02f)`), normiert auf Summe der Energie. Zieht man den Puck direkt auf einen Lautsprecher, spielt dieser zu 100 %, während gegenüberliegende Lautsprecher komplett stumm werden.
- **Subwoofer-Verhalten (`.1` / LFE):**
  - Eigener Umschalter **„Subwoofer (.1) aktiv“** (sichtbar, sofern Setup `.1` enthält).
  - Der Subwoofer unterliegt **keiner** räumlichen X/Y-Richtungsdämpfung. Ist der Schalter aktiv, erhält der LFE-Kanal das Signal über einen **80-Hz-Tiefpassfilter (2. Ordnung Butterworth)** in konstanter Referenzlautstärke; ist er deaktiviert, erhält LFE `0.0f` Stille.
- **Audioquellen & Auto-Orbit:**
  - Wählbar zwischen `Rosa Rauschen`, `Sinuston` (einstellbare Hz) und `Eigene Audiodatei` (nahtloser Loop).
  - **„Auto-Orbit“-Button:** Bewegt den Puck automatisch auf einer Kreisbahn um den Hörplatz (Geschwindigkeit wählbar: Langsam, Mittel, Schnell).
- **Smartphone- vs. TV-Steuerung:**
  - **Smartphone:** Direkte Touch-Steuerung des Pucks auf dem 2D-Canvas (WebSocket-Updates auf max. 30–60 Hz gedrosselt für flüssige Netzwerklast), Play/Pause-Button, Auto-Orbit-Toggle, Panning-Modus-Umschalter, Subwoofer-Switch, Quellenwahl.
  - **TV:** Beim Öffnen ist der Puck direkt aktiv (visueller Fokus-Ring um den Puck). `Hoch / Runter / Links / Rechts` verschieben den Puck flüssig über die Bühne (mit sanfter Beschleunigung beim Gedrückthalten); `OK / Enter` schaltet Play/Pause; über eine seitliche Steuerleiste (erreichbar über Fokus-Wechsel/Menü) lassen sich Quelle, Panning-Modus, Auto-Orbit und Subwoofer umschalten.

---

## 6. Eigene Audiodateien & Synchronisation
- **Unterstützte Formate:** `.wav`, `.mp3`, `.flac`, `.ogg`.
- **Decodierung:** Hochgeladene Dateien werden über Android `MediaExtractor` + `MediaCodec` dekodiert, bei Bedarf auf `48000 Hz` resampelt und als `FloatArray` (Mono- oder Stereo-Summe für das Panning) im RAM gecacht, damit im DSP-Thread keinerlei Disk-I/O oder Garbage-Collection-Ruckler entstehen.
- **Persistenz & Sync:**
  - Dateien werden dauerhaft unter `context.filesDir/custom_audio/<uuid>.bin` (oder Originaldatei + Index) gespeichert.
  - Beim Upload am Smartphone oder über den Browser (`POST /api/audio/upload` als `multipart/form-data`) speichert der TV-Host die Datei lokal und sendet ein WebSocket-Event `LIBRARY_UPDATED` an alle Clients.
  - Wurden Dateien offline auf dem Smartphone hinzugefügt, gleicht die App beim Verbinden mit dem TV die SHA-256-Hashes ab und überträgt fehlende Dateien automatisch auf den TV.

---

## 7. DSP-Qualität & Hardware-/Gehörschutz (Pflicht-Anforderungen)
- **Klickfreie Übergänge:** Jeder Start, Stopp und Kanalwechsel nutzt eine **15-ms-Fade-In-/Fade-Out-Hüllkurve** (Raised Cosine). Änderungen an Frequenz oder Kanal-Gains werden im Audio-Thread sampleweise über **10 ms** geglättet (One-Pole-Smoothing), um Knackser zu verhindern.
- **Digitaler Headroom & Limiter:** Die Standard-Ausgangsamplitude liegt bei **-12 dBFS (`0.25f`)** (einstellbar über einen Master-Pegel-Regler von `-30 dB` bis `0 dB`) kombiniert mit einem Soft-Clip-Limiter bei `±0.98f`, damit reine Sinustöne oder Mehrkanal-Summen Hochtöner und Gehör schützen, falls der AV-Receiver weit aufgedreht ist.

---

## 8. REST- & WebSocket-Protokoll (TV-Server Port 8080)
- `GET /` -> Liefert die eingebettete Web-App (`assets/web/index.html`).
- `GET /api/state` -> Gibt den vollständigen aktuellen `AppState` als JSON zurück.
- `GET /api/audio` -> Liste aller gespeicherten eigenen Audiodateien (`id`, `name`, `durationMs`, `sha256`).
- `POST /api/audio/upload` -> `multipart/form-data` Upload einer Audiodatei; speichert auf TV und triggert WebSocket-Broadcast.
- `DELETE /api/audio/{id}` -> Löscht eine Audiodatei auf dem TV und triggert WebSocket-Broadcast.
- `WS /ws` -> Bidirektionaler JSON-Stream für:
  - `STATE_SYNC` (TV -> Clients: vollständiges State-Objekt)
  - `SET_SETUP` (`2.0` | `2.1` | `3.1` | `5.0` | `5.1` | `7.1`)
  - `SET_MODE` (`HUB` | `SINGLE_SPEAKER` | `GENERATOR` | `PUCK_2D`)
  - `PLAY_SINGLE_SPEAKER` (`channelId`, `playing`, `sourceType`, `frequencyHz`, `audioId`)
  - `UPDATE_GENERATOR` (`preset`, `customChannels`, `signalType`, `frequencyHz`, `playing`)
  - `UPDATE_PUCK` (`x`, `y`, `panningMode`, `subwooferEnabled`, `autoOrbit`, `orbitSpeed`, `sourceType`, `frequencyHz`, `audioId`, `playing`)