# ScrollableGraph

A lightweight, scrollable line chart that works in both Jetpack Compose and classic XML Views.
One API, two worlds.

Perfect when you’re migrating a legacy app to Compose or you simply want a single chart library usable from any UI layer.

<img src="https://github.com/jongmin1217/ComposeScrollGraph/blob/main/readme/Sample.gif"></img>

### Features
- Compose Composable: ScrollableGraph(chartOption = …)
- XML View: <com.bellmin.scrollablegraph.view.ScrollableGraphView …/>
- Multiple lines with per-line styling (color, thickness, caps, dashed)
- X/Y axis labels, grid lines, formatters
- Fixed or horizontally scrollable X-axis
- “DP” and “PX” friendly APIs (use Compose types or platform ints/floats)
- Optional custom fonts (resource based)

### Installation
Replace the coordinates with your group/artifact and latest version.
```kotlin
repositories {
    maven("https://jitpack.io")
}
```
```kotlin
dependencies {
    implementation("com.github.jongmin1217:ComposeScrollGraph:<latest_version>")
}
```

### Requirements
- Android Gradle Plugin 8.x+
- Kotlin 1.9+
- Min SDK: your project’s minSdk (the library supports modern Android; set as needed)

If you only use the XML View, you do not need to enable Compose in your app module; the library includes what it needs.

### Quick Start
1) Jetpack Compose
```kotlin
@Composable
fun ExampleCompose() {
    val lines = listOf(
        ChartLine(
            label = "Series A",
            points = listOf(0f to 1f, 1f to 3f, 2f to 2.2f, 3f to 4.5f),
            style = LineStyleDp(
                color = Color(0xFF4CAF50),
                thickness = 2.dp,
                isRoundCap = true,
                pattern = LinePattern.Solid
            )
        )
    )

    val xLabels = XAxisOption(
        option = XLabelOption.ShowDp(
            labelIndices = listOf(
                0.0 to "Mon", 1.0 to "Tue", 2.0 to "Wed", 3.0 to "Thu"
            ),
            style = LabelStyleDp(
                color = Color(0xFF757575),
                fontSize = 12.dp
            ),
            gridLine = GridLineStyleDp(
                color = Color(0xFFEEEEEE),
                thickness = 1.dp,
                pattern = LinePattern.DashedDp(dashLength = 6.dp, gapLength = 4.dp)
            ),
            textSpace = 8.dp
        )
    )

    val yLabels = YAxisOption(
        option = YLabelOption.ShowDp(
            labelCount = 6,
            min = 0f,
            max = 5f,
            style = LabelStyleDp(
                color = Color(0xFF424242),
                fontSize = 12.dp
            ),
            formatter = "%.1f",      // e.g., 1.0, 2.0 …
            gridLine = GridLineStyleDp(
                color = Color(0xFFE0E0E0),
                thickness = 1.dp,
                pattern = LinePattern.DashedDp()
            ),
            textSpace = 8.dp
        )
    )

    ScrollableGraph(
        chartOption = LineChartOption(
            lines = lines,
            xAxis = xLabels,
            yAxis = yLabels,
            xAxisMode = XAxisMode.Scrollable(
                visibleRange = 2f,   // visible X-window
                step = 1f            // one label per “1.0”
            ),
            chartFrame = ChartFrameOption() // outer frame lines (all Show by default)
        )
    )
}
```
2) XML View
layout.xml
```xml
<com.bellmin.scrollablegraph.view.ScrollableGraphView
    android:id="@+id/graph"
    android:layout_width="match_parent"
    android:layout_height="220dp" />

```
Activity/Fragment
```kotlin
val graph = findViewById<ScrollableGraphView>(R.id.graph)

// Lines
val lines = listOf(
    ChartLine(
        label = "Series A",
        points = listOf(0f to 1f, 1f to 3f, 2f to 2.2f, 3f to 4.5f),
        // Use PX variant to stay XML/platform friendly
        style = LineStylePx(
            color = android.R.color.holo_blue_dark, // resource int
            thickness = 2f,                         // px
            isRoundCap = true,
            pattern = LinePattern.Solid
        )
    )
)

// X axis (PX variant)
val xAxis = XAxisOption(
    option = XLabelOption.ShowPx(
        labelIndices = listOf(0.0 to "Mon", 1.0 to "Tue", 2.0 to "Wed", 3.0 to "Thu"),
        style = LabelStylePx(
            color = android.R.color.darker_gray, // resource int
            fontSize = 12f                       // px
        ),
        gridLine = GridLineStylePx(
            color = android.R.color.darker_gray,
            thickness = 1f,
            pattern = LinePattern.DashedPx()
        ),
        textSpace = 8f // px
    )
)

// Y axis (PX variant)
val yAxis = YAxisOption(
    option = YLabelOption.ShowPx(
        labelCount = 6,
        min = 0f,
        max = 5f,
        style = LabelStylePx(
            color = android.R.color.black,
            fontSize = 12f
        ),
        formatter = "%.1f",
        gridLine = GridLineStylePx(
            color = android.R.color.darker_gray,
            thickness = 1f,
            pattern = LinePattern.DashedPx()
        ),
        textSpace = 8f // px
    )
)

graph.setChart(lines)
graph.setXAxis(xAxis)
graph.setYAxis(yAxis)
graph.setXAxisMode(XAxisMode.Scrollable(visibleRange = 2f, step = 1f))
// Optional: frame around the chart area (defaults to Show on all sides)
// graph.setChartFrame(ChartFrameOption(...))
```
### API Overview
#### Core Model
| Type                              | Purpose                                                             |
| --------------------------------- | ------------------------------------------------------------------- |
| `LineChartOption`                 | Top-level config passed to graph (lines, axes, frame, scroll mode). |
| `ChartLine(label, points, style)` | One line’s data and visual style.                                   |
| `XAxisMode`                       | `FixedRange` or `Scrollable(visibleRange, step)`.                   |
#### Styling (DP & PX twins)
| Compose-friendly       | XML/platform-friendly  | Notes                                                                                           |
| ---------------------- | ---------------------- | ----------------------------------------------------------------------------------------------- |
| `LineStyleDp`          | `LineStylePx`          | Color (Compose `Color` vs resource `Int`), thickness (`Dp` vs `Float px`), round caps, pattern. |
| `GridLineStyleDp`      | `GridLineStylePx`      | Color/thickness/pattern for grid lines.                                                         |
| `LabelStyleDp`         | `LabelStylePx`         | Axis label color, font size, optional font family, weight.                                      |
| `LinePattern.Solid`    | `LinePattern.Solid`    | Shared.                                                                                         |
| `LinePattern.DashedDp` | `LinePattern.DashedPx` | Dash/gap in `Dp` vs `px`.                                                                       |
#### Axes
| Axis          | Hide/Show                  | Key Fields                                                                  |
| ------------- | -------------------------- | --------------------------------------------------------------------------- |
| `XAxisOption` | `Hide`, `ShowDp`, `ShowPx` | `labelIndices: List<Pair<Double,String>>`, `style`, `gridLine`, `textSpace` |
| `YAxisOption` | `Hide`, `ShowDp`, `ShowPx` | `labelCount`, `min`, `max`, `style`, `gridLine`, `formatter`, `textSpace`   |
Formatting tip: formatter = "%.0f" will render integers (e.g., 1, 2).
Use "%.1f" for one decimal (e.g., 1.0, 2.0).

### DP vs PX: When to use what?
- Compose screens → Prefer …Dp variants (Color, Dp, FontFamily).
- XML/Views → Prefer …Px variants (@ColorRes Int, px floats).
  The library converts PX to DP internally so visuals are density-aware.

### Scrollable X-Axis
XAxisMode.Scrollable(visibleRange, step) lets the chart expand horizontally and scroll.
- visibleRange: size of the window on the X domain (e.g., 10f)
- step: distance between ticks/labels in X domain (e.g., 1f)

Example: if your data spans 0f..100f, visibleRange=20f shows 20 units at a time, with labels every step units.

### Custom Fonts
You can pass a FontFamily (Compose) or a resource list (XML):
```kotlin
// Compose
LabelStyleDp(
  fontFamily = FontFamily(
    listOf(
      androidx.compose.ui.text.font.Font(R.font.my_regular, FontWeight.Normal),
      androidx.compose.ui.text.font.Font(R.font.my_bold, FontWeight.Bold)
    )
  )
)
```
```kotlin
// XML side
LabelStylePx(
    fontFamily = listOf(
        GraphFontList(resId = R.font.my_regular, isBold = false),
        GraphFontList(resId = R.font.my_bold,    isBold = true)
    )
)
```

### Practical Tips
- Avoid generating extreme widths on the scrollable chart (huge step counts × tiny visibleRange).
  If you compute width as containerWidth × (totalRange / visibleRange), keep the ratio in a sane bound.
- For .Scrollable, pick a step that correlates with your label density to prevent overdraw and unreadable ticks.
- You can mix DP and PX: e.g., Compose screen with LineStyleDp and XAxisOption.ShowPx is fine—the library normalizes units.

### Example: Build X Labels Programmatically
```kotlin
fun dayLabels(range: IntRange): List<Pair<Double, String>> =
    range.map { day -> day.toDouble() to "Day $day" }

// usage
val xAxis = XAxisOption(
    option = XLabelOption.ShowDp(
        labelIndices = dayLabels(0..30),
        style = LabelStyleDp(fontSize = 12.dp),
        gridLine = GridLineStyleDp(pattern = LinePattern.DashedDp())
    )
)
```

### FAQs
- Q. Do I need Compose if I only use the XML View?
- A. No. The library hides Compose internally; you don’t need to enable Compose in your app module.

- Q. Can I use resource colors on Compose screens?
- A. Yes—use the Px variants or convert via your own @Composable helpers if you prefer.

- Q. How do dashed lines work?
- A. Use LinePattern.DashedDp (Compose) or LinePattern.DashedPx (XML). The library converts PX to DP under the hood.

### License
ComposeScrollGraph is distributed under the terms of the Apache License (Version 2.0). See the [license](https://github.com/jongmin1217/ComposeRatingBar/blob/main/LICENSE) for more information.