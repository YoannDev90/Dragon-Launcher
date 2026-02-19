package org.elnix.dragonlauncher.common.serializables

import android.content.pm.ApplicationInfo
import com.google.gson.annotations.SerializedName

data class AppModel(
    @SerializedName("a") val name: String,
    @SerializedName("b") val packageName: String,
    @SerializedName("c") val isEnabled: Boolean,
    @SerializedName("d") val isSystem: Boolean,
    @SerializedName("e") val isWorkProfile: Boolean,
    @SerializedName("f") val isLaunchable: Boolean?,
    @SerializedName("g") val settings: Map<String, Any> = emptyMap(),
    @SerializedName("h") val userId: Int? = 0,
    @SerializedName("category") val category: AppCategory,
    @SerializedName("isPrivateProfile") val isPrivateProfile: Boolean = false // Android 15+ Private Space
) {
    val action = SwipeActionSerializable.LaunchApp(packageName,isPrivateProfile , userId ?: 0)

    /**
     * Builds a stable cache key for an app icon entry.
     *
     * The key is composed of:
     * - the application package name
     * - a user identifier suffix
     *
     * Format:
     * `packageName#userId`
     *
     * If [userId] is null, `0` is used as a fallback. This ensures:
     * - consistent keys for primary user apps
     * - separation between the same package installed for different users/profiles
     *
     * Example:
     * - `com.example.app#0`
     * - `com.example.app#10`
     *
     * @param packageName The application package name.
     * @param userId The Android user/profile id. Null defaults to 0.
     * @return A unique and deterministic cache key for icon storage.
     */
    val iconCacheKey: CacheKey
        get() = CacheKey("$packageName#${userId ?: 0}")
}


data class CacheKey (
    val cacheKey: String
)

fun SwipeActionSerializable.LaunchApp.toAppModel() =
    AppModel(
        name = packageName,
        packageName = packageName,
        isEnabled = true, // Unknown
        isSystem = false, // Unknown
        isWorkProfile = false, // Unknown
        isLaunchable = true, // Unknown
        userId = userId,
        category = AppCategory.Other,
        isPrivateProfile = false, // Unknown
    )



enum class AppCategory {
    Games,
    Audio,
    Video,
    Images,
    Social,
    News,
    Maps,
    Productivity,
    Accessibility,
    Other
}



fun mapSystemCategoryToSection(category: Int): AppCategory {
    return when (category) {
        ApplicationInfo.CATEGORY_GAME -> AppCategory.Games

        ApplicationInfo.CATEGORY_AUDIO -> AppCategory.Audio
        ApplicationInfo.CATEGORY_VIDEO -> AppCategory.Video
        ApplicationInfo.CATEGORY_IMAGE -> AppCategory.Images

        ApplicationInfo.CATEGORY_SOCIAL -> AppCategory.Social
        ApplicationInfo.CATEGORY_NEWS -> AppCategory.News
        ApplicationInfo.CATEGORY_MAPS -> AppCategory.Maps

        ApplicationInfo.CATEGORY_PRODUCTIVITY -> AppCategory.Productivity
        ApplicationInfo.CATEGORY_ACCESSIBILITY -> AppCategory.Accessibility

        ApplicationInfo.CATEGORY_UNDEFINED -> AppCategory.Other

        else -> AppCategory.Other
    }
}

fun mapAppToSection(app: ApplicationInfo): AppCategory =
     mapSystemCategoryToSection(app.category)




enum class WorkspaceType {
    ALL,
    USER,
    SYSTEM,
    WORK,
    PRIVATE,  // Android 15+ Private Space
    CUSTOM
}

data class Workspace(
    val id: String,
    val name: String,
    val type: WorkspaceType,
    val appIds: List<String>,
    val removedAppIds: List<String>?, // Nullable cause I added it in 1.2.2, so if you were on previous versions, it'll cause crash
    val enabled: Boolean
)


data class AppOverride(
    val packageName: String,
    val customLabel: String? = null,
    val customIcon: CustomIconSerializable? = null,
    val customCategory: String? = null
)



data class WorkspaceState(
    val workspaces: List<Workspace> = defaultWorkspaces,
    val appOverrides: Map<String, AppOverride> = emptyMap(),
    val appAliases: Map<String, Set<String>> = emptyMap()
)

fun resolveApp(
    app: AppModel,
    overrides: Map<String, AppOverride>
): AppModel {
    val o = overrides[app.packageName] ?: return app
    return app.copy(name = o.customLabel ?: app.name)
}


// I disable non-user workspaces by default, enable it if you need it (only used for nerds) (those who download my app are btw :) )
val defaultWorkspaces = listOf(
    Workspace("user", "User", WorkspaceType.USER, emptyList(), listOf("org.elnix.dragonlauncher"), true),
    Workspace("system", "System", WorkspaceType.SYSTEM, emptyList(), emptyList(), false),
    Workspace("all", "All", WorkspaceType.ALL, emptyList(), emptyList(),  false),
    Workspace("work", "Work", WorkspaceType.WORK, emptyList(), emptyList(),  false),
    Workspace("private", "Private Space", WorkspaceType.PRIVATE, emptyList(), emptyList(), false) // Android 15+ only
)



data class IconPackInfo(
    val packageName: String,
    val name: String,
    val isManualOnly: Boolean
)
data class IconMapping(val component: String, val drawable: String)
