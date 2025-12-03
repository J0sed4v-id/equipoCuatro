package com.example.myapplication.ui.login

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import com.example.myapplication.MainActivity
import com.example.myapplication.R
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        supportActionBar?.hide()

        auth = FirebaseAuth.getInstance()

        // Si ya hay sesión iniciada, ir al Home
        if (auth.currentUser != null) {
            irAlHome()
        }

        // 1. Buscamos los controles
        val etEmail = findViewById<TextInputEditText>(R.id.email_edit_text)
        val etPassword = findViewById<TextInputEditText>(R.id.password_edit_text)
        // Necesitamos el layout de la contraseña para mostrar el error rojo (Criterio 5)
        val layoutPassword = findViewById<TextInputLayout>(R.id.password_layout)

        val btnLogin = findViewById<Button>(R.id.login_button)
        val btnRegister = findViewById<TextView>(R.id.register_button)

        // 2. Función para validar reglas y habilitar botones (Criterios 8 y 12)
        fun validarCampos() {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            // Reglas: Email no vacío y Contraseña >= 6
            val emailOk = email.isNotEmpty()
            val passOk = password.length >= 6

            val todoOk = emailOk && passOk

            // Activar o desactivar botones
            btnLogin.isEnabled = todoOk
            btnRegister.isEnabled = todoOk

            // Cambiar estilos visuales (Blanco y Bold)
            if (todoOk) {
                btnLogin.setTextColor(ContextCompat.getColor(this, android.R.color.white))
                btnLogin.typeface = Typeface.DEFAULT_BOLD

                btnRegister.setTextColor(ContextCompat.getColor(this, android.R.color.white))
                btnRegister.typeface = Typeface.DEFAULT_BOLD
            } else {
                // Estilo deshabilitado
                btnLogin.setTextColor(ContextCompat.getColor(this, android.R.color.darker_gray))
                btnLogin.typeface = Typeface.DEFAULT

                // Color gris para registrarse
                btnRegister.setTextColor(ContextCompat.getColor(this, R.color.grey))
                btnRegister.typeface = Typeface.DEFAULT
            }
        }

        // 3. Escuchamos cambios en el Email para validar al instante
        etEmail.doOnTextChanged { _, _, _, _ ->
            validarCampos()
        }

        // 4. Escuchamos cambios en el Password (Criterio 5: Error en tiempo real)
        etPassword.doOnTextChanged { text, _, _, _ ->
            if (text.toString().length < 6) {
                layoutPassword.error = "Mínimo 6 dígitos" // Muestra mensaje rojo
            } else {
                layoutPassword.error = null // Quita mensaje rojo
            }
            validarCampos()
        }

        // 5. BOTÓN LOGIN (Criterios 9 y 10)
        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        irAlHome()
                    } else {
                        // Criterio 9: Mensaje específico si falla
                        Toast.makeText(this, "Login incorrecto", Toast.LENGTH_SHORT).show()
                    }
                }
        }

        // 6. BOTÓN REGISTRARSE (Criterios 13 y 14)
        btnRegister.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        irAlHome()
                    } else {
                        // Criterio 13: Mensaje si el usuario ya existe
                        if (task.exception is FirebaseAuthUserCollisionException) {
                            Toast.makeText(this, "Error en el registro: el usuario ya existe", Toast.LENGTH_LONG).show()
                        } else {
                            Toast.makeText(this, "Error en el registro", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
        }
    }

    private fun irAlHome() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
