package com.example.micabania

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout

class TuCuenta : AppCompatActivity() {
    // Declaro variables
    private lateinit var drawerLayout: DrawerLayout // drawerLayout del menu hamburguesa
    private lateinit var btnMenuHamburguesa: ImageButton // boton menu hamburguesa
    private lateinit var navView: NavigationView
    private lateinit var etNombre:TextInputLayout
    private lateinit var etEmail:TextInputLayout
    private lateinit var etNuevaContrasenia:TextInputLayout
    private lateinit var etConfirmarNuevaContrasenia:TextInputLayout
    private lateinit var btnCambiarContrasenia:Button
    private lateinit var dbHelper: AppDBHelper
    private lateinit var sharedPref: SharedPreferences
    private var idUsuario: Int = -1


    //Metodo OnCreate
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tu_cuenta)

        // Inicializo las variables al cargar la activity
        drawerLayout = findViewById(R.id.drawer_layout)
        btnMenuHamburguesa = findViewById(R.id.btnMenuHamburguesa)
        navView = findViewById(R.id.nav_view)
        etNombre = findViewById(R.id.etNombre)
        etEmail = findViewById(R.id.etEmail)
        etNuevaContrasenia = findViewById(R.id.etNuevaContraseña)
        etConfirmarNuevaContrasenia = findViewById(R.id.etConfirmarNuevaContraseña)
        btnCambiarContrasenia = findViewById(R.id.btnCambiarContraseña)
        dbHelper = DBManager.get()
        sharedPref = getSharedPreferences("MiCabaniaPrefs", Context.MODE_PRIVATE)
        idUsuario = sharedPref.getInt("id_usuario", -1)

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
                    val intent = Intent(this, Soporte::class.java)
                    startActivity(intent)
                    finish()
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

        // Cargo los datos del usuario en la activity
        cargarDatosUsuario()

        //Listener para el boton registrarse
        btnCambiarContrasenia.setOnClickListener{
            currentFocus?.clearFocus()// Elimino el foco de los ET
            // Declaro banderas para verificar si los datos ingresados en los ET son validos.
            val banderaInputContrasenia = verificarInputNuevaContrasenia()
            val banderaInputConfirmarContrasenia = verificarInputConfirmarNuevaContrasenia()

            // Si las banderas son verdaderas, no hay errores en la verificaciones, ejecuta este if
            if (banderaInputContrasenia && banderaInputConfirmarContrasenia){
                DialogConfirmacion(
                    onConfirmar = {
                        val nuevaContrasenia = etNuevaContrasenia.editText?.text.toString().trim()
                        if(dbHelper.actualizarContraseniaUsuario(idUsuario,nuevaContrasenia)){
                            limpiarET() //Limpia los ET
                            mostrarSnackbar(R.id.vista_tu_cuenta, "Cambiaste tu contraseña") //Muestra el Snackbar
                        }
                    }
                ).show(supportFragmentManager, "ConfirmDialog")
            }
        }
    }


    // Funcion para cambiar los colores de los items del menu
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

    // Funcion para cargar los datos del usuario en los et
    private fun cargarDatosUsuario(){
        val usuario = dbHelper.obtenerDatosUsuario(idUsuario)
        etNombre.editText?.setText(usuario.nombre)
        etEmail.editText?.setText(usuario.email)
    }

    //Funcion para mostrar el snackbar
    private fun mostrarSnackbar (idVista:Int, mensaje:String){
        // obtengo el contexto donde se va a mostrar el snackbar
        val contextView = findViewById<View>(idVista)
        // Creo el snackbar
        val snackbar = Snackbar.make(contextView, mensaje, Snackbar.LENGTH_LONG)

        // Personalizo el snackbar
        val snackbarView = snackbar.view
        val background = snackbarView.background
        background.setTint(Color.DKGRAY)  // Cambio el color de fondo
        //Capturo el texto
        val textView = snackbarView.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
        textView.setTextColor(Color.WHITE) // Color texto
        //Muestro el snackbar
        snackbar.show()
    }

    //Funcion para mostrar errores en los inputs (ETs)
    private fun errorCondicion(input: TextInputLayout, errores: Boolean, mensaje: String) {
        if (errores) { // Si es verdadero entra aca (es decir, hay errores)
            input.boxStrokeWidth = 4 // le agrega el borde para el error
            input.isErrorEnabled = true // habilita el campo
            input.error = mensaje // agrega  el texto de error
        } else { // Si es falso entra en este bloque (no hay errores)
            input.error = null  // Si soluciona el error lo borra
            input.isErrorEnabled = false // y desactiva su campo
        }
    }

    //Funciones para validar inputs (ETs)
    //Funcion para validar Input (ET) Nueva Contraseña
    private fun verificarInputNuevaContrasenia():Boolean{
        val banderaInternaNuevaContrasenia = verificarTextoNuevaContrasenia()
        etNuevaContrasenia.editText?.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                etNuevaContrasenia.isErrorEnabled = false
            } else{
                verificarTextoNuevaContrasenia()
            }
        }
        return banderaInternaNuevaContrasenia
    }

    //Funcion para validar Input (ET) Confirmar Nueva Contraseña
    private fun verificarInputConfirmarNuevaContrasenia():Boolean{
        val banderaInternaConfirmarNuevaContrasenia = verificarTextoConfirmarNuevaContrasenia()
        etConfirmarNuevaContrasenia.editText?.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                etConfirmarNuevaContrasenia.isErrorEnabled = false
            } else{
                verificarTextoConfirmarNuevaContrasenia()
            }
        }
        return banderaInternaConfirmarNuevaContrasenia
    }

    // Funciones internas para validar el texto (valor) que ingresa el usuario por los Inputs (ETs)
    //Funcion para validar texto (valor) Input (ET) Nueva Contraseña
    private fun verificarTextoNuevaContrasenia():Boolean{
        val textoNuevaContrasenia = etNuevaContrasenia.editText?.text?.toString() // captura el texto del et
        if (textoNuevaContrasenia.isNullOrEmpty()) {
            errorCondicion(etNuevaContrasenia, true, "Este campo no puede estar vacío")
            return false
        } else if (textoNuevaContrasenia.length > 30) {
            errorCondicion(etNuevaContrasenia, true, "La contraseña no puede tener más de 30 caracteres")
            return false
        } else if (textoNuevaContrasenia.length < 8) {
            errorCondicion(etNuevaContrasenia, true, "La contraseña debe tener al menos 8 caracteres")
            return false
        }else if(!textoNuevaContrasenia.matches(Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#%^&*()_\\-+=\\[\\]{};':\"\\\\|,.<>?]).*\$"))) {
            errorCondicion(etNuevaContrasenia, true, "La contraseña debe incluir mayúscula, minúscula, número y carácter especial.")
            return false
        }else if(dbHelper.verificarContrasenia(idUsuario, textoNuevaContrasenia)) {
            errorCondicion(etNuevaContrasenia, true, "La nueva contraseña no puede ser igual a la actual.")
            return false
        }else {
            errorCondicion(etNuevaContrasenia, false, "")
            return true
        }
    }

    //Funcion para validar texto (valor) Input (ET) Nueva Contraseña
    private fun verificarTextoConfirmarNuevaContrasenia():Boolean{
        val textoNuevaContrasenia = etNuevaContrasenia.editText?.text?.toString() // captura el texto del et
        val textoConfirmarNuevaContrasenia = etConfirmarNuevaContrasenia.editText?.text?.toString() // captura el texto del et

        if(textoConfirmarNuevaContrasenia.isNullOrEmpty()){
            errorCondicion(etConfirmarNuevaContrasenia, true, "Este campo no puede estar vacio")
            return false
        }else if(textoConfirmarNuevaContrasenia.length > 30){
            errorCondicion(etConfirmarNuevaContrasenia, true, "La contraseña no puede tener mas de 30 caracteres" )
            return false
        }else if(textoConfirmarNuevaContrasenia != textoNuevaContrasenia){
            errorCondicion(etConfirmarNuevaContrasenia, true, "Las contraseñas no coinciden" )
            return false
        }
        else {
            errorCondicion(etConfirmarNuevaContrasenia, false, "")
            return true
        }
    }

    // Funcion para limpiar los campos EditText al cambiar contraseña
    private fun limpiarET(){
        etNuevaContrasenia.editText?.text?.clear()
        etConfirmarNuevaContrasenia.editText?.text?.clear()
    }
}