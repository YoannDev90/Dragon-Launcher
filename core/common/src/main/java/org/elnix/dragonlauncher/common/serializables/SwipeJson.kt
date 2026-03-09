package org.elnix.dragonlauncher.common.serializables

import android.content.ComponentName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.elnix.dragonlauncher.common.logging.logE
import org.elnix.dragonlauncher.common.utils.SETTINGS
import java.util.UUID


fun dummySwipePoint(
    action: SwipeActionSerializable? = null,
    id: String? = null
) =
    SwipePointSerializable(
        circleNumber = 0,
        angleDeg = 0.0,
        action = action ?: SwipeActionSerializable.OpenDragonLauncherSettings(),
        id = id ?: UUID.randomUUID().toString(),
        nestId = 0
    )

val defaultSwipePointsValues = dummySwipePoint(null).copy(
    borderStroke = 4f,
    borderStrokeSelected = 8f,
    opacity = 1f,
    cornerRadius = null,
    innerPadding = 5,
    size = 22,
    borderShape = IconShape.Circle,
    borderShapeSelected = IconShape.Circle
)


/**
 * Swipe Actions Serializable, the core of the main gesture idea
 * Holds all the different actions the user can do
 */
@Serializable
sealed class SwipeActionSerializable {
    @Serializable
    data class LaunchApp(
        val packageName: String,
        val isPrivateSpace: Boolean,
        val userId: Int?
    ) : SwipeActionSerializable()

    @Serializable
    data class LaunchShortcut(
        val packageName: String,
        val shortcutId: String
    ) : SwipeActionSerializable()

    @Serializable
    data class OpenUrl(val url: String) : SwipeActionSerializable()
    @Serializable
    data class OpenFile(
        val uri: String,
        val mimeType: String? = null
    ) : SwipeActionSerializable()

    @Serializable
    object NotificationShade : SwipeActionSerializable()
    @Serializable
    object ControlPanel : SwipeActionSerializable()
    @Serializable
    data class OpenAppDrawer(val workspaceId: String? = null) : SwipeActionSerializable()
    @Serializable
    data class OpenDragonLauncherSettings(val route: String = SETTINGS.ROOT) : SwipeActionSerializable()
    @Serializable
    object Lock : SwipeActionSerializable()
    @Serializable
    object ReloadApps : SwipeActionSerializable()

    @Serializable
    object OpenRecentApps : SwipeActionSerializable()
    @Serializable
    data class OpenCircleNest(val nestId: Int) : SwipeActionSerializable()
    @Serializable
    object GoParentNest : SwipeActionSerializable()

    @Serializable
    data class OpenWidget(
        val widgetId: Int,
        val providerPackage: String,
        val providerClass: String
    ) : SwipeActionSerializable()

    @Serializable
object None : SwipeActionSerializable()
}

object SwipeJson {
    private val jsonConfig = Json {
        encodeDefaults = true
        ignoreUnknownKeys = true
    }

    private val jsonPretty = Json {
        encodeDefaults = true
        ignoreUnknownKeys = true
        prettyPrint = true
    }

    /* ───────────── Old format, keep it for legacy support ───────────── */

    fun decodeLegacy(jsonString: String): List<SwipePointSerializable> {
        if (jsonString.isBlank()) return emptyList()
        return try {
            // First try modern format
            jsonConfig.decodeFromString(jsonString)
        } catch (e: Throwable) {
            logE("SwipeJson") { "Modern decode failed, trying legacy migration: ${e.message}" }
            
            // Legacy conversion to valid modern Kotlinx-friendly JSON
            try {
                val legacyArray = JSONArray(jsonString)
                val migratedArray = JSONArray()
                
                for (i in 0 until legacyArray.length()) {
                    val old = legacyArray.getJSONObject(i)
                    val new = JSONObject()
                    
                    // Basic fields
                    new.put("a", old.optInt("a")) // circleNumber
                    new.put("b", old.optDouble("b")) // angleDeg
                    new.put("d", old.optString("d")) // id
                    new.put("e", old.optInt("e", 0)) // nestId
                    
                    // Action migration
                    if (old.has("c")) {
                        val oldAction = old.getJSONObject("c")
                        val type = oldAction.optString("type")
                        val newAction = JSONObject()
                        
                        // Action class names for polymorphism
                        val prefix = "org.elnix.dragonlauncher.common.serializables.SwipeActionSerializable"
                        val fullTypeName = when(type) {
                            "LaunchApp" -> "$prefix.LaunchApp"
                            "LaunchShortcut" -> "$prefix.LaunchShortcut"
                            "OpenUrl" -> "$prefix.OpenUrl"
                            "OpenFile" -> "$prefix.OpenFile"
                            "NotificationShade" -> "$prefix.NotificationShade"
                            "ControlPanel" -> "$prefix.ControlPanel"
                            "OpenAppDrawer" -> "$prefix.OpenAppDrawer"
                            "OpenDragonLauncherSettings" -> "$prefix.OpenDragonLauncherSettings"
                            "Lock" -> "$prefix.Lock"
                            "ReloadApps" -> "$prefix.ReloadApps"
                            "OpenRecentApps" -> "$prefix.OpenRecentApps"
                            "OpenCircleNest" -> "$prefix.OpenCircleNest"
                            "GoParentNest" -> "$prefix.GoParentNest"
                            "OpenWidget" -> "$prefix.OpenWidget"
                            else -> "$prefix.None"
                        }
                        
                        newAction.put("type", fullTypeName)
                        
                        // Copy specific parameters
                        when (type) {
                            "LaunchApp" -> {
                                newAction.put("packageName", oldAction.optString("packageName"))
                                newAction.put("isPrivateSpace", oldAction.optBoolean("isPrivateSpace"))
                                newAction.put("userId", oldAction.optInt("userId", 0))
                            }
                            "LaunchShortcut" -> {
                                newAction.put("packageName", oldAction.optString("packageName"))
                                newAction.put("shortcutId", oldAction.optString("shortcutId"))
                            }
                            "OpenUrl" -> newAction.put("url", oldAction.optString("url"))
                            "OpenFile" -> {
                                newAction.put("uri", oldAction.optString("uri"))
                                newAction.put("mimeType", oldAction.optString("mimeType", null))
                            }
                            "OpenAppDrawer" -> newAction.put("workspaceId", oldAction.optString("workspaceId", null))
                            "OpenDragonLauncherSettings" -> newAction.put("route", oldAction.optString("route", "settings"))
                            "OpenCircleNest" -> newAction.put("nestId", oldAction.optInt("nestId", 0))
                            "OpenWidget" -> {
                                newAction.put("widgetId", oldAction.optInt("widgetId"))
                                newAction.put("providerPackage", oldAction.optString("providerPackage"))
                                newAction.put("providerClass", oldAction.optString("providerClass"))
                            }
                        }
                        new.put("c", newAction)
                    }
                    
                    // Map Shape if present
                    if (old.has("f")) {
                        // Custom icon migration if needed, most is nullable
                        new.put("f", old.get("f"))
                    } else if (old.has("borderShape")) {
                        new.put("borderShape", old.optString("borderShape", "Circle"))
                    }

                    migratedArray.put(new)
                }
                
                jsonConfig.decodeFromString(migratedArray.toString())
            } catch (e2: Throwable) {
                logE("SwipeJson") { "Legacy migration failed: ${e2.message}" }
                emptyList()
            }
        }
    }

    /* ───────────── Points ───────────── */

    fun encodePoints(points: List<SwipePointSerializable>): String =
        jsonConfig.encodeToString(points)

    fun encodePointsPretty(points: List<SwipePointSerializable>): String =
        jsonPretty.encodeToString(points)

    fun decodePoints(json: String): List<SwipePointSerializable> {
        return decodeLegacy(json)
    }

    /* ───────────── Nests ───────────── */

    fun encodeNests(nests: List<CircleNest>): String =
        jsonConfig.encodeToString(nests)

    fun encodeNestsPretty(nests: List<CircleNest>): String =
        jsonPretty.encodeToString(nests)

    fun decodeNests(json: String): List<CircleNest> {
        if (json.isBlank()) return emptyList()
        return try {
            jsonConfig.decodeFromString(json)
        } catch (e: Throwable) {
            logE("SwipeJson") { "Modern nest decode failed: ${e.message}" }
            
            // Legacy nest migration
            try {
                val legacyArray = JSONArray(json)
                val migratedArray = JSONArray()
                
                for (i in 0 until legacyArray.length()) {
                    val old = legacyArray.getJSONObject(i)
                    val new = JSONObject()
                    
                    new.put("id", old.optInt("id", i))
                    
                    // JSON Maps: If empty object, we need to ensure it's not present or is valid empty
                    if (old.has("dragDistances") && old.getJSONObject("dragDistances").length() > 0) {
                        new.put("dragDistances", old.get("dragDistances"))
                    } else {
                        // Default distances if missing or empty
                        val defDist = JSONObject()
                        defDist.put("-1", 150)
                        defDist.put("0", 300)
                        defDist.put("1", 450)
                        defDist.put("2", 600)
                        new.put("dragDistances", defDist)
                    }
                    
                    if (old.has("haptic")) new.put("haptic", old.get("haptic"))
                    if (old.has("minAngleActivation")) new.put("minAngleActivation", old.get("minAngleActivation"))
                    if (old.has("nestRadius")) new.put("nestRadius", old.get("nestRadius"))
                    if (old.has("name")) new.put("name", old.getString("name"))

                    migratedArray.put(new)
                }
                jsonConfig.decodeFromString(migratedArray.toString())
            } catch (e2: Throwable) {
                logE("SwipeJson") { "Nest migration failed: ${e2.message}" }
                emptyList()
            }
        }
    }

    fun encodeAction(action: SwipeActionSerializable?): String? =
        action?.let {
            jsonConfig.encodeToString(it)
        }

    fun decodeAction(jsonString: String): SwipeActionSerializable? {
        if (jsonString.isBlank() || jsonString == "{}") return null
        return try {
            jsonConfig.decodeFromString<SwipeActionSerializable>(jsonString)
        } catch (_: Throwable) {
            null
        }
    }

    private fun <T> decodeSafe(json: String): List<T> {
        // Redundant with the specific methods above since we moved to Kotlinx
        return emptyList()
    }
}
