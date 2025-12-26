package org.elnix.dragonlauncher.ui.drawer

import org.elnix.dragonlauncher.data.SwipeActionSerializable

data class AppModel(
    val name: String,
    val packageName: String,
    val isEnabled: Boolean,
    val isSystem: Boolean,
    val isWorkProfile: Boolean,
    val isLaunchable: Boolean?,
    val settings: Map<String, Any> = emptyMap()
) {
    val action = SwipeActionSerializable.LaunchApp(packageName)
}


enum class WorkspaceType {
    ALL,
    USER,
    SYSTEM,
    WORK,
    CUSTOM
}

data class Workspace(
    val id: String,
    val name: String,
    val type: WorkspaceType,
    val appIds: List<String>,
    val removedAppIds: List<String>?, // Nullable cause I added it recently in 1.2.2, so if you were on previous versions, it'll cause crash
    val enabled: Boolean
)


data class AppOverride(
    val packageName: String,
    val customLabel: String? = null,
    val customIconBase64: String? = null)



data class WorkspaceState(
    val workspaces: List<Workspace> = defaultWorkspaces,
    val appOverrides: Map<String, AppOverride> = emptyMap()
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
    Workspace("user", "User", WorkspaceType.USER, emptyList(), emptyList(), true),
    Workspace("system", "System", WorkspaceType.SYSTEM, emptyList(), emptyList(), false),
    Workspace("all", "All", WorkspaceType.ALL, emptyList(), emptyList(),  false),
    Workspace("work", "Work", WorkspaceType.WORK, emptyList(), emptyList(),  false)
)



data class IconPackInfo(val packageName: String, val name: String)
data class IconMapping(val component: String, val drawable: String)
