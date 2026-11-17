---
name: "SurroundLab — Material You Expressive"
description: "Modern Google Material 3 Expressive Design System for SurroundLab, embracing dynamic tonal surfaces, Google Sans / Roboto typography, organic pill shapes, tactile tonal cards, and accessible tonal palettes."
---

## Overview

SurroundLab im modernen **Material You (Material 3 Expressive)** Stil. Basierend auf Googles modernsten Designrichtlinien (Android 14/15, Jetpack Compose Material 3):
- **Tonal Palette & Dynamic Color**: Harmonische Farbtöne basierend auf HCT (Hue, Chroma, Tone). Deep Indigo-Teal Primary (`#006A6A` / `#4DDAD5`), weiche Tonal Containers (`#CCE8E7`, `#004F4F`), Secondary Tangerine/Amber (`#8B5000` / `#FFB77B`), warme und einladende Surface Containers (`#191C1C`, `#202525`, `#2B3130`).
- **Expressive Shapes**: Organische, großzügige Radien (`rounded-3xl` 28px, `rounded-2xl` 20px, Pill-Badges `rounded-full`), sanfte Segmented Buttons, fließende Tonal Elevation ohne harte "Cyber"-Kanten.
- **Natürliche Android-Ergonomie**: Große Touch-Targets, native Material 3 Floating Pills, Assist Chips, Tonal Icon Buttons und klare visuelle Hierarchien.
- **Entwickler-Vorteil**: 1:1 kompatibel mit Googles offiziellen Jetpack Compose Material 3 Bibliotheken (`androidx.compose.material3.*`) für Android Mobile und Android TV (`androidx.tv.material3.*`), was die spätere Programmierung dramatisch vereinfacht und beschleunigt.

## Colors

Die Farbpalette nutzt Google Material 3 Farbrollen mit standardisierten Tonalitäten für Dark Theme (Cinema-tauglich) und Light Theme.

```yaml
colors:
  primary: "#4DDAD5"
  on-primary: "#003737"
  primary-container: "#004F4F"
  on-primary-container: "#6FF7F2"
  secondary: "#FFB77B"
  on-secondary: "#4C2700"
  secondary-container: "#6B3B00"
  on-secondary-container: "#FFDCC1"
  tertiary: "#ADC6FF"
  on-tertiary: "#102F60"
  tertiary-container: "#2B4678"
  on-tertiary-container: "#D8E2FF"
  background: "#0E1414"
  on-background: "#DEE4E3"
  surface: "#111717"
  on-surface: "#DEE4E3"
  surface-variant: "#1E2625"
  on-surface-variant: "#BEC8C7"
  surface-container: "#192020"
  surface-container-high: "#232B2B"
  surface-container-highest: "#2E3635"
  outline: "#3E4948"
  outline-variant: "#4A5554"
  error: "#FFB4AB"
  on-error: "#690005"
  success: "#7ADB94"
  on-success: "#003919"
  light:
    primary: "#006A6A"
    on-primary: "#FFFFFF"
    surface: "#FAFDFC"
    on-surface: "#191C1C"
  dark:
    primary: "#4DDAD5"
    on-primary: "#003737"
    surface: "#111717"
    on-surface: "#DEE4E3"
```

## Typography

Material 3 nutzt **Roboto** / **Google Sans** für Display, Headline, Title, Body und Label sowie **Roboto Mono** für technische Audio-Metriken.

```yaml
typography:
  display-lg:
    fontFamily: "Roboto"
    fontSize: 44px
    fontWeight: 700
    lineHeight: 52px
    letterSpacing: -0.02em
  display-lg-mobile:
    fontFamily: "Roboto"
    fontSize: 32px
    fontWeight: 700
    lineHeight: 40px
    letterSpacing: -0.01em
  headline-xl:
    fontFamily: "Roboto"
    fontSize: 36px
    fontWeight: 700
    lineHeight: 44px
    letterSpacing: -0.015em
  headline-xl-mobile:
    fontFamily: "Roboto"
    fontSize: 28px
    fontWeight: 700
    lineHeight: 36px
  headline-lg:
    fontFamily: "Roboto"
    fontSize: 28px
    fontWeight: 600
    lineHeight: 36px
  headline-lg-mobile:
    fontFamily: "Roboto"
    fontSize: 22px
    fontWeight: 600
    lineHeight: 30px
  title-lg:
    fontFamily: "Roboto"
    fontSize: 22px
    fontWeight: 600
    lineHeight: 28px
  title-md:
    fontFamily: "Roboto"
    fontSize: 16px
    fontWeight: 500
    lineHeight: 24px
    letterSpacing: 0.01em
  body-lg:
    fontFamily: "Roboto"
    fontSize: 16px
    fontWeight: 400
    lineHeight: 24px
    letterSpacing: 0.02em
  body-md:
    fontFamily: "Roboto"
    fontSize: 14px
    fontWeight: 400
    lineHeight: 20px
    letterSpacing: 0.02em
  label-lg:
    fontFamily: "Roboto"
    fontSize: 14px
    fontWeight: 500
    lineHeight: 20px
    letterSpacing: 0.01em
  label-sm:
    fontFamily: "Roboto"
    fontSize: 11px
    fontWeight: 500
    lineHeight: 16px
    letterSpacing: 0.03em
  mono-display:
    fontFamily: "Roboto Mono"
    fontSize: 40px
    fontWeight: 700
    lineHeight: 48px
  mono-display-mobile:
    fontFamily: "Roboto Mono"
    fontSize: 28px
    fontWeight: 700
    lineHeight: 36px
  mono-lg:
    fontFamily: "Roboto Mono"
    fontSize: 20px
    fontWeight: 600
    lineHeight: 28px
  mono-sm:
    fontFamily: "Roboto Mono"
    fontSize: 12px
    fontWeight: 500
    lineHeight: 16px
```

## Layout & Spacing

Material 3 basiert auf einem weichen 8dp/16dp Rhythmus mit organischen Padding-Stufen.

```yaml
spacing:
  gutter-xs: 4px
  gutter-sm: 8px
  gutter-md: 16px
  gutter-lg: 24px
  gutter-xl: 32px
  section: 48px
  page-margin: 20px
  tv-margin: 48px
```

## Elevation & Depth

Material 3 Expressive ersetzt altmodische Schatten durch **Tonal Elevation**: Höhere Schichten erhalten hellere Tonal-Container (`surface-container-high` und `surface-container-highest`) mit sehr sanften, dezenten Ton-Differenzierungen statt harter Linien.

## Shapes

Ausdrucksstarke Radien nach M3 Expressive Standards:
- Standard Buttons: `rounded-full` (Pill Shape)
- Tonal Cards: `rounded-2xl` (20px) bis `rounded-3xl` (28px)
- Chips: `rounded-xl` (12px) bis `rounded-full` (9999px)

```yaml
rounded:
  sm: 8px
  DEFAULT: 16px
  md: 20px
  lg: 24px
  xl: 28px
  full: 9999px
```

## Motion & Micro-interaction

Googles M3 Easing: `cubic-bezier(0.2, 0, 0, 1)` mit natürlichen Dehnungs- und Tonal-Effekten.

```yaml
motion:
  state-change:
    duration: 150ms
    delay: 0ms
    timingFunction: cubic-bezier(0.2, 0, 0, 1)
  focus-ring:
    duration: 200ms
    delay: 0ms
    timingFunction: cubic-bezier(0.05, 0.7, 0.1, 1)
  wave-pulse:
    duration: 800ms
    delay: 0ms
    timingFunction: cubic-bezier(0.2, 0, 0, 1)
```

## Voice & Content Strategy

Freundlich, modern, intuitiv und nach Google Android Richtlinien. Knappe, klare Aktionsbeschreibungen.
