@file:Suppress("COMPOSE_APPLIER_CALL_MISMATCH")

package org.elnix.dragonlauncher.ui.components

import android.appwidget.AppWidgetManager
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import org.elnix.dragonlauncher.common.FloatingAppObject
import org.elnix.dragonlauncher.common.serializables.SwipeActionSerializable
import org.elnix.dragonlauncher.common.utils.WidgetHostProvider
import org.elnix.dragonlauncher.common.utils.resolveShape
import org.elnix.dragonlauncher.ui.actions.ActionIcon
import org.elnix.dragonlauncher.ui.remembers.LocalIconShape
import kotlin.math.min


@Composable
fun FloatingAppsHostView(
    floatingAppObject: FloatingAppObject,
    cellSizePx: Float,
    modifier: Modifier = Modifier,
    blockTouches: Boolean = false,
    widgetHostProvider: WidgetHostProvider,
    onLaunchAction: () -> Unit
) {
    val ctx = LocalContext.current
    val iconShape = LocalIconShape.current


    if (floatingAppObject.action is SwipeActionSerializable.OpenWidget) {

        val appWidgetManager = remember {
            AppWidgetManager.getInstance(ctx)
        }

        val hostView = remember((floatingAppObject.action as SwipeActionSerializable.OpenWidget).widgetId) {
            widgetHostProvider.createAppWidgetView((floatingAppObject.action as SwipeActionSerializable.OpenWidget).widgetId)?.apply {
                widgetHostProvider.getAppWidgetInfo((floatingAppObject.action as SwipeActionSerializable.OpenWidget).widgetId)?.let { info ->
                    setAppWidget((floatingAppObject.action as SwipeActionSerializable.OpenWidget).widgetId, info)
                }
            }
        } ?: return

        AndroidView(
            modifier = modifier
                .fillMaxSize()
                .pointerInteropFilter { blockTouches },
            factory = {
                FrameLayout(it).apply {
                    addView(
                        hostView,
                        FrameLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                    )
                }
            },
            update = {
                val info = appWidgetManager.getAppWidgetInfo(floatingAppObject.id)
                if (info != null) {
                    hostView.setAppWidget(floatingAppObject.id, info)
                }
            }
        )
    } else {
        val size = min((floatingAppObject.spanX * cellSizePx), (floatingAppObject.spanY * cellSizePx)).toInt()

        ActionIcon(
            action = floatingAppObject.action,
            modifier = modifier
                .fillMaxSize()
                .clip(iconShape.resolveShape())
                .let { mod ->
                    if (blockTouches) { mod } else { mod.clickable{ onLaunchAction() } }
                },
            size = size
        )
    }
}
