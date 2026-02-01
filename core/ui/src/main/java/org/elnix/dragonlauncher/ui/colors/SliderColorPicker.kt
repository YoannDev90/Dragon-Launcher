package org.elnix.dragonlauncher.ui.colors

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.elnix.dragonlauncher.common.utils.colors.adjustBrightness
import org.elnix.dragonlauncher.common.utils.colors.blendWith
import org.elnix.dragonlauncher.common.utils.colors.randomColor
import org.elnix.dragonlauncher.ui.components.DragonIconButton
import org.elnix.dragonlauncher.ui.helpers.SliderWithLabel

@Composable
fun SliderColorPicker(
    actualColor: Color,
    onColorSelected: (Color) -> Unit
) {
    var red by remember { mutableFloatStateOf(actualColor.red) }
    var green by remember { mutableFloatStateOf(actualColor.green) }
    var blue by remember { mutableFloatStateOf(actualColor.blue) }
    var alpha by remember { mutableFloatStateOf(actualColor.alpha) }

    val previousColors = remember { mutableStateListOf<Color>() }
    val color = Color(red, green, blue, alpha)
    val canPopLastColor = previousColors.isNotEmpty()



    fun pushCurrentColor() {
        previousColors.add(color)
    }

    fun popLastColor() {
        if (canPopLastColor) {
            val last = previousColors.removeAt(previousColors.lastIndex)
            red = last.red
            green = last.green
            blue = last.blue
            onColorSelected(color)
        }
    }
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.padding(5.dp)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {

            DragonIconButton(
                onClick = { popLastColor() },
                modifier = Modifier.weight(1f),
                enabled = canPopLastColor
            ) {
                Icon(
                    Icons.Default.Replay,
                    contentDescription = "Reset",
                    tint = MaterialTheme.colorScheme.onSurface.adjustBrightness(if (canPopLastColor) 1f else 0.5f)
                )
            }

            DragonIconButton(
                onClick = {
                    pushCurrentColor()
                    val color = randomColor()
                    red = color.red
                    green = color.green
                    blue = color.blue
                    onColorSelected(color)
                },
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    Icons.Default.Shuffle,
                    contentDescription = "Random Color",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        SliderWithLabel(
            label = "Red :",
            value = red,
            color = Color.Red,
            backgroundColor = MaterialTheme.colorScheme.surface.blendWith(Color.Red, 0.2f),
            valueRange = 0f..1f
        ) {
            red = it
            pushCurrentColor()
            onColorSelected(color)
        }
        SliderWithLabel(
            label = "Green :",
            value = green,
            color = Color.Green,
            backgroundColor = MaterialTheme.colorScheme.surface.blendWith(Color.Green, 0.2f),
            valueRange = 0f..1f
        ) {
            green = it
            pushCurrentColor()
            onColorSelected(color)
        }
        SliderWithLabel(
            label = "Blue :",
            value = blue,
            color = Color.Blue,
            backgroundColor = MaterialTheme.colorScheme.surface.blendWith(Color.Blue, 0.2f),
            valueRange = 0f..1f
        ) {
            blue = it
            pushCurrentColor()
            onColorSelected(color)
        }
    }
}
