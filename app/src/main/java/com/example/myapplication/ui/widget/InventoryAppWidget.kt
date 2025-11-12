package com.example.myapplication.ui.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.widget.RemoteViews
import com.example.myapplication.MyApplication
import com.example.myapplication.R
import com.example.myapplication.ui.login.LoginActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

class InventoryAppWidget : AppWidgetProvider() {

    private val PREFS_NAME = "com.example.myapplication.ui.widget.InventoryAppWidget"
    private val PREF_PREFIX_KEY = "balance_visible_"

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
        val prefs = context.getSharedPreferences(PREFS_NAME, 0)
        val isBalanceVisible = prefs.getBoolean(PREF_PREFIX_KEY + appWidgetId, false)

        GlobalScope.launch(Dispatchers.IO) {
            val products = application.repository.allProducts.first()
            val totalBalance = products.sumOf { it.precio * it.cantidad }
            val formattedBalance = formatBalance(totalBalance)

            val displayBalance = if (isBalanceVisible) formattedBalance else "$ ****"
            val eyeIcon = if (isBalanceVisible) R.drawable.ic_closed_eye else R.drawable.eye

            views.setTextViewText(R.id.tvBalance, displayBalance)
            views.setImageViewResource(R.id.ivEye, eyeIcon)

            setupClickActions(context, appWidgetId, views)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }

    private fun setupClickActions(
        context: Context,
        appWidgetId: Int,
        views: RemoteViews
    ) {
        val pendingIntentFlag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }

        // Acción para el ícono del ojo
        val toggleIntent = Intent(context, InventoryAppWidget::class.java).apply {
            action = TOGGLE_BALANCE_ACTION
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        }
        val togglePendingIntent = PendingIntent.getBroadcast(
            context,
            appWidgetId,
            toggleIntent,
            pendingIntentFlag
        )
        views.setOnClickPendingIntent(R.id.ivEye, togglePendingIntent)

        // Acción para el botón "Gestionar Inventario"
        val loginIntent = Intent(context, LoginActivity::class.java)
        val loginPendingIntent = PendingIntent.getActivity(
            context,
            appWidgetId + 1000, // unique request code
            loginIntent,
            pendingIntentFlag
        )
        views.setOnClickPendingIntent(R.id.btnManageInventory, loginPendingIntent)
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        if (intent.action == TOGGLE_BALANCE_ACTION) {
            val appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)
            if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
                val prefs = context.getSharedPreferences(PREFS_NAME, 0)
                val isVisible = prefs.getBoolean(PREF_PREFIX_KEY + appWidgetId, false)
                val editor = prefs.edit()
                editor.putBoolean(PREF_PREFIX_KEY + appWidgetId, !isVisible)
                editor.apply()

                updateAppWidget(context, AppWidgetManager.getInstance(context), appWidgetId)
            }
        }
    }

    private fun formatBalance(balance: Double): String {
        val format = NumberFormat.getCurrencyInstance(Locale("es", "CO"))
        return format.format(balance).replace("COP", "$")
    }

    companion object {
        const val TOGGLE_BALANCE_ACTION = "TOGGLE_BALANCE_ACTION"
    }
}
