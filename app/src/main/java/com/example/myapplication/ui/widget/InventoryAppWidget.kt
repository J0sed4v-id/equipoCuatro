package com.example.myapplication.ui.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.example.myapplication.MyApplication
import com.example.myapplication.R
import com.example.myapplication.ui.ViewModelFactory
import com.example.myapplication.ui.home.InventoryWidgetViewModel
import com.example.myapplication.ui.login.LoginActivity

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
        val application = context.applicationContext as MyApplication
        val viewModel = ViewModelFactory(application).create(InventoryWidgetViewModel::class.java)

        viewModel.inventoryBalance.observeForever {
            views.setTextViewText(R.id.tvBalance, it)
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }

        viewModel.isBalanceVisible.observeForever { isVisible ->
            val displayBalance = viewModel.getDisplayBalance()
            views.setTextViewText(R.id.tvBalance, displayBalance)
            val eyeIcon = if (isVisible) R.drawable.ic_closed_eye else R.drawable.eye
            views.setImageViewResource(R.id.ivEye, eyeIcon)
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }

        setupClickActions(views, context, appWidgetId)

        appWidgetManager.updateAppWidget(appWidgetId, views)
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

        // Acción para el botón "Gestionar Inventario" - abre la LoginActivity
        val loginIntent = Intent(context, LoginActivity::class.java)
        val loginPendingIntent = PendingIntent.getActivity(
            context,
            appWidgetId + 1000, // unique request code
            loginIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        views.setOnClickPendingIntent(R.id.btnManageInventory, loginPendingIntent)
        views.setOnClickPendingIntent(R.id.iconAdd, loginPendingIntent)
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        if (intent.action == TOGGLE_BALANCE_ACTION) {
            val appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1)
            if (appWidgetId != -1) {
                val application = context.applicationContext as MyApplication
                val viewModel = ViewModelFactory(application).create(InventoryWidgetViewModel::class.java)
                viewModel.toggleBalanceVisibility()
            }
        }
    }

    companion object {
        const val TOGGLE_BALANCE_ACTION = "TOGGLE_BALANCE_ACTION"
    }
}
