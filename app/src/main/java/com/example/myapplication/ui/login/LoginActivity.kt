package com.example.myapplication.ui.login

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.myapplication.MainActivity
import com.example.myapplication.R
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

// Importa la clase FirebaseAuth para la autenticación de Firebase.
// import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var emailEditText: TextInputEditText
    private lateinit var passwordEditText: TextInputEditText
    private lateinit var passwordLayout: TextInputLayout
    private lateinit var loginButton: Button
    private lateinit var registerButton: TextView
    // Declara una instancia de FirebaseAuth.
    // private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        supportActionBar?.hide()

        // --- BLOQUE DE CÓDIGO DE FIREBASE ---
        // Descomenta la siguiente línea para inicializar Firebase Auth.
        // auth = FirebaseAuth.getInstance()

        emailEditText = findViewById(R.id.email_edit_text)
        passwordEditText = findViewById(R.id.password_edit_text)
        passwordLayout = findViewById(R.id.password_layout)
        loginButton = findViewById(R.id.login_button)
        registerButton = findViewById(R.id.register_button)

        loginButton.isEnabled = false
        registerButton.isEnabled = false

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                validateFields()
            }
        }

        emailEditText.addTextChangedListener(textWatcher)
        passwordEditText.addTextChangedListener(textWatcher)

        passwordEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                s?.let {
                    if (it.length < 6) {
                        passwordLayout.error = "Mínimo 6 dígitos"
                    } else {
                        passwordLayout.error = null
                    }
                }
            }
        })

        loginButton.setOnClickListener {
            // Navegación temporal para probar el diseño.
            navigateToHome()

            // --- BLOQUE DE CÓDIGO DE FIREBASE ---
            // Cuando estés listo para integrar Firebase, elimina la línea `navigateToHome()` de arriba
            // y descomenta el siguiente bloque de código.
            /*
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        navigateToHome()
                    } else {
                        showMessage("Login incorrecto")
                    }
                }
            */
        }

        registerButton.setOnClickListener {
            // Navegación temporal para probar el diseño.
            navigateToHome()

            // --- BLOQUE DE CÓDIGO DE FIREBASE ---
            // Cuando estés listo para integrar Firebase, elimina la línea `navigateToHome()` de arriba
            // y descomenta el siguiente bloque de código.
            /*
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        navigateToHome()
                    } else {
                        showMessage("Error en el registro")
                    }
                }
            */
        }
    }

    private fun validateFields() {
        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()
        val areFieldsFilled = email.isNotEmpty() && password.isNotEmpty()
        val isPasswordValid = password.length >= 6

        loginButton.isEnabled = areFieldsFilled && isPasswordValid
        registerButton.isEnabled = areFieldsFilled && isPasswordValid

        if (areFieldsFilled && isPasswordValid) {
            registerButton.setTextColor(ContextCompat.getColor(this, R.color.white))
            registerButton.typeface = Typeface.DEFAULT_BOLD
        } else {
            registerButton.setTextColor(ContextCompat.getColor(this, R.color.grey))
            registerButton.typeface = Typeface.DEFAULT
        }
    }

    private fun navigateToHome() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
