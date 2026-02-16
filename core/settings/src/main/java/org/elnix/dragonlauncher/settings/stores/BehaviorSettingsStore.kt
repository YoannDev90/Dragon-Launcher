package org.elnix.dragonlauncher.settings.stores

import org.elnix.dragonlauncher.common.serializables.SwipeActionSerializable
import org.elnix.dragonlauncher.settings.DataStoreName
import org.elnix.dragonlauncher.settings.Settings
import org.elnix.dragonlauncher.settings.bases.BaseSettingObject
import org.elnix.dragonlauncher.settings.bases.MapSettingsStore

object BehaviorSettingsStore : MapSettingsStore() {

    override val name: String = "Behavior"

    override val dataStoreName = DataStoreName.BEHAVIOR

    override val ALL: List<BaseSettingObject <*, *> >
        get() = listOf(
            backAction,
            doubleClickAction,
            homeAction,
            keepScreenOn,
            leftPadding,
            rightPadding,
            topPadding,
            bottomPadding,
            holdDelayBeforeStartingLongClickSettings,
            longCLickSettingsDuration,
            disableHapticFeedbackGlobally,
            pointsActionSnapsToOuterCircle
        )

    val backAction = Settings.swipeAction(
        key = "backAction",
        dataStoreName = dataStoreName,
        default = SwipeActionSerializable.None
    )

    val doubleClickAction = Settings.swipeAction(
        key = "doubleClickAction",
        dataStoreName = dataStoreName,
        default = SwipeActionSerializable.None
    )

    val homeAction = Settings.swipeAction(
        key = "homeAction",
        dataStoreName = dataStoreName,
        default = SwipeActionSerializable.None
    )

    val keepScreenOn = Settings.boolean(
        key = "keepScreenOn",
        dataStoreName = dataStoreName,
        default = false
    )

    val leftPadding = Settings.int(
        key = "leftPadding",
        dataStoreName = dataStoreName,
        default = 60,
        allowedRange = 0..300
    )

    val rightPadding = Settings.int(
        key = "rightPadding",
        dataStoreName = dataStoreName,
        default = 60,
        allowedRange = 0..300
    )

    val topPadding = Settings.int(
        key = "upPadding",
        dataStoreName = dataStoreName,
        default = 80,
        allowedRange = 0..300
    )

    val bottomPadding = Settings.int(
        key = "downPadding",
        dataStoreName = dataStoreName,
        default = 100,
        allowedRange = 0..300
    )

    val holdDelayBeforeStartingLongClickSettings = Settings.int(
        key = "holdDelayBeforeStartingLongClickSettings",
        dataStoreName = dataStoreName,
        default = 500,
        allowedRange = 200..2000
    )

    val longCLickSettingsDuration = Settings.int(
        key = "longCLickSettingsDuration",
        dataStoreName = dataStoreName,
        default = 1000,
        allowedRange = 200..5000
    )

    val disableHapticFeedbackGlobally = Settings.boolean(
        key = "disableHapticFeedbackGlobally",
        dataStoreName = dataStoreName,
        default = false
    )

    val pointsActionSnapsToOuterCircle = Settings.boolean(
        key = "pointsActionSnapsToOuterCircle",
        dataStoreName = dataStoreName,
        default = true
    )

    val superWarningMode = Settings.boolean(
        key = "superWarningMode",
        dataStoreName = dataStoreName,
        default = false
    )


    val vibrateOnError = Settings.boolean(
        key = "vibrateOnError",
        dataStoreName = dataStoreName,
        default = false
    )


    val alarmSound = Settings.boolean(
        key = "alarmSound",
        dataStoreName = dataStoreName,
        default = false
    )

    val metalPipesSound = Settings.boolean(
        key = "metalPipesSound",
        dataStoreName = dataStoreName,
        default = false
    )

    val superWarningModeSound = Settings.int(
        key = "superWarningModeSound",
        dataStoreName = dataStoreName,
        default = 100,
        allowedRange = 0..100
    )
}
