@file:Suppress("AssignedValueIsNeverRead")

package org.elnix.dragonlauncher.ui.settings.debug

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.elnix.dragonlauncher.common.logging.DragonLogManager
import org.elnix.dragonlauncher.common.logging.logD
import org.elnix.dragonlauncher.common.logging.logE
import org.elnix.dragonlauncher.common.utils.copyToClipboard
import org.elnix.dragonlauncher.common.utils.formatDateTime
import org.elnix.dragonlauncher.settings.stores.DebugSettingsStore
import org.elnix.dragonlauncher.ui.colors.AppObjectsColors
import org.elnix.dragonlauncher.ui.helpers.SwitchRow
import org.elnix.dragonlauncher.ui.helpers.settings.SettingsLazyHeader
import java.io.File

@Composable
fun LogsTab(
    onBack: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val ctx = LocalContext.current

    val enableLogging by DebugSettingsStore.enableLogging.flow(ctx)
        .collectAsState(false)

    var logs by remember { mutableStateOf("") }
    var selectedFile by remember { mutableStateOf<File?>(null) }

    val logFiles by produceState(initialValue = emptyList(), ctx) {
        while (true) {
            value = DragonLogManager.getAllLogFiles(ctx)
            delay(2000)
        }
    }

    SettingsLazyHeader(
        title = "Logs",
        onBack = onBack,
        helpText = "Logs, need more info?",
        onReset = {
            DragonLogManager.clearLogs(ctx)
            selectedFile = null
            logs = ""
        },
        resetText = "Clear all logs",
        content = {

            SwitchRow(
                state = enableLogging,
                text = "Activate Logs"
            ) {
                scope.launch { DebugSettingsStore.enableLogging.set(ctx, it) }
                DragonLogManager.enableLogging(it)
            }

            Button(
                onClick = {
                    DragonLogManager.clearLogs(ctx)
                    selectedFile = null
                    logs = ""
                },
                modifier = Modifier.padding(16.dp)
            ) {
                Text("Clear All Logs")
            }


            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                items(logFiles) { file ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selectedFile = file
                                logs = DragonLogManager.readLogFile(file)
                            },
                        colors = AppObjectsColors.cardColors()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(5.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = file.name,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Text(
                                    text = "${(file.length() / 1024).toInt()}KB • ${file.lastModified().formatDateTime()}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                            Row {
                                IconButton(onClick = {
                                    ctx.copyToClipboard(DragonLogManager.readLogFile(file))
                                }) {
                                    Icon(Icons.Default.ContentCopy, "Copy")
                                }
                                IconButton(onClick = {
                                    exportLogFile(ctx, file)
                                }) {
                                    Icon(Icons.Default.Share, "Export")
                                }
                            }
                        }
                    }
                }
            }

            if (logs.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = AppObjectsColors.cardColors()
                ) {
                    Column {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(5.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = selectedFile?.name ?: "Logs",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Row {
                                IconButton(onClick = {
                                    ctx.copyToClipboard(logs)
                                }) {
                                    Icon(Icons.Default.ContentCopy, "Copy All")
                                }
                                IconButton(onClick = {
                                    logs = ""
                                    selectedFile = null
                                }) {
                                    Icon(Icons.Default.Close, "Close")
                                }
                            }
                        }
                        Text(
                            text = logs,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(400.dp)
                                .verticalScroll(rememberScrollState())
                                .padding(16.dp),
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    )
}

private fun exportLogFile(ctx: Context, file: File) {
    try {
        val cacheDir = ctx.cacheDir
        val shareFile = File(cacheDir, file.name)
        file.copyTo(shareFile, overwrite = true)

        val uri = FileProvider.getUriForFile(
            ctx,
            "${ctx.packageName}.fileprovider",
            shareFile
        )

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, "Dragon Logs - ${file.name}")
            putExtra(Intent.EXTRA_TEXT, "Dragon Launcher logs")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }


        ctx.startActivity(Intent.createChooser(shareIntent, "Share ${file.name}"))
        ctx.logD("LogsTab", " Share opened: ${file.name} (${shareFile.absolutePath})")

    } catch (e: SecurityException) {
        ctx.logE("LogsTab", "FileProvider not configured: ${e.message}")
        val content = DragonLogManager.readLogFile(file)
        val textIntent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, content)
            putExtra(Intent.EXTRA_SUBJECT, "Dragon Logs - ${file.name}")
        }
        ctx.startActivity(Intent.createChooser(textIntent, "Share logs (text)"))
    } catch (e: Exception) {
        ctx.logE("LogsTab", "Share failed: ${e.message}", e)
    }
}
