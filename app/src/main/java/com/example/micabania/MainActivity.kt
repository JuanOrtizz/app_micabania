package com.example.micabania

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        /*intent para el textView de crear cuenta*/
        val textCreaTuCuenta = findViewById<TextView>(R.id.botonCrearCuenta)
        textCreaTuCuenta.setOnClickListener(){
            val intent = Intent(this, CreaTuCuenta::class.java)
            startActivity(intent)
        }
        /*intent para el boton iniciar sesion*/
        val botonIniciarSesion = findViewById<Button>(R.id.botonIngresar)
        botonIniciarSesion.setOnClickListener(){
            val intent = Intent(this, MenuPrincipal::class.java)
            startActivity(intent)
        }
    }
}