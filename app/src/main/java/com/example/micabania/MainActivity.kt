package com.example.micabania

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout

class MainActivity : AppCompatActivity() {
    // Declaro variables
    private lateinit var etEmail:TextInputLayout
    private lateinit var etContrasenia:TextInputLayout
    private lateinit var btnIngresar:Button
    private lateinit var txtCreaTuCuenta:TextView
    private lateinit var launcherRegistro: ActivityResultLauncher<Intent> // Lanza Activity y trae datos de vuelta
    private lateinit var dbHelper: AppDBHelper
    private lateinit var sharedPref: SharedPreferences

    // Funcion OnCreate
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Inicializo el sharedPreferences para verificar si un usuario esta logeado
        sharedPref = getSharedPreferences("MiCabaniaPrefs", Context.MODE_PRIVATE)

        //Obtengo si esta logeado o no
        val logeado = sharedPref.getBoolean("logeado", false)
        // Si ya estaba logeado, carga la activity MenuPrincipal
        if (logeado) {
            val intent = Intent(this, MenuPrincipal::class.java)
            startActivity(intent)
            finish()
            return
        }

        //Inicializo variables al cargar la activity
        etEmail = findViewById(R.id.etEmail)
        etContrasenia = findViewById(R.id.etContraseña)
        btnIngresar = findViewById(R.id.btnIngresar)
        txtCreaTuCuenta = findViewById(R.id.btnCrearCuenta)
        launcherRegistro =  registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            obtenerDatosRegistro(result) // llamo al metodo para obtener datos
        }
        dbHelper = DBManager.get() // Obtengo el dbHelper


        //Listener para el textView de crear cuenta
        txtCreaTuCuenta.setOnClickListener{
            val intent = Intent(this, CreaTuCuenta::class.java)
            launcherRegistro.launch(intent) // lanzo la activity con el launcher para obtener resultados
        }

        //Listener para el boton iniciar sesion
        btnIngresar.setOnClickListener{
            currentFocus?.clearFocus() // Elimino el foco de los ET
            // Declaro banderas para verificar si los datos ingresados en los ET son validos.
            val banderaInputEmail = verificarInputEmail()
            val banderaInputContrasenia = verificarInputContrasenia()
            // Si las banderas son verdaderas, no hay errores en la verificaciones, ejecuta este if
            if (banderaInputEmail && banderaInputContrasenia){
                if(iniciarSesion()){
                    val intent = Intent(this, MenuPrincipal::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        }
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
    //Funcion para validar Input (ET) Email
    private fun verificarInputEmail():Boolean{
        val banderaInternaEmail = verificarTextoEmail()
        etEmail.editText?.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                etEmail.isErrorEnabled = false
            } else{
                verificarTextoEmail()
            }
        }
        return banderaInternaEmail
    }

    //Funcion para validar Input (ET) Contraseña
    private fun verificarInputContrasenia():Boolean{
        val banderaInternaContrasenia = verificarTextoContrasenia()
        etContrasenia.editText?.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                etContrasenia.isErrorEnabled = false
            } else{
                verificarTextoContrasenia()
            }
        }
        return banderaInternaContrasenia
    }

    // Funciones internas para validar el texto (valor) que ingresa el usuario por los Inputs (ETs)
    //Funcion para validar texto (valor) Input (ET) Email
    private fun verificarTextoEmail ():Boolean{
        val textoEmail = etEmail.editText?.text?.toString() // captura el texto del et

        if(textoEmail.isNullOrEmpty()){
            errorCondicion(etEmail, true, "Este campo no puede estar vacio")
            return false
        }else if(textoEmail.length > 320){
            errorCondicion(etEmail, true, "El email no puede tener mas de 320 caracteres" )
            return false
        }else if (!textoEmail.matches(Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"))) { // en un futuro, se va a solicitar una activacion de la cuenta por mail para verificar existencia del correo
            errorCondicion(etEmail, true, "El email no es valido")
            return false
        }else {
            errorCondicion(etEmail, false, "")
            return true
        }
    }

    //Funcion para validar texto (valor) Input (ET) Contraseña
    private fun verificarTextoContrasenia():Boolean{
        val textoContrasenia = etContrasenia.editText?.text?.toString() // captura el texto del et
        if (textoContrasenia.isNullOrEmpty()) {
            errorCondicion(etContrasenia, true, "Este campo no puede estar vacío")
            return false
        } else if (textoContrasenia.length > 30) {
            errorCondicion(etContrasenia, true, "La contraseña no puede tener más de 30 caracteres")
            return false
        }else {
            errorCondicion(etContrasenia, false, "")
            return true
        }
    }

    // Funcion para iniciar sesion
    private fun iniciarSesion():Boolean{
        // capturo los valores
        val email = etEmail.editText?.text.toString().trim().lowercase()
        val contrasenia = etContrasenia.editText?.text.toString().trim()

        val resultado = dbHelper.login(email, contrasenia)
        if (resultado != null){
            val (idUsuario, nombreUsuario) = resultado // Separo ambos valores que vienen del pair
            //guardo en shared preferences
            with(sharedPref.edit()) {
                putInt("id_usuario", idUsuario)
                putBoolean("logeado", true)
                putString("nombre_usuario", nombreUsuario)
                apply()
            }
            return true
        }
        else{
            // Limpio el campo contraseña
            etContrasenia.editText?.text?.clear()
            //Muestro error
            errorCondicion(etEmail, true, "Email o contraseña invalidos. Intenta nuevamente")
            return false
        }
    }

    //Funcion para obtener los datos del registro
    private fun obtenerDatosRegistro(result: ActivityResult){
        // si el resultado de la activity es OK (es decir, se registro el usuario) ejecuta esto
        if (result.resultCode == Activity.RESULT_OK) {
            // obtenemos la informacion de la otra activity (mensaje)
            val data = result.data
            //  obtengo el dato individual (mensaje)
            val mensaje = data?.getStringExtra("mensaje_snackbar")
            // si el mensaje no es nulo o vacio ejecuta esto
            if( !mensaje.isNullOrEmpty()){
                mostrarSnackbar(R.id.vista_main, mensaje) // muesta el snackbar
            }
        }
    }
}