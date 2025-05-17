package com.example.micabania

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class CreaTuCuenta : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_crea_tu_cuenta)

        /*intent para el boton volver*/
        val botonVolver = findViewById<ImageButton>(R.id.botonVolver)
        botonVolver.setOnClickListener(){
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        /*intent para el boton registrarse*/
        val botonRegistrarse = findViewById<Button>(R.id.botonRegistrarse)
        botonRegistrarse.setOnClickListener(){
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}