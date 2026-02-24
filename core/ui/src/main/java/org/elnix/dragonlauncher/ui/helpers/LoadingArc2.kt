package org.elnix.dragonlauncher.ui.helpers

import android.graphics.Path
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.unit.dp
import androidx.graphics.shapes.CornerRounding
import androidx.graphics.shapes.RoundedPolygon
import androidx.graphics.shapes.star
import androidx.graphics.shapes.toPath

/**
 * Displays a rotating “hold-to-activate” progress indicator centered around a given point.
 *
 * This composable renders a star-shaped path that progressively reveals itself based on
 * [progress] (0f–1f). While progress increases, a continuously running rotation animation
 * is applied, producing a dynamic activation effect typically used for long-press actions
 * (e.g., opening settings).
 *
 * The shape is procedurally generated using a [RoundedPolygon] star, scaled to the
 * provided [radius], and partially drawn using [PathMeasure] so only the corresponding
 * segment of the path is visible.
 *
 * If [rgbLoading] is true, the stroke color cycles through the HSV spectrum according
 * to the current progress value. Otherwise, [defaultColor] is used.
 *
 * Rendering is skipped when:
 * - [center] is null
 * - [progress] <= 0f
 *
 * @param center The pivot point (in parent coordinates) around which the shape is drawn
 *               and rotated.
 * @param progress A value in the range 0f..1f representing how much of the shape
 *                 should be revealed.
 * @param radius The radius (in dp) used to size and scale the generated shape.
 * @param rgbLoading Whether to dynamically compute the stroke color using HSV
 *                   based on progress.
 * @param defaultColor The fallback stroke color when [rgbLoading] is false.
 */
@Composable
fun HoldToActivateArc2(
    center: Offset?,
    progress: Float,     // 0f..1f
    radius: Int,
    rgbLoading: Boolean,
    defaultColor: Color
) {
    if (center == null || progress <= 0f) return

    val color =
        if (rgbLoading) Color.hsv(progress * 360f, 1f, 1f)
        else defaultColor


    val starPolygon = remember {
        RoundedPolygon.star(
            numVerticesPerRadius = 12,
            innerRadius = 1f / 3f,
            rounding = CornerRounding(1f / 6f)
        )
    }

    val infiniteTransition = rememberInfiniteTransition(label = "infinite")

    val rotation = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "rotation"
    )

    var morphPath = remember { androidx.compose.ui.graphics.Path() }

    val pathMeasurer = remember {
        PathMeasure()
    }
    val destinationPath = remember {
        androidx.compose.ui.graphics.Path()
    }

    var androidPath = remember {
        Path()
    }
    val matrix = remember {
        Matrix()
    }

    val radius = radius.dp

    Box(
        modifier = Modifier
            .size(radius * 2)
            .drawWithCache {
                androidPath = starPolygon.toPath(androidPath)
                morphPath = androidPath.asComposePath()
                matrix.reset()
                matrix.scale(size.minDimension / 2f, size.minDimension / 2f)
                morphPath.transform(matrix)


                pathMeasurer.setPath(morphPath, false)
                val totalLength = pathMeasurer.length
                destinationPath.reset()
                pathMeasurer.getSegment(0f, totalLength * progress, destinationPath)

                onDrawBehind {
                    withTransform({
                        rotate(
                            degrees = rotation.value,
                            pivot = center
                        )
                        translate(center.x, center.y)
                    }) {
                        drawPath(
                            path = destinationPath,
                            color = color,
                            style = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round)
                        )
                    }
                }
            }
    )
}
