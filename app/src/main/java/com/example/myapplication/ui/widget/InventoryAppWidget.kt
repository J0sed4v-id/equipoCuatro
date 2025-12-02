package com.example.myapplication.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.example.myapplication.R

class InventoryAppWidget : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    private fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        val views = RemoteViews(context.packageName, R.layout.app_widget_inventory)

        // Configurar estado inicial
        setupInitialState(views, context, appWidgetId)

        // Configurar clics
        setupClickActions(views, context, appWidgetId)

        appWidgetManager.updateAppWidget(appWidgetId, views)
    }

    private fun setupInitialState(
        views: RemoteViews,
        context: Context,
        appWidgetId: Int
    ) {
        val prefs = context.getSharedPreferences("widget_prefs", Context.MODE_PRIVATE)
        val isBalanceVisible = prefs.getBoolean("balance_visible_$appWidgetId", false)

        if (isBalanceVisible) {
            views.setTextViewText(R.id.tvBalance, "$ 326.000,00")
            views.setImageViewResource(R.id.ivEye, R.drawable.ic_closed_eye)
        } else {
            views.setTextViewText(R.id.tvBalance, "$ * * * *")
            views.setImageViewResource(R.id.ivEye, R.drawable.eye)
        }
    }

    private fun setupClickActions(
        views: RemoteViews,
        context: Context,
        appWidgetId: Int
    ) {
        // Acción para el ícono del ojo
        val toggleIntent = Intent(context, InventoryAppWidget::class.java).apply {
            action = TOGGLE_BALANCE_ACTION
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        }
        val togglePendingIntent = PendingIntent.getBroadcast(
            context,
            appWidgetId,
            toggleIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        views.setOnClickPendingIntent(R.id.ivEye, togglePendingIntent)

        // Acción para el botón "Gestionar Inventario" - abre la app
        val appIntent = context.packageManager.getLaunchIntentForPackage(context.packageName)
        val appPendingIntent = PendingIntent.getActivity(
            context,
            appWidgetId + 1000,
            appIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        views.setOnClickPendingIntent(R.id.btnManageInventory, appPendingIntent)
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)

        if (intent?.action == TOGGLE_BALANCE_ACTION) {
            val appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1)
            if (appWidgetId != -1 && context != null) {
                toggleBalanceVisibility(context, AppWidgetManager.getInstance(context), appWidgetId)
            }
        }
    }

    private fun toggleBalanceVisibility(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        val views = RemoteViews(context.packageName, R.layout.app_widget_inventory)
        val prefs = context.getSharedPreferences("widget_prefs", Context.MODE_PRIVATE)
        val isBalanceVisible = !prefs.getBoolean("balance_visible_$appWidgetId", false)

        if (isBalanceVisible) {
            views.setTextViewText(R.id.tvBalance, "$ 326.000,00")
            views.setImageViewResource(R.id.ivEye, R.drawable.ic_closed_eye)
        } else {
            views.setTextViewText(R.id.tvBalance, "$ * * * *")
            views.setImageViewResource(R.id.ivEye, R.drawable.eye)
        }

        prefs.edit().putBoolean("balance_visible_$appWidgetId", isBalanceVisible).apply()
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }

    companion object {
        const val TOGGLE_BALANCE_ACTION = "TOGGLE_BALANCE_ACTION"
    }
}