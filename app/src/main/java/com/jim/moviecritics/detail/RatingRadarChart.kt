package com.jim.moviecritics.detail

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

/** One polygon on the [RatingRadarChart]: one value per axis, from 0 to the chart's maximum. */
data class RadarSeries(
    val values: List<Float>,
    val color: Color,
)

/**
 * A radar chart drawn with Compose, replacing MPAndroidChart's RadarChart on the detail page.
 * The first axis points up and the others follow clockwise.
 */
@Composable
fun RatingRadarChart(
    labels: List<String>,
    series: List<RadarSeries>,
    maxValue: Float,
    modifier: Modifier = Modifier,
    gridLevels: Int = 5,
    labelStyle: TextStyle = MaterialTheme.typography.labelLarge,
) {
    val textMeasurer = rememberTextMeasurer()
    val gridColor = MaterialTheme.colorScheme.outlineVariant
    val labelColor = MaterialTheme.colorScheme.onSurface

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
    ) {
        val labelLayouts = labels.map {
            textMeasurer.measure(it, labelStyle.copy(color = labelColor))
        }
        val labelSpace = labelLayouts.maxOfOrNull { maxOf(it.size.width, it.size.height) } ?: 0
        val center = Offset(size.width / 2, size.height / 2)
        val radius = min(size.width, size.height) / 2 - labelSpace - 8.dp.toPx()
        if (radius <= 0f) return@Canvas

        fun vertex(axis: Int, fraction: Float): Offset {
            val angle = -PI / 2 + 2 * PI * axis / labels.size
            return Offset(
                x = center.x + radius * fraction * cos(angle).toFloat(),
                y = center.y + radius * fraction * sin(angle).toFloat()
            )
        }

        // Grid: concentric polygons and one spoke per axis
        val gridStroke = Stroke(width = 1.dp.toPx())
        for (level in 1..gridLevels) {
            val fraction = level.toFloat() / gridLevels
            drawPath(polygon(labels.size) { vertex(it, fraction) }, gridColor, style = gridStroke)
        }
        labels.indices.forEach {
            drawLine(gridColor, center, vertex(it, 1f), strokeWidth = gridStroke.width)
        }

        series.forEach { s ->
            val path = polygon(labels.size) {
                vertex(it, (s.values[it] / maxValue).coerceIn(0f, 1f))
            }
            drawPath(path, s.color.copy(alpha = 0.25f))
            drawPath(path, s.color, style = Stroke(width = 2.dp.toPx()))
        }

        // Labels sit just outside the outer polygon, centred on their spoke
        labelLayouts.forEachIndexed { axis, layout ->
            val anchor = vertex(axis, 1f + (labelSpace / 2 + 4.dp.toPx()) / radius)
            drawText(
                textLayoutResult = layout,
                topLeft = Offset(
                    x = anchor.x - layout.size.width / 2,
                    y = anchor.y - layout.size.height / 2
                )
            )
        }
    }
}

private fun polygon(corners: Int, vertex: (Int) -> Offset): Path = Path().apply {
    for (i in 0 until corners) {
        val point = vertex(i)
        if (i == 0) moveTo(point.x, point.y) else lineTo(point.x, point.y)
    }
    close()
}
