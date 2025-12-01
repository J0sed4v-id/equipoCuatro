package com.example.myapplication.ui.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
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

    // onUpdate sigue igual, llama a updateAppWidget para cada widget.
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        // MUY IMPORTANTE: La acción de toggle se maneja aquí.
        if (intent.action == TOGGLE_BALANCE_ACTION) {
            val appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)
            if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
                val prefs = context.getSharedPreferences(PREFS_NAME, 0)
                val isVisible = prefs.getBoolean(PREF_PREFIX_KEY + appWidgetId, false)
                prefs.edit().putBoolean(PREF_PREFIX_KEY + appWidgetId, !isVisible).apply()

                // Después de cambiar la preferencia, volvemos a dibujar el widget.
                updateAppWidget(context, AppWidgetManager.getInstance(context), appWidgetId)
            }
        }
        // Pasamos el resto de los intents (como el de actualización) a la clase padre.
        super.onReceive(context, intent)
    }

    // Se ha renombrado la función para mayor claridad.
    // Esta función ahora solo actualiza la VISTA. No maneja la lógica de clics.
    private fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        val views = RemoteViews(context.packageName, R.layout.app_widget_inventory)
        val application = context.applicationContext as MyApplication
        val prefs = context.getSharedPreferences(PREFS_NAME, 0)
        val isBalanceVisible = prefs.getBoolean(PREF_PREFIX_KEY + appWidgetId, false)

        // Asignamos los PendingIntents a los botones correctos
        setupClickActions(context, appWidgetId, views)

        GlobalScope.launch(Dispatchers.IO) {
            // Se obtiene la información de la base de datos
            val products = application.repository.allProducts.first()
            val totalBalance = products.sumOf { it.precio * it.cantidad }
            val formattedBalance = formatBalance(totalBalance)

            // Se actualiza la interfaz
            val displayBalance = if (isBalanceVisible) formattedBalance else "$ ****"
            val eyeIcon = if (isBalanceVisible) R.drawable.ic_closed_eye else R.drawable.eye

            views.setTextViewText(R.id.tvBalance, displayBalance)
            views.setImageViewResource(R.id.ivEye, eyeIcon)

            // Finalmente, se le dice al manager que actualice el widget con las vistas modificadas
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }

// En InventoryAppWidget.kt

    // En InventoryAppWidget.kt

    // En InventoryAppWidget.kt

    private fun setupClickActions(
        context: Context,
        appWidgetId: Int,views: RemoteViews
    ) {
        // --- ACCIÓN PARA EL ÍCONO DEL OJO (Broadcast) ---
        // Este se queda como está. Es el más seguro.
        val toggleIntent = Intent(context, InventoryAppWidget::class.java).apply {
            action = TOGGLE_BALANCE_ACTION
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            // Usar una URI única sigue siendo crucial
            data = Uri.parse("widget://toggle/$appWidgetId")
        }

        val togglePendingIntent = PendingIntent.getBroadcast(
            context,
            appWidgetId, // request code
            toggleIntent,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) PendingIntent.FLAG_IMMUTABLE else 0
        )
        views.setOnClickPendingIntent(R.id.ivEye, togglePendingIntent)

        // --- ACCIÓN PARA EL BOTÓN "GESTIONAR" (Activity) ---
        // Aislamos completamente la creación de este PendingIntent.
        // Usamos TaskStackBuilder para crear un PendingIntent de actividad de la forma más robusta posible.
        val loginIntent = Intent(context, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        // Usamos un request code totalmente diferente y estático para esta acción.
        val loginRequestCode = 999

        val loginPendingIntent = PendingIntent.getActivity(
            context,
            loginRequestCode,
            loginIntent,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            } else {
                PendingIntent.FLAG_UPDATE_CURRENT
            }
        )
        views.setOnClickPendingIntent(R.id.btnManageInventory, loginPendingIntent)
    }




    private fun formatBalance(balance: Double): String {
        val format = NumberFormat.getCurrencyInstance(Locale("es", "CO"))
        return format.format(balance).replace("COP", "$")
    }

    companion object {
        const val TOGGLE_BALANCE_ACTION = "com.example.myapplication.TOGGLE_BALANCE_ACTION"

        fun triggerUpdate(context: Context) {
            val intent = Intent(context, InventoryAppWidget::class.java).apply {
                action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            }
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val componentName = ComponentName(context, InventoryAppWidget::class.java)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(componentName)
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds)
            context.sendBroadcast(intent)
        }
    }
}

