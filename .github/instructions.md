# Dragon Launcher - AI Coding Agent Instructions

## Project Overview
Dragon Launcher is a gesture-based Android launcher built with **Jetpack Compose** and **Kotlin**. It's privacy-focused (no internet permission), offline-first, and highly customizable. The app uses a modular architecture with multiple core modules.

## Architecture

### Module Structure
```
app/                          # Main application module
core/
  ├── common/                 # Shared utilities, constants, logging, serializable models
  ├── enumsui/               # UI-related enums (gesture actions, wallpaper modes, drawer actions)
  ├── models/                # ViewModels (Apps, Backup, FloatingApps, AppLifecycle)
  ├── services/              # Android services (SystemControl, DeviceAdmin, Accessibility)
  ├── settings/              # Settings persistence layer (DataStore-based)
  └── ui/                    # Compose UI components and screens
```

### Key Components

**MainActivity** ([app/src/main/java/org/elnix/dragonlauncher/MainActivity.kt](app/src/main/java/org/elnix/dragonlauncher/MainActivity.kt))
- Entry point, implements `WidgetHostProvider`
- Manages AppWidgetHost lifecycle
- Handles HOME intent detection for gesture actions
- Auto-backup trigger on pause/resume

**Navigation Structure** ([core/ui/src/main/java/org/elnix/dragonlauncher/ui/MainAppUi.kt](core/ui/src/main/java/org/elnix/dragonlauncher/ui/MainAppUi.kt))
- `ROUTES.MAIN` → MainScreen (gesture launcher with swipe points)
- `ROUTES.DRAWER` → AppDrawer (app list with search)
- `ROUTES.WELCOME` → WelcomeScreen (first-run setup)
- `SETTINGS.ROOT` → SettingsNavHost (nested settings navigation)

**Gesture System** ([core/ui/src/main/java/org/elnix/dragonlauncher/ui/MainScreen.kt](core/ui/src/main/java/org/elnix/dragonlauncher/ui/MainScreen.kt))
- Swipe-based launcher with configurable "points" (SwipePointSerializable)
- Circle nests for hierarchical app organization
- Custom pointer input handling with touch detection
- Actions configured via SwipeActionSerializable (launch apps, open drawer, lock screen, etc.)

### Settings System

**Critical Pattern**: Dragon uses a robust DataStore-based settings architecture with type-safe wrappers.

**Store Types**:
- `MapSettingsStore` ([core/settings/src/main/java/org/elnix/dragonlauncher/settings/bases/MapSettingsStore.kt](core/settings/src/main/java/org/elnix/dragonlauncher/settings/bases/MapSettingsStore.kt)) - Individual preference keys (most common)
- `JsonSettingsStore` ([core/settings/src/main/java/org/elnix/dragonlauncher/settings/bases/JsonSettingsStore.kt](core/settings/src/main/java/org/elnix/dragonlauncher/settings/bases/JsonSettingsStore.kt)) - Single JSON blob (SwipeSettings, WorkspaceSettings, AppsSettings)

**Creating Settings**:
```kotlin
// In a Store object (e.g., BehaviorSettingsStore)
object MySettingsStore : MapSettingsStore() {
    override val name = "MySettings"
    override val dataStoreName = DataStoreName.BEHAVIOR
    override val ALL: List<BaseSettingObject<*,*>> get() = listOf(myBoolSetting, myIntSetting)
    
    val myBoolSetting = Settings.boolean(
        key = "myBoolKey",
        dataStoreName = dataStoreName,
        default = false
    )
}
```

**Usage in Compose**:
```kotlin
val myValue by MySettingsStore.myBoolSetting.asState()
val myValue by MySettingsStore.myBoolSetting.asStateNull() // Starts with null value

// Old model, mays still use for specific cases:
val myValue by MySettingsStore.myBoolSetting.flow(ctx).collectAsState(defaultValue)
val myValue by MySettingsStore.myBoolSetting.get(ctx)
scope.launch { MySettingsStore.myBoolSetting.set(ctx, newValue) }

```

**Backup Integration**: Settings stores are automatically backed up via `SettingsBackupManager`. Add new stores to `DataStoreName` enum and `SettingsStoreRegistry`.

### ViewModels

**AppsViewModel** ([core/models/src/main/java/org/elnix/dragonlauncher/models/AppsViewModel.kt](core/models/src/main/java/org/elnix/dragonlauncher/models/AppsViewModel.kt))
- Loads all installed apps with PackageManager
- Icon pack support (loads drawables from external icon packs)
- App caching in DataStore for faster startup
- Workspace management (multiple workspaces with app lists)

**AppLifecycleViewModel** ([core/models/src/main/java/org/elnix/dragonlauncher/models/AppLifecycleViewModel.kt](core/models/src/main/java/org/elnix/dragonlauncher/models/AppLifecycleViewModel.kt))
- Tracks HOME button events via SharedFlow
- Manages background/foreground transitions
- Auto-return to main screen after 10s away

**FloatingAppsViewModel** - Manages widget/floating app state

### Accessibility & System Control

**SystemControlService** ([core/services/src/main/java/org/elnix/dragonlauncher/services/SystemControlService.kt](core/services/src/main/java/org/elnix/dragonlauncher/services/SystemControlService.kt))
- Optional AccessibilityService for system actions
- Expand notifications: `GLOBAL_ACTION_NOTIFICATIONS`
- Lock screen: `GLOBAL_ACTION_LOCK_SCREEN`
- Recent apps: `GLOBAL_ACTION_RECENTS`
- Auto-launch Dragon on Xiaomi devices (workaround for launcher restrictions)

**Device Admin** ([core/services/src/main/java/org/elnix/dragonlauncher/services/DeviceAdminReceiver.kt](core/services/src/main/java/org/elnix/dragonlauncher/services/DeviceAdminReceiver.kt))
- Optional, prevents aggressive battery optimization on Xiaomi/HyperOS
- Used alongside accessibility for persistence

## Development Workflows

### Build Flavors
- `stable` - Production release
- `beta` - Beta testing with version suffix
- `fdroid` - F-Droid distribution (no special config)

### Building
```bash
./gradlew assembleStableRelease      # Production APK
./gradlew assembleBetaRelease        # Debug build
```

### Dependencies
Managed via Version Catalog ([gradle/libs.versions.toml](gradle/libs.versions.toml)):
- Compose BOM for UI
- DataStore for persistence
- Gson for JSON serialization
- Reorderable library for drag-and-drop lists

### Signing
Release signing uses `.env` file (not in repo):
```properties
KEYSTORE_FILE=path/to/keystore
KEYSTORE_PASSWORD=***
KEY_ALIAS=***
KEY_PASSWORD=***
```

## Code Style (from [CONTRIBUTING.MD](CONTRIBUTING.MD))

**Naming**:
- `camelCase` for variables/functions/settings keys
- `snake_case` for resources (strings, drawables)
- `SCREAMING_SNAKE_CASE` for constants

**Formatting**:
- Always spaces in brackets: `if (cond) { ... }`
- Else on same line as closing brace: `} else {`
- Use `─` character in comment separators: `/* ─────────── Section ─────────── */`
- Always add docstrings 
- Comment when needed to help readability

**Example**:
```kotlin
/*  ─────────────  Tags constants  ─────────────  */
fun foo() {
    if (cond) {
        bar()
    } else {
        baz()
    }
}
```

## Key Patterns & Idioms

### Logging
```kotlin
import org.elnix.dragonlauncher.common.logging.*
ctx.logD(TAG, "Debug message")
ctx.logI(TAG, "Info message")
ctx.logE(TAG, "Error message", exception)
```

Tags are stored in [Constants.kt](core/common/src/main/java/org/elnix/dragonlauncher/common/utils/Constants.kt)

### Launch Actions
All gesture/swipe actions go through `launchSwipeAction()` ([core/ui/src/main/java/org/elnix/dragonlauncher/ui/actions/launchSwipeAction.kt](core/ui/src/main/java/org/elnix/dragonlauncher/ui/actions/launchSwipeAction.kt)):
- Launches apps, shortcuts, URLs
- Opens system dialogs (notifications, recents)
- Triggers internal navigation (drawer, settings)


## Privacy & Permissions

**No Internet Access** - Dragon never requests `android.permission.INTERNET`
- All data stored locally
- Backup/restore is manual or auto-local via SAF

**Optional Permissions**:
- `BIND_ACCESSIBILITY_SERVICE` - For system actions (lock, notifications, recents)
- Device Admin - Battery optimization workaround for Xiaomi

## Common Tasks

### Adding a New Setting
1. Add to appropriate Store in `core/settings/src/main/java/org/elnix/dragonlauncher/settings/stores/`
2. Use `Settings.boolean()`, `Settings.int()`, `Settings.color()`, etc.
3. Add to store's `ALL` list
4. Use `.flow(ctx)` in Compose, `.set(ctx, value)` to update
5. Prefer now the dedicated Settings objects to mutate value dynamically in settings: [SettingsSliderInt.kt](core/ui/src/main/java/org/elnix/dragonlauncher/ui/components/settings/SettingsSliderInt.kt)


### Adding a New Swipe Action
1. Add enum case to `SwipeActionSerializable` in `core/common/`
2. Add color to theme in `core/ui/src/main/java/org/elnix/dragonlauncher/ui/theme/`
3. Implement in `launchSwipeAction()` function
4. Add icon/label helpers in `core/enumsui/`

### Adding a Screen
1. Add route constant to `ROUTES` or `SETTINGS` in [core/common/src/main/java/org/elnix/dragonlauncher/common/utils/Constants.kt](core/common/src/main/java/org/elnix/dragonlauncher/common/utils/Constants.kt)
2. Add `noAnimComposable(ROUTE) { ... }` in MainAppUi or SettingsNavHost
3. Navigate via `navController.navigate(ROUTE)`

## Testing Notes
- Manual testing required (no automated tests currently)
- Test on physical devices, especially Xiaomi/HyperOS for edge cases
- Check accessibility service behavior on different Android versions

## Important Files Reference
- [MainActivity.kt](app/src/main/java/org/elnix/dragonlauncher/MainActivity.kt) - App entry, widget host
- [MainScreen.kt](core/ui/src/main/java/org/elnix/dragonlauncher/ui/MainScreen.kt) - Gesture launcher UI
- [AppDrawer.kt](core/ui/src/main/java/org/elnix/dragonlauncher/ui/drawer/AppDrawer.kt) - App list with search
- [AppsViewModel.kt](core/models/src/main/java/org/elnix/dragonlauncher/models/AppsViewModel.kt) - App loading logic
- [Settings.kt](core/settings/src/main/java/org/elnix/dragonlauncher/settings/Settings.kt) - Settings factory
- [SystemControlService.kt](core/services/src/main/java/org/elnix/dragonlauncher/services/SystemControlService.kt) - Accessibility actions
- [Constants.kt](core/common/src/main/java/org/elnix/dragonlauncher/common/utils/Constants.kt) - Routes, tags, constants
