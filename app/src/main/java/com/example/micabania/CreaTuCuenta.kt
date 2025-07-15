package com.example.micabania

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputLayout

class CreaTuCuenta : AppCompatActivity() {
    // Declaro variables
    private lateinit var btnVolver:ImageButton
    private lateinit var etNombre:TextInputLayout
    private lateinit var etEmail:TextInputLayout
    private lateinit var etContrasenia: TextInputLayout
    private lateinit var etConfirmarContrasenia: TextInputLayout
    private lateinit var btnRegistrarse:Button
    private lateinit var dbHelper: AppDBHelper

    // Funcion on Create
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crea_tu_cuenta)

        //Inicializo variables al cargar la activity
        btnVolver =  findViewById(R.id.btnVolver)
        etNombre = findViewById(R.id.etNombre)
        etEmail = findViewById(R.id.etEmail)
        etContrasenia = findViewById(R.id.etContraseña)
        etConfirmarContrasenia = findViewById(R.id.etConfirmarContraseña)
        btnRegistrarse = findViewById(R.id.btnRegistrarse)
        dbHelper = DBManager.get()//Obtengo el objeto DBHelper

        //Listener para el boton volver
        btnVolver.setOnClickListener{
            finish()
        }

        //Listener para el boton registrarse
        btnRegistrarse.setOnClickListener{
            currentFocus?.clearFocus() // Elimino el foco de los ET
            // Declaro banderas para verificar si los datos ingresados en los ET son validos.
            val banderaInputNombre = verificarInputNombre()
            val banderaInputEmail = verificarInputEmail()
            val banderaInputContrasenia = verificarInputContrasenia()
            val banderaInputConfirmarContrasenia = verificarInputConfirmarContrasenia()

            // Si las banderas son verdaderas, no hay errores en la verificaciones, ejecuta este if
            if (banderaInputNombre && banderaInputEmail && banderaInputContrasenia && banderaInputConfirmarContrasenia){
                // Inserto en la DB
                if(insertarUsuarioDB()){
                    // ResultIntent para devolver datos del nuevo elemento al menu principal
                    val resultIntent = Intent().apply {
                        putExtra("mensaje_snackbar", "Te registraste con éxito") // Devuelve el mensaje para el snackbar
                    }
                    //funcion setResult para enviar los datos.
                    setResult(Activity.RESULT_OK, resultIntent)
                    finish()// finalizo esta activity asi gestiono la memoria
                }
            }
        }
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
    //Funcion para validar Input (ET) nombre
    private fun verificarInputNombre():Boolean{
        val banderaInternaNombre = verificarTextoNombre()
        etNombre.editText?.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                etNombre.isErrorEnabled = false
            } else{
                verificarTextoNombre()
            }
        }
        return banderaInternaNombre
    }

    //Funcion para validar Input (ET) email
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

    //Funcion para validar Input (ET) contraseña
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

    //Funcion para validar Input (ET) confirmar contraseña
    private fun verificarInputConfirmarContrasenia():Boolean{
        val banderaInternaConfirmarContrasenia = verificarTextoConfirmarContrasenia()
        etConfirmarContrasenia.editText?.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                etConfirmarContrasenia.isErrorEnabled = false
            } else{
                verificarTextoConfirmarContrasenia()
            }
        }
         return banderaInternaConfirmarContrasenia
    }

    // Funciones internas para validar el texto (valor) que ingresa el usuario por los Inputs (ETs)
    //Funcion para validar texto (valor) Input (ET) Nombre
    private fun verificarTextoNombre ():Boolean{
        val textoNombre = etNombre.editText?.text?.toString() // captura el texto del et

        if(textoNombre.isNullOrEmpty()){
            errorCondicion(etNombre, true, "Este campo no puede estar vacio")
            return false
        }else if(textoNombre.length > 100){
            errorCondicion(etNombre, true, "El nombre no puede tener mas de 100 caracteres" )
            return false
        }else if (!textoNombre.matches(Regex("^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$"))) {
            errorCondicion(etNombre, true, "El nombre solo puede contener letras")
            return false
        }else {
            errorCondicion(etNombre, false, "")
            return true
        }
    }

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
            errorCondicion(etEmail, true, "El email no es válido")
            return false
        }else if(dbHelper.existeEmail(textoEmail)){
            errorCondicion(etEmail, true, "El email ya esta en uso")
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
        } else if (textoContrasenia.length < 8) {
            errorCondicion(etContrasenia, true, "La contraseña debe tener al menos 8 caracteres")
            return false
        }else if(!textoContrasenia.matches(Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#%^&*()_\\-+=\\[\\]{};':\"\\\\|,.<>?]).*\$"))) {
            errorCondicion(etContrasenia, true, "La contraseña debe incluir mayúscula, minúscula, número y carácter especial.")
            return false
        }else {
            errorCondicion(etContrasenia, false, "")
            return true
        }
    }

    //Funcion para validar texto (valor) Input (ET) Confirmar Contraseña
    private fun verificarTextoConfirmarContrasenia():Boolean{
        val textoContrasenia = etContrasenia.editText?.text?.toString() // captura el texto del et
        val textoConfirmarContrasenia = etConfirmarContrasenia.editText?.text?.toString() // captura el texto del et

        if(textoConfirmarContrasenia.isNullOrEmpty()){
            errorCondicion(etConfirmarContrasenia, true, "Este campo no puede estar vacio")
            return false
        }else if(textoConfirmarContrasenia.length > 30){
            errorCondicion(etConfirmarContrasenia, true, "La contraseña no puede tener mas de 30 caracteres" )
            return false
        }else if(textoConfirmarContrasenia != textoContrasenia){
            errorCondicion(etConfirmarContrasenia, true, "Las contraseñas no coinciden" )
            return false
        }
        else {
            errorCondicion(etConfirmarContrasenia, false, "")
            return true
        }
    }

    //Funcion para insertar el nuevo usuario en la DB
    private fun insertarUsuarioDB(): Boolean{
        //obtengo los valores
        val nombre = etNombre.editText?.text.toString().trim()
        val email = etEmail.editText?.text.toString().trim().lowercase()
        val contrasenia = etContrasenia.editText?.text.toString().trim()

        val guardado = dbHelper.insertarUsuario(nombre, email, contrasenia)
        return guardado
    }
}