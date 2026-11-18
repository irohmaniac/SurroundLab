<p align="center">
  <a href="README.md">English</a> |
  <a href="README.de.md">Deutsch</a> |
  <a href="README.es.md">Español</a> |
  <a href="README.fr.md">Français</a> |
  <a href="README.pt.md">Português</a> |
  <strong>日本語</strong>
</p>

<p align="center">
  <img src="docs/images/ja-JP/02_feature_graphic_1024x500.png" alt="SurroundLab バナー 日本語" width="100%" />
</p>

<h1 align="center">SurroundLab — ディスクリート 5.1 / 7.1ch ホームシアター調整 &amp; 2D立体音響ステージ</h1>

<p align="center">
  <strong>Android TV / Google TV • Android スマホ • ブラウザ &amp; iOS Web リモコン対応</strong><br/>
  <em>HDMI / eARC 経由の本物の 48 kHz マルチチャンネル PCM 出力 — アップミックスの誤魔化しなし。</em>
</p>

---

## SurroundLab とは？

AV アンプでホームシアターを組んだ方なら一度は経験する悩みがあります。YouTube で「5.1ch / 7.1ch サラウンドテスト」を検索しても、実際には 2.0ch ステレオ音声で、AV アンプのアップミキサーが勝手に全スピーカーへ振り分けているだけ。本物のディスクリート 7.1ch テスト動画は YouTube に存在すらしません。

**SurroundLab** は、まさにその自宅シアターでの不満から生まれた、**Android TV・Google TV・スマートフォン**向けの本格アコースティック・ツールキットです。正確なスピーカー調整だけでなく、サラウンドシステムの実力を体感して楽しむこともできます。

圧縮動画ではなく、HDMI / eARC 経由でリアルタイムに **48 kHz ディスクリート・マルチチャンネル PCM ストリーム（2.0 ～ 7.1ch）** を直接生成。選択されていないチャンネルには純粋なデジタル無音（`0.0f`）が送られます。

---

## スクリーンショット

<table>
  <tr>
    <td align="center"><img src="docs/images/ja-JP/phone_02_puck_2d_1080x1920.png" width="175" alt="2D サウンドパック" /></td>
    <td align="center"><img src="docs/images/ja-JP/phone_01_single_speaker_1080x1920.png" width="175" alt="個別スピーカーテスト" /></td>
    <td align="center"><img src="docs/images/ja-JP/phone_03_generator_1080x1920.png" width="175" alt="1Hz 周波数ステッパー" /></td>
    <td align="center"><img src="docs/images/ja-JP/phone_05_room_sweep_1080x1920.png" width="175" alt="ルーム音響・自動測定" /></td>
    <td align="center"><img src="docs/images/ja-JP/phone_04_multi_device_sync_1080x1920.png" width="175" alt="ワイヤレス操作" /></td>
  </tr>
</table>

<table>
  <tr>
    <td align="center"><img src="docs/images/ja-JP/tv_01_home_hub_1920x1080.png" width="440" alt="Android TV ホーム" /></td>
    <td align="center"><img src="docs/images/ja-JP/tv_02_split_stage_puck_1920x1080.png" width="440" alt="Android TV 360° オービット" /></td>
  </tr>
</table>

---

## 4 つのコアモード

- **01 — 個別スピーカーテスト:** `2.0` ～ `7.1ch` の各チャンネルを 1 本ずつ個別に再生。**ピンクノイズ**、**正弦波**、または**手持ちの音源ファイル**で配線や音圧バランスを確認できます。
- **02 — 周波数＆プリセットジェネレーター:** **20 Hz ～ 20 kHz** を **1 Hz 単位**で精密スイープ。部屋の共振（ルームモード）やクロスオーバー、*サブウーファー以外* などのプリセットを素早くチェックできます。
- **03 — 2D 空間サウンドパック:** リビングで音源を 360° リアルタイムに移動させたり、**自動オービット**（低速 / 標準 / 高速）でソファの周りを周回させたりできます。
- **04 — ルーム音響・自動測定（Android アプリ専用）:** 試聴位置で Android スマホのマイクを使い、TV 接続時でもスマホ単体（ケーブル/Bluetooth）でも周波数特性を自動測定します。事前の**レベルチェック**、環境ノイズに強い狭帯域 **Goertzel 解析**、リニア（Hz）/ 対数（オクターブ）ステップ、表示切替チェックボックス付きの**複数試聴位置グラフ**＋**平均カーブ**、**リニア / dBA** 切替、測定比較、**A4 PDF レポート出力**（縦向き 1 ページ 3 件、単体測定時はワイドな横向きも選択可）に対応しています。（※ ブラウザ / iOS Web UI ではローカル HTTP 環境でのマイク制限により本モードは無効化されます。）

---

## ダウンロードとインストール

### Android スマートフォン
1. [**Latest Release**](https://github.com/irohmaniac/SurroundLab/releases/latest) ページを開きます。
2. **`SurroundLab.apk`** をダウンロードしてインストールします。

### Chromecast / Google TV / Android TV（PC 不要）
1. テレビの **Google Play ストア**で **「Downloader by AFTVnews」**（オレンジ色のアイコン）を検索してインストールします。
2. テレビで **Downloader** を開き、コード **`7281113`**（または下の直接 URL）を入力して **Go** を押します：
   ```text
   7281113
   ```
   *(直接 URL: `https://github.com/irohmaniac/SurroundLab/releases/latest/download/SurroundLab.apk`)*
3. インストール許可を求められたら **「設定」→「許可」** をオンにして **「インストール」** を押します。
4. テレビで **SurroundLab** を起動します。同じ Wi-Fi にあればスマホと自動的に接続されます。

---

## 透明性に関する注記

本アプリ（Kotlin/Compose ソースコード、リアルタイム DSP オーディオエンジン、各言語翻訳、紹介用グラフィックを含む）は AI の支援を受けて開発され、実際のホームシアター環境（Chromecast with Google TV、AV アンプ、Android スマートフォン）で検証・調整されています。

