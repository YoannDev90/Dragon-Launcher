package org.elnix.dragonlauncher.utils.workspace

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.elnix.dragonlauncher.data.stores.WorkspaceSettingsStore
import org.elnix.dragonlauncher.ui.drawer.AppOverride
import org.elnix.dragonlauncher.ui.drawer.Workspace
import org.elnix.dragonlauncher.ui.drawer.WorkspaceState
import org.elnix.dragonlauncher.ui.drawer.WorkspaceType
import org.elnix.dragonlauncher.ui.drawer.defaultWorkspaces
import org.json.JSONObject


class WorkspaceViewModel(application: Application) : AndroidViewModel(application) {

    private val gson = Gson()
    @SuppressLint("StaticFieldLeak")
    private val ctx = application.applicationContext

    private val _state = MutableStateFlow(
        WorkspaceState(
            workspaces = defaultWorkspaces,
            appOverrides = emptyMap()
        )
    )
    val state: StateFlow<WorkspaceState> = _state.asStateFlow()

    /** Get enabled workspaces only */
    val enabledState: StateFlow<WorkspaceState> = _state
        .map { state ->
            state.copy(
                workspaces = state.workspaces.filter { it.enabled }
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = WorkspaceState(
                workspaces = defaultWorkspaces.filter { it.enabled },
                appOverrides = emptyMap()
            )
        )


    private val _selectedWorkspaceId = MutableStateFlow("user")
    val selectedWorkspaceId: StateFlow<String> = _selectedWorkspaceId.asStateFlow()

    init {
        load()
    }

    private fun load() = viewModelScope.launch(Dispatchers.IO) {
        val json = WorkspaceSettingsStore.getAll(ctx).toString()
        _state.value = gson.fromJson(json, WorkspaceState::class.java)
    }


    private fun persist() = viewModelScope.launch(Dispatchers.IO) {
        WorkspaceSettingsStore.setAll(
            ctx,
            JSONObject(gson.toJson(_state.value))
        )
    }

    fun selectWorkspace(id: String) {
        _selectedWorkspaceId.value = id
    }


    /** Enable/disable a workspace */
    fun setWorkspaceEnabled(id: String, enabled: Boolean) {
        _state.value = _state.value.copy(
            workspaces = _state.value.workspaces.map { workspace ->
                if (workspace.id == id) {
                    workspace.copy(enabled = enabled)
                } else {
                    workspace
                }
            }
        )
        persist()
    }

    fun createWorkspace(name: String) {
        _state.value = _state.value.copy(
            workspaces = _state.value.workspaces +
                    Workspace(
                        id = System.currentTimeMillis().toString(),
                        name = name,
                        type = WorkspaceType.CUSTOM,
                        enabled = true,
                        appIds = emptyList()
                    )
        )
        persist()
    }

    fun renameWorkspace(id: String, name: String) {
        _state.value = _state.value.copy(
            workspaces = _state.value.workspaces.map {
                if (it.id == id) it.copy(name = name) else it
            }
        )
        persist()
    }

    fun deleteWorkspace(id: String) {
        _state.value = _state.value.copy(
            workspaces = _state.value.workspaces.filterNot { it.id == id }
        )
        persist()
    }

    fun setWorkspaceOrder(newOrder: List<Workspace>) {
        _state.value = _state.value.copy(workspaces = newOrder)
        persist()
    }


    // Apps operations
    fun addAppToWorkspace(workspaceId: String, packageName: String) {
        _state.value = _state.value.copy(
            workspaces = _state.value.workspaces.map { ws ->
                if (ws.id == workspaceId && ws.type == WorkspaceType.CUSTOM) {
                    if (packageName in ws.appIds) ws
                    else ws.copy(appIds = ws.appIds + packageName)
                } else ws
            }
        )
        persist()
    }

    fun removeAppFromWorkspace(workspaceId: String, packageName: String) {
        _state.value = _state.value.copy(
            workspaces = _state.value.workspaces.map { ws ->
                if (ws.id == workspaceId && ws.type == WorkspaceType.CUSTOM) {
                    ws.copy(appIds = ws.appIds - packageName)
                } else ws
            }
        )
        persist()
    }

    fun renameApp(packageName: String, name: String) {
        _state.value = _state.value.copy(
            appOverrides = _state.value.appOverrides +
                    (packageName to AppOverride(packageName, name))
        )
        persist()
    }

    fun setAppIcon(packageName: String, uri: String) {
        val prev = _state.value.appOverrides[packageName]
        _state.value = _state.value.copy(
            appOverrides = _state.value.appOverrides +
                    (packageName to (prev?.copy(customIconUri = uri)
                        ?: AppOverride(packageName, customIconUri = uri)))
        )
        persist()
    }


    fun resetWorkspacesAndOverrides() {
        _state.value = WorkspaceState(
            workspaces = defaultWorkspaces,
            appOverrides = emptyMap()
        )
        persist()
    }

}
