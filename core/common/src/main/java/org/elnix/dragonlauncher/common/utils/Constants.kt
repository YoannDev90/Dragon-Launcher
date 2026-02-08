package org.elnix.dragonlauncher.common.utils

import org.elnix.dragonlauncher.common.serializables.SwipeActionSerializable

const val obtainiumPackageName = "dev.imranr.obtainium.fdroid"



val systemLaunchers = listOf(
    // Xiaomi/RedMagic/HyperOS/MIUI
    "com.miui.home",
    "com.miui.home.launcher",
    "com.zui.launcher",
    "com.redmagic.launcher",

    // Samsung OneUI
    "com.sec.android.app.launcher",
    "com.samsung.android.app.launcher",

    // ZTE/Nubia
    "com.zte.mifavor.launcher",
    "com.android.nubialauncher",

    // OnePlus OxygenOS/ColorOS
    "com.oneplus.launcher",
    "com.oplus.launcher",

    // OPPO/Realme
    "com.oppo.launcher",
    "com.coloros.safecenter.launcher",

    // Huawei EMUI/HarmonyOS
    "com.huawei.android.launcher",
    "com.huawei.android.home",

    // Google Pixel/Stock Android
    "com.google.android.apps.nexuslauncher",
    "com.android.launcher3",

    // Sony
    "com.sonymobile.home",

    // LG
    "com.lge.launcher2",
    "com.lge.launcher3",

    // HTC
    "com.htc.launcher",

    // Motorola
    "com.motorola.blur.launcher",

    // Vivo FuntouchOS
    "com.iuni.launcher",

    // Nothing OS
    "com.nothing.launcher",

    // Fairphone
    "ch.fairphone.launcher"
)

/** List of routes that the routes killer ignores when the user leave the app for too long, usually files pickers */
val ignoredReturnRoutes = listOf(
    ROUTES.WELCOME,
    SETTINGS.BACKUP,
    SETTINGS.WALLPAPER,
    SETTINGS.FLOATING_APPS
)

val transparentScreens = listOf(
    ROUTES.MAIN,
    ROUTES.DRAWER,
    SETTINGS.ROOT, // To tell the main NavHost that settings are transparent, then the settings scaffold will take the relay and apply the color where it needs
    SETTINGS.WALLPAPER,
    SETTINGS.FLOATING_APPS
)


/* Themes loader utils */
const val themesDir = "themes"
val imageExts = listOf("png", "jpg", "jpeg", "webp")




val defaultChoosableActions = listOf(
    SwipeActionSerializable.OpenCircleNest(0),
    SwipeActionSerializable.GoParentNest,
    SwipeActionSerializable.LaunchApp(""),
    SwipeActionSerializable.OpenUrl(""),
    SwipeActionSerializable.OpenFile(""),
    SwipeActionSerializable.NotificationShade,
    SwipeActionSerializable.ControlPanel,
    SwipeActionSerializable.OpenAppDrawer,
    SwipeActionSerializable.Lock,
    SwipeActionSerializable.ReloadApps,
    SwipeActionSerializable.OpenRecentApps,
    SwipeActionSerializable.OpenDragonLauncherSettings
)


/*  ─────────────  Tags constants  ─────────────  */
const val TAG = "DragonLauncherDebug"
const val APPS_TAG = "AppsVm"
const val ICONS_TAG = "IconsDebug"
const val BACKUP_TAG = "SettingsBackupManager"
const val SWIPE_TAG = "SwipeDebug"
const val WIDGET_TAG = "WidgetsDebug"
const val FLOATING_APPS_TAG = "FloatingAppsDebug"
const val ACCESSIBILITY_TAG = "SystemControl"
const val IMAGE_TAG = "ImageDebug"
const val SETTINGS_TAG = "SettingsDebug"
const val ICON_PACK_TAG = "IconsPacks"
const val SHAPES_TAG = "ShapesDebug"
const val COLORS_TAG = "ColorsDebug"
const val DRAWER_TAG = "DrawerDebug"



/*  ─────────────  Settings Screen Constants  ─────────────  */

const val POINT_RADIUS_PX = 40f
const val TOUCH_THRESHOLD_PX = 100f

const val SNAP_STEP_DEG = 15.0


/*  ─────────────  Links  ─────────────  */

const val discordInviteLink = "https://discord.gg/6UyuP8EBWS"


const val HOME_REENTER_WINDOW_MS = 80L
