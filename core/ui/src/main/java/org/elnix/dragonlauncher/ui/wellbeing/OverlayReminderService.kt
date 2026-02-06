package org.elnix.dragonlauncher.ui.wellbeing

import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.Gravity
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import kotlinx.coroutines.delay

/**
 * Service that shows a beautiful overlay popup at the top of the screen
 * to remind the user they've been on a paused app for a while.
 * Auto-dismisses after a few seconds.
 */
class OverlayReminderService : Service() {

    companion object {
        private const val TAG = "OverlayReminderService"
        const val EXTRA_APP_NAME = "extra_app_name"
        const val EXTRA_TIME_TEXT = "extra_time_text"
        const val EXTRA_MODE = "extra_overlay_mode" // "reminder" | "time_warning"
        private const val DISMISS_DELAY = 5000L // 5 seconds

        fun show(ctx: Context, appName: String, timeText: String) {
            if (!Settings.canDrawOverlays(ctx)) {
                Log.w(TAG, "Cannot show overlay: permission not granted")
                return
            }
            try {
                val intent = Intent(ctx, OverlayReminderService::class.java).apply {
                    putExtra(EXTRA_APP_NAME, appName)
                    putExtra(EXTRA_TIME_TEXT, timeText)
                    putExtra(EXTRA_MODE, "reminder")
                }
                ctx.startService(intent)
                Log.d(TAG, "Overlay reminder service started")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to start overlay service", e)
            }
        }

        fun showTimeWarning(ctx: Context, appName: String, remainingText: String) {
            if (!Settings.canDrawOverlays(ctx)) {
                Log.w(TAG, "Cannot show time warning: permission not granted")
                return
            }
            try {
                val intent = Intent(ctx, OverlayReminderService::class.java).apply {
                    putExtra(EXTRA_APP_NAME, appName)
                    putExtra(EXTRA_TIME_TEXT, remainingText)
                    putExtra(EXTRA_MODE, "time_warning")
                }
                ctx.startService(intent)
                Log.d(TAG, "Time warning overlay service started")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to start time warning service", e)
            }
        }
    }

    private var windowManager: WindowManager? = null
    private var overlayView: FrameLayout? = null
    private val handler = Handler(Looper.getMainLooper())

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        try {
            if (!Settings.canDrawOverlays(this)) {
                Log.w(TAG, "Overlay permission not granted, stopping service")
                stopSelf()
                return START_NOT_STICKY
            }

            val appName = intent?.getStringExtra(EXTRA_APP_NAME) ?: "App"
            val timeText = intent?.getStringExtra(EXTRA_TIME_TEXT) ?: ""
            val mode = intent?.getStringExtra(EXTRA_MODE) ?: "reminder"

            Log.d(TAG, "onStartCommand: mode=$mode, app=$appName, time=$timeText")

            // Remove previous overlay if any
            removeOverlay()
            showOverlay(appName, timeText, mode)
        } catch (e: Exception) {
            Log.e(TAG, "Error in onStartCommand", e)
            stopSelf()
        }
        return START_NOT_STICKY
    }

    private fun showOverlay(appName: String, timeText: String, mode: String = "reminder") {
        try {
            Log.d(TAG, "showOverlay called: mode=$mode")
            windowManager = getSystemService(WINDOW_SERVICE) as WindowManager

            val layoutParams = WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                else
                    @Suppress("DEPRECATION")
                    WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                        WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                PixelFormat.TRANSLUCENT
            ).apply {
                gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
            }

            // Create lifecycle-aware ComposeView
            val lifecycleOwner = OverlayLifecycleOwner()
            lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
            lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_START)
            lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)

            val container = FrameLayout(this)
            val composeView = ComposeView(this).apply {
                setViewTreeLifecycleOwner(lifecycleOwner)
                setViewTreeSavedStateRegistryOwner(lifecycleOwner)
                setContent {
                    MaterialTheme {
                        if (mode == "time_warning") {
                            TimeWarningOverlayContent(appName = appName, remainingText = timeText)
                        } else {
                            OverlayReminderContent(appName = appName, timeText = timeText)
                        }
                    }
                }
            }
            container.addView(composeView)

            overlayView = container
            try {
                windowManager?.addView(container, layoutParams)
                Log.d(TAG, "Overlay view added successfully")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to add overlay view", e)
                overlayView = null
                stopSelf()
                return
            }

            // Auto-dismiss after delay
            handler.postDelayed({
                Log.d(TAG, "Auto-dismissing overlay")
                lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
                lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
                lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
                removeOverlay()
                stopSelf()
            }, DISMISS_DELAY)
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error in showOverlay", e)
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
        handler.removeCallbacksAndMessages(null)
        removeOverlay()
        super.onDestroy()
    }
}


/**
 * A minimal LifecycleOwner + SavedStateRegistryOwner for overlay ComposeViews.
 */
private class OverlayLifecycleOwner : LifecycleOwner, SavedStateRegistryOwner {
    private val lifecycleRegistry = LifecycleRegistry(this)
    private val savedStateRegistryController = SavedStateRegistryController.create(this)

    override val lifecycle: Lifecycle get() = lifecycleRegistry
    override val savedStateRegistry: SavedStateRegistry
        get() = savedStateRegistryController.savedStateRegistry

    init {
        savedStateRegistryController.performRestore(null)
    }

    fun handleLifecycleEvent(event: Lifecycle.Event) {
        lifecycleRegistry.handleLifecycleEvent(event)
    }
}

// ─────────── Beautiful Overlay Composable ───────────

private val OverlayPurple = Color(0xFF6C5CE7)
private val OverlayTeal = Color(0xFF00CEC9)
private val OverlayDark = Color(0xFF1A1A2E)
private val OverlayGlass = Color(0xFF16213E)

@Composable
private fun OverlayReminderContent(appName: String, timeText: String) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(100) // small delay for enter animation
        visible = true
        delay(4500)
        visible = false
    }

    val infiniteTransition = rememberInfiniteTransition(label = "overlay_glow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(
            initialOffsetY = { -it },
            animationSpec = tween(600, easing = FastOutSlowInEasing)
        ) + fadeIn(tween(400)),
        exit = slideOutVertically(
            targetOffsetY = { -it },
            animationSpec = tween(500, easing = FastOutSlowInEasing)
        ) + fadeOut(tween(300))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 24.dp,
                        shape = RoundedCornerShape(24.dp),
                        ambientColor = OverlayPurple.copy(alpha = 0.3f),
                        spotColor = OverlayPurple.copy(alpha = 0.5f)
                    )
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                OverlayDark,
                                OverlayGlass
                            )
                        )
                    )
                    .border(
                        width = 1.dp,
                        brush = Brush.linearGradient(
                            colors = listOf(
                                OverlayPurple.copy(alpha = glowAlpha),
                                OverlayTeal.copy(alpha = glowAlpha * 0.6f),
                                Color.Transparent
                            )
                        ),
                        shape = RoundedCornerShape(24.dp)
                    )
                    .padding(20.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Dragon emoji/indicator with pulsing glow
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(48.dp)
                            .graphicsLayer {
                                scaleX = pulseScale
                                scaleY = pulseScale
                            }
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        OverlayPurple.copy(alpha = 0.4f),
                                        Color.Transparent
                                    )
                                ),
                                CircleShape
                            )
                    ) {
                        Text(
                            text = "🐉",
                            fontSize = 24.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Still on $appName · $timeText",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = FontFamily.SansSerif,
                            color = Color.White,
                            maxLines = 1
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Take a break, your eyes will thank you 🌿",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color.White.copy(alpha = 0.6f),
                            maxLines = 1
                        )
                    }
                }
            }

            // Subtle progress bar at bottom (auto-dismiss indicator)
            OverlayProgressBar(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 32.dp)
                    .padding(bottom = 4.dp)
            )
        }
    }
}

@Composable
private fun OverlayProgressBar(modifier: Modifier = Modifier) {
    // Animate once from 1 -> 0 while the overlay is shown.
    val progressAnim = remember { Animatable(1f) }
    LaunchedEffect(Unit) {
        progressAnim.animateTo(
            targetValue = 0f,
            animationSpec = tween(
                durationMillis = 4500,
                easing = LinearEasing
            )
        )
    }
    val progress = progressAnim.value

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(3.dp)
            .clip(RoundedCornerShape(2.dp))
            .background(Color.White.copy(alpha = 0.1f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(progress)
                .height(3.dp)
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(OverlayTeal, OverlayPurple)
                    ),
                    RoundedCornerShape(2.dp)
                )
        )
    }
}

// ─────────── Time Warning Overlay ("5 min remaining") ───────────

private val WarningOrange = Color(0xFFFFA502)
private val WarningRed = Color(0xFFFF6348)

@Composable
private fun TimeWarningOverlayContent(appName: String, remainingText: String) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(100)
        visible = true
        delay(4500)
        visible = false
    }

    val infiniteTransition = rememberInfiniteTransition(label = "warning_glow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "warning_glow_alpha"
    )

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "warning_pulse"
    )

    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(
            initialOffsetY = { -it },
            animationSpec = tween(600, easing = FastOutSlowInEasing)
        ) + fadeIn(tween(400)),
        exit = slideOutVertically(
            targetOffsetY = { -it },
            animationSpec = tween(500, easing = FastOutSlowInEasing)
        ) + fadeOut(tween(300))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 24.dp,
                        shape = RoundedCornerShape(24.dp),
                        ambientColor = WarningOrange.copy(alpha = 0.3f),
                        spotColor = WarningRed.copy(alpha = 0.5f)
                    )
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF1A1A2E),
                                Color(0xFF2D1B3D)
                            )
                        )
                    )
                    .border(
                        width = 1.dp,
                        brush = Brush.linearGradient(
                            colors = listOf(
                                WarningOrange.copy(alpha = glowAlpha),
                                WarningRed.copy(alpha = glowAlpha * 0.6f),
                                Color.Transparent
                            )
                        ),
                        shape = RoundedCornerShape(24.dp)
                    )
                    .padding(20.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Warning indicator with pulsing glow
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(48.dp)
                            .graphicsLayer {
                                scaleX = pulseScale
                                scaleY = pulseScale
                            }
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        WarningOrange.copy(alpha = 0.4f),
                                        Color.Transparent
                                    )
                                ),
                                CircleShape
                            )
                    ) {
                        Text(
                            text = "⏳",
                            fontSize = 24.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "$remainingText left on $appName",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = FontFamily.SansSerif,
                            color = Color.White,
                            maxLines = 1
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Your session is almost over ⚡",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Normal,
                            color = WarningOrange.copy(alpha = 0.8f),
                            maxLines = 1
                        )
                    }
                }
            }

            // Progress bar
            OverlayProgressBar(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 32.dp)
                    .padding(bottom = 4.dp)
            )
        }
    }
}
