package org.elnix.dragonlauncher.common.serializables

import kotlinx.serialization.Serializable

@Serializable
sealed class IconShape {

    @Serializable
    data object PlatformDefault : IconShape()

    @Serializable
    data object Circle : IconShape()

    @Serializable
    data object Square : IconShape()

    @Serializable
    data object RoundedSquare : IconShape()

    @Serializable
    data object Triangle : IconShape()

    @Serializable
    data object Squircle : IconShape()

    @Serializable
    data object Hexagon : IconShape()

    @Serializable
    data object Pentagon : IconShape()

    @Serializable
    data object Teardrop : IconShape()

    @Serializable
    data object Pebble : IconShape()

    @Serializable
    data object EasterEgg : IconShape()


    @Serializable
    data object Random : IconShape()


    @Serializable
    data object Dragon : IconShape()
    @Serializable
    data class Custom(
        val shape: CustomIconShapeSerializable
    ) : IconShape()
}


val allShapes = listOf(
    IconShape.PlatformDefault,
    IconShape.Circle,
    IconShape.Square,
    IconShape.RoundedSquare,
    IconShape.Triangle,
    IconShape.Squircle,
    IconShape.Hexagon,
    IconShape.Pentagon,
    IconShape.Teardrop,
    IconShape.Pebble,
    IconShape.EasterEgg,
    IconShape.Random,
    IconShape.Dragon
)
