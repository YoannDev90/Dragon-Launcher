package org.elnix.dragonlauncher.ui.dialogs

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.elnix.dragonlauncher.common.R
import org.elnix.dragonlauncher.ui.UiConstants.DragonShape
import org.elnix.dragonlauncher.ui.colors.AppObjectsColors

/**
 * Dialog for entering a PIN to unlock settings.
 */
@Composable
fun PinUnlockDialog(
    onDismiss: () -> Unit,
    onPinEntered: (String) -> Unit,
    errorMessage: String? = null
) {
    var pin by remember { mutableStateOf("") }
    FullScreenPinPrompt(
        title = stringResource(R.string.unlock_settings),
        subtitle = stringResource(R.string.enter_pin),
        pinValue = pin,
        onPinChanged = { pin = it },
        primaryText = stringResource(R.string.unlock_settings),
        onPrimaryAction = { onPinEntered(pin) },
        onDismiss = onDismiss,
        errorMessage = errorMessage,
        minDigits = 4
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

    val pinTooShort = stringResource(R.string.pin_too_short)
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
        primaryText = if (isConfirmStep) stringResource(R.string.set_pin) else stringResource(R.string.next),
        onPrimaryAction = {
            if (!isConfirmStep) {
                if (firstPin.length < 4) {
                    errorMessage = pinTooShort
                } else {
                    isConfirmStep = true
                    confirmPin = ""
                }
            } else {
                when {
                    firstPin.length < 4 -> errorMessage = pinTooShort
                    firstPin != confirmPin -> errorMessage = pinMismatch
                    else -> onPinSet(firstPin)
                }
            }
        },
        onDismiss = onDismiss,
        errorMessage = errorMessage,
        minDigits = 4,
        secondaryText = if (isConfirmStep) stringResource(R.string.back) else stringResource(R.string.cancel),
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
    primaryText: String,
    onPrimaryAction: () -> Unit,
    onDismiss: () -> Unit,
    errorMessage: String? = null,
    minDigits: Int = 4,
    secondaryText: String = stringResource(R.string.cancel),
    onSecondaryAction: () -> Unit = onDismiss
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnClickOutside = false,
            dismissOnBackPress = true
        )
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
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
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(34.dp)
                    )
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    PinIndicator(length = pinValue.length)
                    if (errorMessage != null) {
                        Text(
                            text = errorMessage,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                NumericPinPad(
                    onDigit = { digit ->
                        if (pinValue.length < 8) {
                            onPinChanged(pinValue + digit)
                        }
                    },
                    onBackspace = {
                        if (pinValue.isNotEmpty()) {
                            onPinChanged(pinValue.dropLast(1))
                        }
                    },
                    onClear = { onPinChanged("") }
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    TextButton(
                        onClick = onSecondaryAction,
                        colors = AppObjectsColors.cancelButtonColors(),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(secondaryText)
                    }

                    Button(
                        onClick = onPrimaryAction,
                        enabled = pinValue.length >= minDigits,
                        colors = AppObjectsColors.buttonColors(),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(primaryText)
                    }
                }
            }
        }
    }
}

@Composable
private fun PinIndicator(length: Int) {
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        repeat(8) { index ->
            val filled = index < length
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(
                        color = if (filled) MaterialTheme.colorScheme.primary else Color.Transparent,
                        shape = CircleShape
                    )
            )
        }
    }
}

@Composable
private fun NumericPinPad(
    onDigit: (String) -> Unit,
    onBackspace: () -> Unit,
    onClear: () -> Unit
) {
    val rows = listOf(
        listOf("1", "2", "3"),
        listOf("4", "5", "6"),
        listOf("7", "8", "9")
    )

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        rows.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                row.forEach { digit ->
                    KeypadButton(
                        modifier = Modifier.weight(1f),
                        onClick = { onDigit(digit) }
                    ) {
                        Text(text = digit, style = MaterialTheme.typography.titleLarge)
                    }
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            KeypadButton(
                modifier = Modifier.weight(1f),
                onClick = onClear
            ) {
                Text(text = stringResource(R.string.clear_all), style = MaterialTheme.typography.labelLarge)
            }
            KeypadButton(
                modifier = Modifier.weight(1f),
                onClick = { onDigit("0") }
            ) {
                Text(text = "0", style = MaterialTheme.typography.titleLarge)
            }
            KeypadButton(
                modifier = Modifier.weight(1f),
                onClick = onBackspace
            ) {
                Icon(
                    imageVector = Icons.Default.Backspace,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
private fun KeypadButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier,
        onClick = onClick,
        shape = DragonShape,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp),
            contentAlignment = Alignment.Center
        ) {
            content()
        }
    }
}
