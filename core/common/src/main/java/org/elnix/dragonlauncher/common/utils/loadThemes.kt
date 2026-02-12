package org.elnix.dragonlauncher.common.utils

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.elnix.dragonlauncher.common.utils.Constants.Paths.imageExts
import org.elnix.dragonlauncher.common.utils.Constants.Paths.themesDir
import org.json.JSONObject

suspend fun loadThemes(ctx: Context): List<ThemeObject> = withContext(Dispatchers.IO) {
    val am = ctx.assets
    val jsonFiles = am.list(themesDir)?.filter { it.endsWith(".json") }.orEmpty()
    val themesList = mutableListOf<ThemeObject>()

    jsonFiles.forEach { jsonFileName ->
        try {
            val jsonString = am.open("${themesDir}/$jsonFileName").bufferedReader().use { it.readText() }
            val jsonObject = JSONObject(jsonString)

            val themeBaseName = jsonFileName.removeSuffix(".json")
            val themeName = themeBaseName.replace(Regex("[-_]"), " ")
                .split(" ").joinToString(" ") { it.replaceFirstChar { char -> char.uppercaseChar() } }

            // Find exact matching image
            val imageAssetPath = imageExts.firstOrNull { ext ->
                val imageFile = "${themeBaseName}.$ext"
                am.list(themesDir)?.contains(imageFile) == true
            }?.let { ext -> "${themesDir}/${themeBaseName}.$ext" }

            themesList.add(ThemeObject(
                name = themeName,
                json = jsonObject,
                imageAssetPath = imageAssetPath
            ))
        } catch (e: Exception) {
            println("Failed to load theme $jsonFileName: ${e.message}")
        }
    }
    themesList
}
