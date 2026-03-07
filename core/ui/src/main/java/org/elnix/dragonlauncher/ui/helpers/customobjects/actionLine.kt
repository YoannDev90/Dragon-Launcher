package org.elnix.dragonlauncher.ui.helpers.customobjects

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.dp
import org.elnix.dragonlauncher.common.serializables.CustomObjectSerializable
import org.elnix.dragonlauncher.common.utils.UiConstants
import org.elnix.dragonlauncher.enumsui.AngleLineObjects
import org.elnix.dragonlauncher.enumsui.AngleLineObjects.Angle
import org.elnix.dragonlauncher.enumsui.AngleLineObjects.End
import org.elnix.dragonlauncher.enumsui.AngleLineObjects.Line
import org.elnix.dragonlauncher.enumsui.AngleLineObjects.Start
import org.elnix.dragonlauncher.ui.helpers.nests.drawNeonGlowArc
import org.elnix.dragonlauncher.ui.helpers.nests.drawNeonGlowLine

fun DrawScope.actionLine(
    start: Offset,
    end: Offset,

    order: List<AngleLineObjects>,

    showLineObjectPreview: Boolean,
    showAngleLineObjectPreview: Boolean,
    showStartObjectPreview: Boolean,
    showEndObjectPreview: Boolean,

    lineCustomObject: CustomObjectSerializable,
    angleLineCustomObject: CustomObjectSerializable,
    startCustomObject: CustomObjectSerializable,
    endCustomObject: CustomObjectSerializable,
    sweepAngle: Float,
    lineColor: Color
) {

    order.forEach { drawObject ->
        when (drawObject) {
            Line -> {
                if (showLineObjectPreview) {
                    lineObject(
                        start = start,
                        end = end,
                        lineColor = lineColor,
                        lineCustomObject = lineCustomObject
                    )
                }
            }
            Angle -> {
                // The angle rotating around the start point (have to fix that and allow more customization) TODO
                // The "do you hate it?" thing in settings
                if (showAngleLineObjectPreview) {
                    angleObject(
                        center = start,
                        sweepAngle = sweepAngle,
                        lineColor = lineColor,
                        angleLineCustomObject = angleLineCustomObject
                    )
                }
            }
            Start -> {
                if (showStartObjectPreview) {
                    customObject(
                        customObject = startCustomObject,
                        default = UiConstants.defaultStartCustomObject,
                        angleColor = lineColor,
                        center = start
                    )
                }
            }
            End -> {
                if (showEndObjectPreview) {
                    customObject(
                        customObject = endCustomObject,
                        default = UiConstants.defaultEndCustomObject,
                        angleColor = lineColor,
                        center = end
                    )
                }
            }
        }
    }
}


private fun DrawScope.lineObject(
    start: Offset,
    end: Offset,
    lineColor: Color,
    lineCustomObject: CustomObjectSerializable,
) {
    val lineGlow = lineCustomObject.glow
    val lineStrokeWidth = (lineCustomObject.stroke ?: UiConstants.defaultLineCustomObject.stroke!!).dp.toPx()

    val glowRadius = if (lineGlow != null) {
        (lineGlow.radius ?: UiConstants.defaultAngleCustomObject.glow!!.radius!!).dp.toPx()
    } else 0f

    val glowColor = if (lineGlow != null) {
        lineGlow.color ?: UiConstants.defaultAngleCustomObject.glow!!.color
    } else null


    drawNeonGlowLine(
        start = start,
        end = end,
        color = lineCustomObject.color ?: lineColor,
        lineStrokeWidth = lineStrokeWidth,
        glowRadius = glowRadius,
        glowColor = glowColor,
        erase = lineCustomObject.eraseBackground ?: UiConstants.defaultLineCustomObject.eraseBackground!!
    )
}

private fun DrawScope.angleObject(
    center: Offset,
    sweepAngle: Float,
    lineColor: Color,
    angleLineCustomObject: CustomObjectSerializable,
) {
    val arcStroke = (angleLineCustomObject.stroke ?: UiConstants.defaultAngleCustomObject.stroke!!).dp.toPx()

    if (arcStroke > 0f) {
        val arcSize =
            (angleLineCustomObject.size ?: UiConstants.defaultAngleCustomObject.size!!)
                .dp.toPx() / 2


        val rect = Rect(
            center.x - arcSize,
            center.y - arcSize,
            center.x + arcSize,
            center.y + arcSize
        )

        val angleGLow = angleLineCustomObject.glow


        val glowRadius = if (angleGLow != null) {
            (angleGLow.radius ?: UiConstants.defaultAngleCustomObject.glow!!.radius!!).dp.toPx()
        } else 0f

        val glowColor = if (angleGLow != null) {
            angleGLow.color
                ?: UiConstants.defaultAngleCustomObject.glow!!.color
        } else null


        drawNeonGlowArc(
            topLeft = rect.topLeft,
            size = Size(rect.width, rect.height),
            startAngle = -90f,
            sweepAngle = sweepAngle,
            color = angleLineCustomObject.color ?: lineColor,
            lineStrokeWidth = arcStroke,
            glowRadius = glowRadius,
            glowColor = glowColor,
            erase = angleLineCustomObject.eraseBackground
                ?: UiConstants.defaultLineCustomObject.eraseBackground!!
        )
    }
}
