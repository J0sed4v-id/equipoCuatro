package com.example.myapplication

import com.example.myapplication.databinding.ActivityMainBinding

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myapplication.ui.home.HomeFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

//=======
//        if (savedInstanceState == null) {
//            supportFragmentManager.beginTransaction()
//                .replace(R.id.fragmentContainer, HomeFragment())
//                .commit()
//        }
//
//        // Mantiene el ajuste de insets del sistema
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }
//
//        // Botón para ir a la pantalla "Agregar Producto"
//        val btnAgregar = findViewById<Button>(R.id.btnAgregarProducto)
//        btnAgregar.setOnClickListener {
//            val intent = Intent(this, AgregarProductoActivity::class.java)
//            startActivity(intent)
//        }
//>>>>>>> master
    }
}
