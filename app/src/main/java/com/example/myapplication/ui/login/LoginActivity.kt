package com.example.myapplication.ui.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import com.airbnb.lottie.LottieAnimationView
import com.example.myapplication.MainActivity
import com.example.myapplication.R
import java.util.concurrent.Executor

class LoginActivity : AppCompatActivity() {

    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Revisar si ya hay sesión activa
        val sharedPref = getSharedPreferences("UserSession", MODE_PRIVATE)
        val isLoggedIn = sharedPref.getBoolean("isLoggedIn", false)

        if (isLoggedIn) {
            // Si ya hay sesión guardada → saltar el login
            navigateToHome()
            return // No es necesario llamar a finish() aquí, porque navigateToHome ya lo hace
        }

        setContentView(R.layout.activity_login)
        setupBiometricAuthentication()
        setupFingerprintAnimation()

        // --- CAMBIO SUGERIDO ---
        // Muestra el diálogo de autenticación tan pronto como la actividad es visible.
        showBiometricPrompt()
    }

    private fun setupBiometricAuthentication() {
        executor = ContextCompat.getMainExecutor(this)

        biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    // No mostramos el toast si el usuario cancela manualmente
                    if (errorCode != BiometricPrompt.ERROR_NEGATIVE_BUTTON && errorCode != BiometricPrompt.ERROR_USER_CANCELED) {
                        showMessage("Error de autenticación: $errString")
                    }
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    showMessage("¡Autenticación exitosa!")

                    // ✅ Guardar sesión
                    val sharedPref = getSharedPreferences("UserSession", MODE_PRIVATE)
                    with(sharedPref.edit()) {
                        putBoolean("isLoggedIn", true)
                        apply()
                    }

                    // ✅ Ir al Home
                    navigateToHome()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    showMessage("Huella no reconocida")
                }
            })

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Autenticación con huella digital")
            .setSubtitle("Usa tu huella para acceder a la app")
            .setNegativeButtonText("Cancelar")
            .build()
    }

    private fun setupFingerprintAnimation() {
        val fingerprintAnimation = findViewById<LottieAnimationView>(R.id.fingerprint_animation)
        // Mantenemos el click por si el usuario cancela y quiere reintentar
        fingerprintAnimation.setOnClickListener {
            showBiometricPrompt()
        }
    }

    private fun showBiometricPrompt() {
        val biometricManager = androidx.biometric.BiometricManager.from(this)
        when (biometricManager.canAuthenticate(androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
            androidx.biometric.BiometricManager.BIOMETRIC_SUCCESS -> {
                biometricPrompt.authenticate(promptInfo)
            }
            else -> {
                // Si no hay biométricos, ¿qué hacer? Por ahora, mostramos mensaje.
                showMessage("La autenticación biométrica no está disponible")
                // En una app real, aquí podrías ofrecer un login con PIN o contraseña.
            }
        }
    }

    private fun navigateToHome() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish() // Evita que el usuario vuelva al login con el botón atrás
    }

    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}

