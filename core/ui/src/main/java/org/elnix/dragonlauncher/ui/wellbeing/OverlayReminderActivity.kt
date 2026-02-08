package org.elnix.dragonlauncher.ui.wellbeing

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.MotionEvent
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
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
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.elnix.dragonlauncher.common.R
import org.elnix.dragonlauncher.settings.stores.WellbeingSettingsStore

/**
 * Service that displays a non-intrusive overlay popup using ComposeView
 * Allows clicks to pass through to underlying app except on the card itself
 */
class OverlayReminderActivity : Service() {

    companion object {
        const val EXTRA_APP_NAME = "extra_app_name"
        const val EXTRA_MODE = "extra_mode" // "reminder" or "time_warning"
        
        // Time information
        const val EXTRA_SESSION_TIME = "extra_session_time"
        const val EXTRA_TODAY_TIME = "extra_today_time"
        const val EXTRA_REMAINING_TIME = "extra_remaining_time"
        const val EXTRA_HAS_LIMIT = "extra_has_limit"
        
        private const val TAG = "OverlayReminderActivity"
        
        fun show(ctx: Context, appName: String, sessionTime: String, todayTime: String, remainingTime: String, hasLimit: Boolean, mode: String = "reminder") {
            if (!Settings.canDrawOverlays(ctx)) {
                Log.w(TAG, "Cannot show overlay: permission not granted")
                return
            }
            try {
                val intent = Intent(ctx, OverlayReminderActivity::class.java).apply {
                    putExtra(EXTRA_APP_NAME, appName)
                    putExtra(EXTRA_SESSION_TIME, sessionTime)
                    putExtra(EXTRA_TODAY_TIME, todayTime)
                    putExtra(EXTRA_REMAINING_TIME, remainingTime)
                    putExtra(EXTRA_HAS_LIMIT, hasLimit)
                    putExtra(EXTRA_MODE, mode)
                }
                ctx.startService(intent)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to start overlay service", e)
            }
        }
    }

    private var windowManager: WindowManager? = null
    private var overlayView: ViewGroup? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        try {
            if (!Settings.canDrawOverlays(this)) {
                Log.w(TAG, "Overlay permission not granted")
                stopSelf()
                return START_NOT_STICKY
            }

            val appName = intent?.getStringExtra(EXTRA_APP_NAME) ?: "App"
            val mode = intent?.getStringExtra(EXTRA_MODE) ?: "reminder"
            val sessionTime = intent?.getStringExtra(EXTRA_SESSION_TIME) ?: ""
            val todayTime = intent?.getStringExtra(EXTRA_TODAY_TIME) ?: ""
            val remainingTime = intent?.getStringExtra(EXTRA_REMAINING_TIME) ?: ""
            val hasLimit = intent?.getBooleanExtra(EXTRA_HAS_LIMIT, false) ?: false

            Log.d(TAG, "onStartCommand: mode=$mode, app=$appName")

            removeOverlay()
            showOverlay(appName, sessionTime, todayTime, remainingTime, hasLimit, mode)
        } catch (e: Exception) {
            Log.e(TAG, "Error in onStartCommand", e)
            stopSelf()
        }
        return START_NOT_STICKY
    }

    private fun showOverlay(
        appName: String,
        sessionTime: String,
        todayTime: String,
        remainingTime: String,
        hasLimit: Boolean,
        mode: String
    ) {
        try {
            windowManager = getSystemService(WINDOW_SERVICE) as WindowManager

            // Create transparent container that won't block clicks
            val container = TransparentClickPassThroughView(this)
            
            // Create ComposeView for the card
            val composeView = ComposeView(this).apply {
                setBackgroundColor(android.graphics.Color.TRANSPARENT)
                setContent {
                    MaterialTheme {
                        var visible by remember { mutableStateOf(false) }
                        
                        LaunchedEffect(Unit) {
                            visible = true
                        }
                        
                        AnimatedVisibility(
                            visible = visible,
                            enter = slideInVertically(
                                initialOffsetY = { -it },
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessLow
                                )
                            ) + fadeIn(),
                            exit = slideOutVertically(
                                targetOffsetY = { -it },
                                animationSpec = tween(300)
                            ) + fadeOut()
                        ) {
                            // Load display preferences
                            val showSession = runBlocking { 
                                WellbeingSettingsStore.popupShowSessionTime.flow(this@OverlayReminderActivity).first()
                            }
                            val showToday = runBlocking {
                                WellbeingSettingsStore.popupShowTodayTime.flow(this@OverlayReminderActivity).first()
                            }
                            val showRemaining = runBlocking {
                                WellbeingSettingsStore.popupShowRemainingTime.flow(this@OverlayReminderActivity).first()
                            }

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(WindowInsets.safeDrawing.asPaddingValues())
                                    .padding(horizontal = 16.dp)
                                    .padding(top = 8.dp),
                                contentAlignment = Alignment.TopCenter
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
                                        onDismiss = { 
                                            removeOverlay()
                                            stopSelf()
                                        }
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
                                        onDismiss = { 
                                            removeOverlay()
                                            stopSelf()
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
            container.addView(composeView)

            val layoutParams = WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                else
                    @Suppress("DEPRECATION")
                    WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                        WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                android.graphics.PixelFormat.TRANSLUCENT
            )

            overlayView = container
            windowManager?.addView(container, layoutParams)
            Log.d(TAG, "Overlay view added successfully")

            // Auto-dismiss after 7 seconds using Handler (Service doesn't have window)
            Handler(Looper.getMainLooper()).postDelayed({
                try {
                    if (overlayView != null) {
                        removeOverlay()
                        stopSelf()
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error in auto-dismiss", e)
                }
            }, 7000)
        } catch (e: Exception) {
            Log.e(TAG, "Error in showOverlay", e)
            stopSelf()
        }
    }

    private fun removeOverlay() {
        try {
            overlayView?.let { 
                windowManager?.removeView(it)
                Log.d(TAG, "Overlay removed")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error removing overlay", e)
        }
        overlayView = null
    }

    override fun onDestroy() {
        super.onDestroy()
        removeOverlay()
    }
}

/**
 * Custom ViewGroup that passes through touch events except on clickable children
 */
private class TransparentClickPassThroughView(context: Context) : FrameLayout(context) {
    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        // Never intercept - let children handle, and pass through to underlying app if needed
        return false
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
