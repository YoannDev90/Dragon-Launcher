package org.elnix.dragonlauncher.common.serializables

import com.google.gson.annotations.SerializedName

/**
 * Describes a highly customizable icon that can be rendered from multiple sources
 * (vector, bitmap, text, or procedural shape) with advanced visual controls.
 *
 * This model is renderer-agnostic and supports extreme theming and animation use cases.
 */
data class CustomIconSerializable(

    /** Icon source type determining how `source` is interpreted. */
    @SerializedName("a")
    val type: IconType? = null,

    /**
     * Icon source reference.
     * - BITMAP: base64-encoded image
     * - ICON_PACK: base64-encoded image
     * - TEXT: emoji or glyph
     * - SHAPE: renderer-defined primitive
     */
    @SerializedName("b")
    val source: String? = null,

    /** Tint color (ARGB) applied after rendering. */
    @SerializedName("c")
    val tint: Long? = null,

    /** Icon opacity multiplier (0.0 – 1.0). */
    @SerializedName("d")
    val opacity: Float? = null,

    /** Per-corner radius override for icon clipping. */
    @SerializedName("f")
    val corners: CornerRadiusSerializable? = null,

    /** Stroke width (dp) around the icon shape. */
    @SerializedName("g")
    val strokeWidth: Float? = null,

    /** Stroke color (ARGB) around the icon. */
    @SerializedName("h")
    val strokeColor: Long? = null,

    /** Blur radius for icon shadow. */
    @SerializedName("i")
    val shadowRadius: Float? = null,

    /** Shadow color (ARGB). */
    @SerializedName("j")
    val shadowColor: Long? = null,

    /** Horizontal shadow offset (dp). */
    @SerializedName("k")
    val shadowOffsetX: Float? = null,

    /** Vertical shadow offset (dp). */
    @SerializedName("l")
    val shadowOffsetY: Float? = null,

    /** Rotation applied to the icon in degrees. */
    @SerializedName("m")
    val rotationDeg: Float? = null,

    /** Horizontal scale multiplier. */
    @SerializedName("n")
    val scaleX: Float? = null,

    /** Vertical scale multiplier. */
    @SerializedName("o")
    val scaleY: Float? = null,

    /** Optional blend mode name (renderer-defined, e.g. SRC_IN, MULTIPLY). */
    @SerializedName("p")
    val blendMode: String? = null
)

/**
 * Defines how a custom icon should be interpreted and rendered.
 */
enum class IconType {

    /** Icon sourced from an installed icon pack. */
    ICON_PACK,

    /** icon sourced from and image (PNG, JPG, WEBP). */
    BITMAP,

    /** Text-based icon (emoji, glyph, font icon). */
    TEXT,
    PLAIN_COLOR,

    /** Procedural or primitive shape rendered by code. */
    SHAPE
}


/**
 * Defines independent corner radii for a rectangular shape.
 *
 * Any null value falls back to renderer defaults or global radius.
 */
data class CornerRadiusSerializable(
    @SerializedName("a") val topLeft: Float? = null,
    @SerializedName("b") val topRight: Float? = null,
    @SerializedName("c") val bottomRight: Float? = null,
    @SerializedName("d") val bottomLeft: Float? = null
)
