package org.elnix.dragonlauncher.ui.wellbeing

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.elnix.dragonlauncher.common.R
import org.elnix.dragonlauncher.settings.stores.WellbeingSettingsStore

/**
 * Activity-based overlay for wellbeing reminders
 * Non-intrusive with transparent background and pass-through touch handling
 */
class OverlayReminderActivity : ComponentActivity() {

    companion object {
        const val EXTRA_APP_NAME = "extra_app_name"
        const val EXTRA_MODE = "extra_mode" // "reminder" or "time_warning"
        
        // Time information
        const val EXTRA_SESSION_TIME = "extra_session_time"
        const val EXTRA_TODAY_TIME = "extra_today_time"
        const val EXTRA_REMAINING_TIME = "extra_remaining_time"
        const val EXTRA_HAS_LIMIT = "extra_has_limit"
        
        fun show(ctx: Context, appName: String, sessionTime: String, todayTime: String, remainingTime: String, hasLimit: Boolean, mode: String = "reminder") {
            if (!Settings.canDrawOverlays(ctx)) return
            val intent = Intent(ctx, OverlayReminderActivity::class.java).apply {
                putExtra(EXTRA_APP_NAME, appName)
                putExtra(EXTRA_SESSION_TIME, sessionTime)
                putExtra(EXTRA_TODAY_TIME, todayTime)
                putExtra(EXTRA_REMAINING_TIME, remainingTime)
                putExtra(EXTRA_HAS_LIMIT, hasLimit)
                putExtra(EXTRA_MODE, mode)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            ctx.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Make activity completely transparent with proper flags for overlay
        window.setBackgroundDrawableResource(android.R.color.transparent)

        // Critical: Make window non-focusable and non-touch-modal to let clicks pass through
        window.addFlags(
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        } else {
            @Suppress("DEPRECATION")
            window.addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
            )
        }

        val appName = intent.getStringExtra(EXTRA_APP_NAME) ?: "App"
        val mode = intent.getStringExtra(EXTRA_MODE) ?: "reminder"
        val sessionTime = intent.getStringExtra(EXTRA_SESSION_TIME) ?: ""
        val todayTime = intent.getStringExtra(EXTRA_TODAY_TIME) ?: ""
        val remainingTime = intent.getStringExtra(EXTRA_REMAINING_TIME) ?: ""
        val hasLimit = intent.getBooleanExtra(EXTRA_HAS_LIMIT, false)

        setContent {
            MaterialTheme {
                var visible by remember { mutableStateOf(false) }
                
                // Load display preferences in Compose scope
                val showSession by WellbeingSettingsStore.popupShowSessionTime.flow(this@OverlayReminderActivity).collectAsState(initial = true)
                val showToday by WellbeingSettingsStore.popupShowTodayTime.flow(this@OverlayReminderActivity).collectAsState(initial = true)
                val showRemaining by WellbeingSettingsStore.popupShowRemainingTime.flow(this@OverlayReminderActivity).collectAsState(initial = true)
                
                LaunchedEffect(Unit) {
                    visible = true
                }
                
                if (visible) {
                    // Use Dialog composable for proper touch pass-through
                    // Clicks outside the card will pass to underlying app
                    Dialog(
                        onDismissRequest = { finish() },
                        properties = DialogProperties(
                            dismissOnBackPress = true,
                            dismissOnClickOutside = true,
                            usePlatformDefaultWidth = false // Important: allows custom sizing
                        )
                    ) {
                        if (mode == "time_warning") {
                            TimeWarningCard(
                                appName = appName,
                                sessionTime = sessionTime,
                                todayTime = todayTime,
                                remainingTime = remainingTime,
                                showSession = showSession,
                                showToday = showToday,
                                showRemaining = showRemaining,
                                onDismiss = { finish() }
                            )
                        } else {
                            ReminderCard(
                                appName = appName,
                                sessionTime = sessionTime,
                                todayTime = todayTime,
                                remainingTime = remainingTime,
                                hasLimit = hasLimit,
                                showSession = showSession,
                                showToday = showToday,
                                showRemaining = showRemaining,
                                onDismiss = { finish() }
                            )
                        }
                    }
                }
            }
        }

        // Auto-dismiss after 7 seconds
        window.decorView.postDelayed({
            if (!isFinishing) {
                finish()
            }
        }, 7000)
    }
}

@Composable
private fun ReminderCard(
    appName: String,
    sessionTime: String,
    todayTime: String,
    remainingTime: String,
    hasLimit: Boolean,
    showSession: Boolean,
    showToday: Boolean,
    showRemaining: Boolean,
    onDismiss: () -> Unit
) {
    val scale = remember { Animatable(0.8f) }
    
    LaunchedEffect(Unit) {
        scale.animateTo(1f, animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ))
    }
    
    Card(
        modifier = Modifier
            .fillMaxWidth(0.92f)
            .scale(scale.value),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // Header with close button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "⏰",
                        fontSize = 24.sp
                    )
                    Column {
                        Text(
                            text = appName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = stringResource(R.string.reminder_overlay_subtext),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                            fontSize = 11.sp
                        )
                    }
                }
                
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Time statistics with separators
            val timeInfos = buildList {
                if (showSession && sessionTime.isNotEmpty()) {
                    add(TimeInfo(
                        label = stringResource(R.string.popup_session_label),
                        value = sessionTime
                    ))
                }
                if (showToday && todayTime.isNotEmpty()) {
                    add(TimeInfo(
                        label = stringResource(R.string.popup_today_label),
                        value = todayTime
                    ))
                }
                if (showRemaining && hasLimit) {
                    add(TimeInfo(
                        label = stringResource(R.string.popup_remaining_label),
                        value = if (remainingTime.isNotEmpty()) remainingTime 
                                else stringResource(R.string.popup_no_limit)
                    ))
                }
            }
            
            if (timeInfos.isNotEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    timeInfos.forEachIndexed { index, info ->
                        TimeInfoItem(
                            label = info.label,
                            value = info.value,
                            modifier = Modifier.weight(1f)
                        )
                        
                        if (index < timeInfos.size - 1) {
                            VerticalDivider(
                                modifier = Modifier
                                    .height(40.dp)
                                    .padding(horizontal = 8.dp),
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TimeWarningCard(
    appName: String,
    sessionTime: String,
    todayTime: String,
    remainingTime: String,
    showSession: Boolean,
    showToday: Boolean,
    showRemaining: Boolean,
    onDismiss: () -> Unit
) {
    val scale = remember { Animatable(0.8f) }
    val pulse = rememberInfiniteTransition(label = "pulse")
    val pulseScale = pulse.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )
    
    LaunchedEffect(Unit) {
        scale.animateTo(1f, animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ))
    }
    
    Card(
        modifier = Modifier
            .fillMaxWidth(0.92f)
            .scale(scale.value * pulseScale.value),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "⏱️",
                        fontSize = 24.sp
                    )
                    Column {
                        Text(
                            text = appName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                        Text(
                            text = stringResource(R.string.time_warning_subtitle),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.7f),
                            fontSize = 11.sp
                        )
                    }
                }
                
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.6f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Time statistics
            val timeInfos = buildList {
                if (showSession && sessionTime.isNotEmpty()) {
                    add(TimeInfo(
                        label = stringResource(R.string.popup_session_label),
                        value = sessionTime
                    ))
                }
                if (showToday && todayTime.isNotEmpty()) {
                    add(TimeInfo(
                        label = stringResource(R.string.popup_today_label),
                        value = todayTime
                    ))
                }
                if (showRemaining && remainingTime.isNotEmpty()) {
                    add(TimeInfo(
                        label = stringResource(R.string.popup_remaining_label),
                        value = remainingTime
                    ))
                }
            }
            
            if (timeInfos.isNotEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    timeInfos.forEachIndexed { index, info ->
                        TimeInfoItem(
                            label = info.label,
                            value = info.value,
                            isWarning = true,
                            modifier = Modifier.weight(1f)
                        )
                        
                        if (index < timeInfos.size - 1) {
                            VerticalDivider(
                                modifier = Modifier
                                    .height(40.dp)
                                    .padding(horizontal = 8.dp),
                                color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.2f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TimeInfoItem(
    label: String,
    value: String,
    isWarning: Boolean = false,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = if (isWarning) 
                MaterialTheme.colorScheme.onErrorContainer 
            else 
                MaterialTheme.colorScheme.onPrimaryContainer,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = if (isWarning)
                MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.7f)
            else
                MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
            textAlign = TextAlign.Center,
            fontSize = 11.sp
        )
    }
}

private data class TimeInfo(val label: String, val value: String)
