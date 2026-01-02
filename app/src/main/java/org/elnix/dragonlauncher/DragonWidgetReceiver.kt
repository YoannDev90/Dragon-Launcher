package org.elnix.dragonlauncher

import androidx.glance.appwidget.GlanceAppWidget

import androidx.glance.appwidget.GlanceAppWidgetReceiver

class DragonWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = DragonLauncherWidget
}
