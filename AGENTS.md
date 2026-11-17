# AGENTS.md – Verbindliche Entwicklungs-, Sicherheits- & Play-Store-Regeln (SurroundLab)

Dieses Dokument legt verbindliche Architektur-, Sicherheits-, Build- und Qualitätsregeln für alle Coding-Agenten in diesem Projekt fest. Lies vor Arbeitsbeginn zwingend `SPEC.md` (Funktions- & Audio-Spezifikation) sowie – falls im Projekt vorhanden – `DESIGN.md` (UI/UX-Designsystem).

---

## 1. Systemumgebung, Android-SDKs & Build-Prozess

### 1.1 Lokale Umgebung & SDK-Zielwerte (Play-Store-konform)
- **Lokale Tools:** Die Android-Entwicklungsumgebung (JDK, Gradle, Android SDK) ist lokal eingerichtet. Ändere keine globalen Systemvariablen oder SDK-Installationen.
- **SDK-Versionen:**
  - `minSdk = 26` (Android 8.0 – volle Kompatibilität mit Chromecast mit Google TV & modernen Smartphones)
  - `compileSdk` & `targetSdk`: Prüfe im lokalen Android SDK (`$ANDROID_HOME/platforms`), ob **API 36 (Android 16)** installiert ist; falls ja, nutze `36`, andernfalls **API 35 (Android 15)** (erfüllt aktuelle Google-Play-Store-Anforderungen ohne lokale SDK-Downloads zu erzwingen).
  - JVM / Kotlin Target: `17`
- **Paketname (Namespace):** Verwende einen gültigen, nicht generischen Paketnamen (z. B. `de.surroundlab.app` oder `io.surroundlab.app`). Verwende **niemals** `com.example.*` (wird vom Google Play Store abgelehnt).

### 1.2 APK-Build & Release-Vorbereitung
- **Debug-APK (Pflicht nach jedem Meilenstein):**
  - Kompiliere nach jedem abgeschlossenen Meilenstein und zwingend vor Abschluss deiner Arbeit eine installierbare Debug-APK (`./gradlew assembleDebug` bzw. unter Windows `.\gradlew.bat assembleDebug`).
  - Stelle sicher, dass der Build ohne Fehler durchläuft und das Artefakt unter `app/build/outputs/apk/debug/app-debug.apk` liegt.
- **Play-Store-Release-Fähigkeit (`bundleRelease`):**
  - Konfiguriere den `release`-Build-Typ von Beginn an sauber mit `isMinifyEnabled = true`, `isShrinkResources = true` sowie vollständigen `proguard-rules.pro`-Regeln für **Ktor**, **kotlinx.serialization** und **ZXing**, damit später ohne Code-Änderungen ein Google-Play-App-Bundle (`./gradlew bundleRelease`) gebaut werden kann.
  - Stelle 16-KB-Page-Size-Kompatibilität sicher (`packaging { jniLibs { useLegacyPackaging = false } }`) und vermeide veraltete native `.so`-Bibliotheken.
- **ADB-Deployment-Regel (Striktes Verbot automatischer Installation):**
  - Führe **niemals** selbstständig `adb install`, `adb push` oder schreibende ADB-Befehle auf verbundenen Geräten aus.
  - Gib nach erfolgreichem Build den exakten Pfad zur `.apk` aus und frage den Nutzer am Ende deiner Antwort, ob PC, Smartphone oder TV per ADB verbunden sind und du die Installation auf die Geräte übernehmen sollst.

---

## 2. Google Play Store & Universal-APK Standards (Mobile + TV)

### 2.1 Dual-Device-Manifest (Smartphone & Android TV in einer APK)
- Deklariere im `AndroidManifest.xml`:
  - `<uses-feature android:name="android.software.leanback" android:required="false" />`
  - `<uses-feature android:name="android.hardware.touchscreen" android:required="false" />`
- Versehe die `MainActivity` sowohl mit `android.intent.category.LAUNCHER` als auch mit `android.intent.category.LEANBACK_LAUNCHER`.
- **Pflicht-Asset für Android TV (Play Store Richtlinie):** Erstelle eine gültige TV-Banner-Grafik (`res/drawable/tv_banner.xml` oder PNG mit exakt `320x180 px` Seitenverhältnis) und referenziere sie im `<application>`-Tag via `android:banner="@drawable/tv_banner"`. Ohne dieses Banner lehnt Google Play Android-TV-Builds ab.
- **D-Pad-Fokus-Garantie:** Jedes interaktive Element im TV-Modus muss per Steuerkreuz (`DPAD_UP/DOWN/LEFT/RIGHT/CENTER`) erreichbar sein und einen klar sichtbaren Fokus-Indikator besitzen.

### 2.2 Zero-Permission-Strategie & Foreground Services
- **Dateiauswahl ohne Speicherberechtigung:** Nutze zum Auswählen eigener Audiodateien auf dem Smartphone ausschließlich Androids *Storage Access Framework* (`ActivityResultContracts.OpenDocument` mit MIME-Types `audio/*`). Deklariere **keine** `READ_EXTERNAL_STORAGE`- oder `READ_MEDIA_AUDIO`-Berechtigungen.
- **LAN-Erkennung ohne Standortberechtigung:** Nutze `android.net.nsd.NsdManager` (mDNS) mit `INTERNET`, `ACCESS_NETWORK_STATE`, `ACCESS_WIFI_STATE` und `CHANGE_WIFI_MULTICAST_STATE`. Deklariere **keine** Standort- oder `NEARBY_WIFI_DEVICES`-Berechtigungen.
- **API 34+ Foreground Service:** Falls der TV-Host-Server oder die Audio-Engine in einem `Service` läuft, deklariere zwingend `FOREGROUND_SERVICE` und `FOREGROUND_SERVICE_MEDIA_PLAYBACK` im Manifest, setze `android:foregroundServiceType="mediaPlayback"` und starte den Dienst mit `ServiceCompat.startForeground(...)` samt gültiger Medien-Notification.

---

## 3. Arbeitsweise & Code-Architektur

### 3.1 Inkrementelle Meilensteine
Setze das Projekt in dieser Reihenfolge um und validiere jeden Schritt per Gradle-Build:
1. **Grundgerüst & Manifest:** Gradle Kotlin DSL (`libs.versions.toml`), Universal-Manifest, TV-Banner, `network_security_config.xml`, Test-Build.
2. **Domain-Modell & Persistenz:** Lautsprecher-Setups (`2.0` bis `7.1`), DataStore-Einstellungen, Audio-Bibliothek (`UUID`-Speicherung + Metadaten).
3. **Echtzeit-DSP-Engine:** `AudioTrack` (48 kHz Float-PCM, `CHANNEL_OUT_STEREO` / `5POINT1` / `7POINT1_SURROUND`), Sinus-/Pink-Noise-Generatoren, `MediaCodec`-Decoder, 2D-VBAP/Distance-Panner, 80-Hz-LFE-Tiefpass, Soft-Limiter & Fades.
4. **Netzwerk-Layer:** Eingebetteter Ktor-Server (REST + WebSockets), `NsdManager` (mDNS Host & Client Discovery), bidirektionaler `StateFlow`-Sync.
5. **Android TV UI:** Compose-Oberfläche für 10-Foot-Bedienung (Setup, Hub, Einzellautsprecher-Liste + Raumvorschau, Frequenz-Stepper, 2D-Puck mit D-Pad, QR-Code-Dialog).
6. **Android Mobile UI:** Compose-Touch-Oberfläche (Setup, Hub, interaktiver Raumplan mit Einzel-Gears, haptisches Frequenz-Drehrad, Touch-2D-Puck, Audio-Upload-Sheet).
7. **Eingebettete Web-UI:** Responsive HTML5/JS/Canvas-Web-App unter `app/src/main/assets/web/` für Browser/iOS-Fallback über denselben WebSocket-Endpunkt.

### 3.2 Code-Qualität
- **Keine Platzhalter:** Schreibe ausschließlich vollständigen, lauffähigen Code. Kommentare wie `// TODO`, unimplementierte Button-Handler oder Dummy-Werte sind verboten.
- **Clean Architecture / MVVM:** Trenne DSP-Audio-Engine, Ktor-Netzwerk, Repository und UI strikt über Kotlin `StateFlow` und unidirektionalen Datenfluss.

---

## 4. Sicherheits-, Netzwerk- & Hardware-Schutzregeln

### 4.1 Netzwerksicherheit (Ktor & LAN)
- **Eingeschränkter Cleartext-HTTP-Zugriff:** Erlaube unverschlüsseltes `http://` über `res/xml/network_security_config.xml` **ausschließlich** für private RFC-1918-Heimnetzwerke (`192.168.0.0/16`, `10.0.0.0/8`, `172.16.0.0/12`). Setze **niemals** ein globales `android:usesCleartextTraffic="true"` (würde Play-Store-Sicherheitswarnungen auslösen).
- **Sitzungs-Token:** Generiere beim Start des TV-Servers einen 6-stelligen alphanumerischen Zufalls-Token, übermittle ihn per mDNS-TXT-Record an die Android-App und bette ihn in den Browser-QR-Code (`http://<TV-IP>:8080/?token=<TOKEN>`) ein. Lehne WebSocket- oder Upload-Anfragen ohne gültigen Token ab.
- **Sichere Datei-Uploads (Anti-Path-Traversal):**
  - Verwende **niemals** den vom Client gesendeten Dateinamen als Dateipfad. Speichere hochgeladene Audiodateien ausschließlich unter einer neu generierten `UUID` (`<uuid>.audio`) im internen App-Speicher (`context.filesDir/custom_audio/`). Der Originalname wird bereinigt als reiner Anzeigetext in den Metadaten gespeichert.
  - Validiere vor dem Speichern, dass `MediaExtractor` mindestens eine gültige Audio-Spur erkennt (andernfalls HTTP `400 Bad Request`).
  - Begrenze Uploads auf maximal **30 MB pro Datei**.

### 4.2 Hardware-, Speicher- & Gehörschutz
- **RAM-Budget für Chromecast / Android TV:** Begrenze den In-Memory-Cache für dekodierte Float-PCM-Tracks auf maximal **64 MB** (LRU-Cache, der älteste ungenutzte Tracks auslagert und bei Bedarf aus `filesDir` neu dekodiert), um `OutOfMemoryError` auf 2-GB-TV-Sticks auszuschließen.
- **Zero-Allocation im Audio-Thread:** Innerhalb der Echtzeit-Schleife, die `AudioTrack.write()` füttert, sind Objekt-Allokationen (`new`, Listen-Erzeugung, String-Formatierung, JSON-Parsing, Logging) strikt untersagt, um Garbage-Collection-Dropouts und Knackser zu vermeiden.
- **Standby-Sperre:** Aktiviere im TV-Host-Modus sowie während laufender Audio-Wiedergabe `FLAG_KEEP_SCREEN_ON`, `WifiManager.WifiLock` und `WifiManager.MulticastLock` und gib alle Locks bei `onDestroy()` sauber frei.
- **Gehör- & Lautsprecherschutz:**
  - Setze die Standard-Ausgangsamplitude auf **-12 dBFS (`0.25f`)** und integriere immer einen Soft-Clip-Limiter (`tanh` / Clamp bei `±0.98f`), um Clipping und Hochtöner-Schäden bei hochgedrehtem AV-Receiver zu verhindern.
  - Erzwinge bei jedem Start, Stopp und Kanalwechsel eine **15-ms-Raised-Cosine-Fade-In/Fade-Out-Rampe** sowie eine **10-ms-Glättung** bei Frequenz- und Gain-Änderungen.