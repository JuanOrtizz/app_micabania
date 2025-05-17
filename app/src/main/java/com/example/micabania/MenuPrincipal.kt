package com.example.micabania

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MenuPrincipal : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_menu_principal)

        /*intent para el boton agregar propiedad*/
        val botonAgregarPropiedad = findViewById<Button>(R.id.agregar_propiedad)
        botonAgregarPropiedad.setOnClickListener(){
            val intent = Intent(this, AgregarPropiedad::class.java)
            startActivity(intent)
        }
        /*intent para el boton de la Propiedad (ingresar al menu de esta)*/
        val botonPropiedad = findViewById<Button>(R.id.propiedadEjemplo)
        botonPropiedad.setOnClickListener(){
            val intent = Intent(this, MenuPropiedad::class.java)
            startActivity(intent)
        }
    }
}