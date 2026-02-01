package org.elnix.dragonlauncher.ui.remembers

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import org.elnix.dragonlauncher.common.utils.definedOrNull
import org.elnix.dragonlauncher.settings.stores.ColorSettingsStore

@Composable
fun rememberCustomColorScheme(defaultColorScheme: ColorScheme): ColorScheme {

    val ctx = LocalContext.current


    /* ───────────── PRIMARY ───────────── */

    val primary by ColorSettingsStore.primaryColor
        .flow(ctx)
        .collectAsState(initial = null)

    val onPrimary by ColorSettingsStore.onPrimaryColor
        .flow(ctx)
        .collectAsState(initial = null)

    val primaryContainer by ColorSettingsStore.primaryContainerColor
        .flow(ctx)
        .collectAsState(initial = null)

    val onPrimaryContainer by ColorSettingsStore.onPrimaryContainerColor
        .flow(ctx)
        .collectAsState(initial = null)

    val inversePrimary by ColorSettingsStore.inversePrimaryColor
        .flow(ctx)
        .collectAsState(initial = null)

    val primaryFixed by ColorSettingsStore.primaryFixedColor
        .flow(ctx)
        .collectAsState(initial = null)

    val primaryFixedDim by ColorSettingsStore.primaryFixedDimColor
        .flow(ctx)
        .collectAsState(initial = null)

    val onPrimaryFixed by ColorSettingsStore.onPrimaryFixedColor
        .flow(ctx)
        .collectAsState(initial = null)

    val onPrimaryFixedVariant by ColorSettingsStore.onPrimaryFixedVariantColor
        .flow(ctx)
        .collectAsState(initial = null)


    /* ───────────── SECONDARY ───────────── */

    val secondary by ColorSettingsStore.secondaryColor
        .flow(ctx)
        .collectAsState(initial = null)

    val onSecondary by ColorSettingsStore.onSecondaryColor
        .flow(ctx)
        .collectAsState(initial = null)

    val secondaryContainer by ColorSettingsStore.secondaryContainerColor
        .flow(ctx)
        .collectAsState(initial = null)

    val onSecondaryContainer by ColorSettingsStore.onSecondaryContainerColor
        .flow(ctx)
        .collectAsState(initial = null)

    val secondaryFixed by ColorSettingsStore.secondaryFixedColor
        .flow(ctx)
        .collectAsState(initial = null)

    val secondaryFixedDim by ColorSettingsStore.secondaryFixedDimColor
        .flow(ctx)
        .collectAsState(initial = null)

    val onSecondaryFixed by ColorSettingsStore.onSecondaryFixedColor
        .flow(ctx)
        .collectAsState(initial = null)

    val onSecondaryFixedVariant by ColorSettingsStore.onSecondaryFixedVariantColor
        .flow(ctx)
        .collectAsState(initial = null)


    /* ───────────── TERTIARY ───────────── */

    val tertiary by ColorSettingsStore.tertiaryColor
        .flow(ctx)
        .collectAsState(initial = null)

    val onTertiary by ColorSettingsStore.onTertiaryColor
        .flow(ctx)
        .collectAsState(initial = null)

    val tertiaryContainer by ColorSettingsStore.tertiaryContainerColor
        .flow(ctx)
        .collectAsState(initial = null)

    val onTertiaryContainer by ColorSettingsStore.onTertiaryContainerColor
        .flow(ctx)
        .collectAsState(initial = null)

    val tertiaryFixed by ColorSettingsStore.tertiaryFixedColor
        .flow(ctx)
        .collectAsState(initial = null)

    val tertiaryFixedDim by ColorSettingsStore.tertiaryFixedDimColor
        .flow(ctx)
        .collectAsState(initial = null)

    val onTertiaryFixed by ColorSettingsStore.onTertiaryFixedColor
        .flow(ctx)
        .collectAsState(initial = null)

    val onTertiaryFixedVariant by ColorSettingsStore.onTertiaryFixedVariantColor
        .flow(ctx)
        .collectAsState(initial = null)


    /* ───────────── BACKGROUND / SURFACE ───────────── */

    val background by ColorSettingsStore.backgroundColor
        .flow(ctx)
        .collectAsState(initial = null)

    val onBackground by ColorSettingsStore.onBackgroundColor
        .flow(ctx)
        .collectAsState(initial = null)

    val surface by ColorSettingsStore.surfaceColor
        .flow(ctx)
        .collectAsState(initial = null)

    val onSurface by ColorSettingsStore.onSurfaceColor
        .flow(ctx)
        .collectAsState(initial = null)

    val surfaceVariant by ColorSettingsStore.surfaceVariantColor
        .flow(ctx)
        .collectAsState(initial = null)

    val onSurfaceVariant by ColorSettingsStore.onSurfaceVariantColor
        .flow(ctx)
        .collectAsState(initial = null)

    val surfaceTint by ColorSettingsStore.surfaceTintColor
        .flow(ctx)
        .collectAsState(initial = null)

    val inverseSurface by ColorSettingsStore.inverseSurfaceColor
        .flow(ctx)
        .collectAsState(initial = null)

    val inverseOnSurface by ColorSettingsStore.inverseOnSurfaceColor
        .flow(ctx)
        .collectAsState(initial = null)


    /* ───────────── SURFACE CONTAINERS ───────────── */

    val surfaceBright by ColorSettingsStore.surfaceBrightColor
        .flow(ctx)
        .collectAsState(initial = null)

    val surfaceDim by ColorSettingsStore.surfaceDimColor
        .flow(ctx)
        .collectAsState(initial = null)

    val surfaceContainer by ColorSettingsStore.surfaceContainerColor
        .flow(ctx)
        .collectAsState(initial = null)

    val surfaceContainerLow by ColorSettingsStore.surfaceContainerLowColor
        .flow(ctx)
        .collectAsState(initial = null)

    val surfaceContainerLowest by ColorSettingsStore.surfaceContainerLowestColor
        .flow(ctx)
        .collectAsState(initial = null)

    val surfaceContainerHigh by ColorSettingsStore.surfaceContainerHighColor
        .flow(ctx)
        .collectAsState(initial = null)

    val surfaceContainerHighest by ColorSettingsStore.surfaceContainerHighestColor
        .flow(ctx)
        .collectAsState(initial = null)


    /* ───────────── ERROR ───────────── */

    val error by ColorSettingsStore.errorColor
        .flow(ctx)
        .collectAsState(initial = null)

    val onError by ColorSettingsStore.onErrorColor
        .flow(ctx)
        .collectAsState(initial = null)

    val errorContainer by ColorSettingsStore.errorContainerColor
        .flow(ctx)
        .collectAsState(initial = null)

    val onErrorContainer by ColorSettingsStore.onErrorContainerColor
        .flow(ctx)
        .collectAsState(initial = null)


    /* ───────────── OUTLINE / MISC ───────────── */

    val outline by ColorSettingsStore.outlineColor
        .flow(ctx)
        .collectAsState(initial = null)

    val outlineVariant by ColorSettingsStore.outlineVariantColor
        .flow(ctx)
        .collectAsState(initial = null)

    val scrim by ColorSettingsStore.scrimColor
        .flow(ctx)
        .collectAsState(initial = null)



//    val dynamicColor by ColorModesSettingsStore.dynamicColor.flow(ctx)
//        .collectAsState(ColorModesSettingsStore.dynamicColor.default)
//
//    val defaultTheme by ColorModesSettingsStore.defaultTheme.flow(ctx)
//        .collectAsState(ColorModesSettingsStore.defaultTheme.default)
//

//    val defaultColorScheme: ColorScheme = getDefaultColorScheme(defaultTheme, dynamicColor) ?: AmoledDragonColorScheme


    return remember {
        ColorScheme(
            primary                    = primary                   .definedOrNull() ?: defaultColorScheme.primary,
            onPrimary                  = onPrimary                 .definedOrNull() ?: defaultColorScheme.onPrimary,
            primaryContainer           = primaryContainer          .definedOrNull() ?: defaultColorScheme.primaryContainer,
            onPrimaryContainer         = onPrimaryContainer        .definedOrNull() ?: defaultColorScheme.onPrimaryContainer,
            inversePrimary             = inversePrimary            .definedOrNull() ?: defaultColorScheme.inversePrimary,

            secondary                  = secondary                 .definedOrNull() ?: defaultColorScheme.secondary,
            onSecondary                = onSecondary               .definedOrNull() ?: defaultColorScheme.onSecondary,
            secondaryContainer         = secondaryContainer        .definedOrNull() ?: defaultColorScheme.secondaryContainer,
            onSecondaryContainer       = onSecondaryContainer      .definedOrNull() ?: defaultColorScheme.onSecondaryContainer,

            tertiary                   = tertiary                  .definedOrNull() ?: defaultColorScheme.tertiary,
            onTertiary                 = onTertiary                .definedOrNull() ?: defaultColorScheme.onTertiary,
            tertiaryContainer          = tertiaryContainer         .definedOrNull() ?: defaultColorScheme.tertiaryContainer,
            onTertiaryContainer        = onTertiaryContainer       .definedOrNull() ?: defaultColorScheme.onTertiaryContainer,

            background                 = background                .definedOrNull() ?: defaultColorScheme.background,
            onBackground               = onBackground              .definedOrNull() ?: defaultColorScheme.onBackground,

            surface                    = surface                   .definedOrNull() ?: defaultColorScheme.surface,
            onSurface                  = onSurface                 .definedOrNull() ?: defaultColorScheme.onSurface,
            surfaceVariant             = surfaceVariant            .definedOrNull() ?: defaultColorScheme.surfaceVariant,
            onSurfaceVariant           = onSurfaceVariant          .definedOrNull() ?: defaultColorScheme.onSurfaceVariant,
            surfaceTint                = surfaceTint               .definedOrNull() ?: defaultColorScheme.surfaceTint,

            inverseSurface             = inverseSurface            .definedOrNull() ?: defaultColorScheme.inverseSurface,
            inverseOnSurface           = inverseOnSurface          .definedOrNull() ?: defaultColorScheme.inverseOnSurface,

            error                      = error                     .definedOrNull() ?: defaultColorScheme.error,
            onError                    = onError                   .definedOrNull() ?: defaultColorScheme.onError,
            errorContainer             = errorContainer            .definedOrNull() ?: defaultColorScheme.errorContainer,
            onErrorContainer           = onErrorContainer          .definedOrNull() ?: defaultColorScheme.onErrorContainer,

            outline                    = outline                   .definedOrNull() ?: defaultColorScheme.outline,
            outlineVariant             = outlineVariant            .definedOrNull() ?: defaultColorScheme.outlineVariant,
            scrim                      = scrim                     .definedOrNull() ?: defaultColorScheme.scrim,

            surfaceBright              = surfaceBright             .definedOrNull() ?: defaultColorScheme.surfaceBright,
            surfaceContainer           = surfaceContainer          .definedOrNull() ?: defaultColorScheme.surfaceContainer,
            surfaceContainerHigh       = surfaceContainerHigh      .definedOrNull() ?: defaultColorScheme.surfaceContainerHigh,
            surfaceContainerHighest    = surfaceContainerHighest   .definedOrNull() ?: defaultColorScheme.surfaceContainerHighest,
            surfaceContainerLow        = surfaceContainerLow       .definedOrNull() ?: defaultColorScheme.surfaceContainerLow,
            surfaceContainerLowest     = surfaceContainerLowest    .definedOrNull() ?: defaultColorScheme.surfaceContainerLowest,
            surfaceDim                 = surfaceDim                .definedOrNull() ?: defaultColorScheme.surfaceDim,

            primaryFixed               = primaryFixed              .definedOrNull() ?: defaultColorScheme.primaryFixed,
            primaryFixedDim            = primaryFixedDim           .definedOrNull() ?: defaultColorScheme.primaryFixedDim,
            onPrimaryFixed             = onPrimaryFixed            .definedOrNull() ?: defaultColorScheme.onPrimaryFixed,
            onPrimaryFixedVariant      = onPrimaryFixedVariant     .definedOrNull() ?: defaultColorScheme.onPrimaryFixedVariant,

            secondaryFixed             = secondaryFixed            .definedOrNull() ?: defaultColorScheme.secondaryFixed,
            secondaryFixedDim          = secondaryFixedDim         .definedOrNull() ?: defaultColorScheme.secondaryFixedDim,
            onSecondaryFixed           = onSecondaryFixed          .definedOrNull() ?: defaultColorScheme.onSecondaryFixed,
            onSecondaryFixedVariant    = onSecondaryFixedVariant   .definedOrNull() ?: defaultColorScheme.onSecondaryFixedVariant,

            tertiaryFixed              = tertiaryFixed             .definedOrNull() ?: defaultColorScheme.tertiaryFixed,
            tertiaryFixedDim           = tertiaryFixedDim          .definedOrNull() ?: defaultColorScheme.tertiaryFixedDim,
            onTertiaryFixed            = onTertiaryFixed           .definedOrNull() ?: defaultColorScheme.onTertiaryFixed,
            onTertiaryFixedVariant     = onTertiaryFixedVariant    .definedOrNull() ?: defaultColorScheme.onTertiaryFixedVariant,
        )

    }
}
