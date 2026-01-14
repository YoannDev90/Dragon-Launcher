package org.elnix.dragonlauncher.common.logging

import android.content.Context
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DragonLogManager {
    private const val TAG = "DragonLogManager"
    private val logQueue = mutableListOf<String>()
    private val scope = CoroutineScope(Dispatchers.IO)
    private var logFile: File? = null
    private var isLoggingEnabled = false
    private var maxFileSizeBytes = 5 * 1024 * 1024 // 5MB
    private val logLock = Any()

    private var currentSessionFile: File? = null
    private var allLogFiles = mutableListOf<File>()

    fun init(ctx: Context) {
        updateLogDirectory(ctx)
    }


    private fun updateLogDirectory(ctx: Context) {
        val logDir = File(ctx.filesDir, "logs")
        if (!logDir.exists()) logDir.mkdirs()

        currentSessionFile = File(logDir, "dragon_logs_current.txt")
        allLogFiles = (logDir.listFiles()?.filter {
            it.name.startsWith("dragon_logs_")
        }?.toList() ?: emptyList()).toMutableList()

        rotateIfNeeded()
    }

    fun getAllLogFiles(context: Context): List<File> {
        updateLogDirectory(context)
        val sessionFile = currentSessionFile
        val uniqueFiles = allLogFiles.toMutableList().apply {
            if (sessionFile != null && !contains(sessionFile)) {
                add(sessionFile)
            }
        }
        return uniqueFiles
    }


    fun enableLogging(enable: Boolean) {
        synchronized(logLock) {
            isLoggingEnabled = enable
            if (enable) flushQueue()
        }
    }

    private fun rotateIfNeeded() {
        logFile?.let { file ->
            if (file.length() > maxFileSizeBytes) {
                val newFile = File(file.parent, "dragon_logs_old_${getDateString()}.txt")
                file.renameTo(newFile)
                logFile = File(file.parent, "dragon_logs_${getDateString()}.txt")
            }
        }
    }

    private fun getDateString(): String {
        return SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date()) // Fixed typo: HHmmss
    }

    fun log(priority: Int, tag: String, message: String, throwable: Throwable? = null) {
        val logLine = formatLog(priority, tag, message, throwable)

        scope.launch {
            try {
                synchronized(logLock) {
                    currentSessionFile?.let { file ->
                        FileWriter(file, true).use { writer ->
                            writer.append(logLine).append("\n").flush()
                        }
                    }

                    if (isLoggingEnabled) {
                        logQueue.add(logLine)
                        if (logQueue.size > 1000) flushQueue()
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to write log: ${e.message}")
            }
        }
    }

    private fun formatLog(priority: Int, tag: String, message: String, throwable: Throwable?): String {
        val time = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault()).format(Date())
        val level = when (priority) {
            Log.VERBOSE -> "V"
            Log.DEBUG -> "D"
            Log.INFO -> "I"
            Log.WARN -> "W"
            Log.ERROR -> "E"
            else -> "?"
        }
        val fullMessage = if (throwable != null) "$message\n${Log.getStackTraceString(throwable)}" else message

        return "[$time] $level/$tag: $fullMessage"
    }

    private fun flushQueue() {
        if (logQueue.isEmpty() || logFile == null) return

        scope.launch {
            try {
                synchronized(logLock) {
                    FileWriter(logFile, true).use { writer ->
                        logQueue.forEach { writer.append(it).append("\n") }
                        writer.flush()
                    }
                    logQueue.clear()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to flush log queue: ${e.message}")
            }
        }
    }

    fun clearLogs(ctx: Context) {
        val logDir = File(ctx.filesDir, "logs")
        logDir.listFiles()?.forEach {
            if (it.name.startsWith("dragon_logs_") || it.name == "dragon_logs_current.txt") {
                it.delete()
            }
        }
        // Reset state
        currentSessionFile = null
        allLogFiles.clear()
        ctx.logD("DragonLogManager", "All logs cleared")
    }

    fun readLogFile(file: File): String {
        return try {
            file.readText()
        } catch (e: Exception) {
            "Failed to read log file: ${e.message}"
        }
    }
}
