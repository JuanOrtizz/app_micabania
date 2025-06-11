package com.example.micabania

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Soporte : AppCompatActivity() {
    // Declaro variables
    private lateinit var btnSMS:Button
    private lateinit var btnEmail:Button

    // Funcion OnCreate
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_soporte)

        // Inicializo las variables al cargar la activity
        btnSMS = findViewById(R.id.botonContactoSms)
        btnEmail = findViewById(R.id.botonContactoMail)

        // Listener para el boton contacto via sms
        btnSMS.setOnClickListener {
            val numero = "" // <-- numero de soporte de la app
            val mensaje = "Hola, necesito ayuda con la aplicación" // mensaje predefinido

            val intent = Intent(Intent.ACTION_SENDTO).apply { // indica que abra la app para sms y cree un mensaje para el numero de soporte con un texto predefinido
                data = Uri.parse("smsto:$numero") // numero al que manda el email
                putExtra("sms_body", mensaje) // mensaje predefinido
            }
            startActivity(intent) //inicia la app de SMS
        }

        // Listener para el boton contacto via Email
        btnEmail.setOnClickListener{
            val email = ""
            val intent = Intent(Intent.ACTION_SENDTO).apply { // indica que abra la app para emails y cree un correo para la direccion de soporte con un asunto predefinido
                data = Uri.parse("mailto:$email") // direccion de soporte
                putExtra(Intent.EXTRA_SUBJECT, "Consulta sobre la app") // asunto del mail
            }
            startActivity(intent)// inicia la app para enviar el email
        }
    }
}