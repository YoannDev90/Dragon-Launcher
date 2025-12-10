package org.elnix.dragonlauncher.utils

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.elnix.dragonlauncher.ui.whatsnew.Update



suspend fun loadChangelogs(context: Context, currentVersionCode: Int): List<Update> {
    return withContext(Dispatchers.IO) {
        val am = context.assets
        val changelogDir = "changelogs"

        val filesInDir = am.list(changelogDir) ?: emptyArray()
        Log.d("Changelogs", "Files in $changelogDir: ${filesInDir.toList()}")

        val versionFiles = filesInDir
            .filter { it.matches(Regex("\\d+\\.txt$")) }
            .mapNotNull { name ->
                name.removeSuffix(".txt").toIntOrNull()?.let { ver -> ver to name }
            }
            .filter { it.first <= currentVersionCode }
            .sortedByDescending { it.first }

        versionFiles.mapNotNull { (versionCode, filename) ->
            try {
                val lines = am.open("$changelogDir/$filename")
                    .bufferedReader()
                    .readLines()

                // First line = version name, rest = changes
                val versionName = lines.firstOrNull()?.trim()?.takeIf { it.isNotEmpty() } ?: "v$versionCode"
                val changes = lines.drop(1)  // Skip version line
                    .filter { it.trim().isNotEmpty() && it.trim().startsWith("* ") }
                    .map { it.trim().removePrefix("* ").trim() }

                Update(
                    versionCode = versionCode,
                    versionName = versionName,
                    changes = changes
                )
            } catch (e: Exception) {
                Log.e("Changelogs", "Failed to parse $filename", e)
                null
            }
        }
    }
}
