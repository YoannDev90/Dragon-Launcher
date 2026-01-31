package org.elnix.dragonlauncher.common.serializables

import com.google.gson.annotations.SerializedName

/**
 * Defines independent corner radii for a rectangular shape.
 *
 * Any null value falls back to renderer defaults or global radius.
 */
data class CustomIconShapeSerializable(
    @SerializedName("topLeft") val topLeft: CornerRadiusSerializable? = null,
    @SerializedName("topRight") val topRight: CornerRadiusSerializable? = null,
    @SerializedName("bottomRight") val bottomRight: CornerRadiusSerializable? = null,
    @SerializedName("bottomLeft") val bottomLeft: CornerRadiusSerializable? = null
)


/**
 * Icon corner type
 *
 * Defined the type of the cutout for the specific corner
 */
enum class IconCornerType { FILLET, CHAMFER }

/**
 * Corner radius serializable, corner cutout for a single corner
 *
 * @property corner from 0f to 1f, float that defines how much the corner
 * @property type
 */
data class CornerRadiusSerializable(
    @SerializedName("corner") val corner: Float? = null,
    @SerializedName("type") val type: IconCornerType? = null
)
