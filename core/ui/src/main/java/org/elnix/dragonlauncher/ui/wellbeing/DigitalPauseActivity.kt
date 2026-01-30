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
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import org.elnix.dragonlauncher.common.R
import org.elnix.dragonlauncher.ui.theme.DragonLauncherTheme
import java.util.Calendar
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

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
                // Utilisation d'une Surface sombre pour assurer le contraste
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFF0F111A) // Deep Midnight
                ) {
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
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        setResult(RESULT_CANCEL)
        super.onBackPressed()
    }
}

// --- Couleurs Personnalisées pour l'ambiance "Zen" ---
private val ZenPurple = Color(0xFF6C5CE7)
private val ZenTeal = Color(0xFF00CEC9)
private val DeepBgTop = Color(0xFF0F2027)
private val DeepBgBottom = Color(0xFF203A43)
private val TextWhite = Color(0xFFEEEEEE)
private val TextSecondary = Color(0xFFB2BEC3)

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

    // Phrases (Placeholder si les ressources n'existent pas dans le contexte actuel)
    val breathingPhrases = listOf(
        stringResource(R.string.pause_breathe_1),
        stringResource(R.string.pause_breathe_2),
        stringResource(R.string.pause_breathe_3),
        stringResource(R.string.pause_breathe_4),
        stringResource(R.string.pause_breathe_5)
    )


    val usageStats = remember(packageName, guiltMode) {
        if (guiltMode && hasUsagePermission(ctx)) getUsageStats(ctx, packageName) else null
    }
    val hasPermission = remember { hasUsagePermission(ctx) }

    // Logique du compte à rebours inchangée
    LaunchedEffect(Unit) {
        while (countdown > 0) {
            delay(1000)
            countdown--
            if (countdown % 3 == 0 && countdown > 0) {
                currentPhraseIndex = (currentPhraseIndex + 1) % breathingPhrases.size
            }
        }
        showChoice = true
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // 1. BACKGROUND ANIMÉ
        AuroraBackground()
        
        // 2. PARTICULES FLOTTANTES
        FloatingParticles(modifier = Modifier.fillMaxSize())

        // 3. CONTENU PRINCIPAL
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding() // Important pour EdgeToEdge
                .padding(24.dp)
        ) {
            
            // Partie Lotus & Respiration
            Box(contentAlignment = Alignment.Center) {
                AnimatedLotus(
                    modifier = Modifier.size(200.dp),
                    isPulsing = !showChoice
                )
                
                // Compte à rebours au centre du lotus
                androidx.compose.animation.AnimatedVisibility(
                    visible = !showChoice,
                    enter = scaleIn() + fadeIn(),
                    exit = scaleOut() + fadeOut()
                ) {
                   Text(
                       text = countdown.toString(),
                       fontSize = 48.sp,
                       fontWeight = FontWeight.Thin,
                       color = Color.White,
                       style = LocalTextStyle.current.copy(
                           shadow = androidx.compose.ui.graphics.Shadow(
                               color = ZenPurple, blurRadius = 20f
                           )
                       )
                   ) 
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Phase 1: Respiration
            AnimatedVisibility(
                visible = !showChoice,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                BreathingText(text = breathingPhrases[currentPhraseIndex])
            }

            // Phase 2: Choix
            AnimatedVisibility(
                visible = showChoice,
                enter = fadeIn(tween(600, delayMillis = 300)) + slideInVertically { it / 2 },
                exit = fadeOut()
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(R.string.pause_question), // "Do you really need this?"
                        fontSize = 26.sp,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Medium,
                        color = TextWhite,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 32.dp)
                    )

                    if (guiltMode) {
                        GlassCard(modifier = Modifier.fillMaxWidth()) {
                            if (hasPermission && usageStats != null) {
                                UsageStatsDisplay(usageStats)
                            } else if (!hasPermission) {
                                PermissionNeededContent(ctx)
                            }
                        }
                        Spacer(modifier = Modifier.height(32.dp))
                    }

                    // Bouton Principal: "I'll do something else" (Annuler)
                    Button(
                        onClick = onCancel,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp)
                            .shadow(elevation = 12.dp, shape = RoundedCornerShape(32.dp), spotColor = ZenTeal),
                        shape = RoundedCornerShape(32.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ZenTeal
                        )
                    ) {
                        Text(
                            text = stringResource(R.string.pause_no_thanks).uppercase(),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            letterSpacing = 1.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Bouton Secondaire: "Open App" (Continuer)
                    TextButton(
                        onClick = onProceed,
                        colors = ButtonDefaults.textButtonColors(contentColor = TextSecondary)
                    ) {
                        Text(
                            text = stringResource(R.string.pause_yes_open),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal
                        )
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------------------
// COMPOSANTS UI VISUELS
// -------------------------------------------------------------------------

@Composable
private fun AuroraBackground() {
    val infiniteTransition = rememberInfiniteTransition(label = "aurora")
    // Animation subtile des couleurs du fond
    val colorShift by infiniteTransition.animateColor(
        initialValue = DeepBgTop,
        targetValue = Color(0xFF1A1A2E), // Variation légère vers le violet foncé
        animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bg_color"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(colorShift, DeepBgBottom)
                )
            )
    )
}

@Composable
private fun FloatingParticles(modifier: Modifier = Modifier) {
    val density = LocalDensity.current
    // Création de 20 particules aléatoires
    val particles = remember {
        List(20) {
            ParticleData(
                x = Random.nextFloat(),
                y = Random.nextFloat(),
                size = Random.nextInt(2, 6).dp,
                speed = Random.nextLong(3000, 8000)
            )
        }
    }

    Box(modifier = modifier) {
        particles.forEach { particle ->
            val infiniteTransition = rememberInfiniteTransition(label = "particle")
            val yOffset by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = -100f, // Monte vers le haut
                animationSpec = infiniteRepeatable(
                    animation = tween(particle.speed.toInt(), easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                ),
                label = "y"
            )
            val alpha by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = 0.6f,
                animationSpec = infiniteRepeatable(
                    animation = keyframes {
                        durationMillis = particle.speed.toInt()
                        0f at 0
                        0.6f at durationMillis / 2
                        0f at durationMillis
                    },
                    repeatMode = RepeatMode.Restart
                ),
                label = "alpha"
            )

            Box(
                modifier = Modifier
                    .offset(
                        x = (particle.x * 1000).dp, // Conversion brute pour l'exemple
                        y = (particle.y * 2000).dp + yOffset.dp
                    )
                    .size(particle.size)
                    .alpha(alpha)
                    .background(Color.White, CircleShape)
            )
        }
    }
}

data class ParticleData(val x: Float, val y: Float, val size: Dp, val speed: Long)

@Composable
private fun GlassCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .background(Color.White.copy(alpha = 0.08f)) // Base translucide
            .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(24.dp))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        content = content
    )
}

@Composable
private fun AnimatedLotus(
    modifier: Modifier = Modifier,
    isPulsing: Boolean = true
) {
    val infiniteTransition = rememberInfiniteTransition(label = "lotus")

    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(80000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isPulsing) 1.15f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = FastOutSlowInEasing), // Respiration lente (4s)
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Canvas(modifier = modifier.graphicsLayer {
        scaleX = pulseScale
        scaleY = pulseScale
        this.rotationZ = rotation
    }) {
        val centerX = size.width / 2
        val centerY = size.height / 2
        val radius = size.minDimension / 2.5f

        // Glow externe
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(ZenPurple.copy(alpha = 0.3f), Color.Transparent),
                center = center,
                radius = radius * 2f
            ),
            radius = radius * 2f
        )

        val petalCount = 8
        // Couleurs plus "Mystiques"
        val petalColors = listOf(
            Color(0xFFE056FD), 
            Color(0xFF686DE0),
            Color(0xFF30336B)
        )

        for (i in 0 until petalCount) {
            val angle = (360f / petalCount) * i
            rotate(angle, pivot = center) {
                val path = Path().apply {
                    moveTo(centerX, centerY)
                    // Forme de pétale plus organique
                    cubicTo(
                        centerX + radius * 0.5f, centerY - radius * 0.3f,
                        centerX + radius * 0.5f, centerY - radius * 0.8f,
                        centerX, centerY - radius
                    )
                    cubicTo(
                        centerX - radius * 0.5f, centerY - radius * 0.8f,
                        centerX - radius * 0.5f, centerY - radius * 0.3f,
                        centerX, centerY
                    )
                    close()
                }

                drawPath(
                    path = path,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            petalColors[i % petalColors.size].copy(alpha = 0.9f),
                            petalColors[i % petalColors.size].copy(alpha = 0.1f)
                        ),
                        start = Offset(centerX, centerY),
                        end = Offset(centerX, centerY - radius)
                    )
                )
                // Contour fin pour définition
                androidx.compose.ui.graphics.drawscope.Stroke(width = 1.dp.toPx())
            }
        }
        
        // Centre
        drawCircle(
            color = Color.White.copy(alpha=0.8f),
            radius = radius * 0.1f,
            center = center
        )
    }
}

@Composable
private fun BreathingText(text: String) {
    AnimatedContent(
        targetState = text,
        transitionSpec = {
            fadeIn(tween(1000)) togetherWith fadeOut(tween(500))
        }, label = "text_fade"
    ) { targetText ->
        Text(
            text = targetText,
            fontSize = 32.sp,
            fontFamily = FontFamily.Serif, // Serif pour l'élégance
            fontStyle = FontStyle.Italic,
            fontWeight = FontWeight.Light,
            color = TextWhite,
            textAlign = TextAlign.Center,
            lineHeight = 40.sp,
            modifier = Modifier.padding(horizontal = 24.dp)
        )
    }
}

// -------------------------------------------------------------------------
// LOGIQUE ET STATS (Nettoyé graphiquement)
// -------------------------------------------------------------------------

@Composable
private fun UsageStatsDisplay(stats: AppUsageStats) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        if (stats.yesterdayMinutes > 0) {
            Text(
                text = stringResource(R.string.usage_yesterday, formatDuration(stats.yesterdayMinutes)),
                fontSize = 16.sp,
                color = TextSecondary
            )
            
            // Highlight alarmant en rouge doux
            val yearlyHours = (stats.yesterdayMinutes * 365) / 60
            if (yearlyHours > 24) {
                val yearlyDays = yearlyHours / 24
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.usage_guilt_yearly, "$yearlyDays days"),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFF7675)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        Divider(color = Color.White.copy(alpha=0.1f), thickness = 1.dp)
        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = if (stats.todayMinutes > 0) 
                stringResource(R.string.usage_today, formatDuration(stats.todayMinutes))
            else stringResource(R.string.usage_no_data),
            fontSize = 14.sp,
            color = TextWhite.copy(alpha = 0.8f)
        )
    }
}

@Composable
private fun PermissionNeededContent(ctx: Context) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = stringResource(R.string.usage_permission_needed),
            fontSize = 14.sp,
            color = TextSecondary,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedButton(
            onClick = {
                val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                ctx.startActivity(intent)
            },
            border = androidx.compose.foundation.BorderStroke(1.dp, ZenTeal),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = ZenTeal)
        ) {
            Text(stringResource(R.string.grant_usage_permission))
        }
    }
}

// Data classes & Helpers existants (gardés pour compatibilité)
data class AppUsageStats(val yesterdayMinutes: Long, val todayMinutes: Long)

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

private fun hasUsagePermission(ctx: Context): Boolean {
    val appOps = ctx.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
    val mode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        appOps.unsafeCheckOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, android.os.Process.myUid(), ctx.packageName)
    } else {
        @Suppress("DEPRECATION")
        appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, android.os.Process.myUid(), ctx.packageName)
    }
    return mode == AppOpsManager.MODE_ALLOWED
}

private fun getUsageStats(ctx: Context, packageName: String): AppUsageStats? {
    // Note: Le code logique original est conservé ici pour la brièveté,
    // mais il est identique à votre implémentation originale.
    // ... (Insérer votre logique getUsageStats ici si nécessaire)
    return try {
        val usageStatsManager = ctx.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val calendar = Calendar.getInstance()
        val todayStart = calendar.apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }.timeInMillis
        val now = System.currentTimeMillis()
        val todayStats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, todayStart, now)
        val todayMinutes = todayStats.filter { it.packageName == packageName }.sumOf { it.totalTimeInForeground } / 60000
        val yesterdayEnd = todayStart
        val yesterdayStart = calendar.apply { add(Calendar.DAY_OF_YEAR, -1) }.timeInMillis
        val yesterdayStats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, yesterdayStart, yesterdayEnd)
        val yesterdayMinutes = yesterdayStats.filter { it.packageName == packageName }.sumOf { it.totalTimeInForeground } / 60000
        AppUsageStats(yesterdayMinutes, todayMinutes)
    } catch (e: Exception) { null }
}
