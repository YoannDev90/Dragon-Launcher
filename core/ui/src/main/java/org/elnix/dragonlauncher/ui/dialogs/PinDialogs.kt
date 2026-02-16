@file:Suppress("AssignedValueIsNeverRead")

package org.elnix.dragonlauncher.ui.dialogs

import android.content.Context
import android.media.AudioManager
import android.media.SoundPool
import androidx.activity.compose.BackHandler
import androidx.compose.animation.Animatable
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.elnix.dragonlauncher.common.R
import org.elnix.dragonlauncher.common.utils.semiTransparentIfDisabled
import org.elnix.dragonlauncher.common.utils.vibrate
import org.elnix.dragonlauncher.settings.stores.BehaviorSettingsStore
import org.elnix.dragonlauncher.ui.components.settings.asState
import org.elnix.dragonlauncher.ui.modifiers.rememberPressedShape

/**
 * Dialog for entering a PIN to unlock settings.
 */
@Composable
fun PinUnlockDialog(
    onDismiss: () -> Unit,
    pin: () -> String,
    failedTries: () -> Int,
    onPinChanged: (String) -> Unit,
    onValidate: () -> Unit,
    errorMessage: String? = null
) {
    val pin = pin()
    val failedTries = failedTries()

    FullScreenPinPrompt(
        title = stringResource(R.string.unlock_settings),
        subtitle = stringResource(R.string.enter_pin),
        pinValue = pin,
        onPinChanged = onPinChanged,
        onPrimaryAction = onValidate,
        onDismiss = onDismiss,
        errorMessage = errorMessage,
        failedTries = failedTries
    )
}


/**
 * Dialog for setting up a new PIN (enter + confirm).
 */
@Composable
fun PinSetupDialog(
    onDismiss: () -> Unit,
    onPinSet: (String) -> Unit
) {
    var firstPin by remember { mutableStateOf("") }
    var confirmPin by remember { mutableStateOf("") }
    var isConfirmStep by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var failedTries by remember { mutableStateOf(0) }
    val pinMismatch = stringResource(R.string.pin_mismatch)

    val currentPin = if (isConfirmStep) confirmPin else firstPin

    FullScreenPinPrompt(
        title = stringResource(R.string.set_pin),
        subtitle = if (isConfirmStep) stringResource(R.string.confirm_pin) else stringResource(R.string.enter_pin),
        pinValue = currentPin,
        onPinChanged = { newValue ->
            errorMessage = null
            if (isConfirmStep) {
                confirmPin = newValue
            } else {
                firstPin = newValue
            }
        },
        onPrimaryAction = {
            if (!isConfirmStep) {
                isConfirmStep = true
                confirmPin = ""
            } else {
                when {
                    // Error
                    firstPin != confirmPin -> {
                        errorMessage = pinMismatch
                        confirmPin = ""
                        failedTries++
                    }

                    else -> onPinSet(firstPin)
                }
            }
        },
        onDismiss = onDismiss,
        errorMessage = errorMessage,
        failedTries = failedTries,
        onSecondaryAction = {
            if (isConfirmStep) {
                isConfirmStep = false
                confirmPin = ""
                errorMessage = null
            } else {
                onDismiss()
            }
        }
    )
}

@Composable
private fun FullScreenPinPrompt(
    title: String,
    subtitle: String,
    pinValue: String,
    onPinChanged: (String) -> Unit,
    onPrimaryAction: () -> Unit,
    onDismiss: () -> Unit,
    errorMessage: String? = null,
    failedTries: Int,
    minDigits: Int = 1,
    maxDigits: Int = Int.MAX_VALUE,
    onSecondaryAction: () -> Unit = onDismiss
) {
    val ctx = LocalContext.current


    val horizontalOffsetError = Animatable(
        initialValue = 0f
    )

    LaunchedEffect(failedTries) {
        if (failedTries > 0) {
            var left = true
            repeat(5) {
                horizontalOffsetError.animateTo(
                    animationSpec = tween(
                        durationMillis = 100,
                        easing = LinearEasing
                    ),
                    targetValue = if (left) -5f
                    else 5f
                )
                left = !left
            }
            horizontalOffsetError.animateTo(0f)
        }
    }

    val superWaningMode by BehaviorSettingsStore.superWarningMode.asState()
    val superWaningModeSound by BehaviorSettingsStore.superWarningModeSound.asState()

    val backgroundOverlayColor = Animatable(
        Color.Transparent
    )

    val soundPool = remember {
        SoundPool.Builder()
            .setMaxStreams(1)
            .build()
    }
    val soundId = soundPool.load(ctx, R.raw.warning, 1)

    DisposableEffect(Unit) {
        onDispose {
            soundPool.release()
        }
    }

    LaunchedEffect(failedTries) {
        if (failedTries > 0 && superWaningMode) {

            val audioManager = ctx.getSystemService(Context.AUDIO_SERVICE) as AudioManager
            val max = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)

            if (superWaningModeSound > 0f) {
                audioManager.setStreamVolume(
                    AudioManager.STREAM_MUSIC,
                    superWaningModeSound.coerceAtMost(max),
                    0
                )


                // Plays annoying sound alarm infinitely
                soundPool.setOnLoadCompleteListener { _, sampleId, status ->
                    if (status == 0) {
                        soundPool.play(
                            sampleId,
                            1f,
                            1f,
                            1,
                            -1,
                            1f
                        )
                    }
                }
            }

            while (true) {
                backgroundOverlayColor.animateTo(Color.Red)
                vibrate(ctx, 500L)
                backgroundOverlayColor.animateTo(Color.Transparent)
            }
        }
    }

    // Lock color animation system
    val defaultLockColor = MaterialTheme.colorScheme.primary
    val errorColor = MaterialTheme.colorScheme.error

    val lockColor = Animatable(
        initialValue = defaultLockColor
    )

    LaunchedEffect(failedTries) {
        if (failedTries > 0) {
            lockColor.animateTo(errorColor)
        }
    }

    LaunchedEffect(errorMessage) {
        if (errorMessage == null) {
            lockColor.animateTo(defaultLockColor)
        }
    }


    BackHandler(onBack = onDismiss)

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundOverlayColor.value)
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = lockColor.value,
                    modifier = Modifier
                        .offset(x = horizontalOffsetError.value.dp)
                        .size(34.dp)
                )

                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface

                )

                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                // Draws the pin digits TODO use shapes
                PinIndicator(length = pinValue.length)

                AnimatedVisibility(errorMessage != null) {
                    if (errorMessage != null) {
                        Text(
                            text = errorMessage,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            NumericPinPad(
                modifier = Modifier.fillMaxWidth(),
                onDigit = { digit ->
                    if (pinValue.length < maxDigits) {
                        onPinChanged(pinValue + digit)
                    }
                },
                validateEnabled = pinValue.length >= minDigits,
                onValidate = onPrimaryAction,
                backSpaceOrClose = pinValue.isNotEmpty(),
                onClear = {
                    if (pinValue.isEmpty()) onSecondaryAction()
                    else onPinChanged("")
                }
            )
        }
    }
}

@Composable
private fun PinIndicator(length: Int) {
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        repeat(length) {// index ->
//            val filled = index < length
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(
                        color = /*if (filled)*/ MaterialTheme.colorScheme.primary, /*else Color.Transparent,*/
                        shape = CircleShape
                    )
            )
        }
    }
}

@Composable
private fun NumericPinPad(
    modifier: Modifier,
    validateEnabled: Boolean,
    backSpaceOrClose: Boolean,
    onDigit: (String) -> Unit,
    onValidate: () -> Unit,
    onClear: () -> Unit
) {
    val rows = listOf(
        listOf("1", "2", "3"),
        listOf("4", "5", "6"),
        listOf("7", "8", "9")
    )

    val spacing = 20.dp


    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(spacing)
    ) {
        rows.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing)
            ) {
                row.forEach { digit ->
                    KeypadButton(
                        text = digit,
                        modifier = Modifier.weight(1f),
                        onClick = onDigit
                    )
                }
            }
        }



        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(spacing)
        ) {
            AnimatedContent(
                targetState = backSpaceOrClose,
                modifier = Modifier.weight(1f)
            ) {
                val icon = if (it) Icons.AutoMirrored.Filled.Backspace
                else Icons.Default.Close

                KeypadButton(
                    icon = icon,
                    tint = MaterialTheme.colorScheme.error,
                    onClick = onClear
                )
            }

            KeypadButton(
                text = "0",
                modifier = Modifier.weight(1f),
                onClick = onDigit
            )

            KeypadButton(
                icon = Icons.Default.Check,
                tint = Color.Green,
                modifier = Modifier.weight(1f),
                onClick = onValidate,
                enabled = validateEnabled
            )
        }
    }
}

@Composable
private fun KeypadButton(
    enabled: Boolean = true,
    icon: ImageVector,
    tint: Color,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
) {

    Box(
        modifier = modifier.keyPadModifier(enabled, onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint
        )
    }
}

@Composable
private fun KeypadButton(
    text: String,
    modifier: Modifier = Modifier,
    onClick: (String) -> Unit,
) {

    Box(
        modifier = modifier.keyPadModifier { onClick(text) },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.ExtraBold
        )
    }
}


@Composable
private fun Modifier.keyPadModifier(
    enabled: Boolean = true,
    onClick: (() -> Unit)? = null
): Modifier {

    val (shape, interactionSource) = rememberPressedShape()

    return this
        .aspectRatio(1f)
        .clip(shape)
        .then(
            onClick?.let { click ->
                Modifier.clickable(
                    enabled = enabled,
                    interactionSource = interactionSource,
                    onClick = click
                )
            } ?: Modifier
        )
        .background(MaterialTheme.colorScheme.surface.semiTransparentIfDisabled(enabled))
        .padding(15.dp)
}
