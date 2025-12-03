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
import android.widget.Toast
import com.example.myapplication.MyApplication
import com.example.myapplication.R
import com.example.myapplication.ui.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth
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

    override fun onReceive(context: Context, intent: Intent) {
        // --- COMIENZO DEL CAMBIO: CRITERIO 7 ---
        // Se maneja la acción de clic en el ícono del ojo para mostrar u ocultar el saldo.
        if (intent.action == TOGGLE_BALANCE_ACTION) {
            val appWidgetId = intent.getIntExtra(
                AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID
            )
            if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
                // Se obtiene una instancia de FirebaseAuth para verificar si el usuario está autenticado.
                val auth = FirebaseAuth.getInstance()

                // Criterio 7: Se valida que el usuario esté logueado.
                if (auth.currentUser != null) {
                    // Si el usuario está logueado, se alterna la visibilidad del saldo.
                    val prefs = context.getSharedPreferences(PREFS_NAME, 0)
                    val isVisible = prefs.getBoolean(PREF_PREFIX_KEY + appWidgetId, false)
                    prefs.edit().putBoolean(PREF_PREFIX_KEY + appWidgetId, !isVisible).apply()

                    // Se actualiza el widget para que refleje el cambio de visibilidad.
                    updateAppWidget(context, AppWidgetManager.getInstance(context), appWidgetId)
                } else {
                    // Si el usuario no está logueado, se muestra un mensaje y se le redirige a la pantalla de login.
                    Toast.makeText(context, "Inicie sesión para ver el saldo", Toast.LENGTH_SHORT).show()
                    val loginIntent = Intent(context, LoginActivity::class.java).apply {
                        // Se agregan flags para que la pantalla de login sea una nueva tarea y limpie las anteriores.
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                    context.startActivity(loginIntent)
                }
            }
        }
        // --- FIN DEL CAMBIO ---
        super.onReceive(context, intent)
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

        // Configurar las acciones de clic
        setupClickActions(context, appWidgetId, views)

        GlobalScope.launch(Dispatchers.IO) {
            try {
                // --- COMIENZO DEL CAMBIO: CORRECCIÓN DE ACCESO A DATOS ---
                // Se obtiene la instancia de la base de datos desde la clase Application
                // y se accede al DAO para obtener los productos.
                val products = application.database.productDao().getAllProducts().first()
                // --- FIN DEL CAMBIO ---
                val totalBalance = products.sumOf { it.precio * it.cantidad }
                val formattedBalance = formatBalance(totalBalance)

                // Actualizar la interfaz
                val displayBalance = if (isBalanceVisible) {
                    formattedBalance
                } else {
                    "$ * * * *"
                }

                val eyeIcon = if (isBalanceVisible) {
                    R.drawable.ic_closed_eye
                } else {
                    R.drawable.eye
                }

                // Actualizar vistas en el hilo principal
                GlobalScope.launch(Dispatchers.Main) {
                    views.setTextViewText(R.id.tvBalance, displayBalance)
                    views.setImageViewResource(R.id.ivEye, eyeIcon)

                    // Actualizar el widget
                    appWidgetManager.updateAppWidget(appWidgetId, views)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                // En caso de error, mostrar valores por defecto
                GlobalScope.launch(Dispatchers.Main) {
                    views.setTextViewText(R.id.tvBalance, "$ * * * *")
                    appWidgetManager.updateAppWidget(appWidgetId, views)
                }
            }
        }
    }

    private fun setupClickActions(
        context: Context,
        appWidgetId: Int,
        views: RemoteViews
    ) {
        // --- ACCIÓN PARA EL ÍCONO DEL OJO (Broadcast) ---
        val toggleIntent = Intent(context, InventoryAppWidget::class.java).apply {
            action = TOGGLE_BALANCE_ACTION
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            data = Uri.parse("widget://toggle/$appWidgetId")
        }

        val togglePendingIntent = PendingIntent.getBroadcast(
            context,
            appWidgetId,
            toggleIntent,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            } else {
                PendingIntent.FLAG_UPDATE_CURRENT
            }
        )
        views.setOnClickPendingIntent(R.id.ivEye, togglePendingIntent)

        // --- ACCIÓN PARA EL BOTÓN "GESTIONAR" (Activity) ---
        val loginIntent = Intent(context, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val loginRequestCode = 9999 // Código diferente al del ojo

        val loginPendingIntent = PendingIntent.getActivity(
            context,
            loginRequestCode,
            loginIntent,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            } else {
                PendingIntent.FLAG_UPDATE_CURRENT
            }
        )
        views.setOnClickPendingIntent(R.id.btnManageInventory, loginPendingIntent)
    }

    private fun formatBalance(balance: Double): String {
        return try {
            val format = NumberFormat.getCurrencyInstance(Locale("es", "CO"))
            val formatted = format.format(balance)

            // Formato personalizado para que coincida con "$ 326.000,00"
            formatted.replace("COP", "$")
                .replace("$", "$ ")
                .trim()
        } catch (e: Exception) {
            "$ 0,00"
        }
    }

    companion object {
        const val TOGGLE_BALANCE_ACTION = "com.example.myapplication.TOGGLE_BALANCE_ACTION"

        fun triggerUpdate(context: Context) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val componentName = ComponentName(context, InventoryAppWidget::class.java)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(componentName)

            val intent = Intent(context, InventoryAppWidget::class.java).apply {
                action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds)
            }
            context.sendBroadcast(intent)
        }
    }
}