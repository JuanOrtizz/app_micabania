package com.example.micabania

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class CategoriaCocina : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_categoria_cocina)

        /*intent para el boton volver*/
        val botonVolver = findViewById<ImageButton>(R.id.volver)
        botonVolver.setOnClickListener(){
            val intent = Intent(this, MenuPropiedad::class.java)
            startActivity(intent)
        }

        /*intent el boton agregar elemento*/
        val botonAgregarElemento = findViewById<Button>(R.id.agregarElemento)
        botonAgregarElemento.setOnClickListener(){
            val intent = Intent(this, AgregarElemento::class.java)
            startActivity(intent)
        }
    }
}