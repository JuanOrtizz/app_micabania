package com.example.micabania

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView

class Soporte : AppCompatActivity() {
    // Declaro variables
    private lateinit var drawerLayout: DrawerLayout // drawerLayout del menu hamburguesa
    private lateinit var btnMenuHamburguesa: ImageButton // boton menu hamburguesa
    private lateinit var navView: NavigationView
    private lateinit var btnSMS:Button
    private lateinit var btnEmail:Button
    private lateinit var sharedPref: SharedPreferences
    private var idUsuarioSharedPref: Int = -1

    // Funcion OnCreate
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_soporte)

        // Inicializo las variables al cargar la activity
        drawerLayout = findViewById(R.id.drawer_layout)
        btnMenuHamburguesa = findViewById(R.id.btnMenuHamburguesa)
        navView = findViewById(R.id.nav_view)
        btnSMS = findViewById(R.id.btnContactoSms)
        btnEmail = findViewById(R.id.btnContactoMail)
        sharedPref = getSharedPreferences("MiCabaniaPrefs", Context.MODE_PRIVATE)
        idUsuarioSharedPref = sharedPref.getInt("idUsuario", -1)

        // Listener al btn menu hamburguesa
        btnMenuHamburguesa.setOnClickListener {
            if (drawerLayout.isDrawerOpen(navView)) { //Si esta abierto lo cierra (al menu)
                drawerLayout.closeDrawer(navView)
            } else {
                drawerLayout.openDrawer(navView)// Sino lo abre (al menu)
            }
        }

        //Cambio los colores del menu
        cambiarColoresElementosMenu()

        // Listeners para cada elemento del menu y realizar la navegacion
        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_cuenta -> { // Listener para el item Cuenta
                    val intent = Intent(this, TuCuenta::class.java)
                    startActivity(intent)
                    finish()
                    true
                }
                R.id.nav_propiedades -> { // Listener para el item Propiedades
                    finish()
                    true
                }
                R.id.nav_faqs -> { //Listener para el item FAQs
                    val intent = Intent(this, FAQs::class.java)
                    startActivity(intent)
                    finish()
                    true
                }
                R.id.nav_soporte -> { // Listener para el item Soporte
                    true
                }
                R.id.nav_logout -> { // Listener para el item Logout
                    // Limpio sharedPreferences al cerrar sesion
                    with(sharedPref.edit()) {
                        clear()
                        apply()
                    }
                    val intent = Intent(this, MainActivity::class.java)
                    // Para evitar que el usuario pueda volver a menu principal volviendo para atras
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                    true
                }
                else -> false
            }.also {
                drawerLayout.closeDrawers()
            }
        }

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

    private fun cambiarColoresElementosMenu(){
        navView.itemIconTintList = null // Saco el color gris de los iconos que trae por default
        val menu = navView.menu // capturo el menu
        val logoutItem = menu.findItem(R.id.nav_logout) //capturo el elemento logout (CERRAR SESION)

        // Creo un Spannable para cambiar el color del item CERRAR SESION
        val spannableTitle = SpannableString(logoutItem.title)
        val rojo = ContextCompat.getColor(this, R.color.rojo)
        spannableTitle.setSpan(
            ForegroundColorSpan(rojo),
            0,
            spannableTitle.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        // Asigno el titulo con el nuevo color a CERRAR SESION
        logoutItem.title = spannableTitle
    }
}