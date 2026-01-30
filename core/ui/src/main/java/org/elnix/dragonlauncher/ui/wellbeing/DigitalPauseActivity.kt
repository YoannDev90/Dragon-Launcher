package org.elnix.dragonlauncher.ui.wellbeing

import android.app.AppOpsManager
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import org.elnix.dragonlauncher.common.R
import org.elnix.dragonlauncher.ui.theme.DragonLauncherTheme
import java.util.Calendar
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin


/**
 * Full-screen Digital Pause Activity.
 * Shows a mindfulness screen before launching a distracting app.
 */
class DigitalPauseActivity : ComponentActivity() {

    companion object {
        const val EXTRA_PACKAGE_NAME = "extra_package_name"
        const val EXTRA_PAUSE_DURATION = "extra_pause_duration"
        const val EXTRA_GUILT_MODE = "extra_guilt_mode"
        const val RESULT_PROCEED = 1
        const val RESULT_CANCEL = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val packageName = intent.getStringExtra(EXTRA_PACKAGE_NAME) ?: run {
            finish()
            return
        }
        val pauseDuration = intent.getIntExtra(EXTRA_PAUSE_DURATION, 10)
        val guiltMode = intent.getBooleanExtra(EXTRA_GUILT_MODE, false)

        setContent {
            DragonLauncherTheme {
                DigitalPauseScreen(
                    packageName = packageName,
                    pauseDuration = pauseDuration,
                    guiltMode = guiltMode,
                    onProceed = {
                        setResult(RESULT_PROCEED)
                        finish()
                    },
                    onCancel = {
                        setResult(RESULT_CANCEL)
                        finish()
                    }
                )
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        // Prevent back press during countdown
        setResult(RESULT_CANCEL)
        super.onBackPressed()
    }
}


@Composable
fun DigitalPauseScreen(
    packageName: String,
    pauseDuration: Int,
    guiltMode: Boolean,
    onProceed: () -> Unit,
    onCancel: () -> Unit
) {
    val ctx = LocalContext.current

    var countdown by remember { mutableIntStateOf(pauseDuration) }
    var showChoice by remember { mutableStateOf(false) }
    var currentPhraseIndex by remember { mutableIntStateOf(0) }

    val breathingPhrases = listOf(
        stringResource(R.string.pause_breathe_1),
        stringResource(R.string.pause_breathe_2),
        stringResource(R.string.pause_breathe_3),
        stringResource(R.string.pause_breathe_4),
        stringResource(R.string.pause_breathe_5)
    )

    // Usage stats
    val usageStats = remember(packageName, guiltMode) {
        if (guiltMode && hasUsagePermission(ctx)) {
            getUsageStats(ctx, packageName)
        } else null
    }
    val hasPermission = remember { hasUsagePermission(ctx) }

    // Countdown effect
    LaunchedEffect(Unit) {
        while (countdown > 0) {
            delay(1000)
            countdown--

            // Change phrase every 3 seconds
            if (countdown % 3 == 0 && countdown > 0) {
                currentPhraseIndex = (currentPhraseIndex + 1) % breathingPhrases.size
            }
        }
        showChoice = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp)
        ) {
            // Animated Lotus
            AnimatedLotus(
                modifier = Modifier.size(180.dp),
                isPulsing = !showChoice
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Breathing phase
            AnimatedVisibility(
                visible = !showChoice,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Breathing phrase with fade animation
                    BreathingText(
                        text = breathingPhrases[currentPhraseIndex]
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // Countdown
                    Text(
                        text = countdown.toString(),
                        fontSize = 72.sp,
                        fontWeight = FontWeight.Light,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
            }

            // Choice phase
            AnimatedVisibility(
                visible = showChoice,
                enter = fadeIn(tween(500)) + scaleIn(tween(500)),
                exit = fadeOut()
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Question
                    Text(
                        text = stringResource(R.string.pause_question),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 24.dp)
                    )

                    // Usage stats (guilt mode)
                    if (guiltMode) {
                        if (hasPermission && usageStats != null) {
                            UsageStatsDisplay(usageStats)
                        } else if (!hasPermission) {
                            PermissionNeededCard(ctx)
                        }

                        Spacer(modifier = Modifier.height(32.dp))
                    }

                    // "No" button - prominent
                    Button(
                        onClick = onCancel,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(28.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF4CAF50)
                        )
                    ) {
                        Text(
                            text = stringResource(R.string.pause_no_thanks),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // "Yes" button - muted
                    TextButton(
                        onClick = onProceed,
                        modifier = Modifier.alpha(0.5f)
                    ) {
                        Text(
                            text = stringResource(R.string.pause_yes_open),
                            fontSize = 14.sp,
                            color = Color.White.copy(alpha = 0.6f)
                        )
                    }
                }
            }
        }
    }
}


@Composable
private fun AnimatedLotus(
    modifier: Modifier = Modifier,
    isPulsing: Boolean = true
) {
    val infiniteTransition = rememberInfiniteTransition(label = "lotus")

    val scale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(60000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    val actualScale = if (isPulsing) scale else 1f

    Canvas(modifier = modifier) {
        val center = Offset(size.width / 2, size.height / 2)
        val radius = size.minDimension / 2 * actualScale

        // Glow effect
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFFE8B5FF).copy(alpha = glowAlpha),
                    Color.Transparent
                ),
                center = center,
                radius = radius * 1.5f
            ),
            radius = radius * 1.5f,
            center = center
        )

        // Draw lotus petals
        val petalCount = 8
        val petalColors = listOf(
            Color(0xFFE8B5FF),
            Color(0xFFB5D4FF),
            Color(0xFFFFB5E8),
            Color(0xFFB5FFD4)
        )

        rotate(rotation, center) {
            for (i in 0 until petalCount) {
                val angle = (i * 360f / petalCount) - 90
                val petalColor = petalColors[i % petalColors.size]

                drawPetal(
                    center = center,
                    radius = radius * 0.7f,
                    angle = angle,
                    color = petalColor.copy(alpha = 0.8f)
                )
            }
        }

        // Center circle
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFFFFD700),
                    Color(0xFFFF8C00)
                ),
                center = center,
                radius = radius * 0.2f
            ),
            radius = radius * 0.15f,
            center = center
        )
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawPetal(
    center: Offset,
    radius: Float,
    angle: Float,
    color: Color
) {
    val angleRad = angle * PI.toFloat() / 180f
    val petalLength = radius * 0.9f
    val petalWidth = radius * 0.35f

    val path = Path().apply {
        moveTo(center.x, center.y)

        // Control points for bezier curve
        val cp1x = center.x + cos(angleRad - 0.3f) * petalLength * 0.5f
        val cp1y = center.y + sin(angleRad - 0.3f) * petalLength * 0.5f

        val tipX = center.x + cos(angleRad) * petalLength
        val tipY = center.y + sin(angleRad) * petalLength

        val cp2x = center.x + cos(angleRad + 0.3f) * petalLength * 0.5f
        val cp2y = center.y + sin(angleRad + 0.3f) * petalLength * 0.5f

        quadraticTo(cp1x, cp1y, tipX, tipY)
        quadraticTo(cp2x, cp2y, center.x, center.y)

        close()
    }

    drawPath(path, color)
}


@Composable
private fun BreathingText(text: String) {
    val alpha = remember { Animatable(0f) }

    LaunchedEffect(text) {
        alpha.snapTo(0f)
        alpha.animateTo(1f, tween(500))
    }

    Text(
        text = text,
        fontSize = 22.sp,
        fontWeight = FontWeight.Light,
        color = Color.White.copy(alpha = alpha.value),
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(horizontal = 16.dp)
    )
}


/*  ─────────────  Usage Stats  ─────────────  */

data class AppUsageStats(
    val yesterdayMinutes: Long,
    val todayMinutes: Long
)

private fun hasUsagePermission(ctx: Context): Boolean {
    val appOps = ctx.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
    val mode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        appOps.unsafeCheckOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            android.os.Process.myUid(),
            ctx.packageName
        )
    } else {
        @Suppress("DEPRECATION")
        appOps.checkOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            android.os.Process.myUid(),
            ctx.packageName
        )
    }
    return mode == AppOpsManager.MODE_ALLOWED
}

private fun getUsageStats(ctx: Context, packageName: String): AppUsageStats? {
    return try {
        val usageStatsManager = ctx.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager

        val calendar = Calendar.getInstance()

        // Today's stats
        val todayStart = calendar.apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }.timeInMillis
        val now = System.currentTimeMillis()

        val todayStats = usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY,
            todayStart,
            now
        )
        val todayMinutes = todayStats
            .filter { it.packageName == packageName }
            .sumOf { it.totalTimeInForeground } / 60000

        // Yesterday's stats
        val yesterdayEnd = todayStart
        val yesterdayStart = calendar.apply {
            add(Calendar.DAY_OF_YEAR, -1)
        }.timeInMillis

        val yesterdayStats = usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY,
            yesterdayStart,
            yesterdayEnd
        )
        val yesterdayMinutes = yesterdayStats
            .filter { it.packageName == packageName }
            .sumOf { it.totalTimeInForeground } / 60000

        AppUsageStats(yesterdayMinutes, todayMinutes)
    } catch (e: Exception) {
        null
    }
}

@Composable
private fun UsageStatsDisplay(stats: AppUsageStats) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Color.White.copy(alpha = 0.1f),
                RoundedCornerShape(16.dp)
            )
            .padding(16.dp)
    ) {
        if (stats.yesterdayMinutes > 0) {
            Text(
                text = stringResource(
                    R.string.usage_yesterday,
                    formatDuration(stats.yesterdayMinutes)
                ),
                fontSize = 16.sp,
                color = Color.White.copy(alpha = 0.9f),
                textAlign = TextAlign.Center
            )

            // Calculate yearly equivalent
            val yearlyHours = (stats.yesterdayMinutes * 365) / 60
            if (yearlyHours > 24) {
                val yearlyDays = yearlyHours / 24
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(
                        R.string.usage_guilt_yearly,
                        "$yearlyDays days"
                    ),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFF6B6B),
                    textAlign = TextAlign.Center
                )
            }
        }

        if (stats.todayMinutes > 0) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(
                    R.string.usage_today,
                    formatDuration(stats.todayMinutes)
                ),
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
        }

        if (stats.yesterdayMinutes == 0L && stats.todayMinutes == 0L) {
            Text(
                text = stringResource(R.string.usage_no_data),
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.5f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun PermissionNeededCard(ctx: Context) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Color.White.copy(alpha = 0.1f),
                RoundedCornerShape(16.dp)
            )
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(R.string.usage_permission_needed),
            fontSize = 14.sp,
            color = Color.White.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(
            onClick = {
                val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                ctx.startActivity(intent)
            }
        ) {
            Text(
                text = stringResource(R.string.grant_usage_permission),
                color = Color(0xFF64B5F6)
            )
        }
    }
}

private fun formatDuration(minutes: Long): String {
    return when {
        minutes >= 60 -> {
            val hours = minutes / 60
            val mins = minutes % 60
            if (mins > 0) "${hours}h ${mins}m" else "${hours}h"
        }
        else -> "${minutes}min"
    }
}
