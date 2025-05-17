package com.example.micabania

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MenuPropiedad : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_menu_propiedad)

        /*intent para el boton volver*/
        val botonVolver = findViewById<ImageButton>(R.id.volver)
        botonVolver.setOnClickListener(){
            val intent = Intent(this, MenuPrincipal::class.java)
            startActivity(intent)
        }

        /*intent para el boton categoria Cocina*/
        val botonCategoriaCocina = findViewById<Button>(R.id.categoriaCocina)
        botonCategoriaCocina.setOnClickListener(){
            val intent = Intent(this, CategoriaCocina::class.java)
            startActivity(intent)
        }
    }
}