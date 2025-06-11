package com.example.micabania

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Im
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout

class CreaTuCuenta : AppCompatActivity() {
    private lateinit var btnVolver:ImageButton
    private lateinit var etNombre:TextInputLayout
    private lateinit var etEmail:TextInputLayout
    private lateinit var etContraseña: TextInputLayout
    private lateinit var etConfirmarContraseña: TextInputLayout
    private lateinit var btnRegistrarse:Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crea_tu_cuenta)

        //Inicializo variables al cargar la activity
        btnVolver =  findViewById(R.id.botonVolver)
        etNombre = findViewById(R.id.etNombre)
        etEmail = findViewById(R.id.etEmail)
        etContraseña = findViewById(R.id.etContraseña)
        etConfirmarContraseña = findViewById(R.id.etConfirmarContraseña)
        btnRegistrarse = findViewById(R.id.botonRegistrarse)

        /*Listener para el boton volver*/
        btnVolver.setOnClickListener(){
            finish()
        }

        /*Listener para el boton registrarse*/
        btnRegistrarse.setOnClickListener(){
            currentFocus?.clearFocus() // Elimino el foco de los ET
            // Declaro banderas para verificar si los datos ingresados en los ET son validos.
            var banderaInputNombre = verificarInputNombre()
            var banderaInputEmail = verificarInputEmail()
            var banderaInputContraseña = verificarInputContraseña()
            var banderaInputConfirmarContraseña = verificarInputConfirmarContraseña()

            // Si las banderas son verdaderas, no hay errores en la verificaciones, ejecuta este if
            if (banderaInputNombre && banderaInputEmail && banderaInputContraseña && banderaInputConfirmarContraseña){
                // ResultIntent para devolver datos del nuevo elemento al menu principal
                val resultIntent = Intent().apply {
                    putExtra("mensaje_snackbar", "Te registraste con éxito") // Devuelve el mensaje para el snackbar
                }
                //Metodo setResult para enviar los datos.
                setResult(Activity.RESULT_OK, resultIntent)
                finish()// finalizo esta activity asi gestiono la memoria
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
        var banderaInternaNombre = verificarTextoNombre()
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
        var banderaInternaEmail = verificarTextoEmail()
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
    private fun verificarInputContraseña():Boolean{
        var banderaInternaContraseña = verificarTextoContraseña()
        etContraseña.editText?.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                etContraseña.isErrorEnabled = false
            } else{
                verificarTextoContraseña()
            }
        }
        return banderaInternaContraseña
    }

    //Funcion para validar Input (ET) confirmar contraseña
    private fun verificarInputConfirmarContraseña():Boolean{
        var banderaInternaConfirmarContraseña = verificarTextoConfirmarContraseña()
        etConfirmarContraseña.editText?.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                etConfirmarContraseña.isErrorEnabled = false
            } else{
                verificarTextoConfirmarContraseña()
            }
        }
         return banderaInternaConfirmarContraseña
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
            errorCondicion(etEmail, true, "El email no es valido")
            return false
        }else { // Cuando agregue DB va a agregar una validacion para comprobar que no exista una cuenta con ese email
            errorCondicion(etEmail, false, "")
            return true
        }
    }

    //Funcion para validar texto (valor) Input (ET) Contraseña
    private fun verificarTextoContraseña():Boolean{
        val textoContraseña = etContraseña.editText?.text?.toString() // captura el texto del et
        if (textoContraseña.isNullOrEmpty()) {
            errorCondicion(etContraseña, true, "Este campo no puede estar vacío")
            return false
        } else if (textoContraseña.length > 30) {
            errorCondicion(etContraseña, true, "La contraseña no puede tener más de 30 caracteres")
            return false
        } else if (textoContraseña.length < 8) {
            errorCondicion(etContraseña, true, "La contraseña debe tener al menos 8 caracteres")
            return false
        }else if(!textoContraseña.matches(Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#\\\$%^&*()_\\-+=\\[\\]{};':\"\\\\|,.<>\\/?]).*\$"))) {
            errorCondicion(etContraseña, true, "La contraseña debe incluir mayúscula, minúscula, número y carácter especial.")
            return false
        }else {
            errorCondicion(etContraseña, false, "")
            return true
        }
    }

    //Funcion para validar texto (valor) Input (ET) Confirmar Contraseña
    private fun verificarTextoConfirmarContraseña():Boolean{
        val textoContraseña = etContraseña.editText?.text?.toString() // captura el texto del et
        val textoConfirmarContraseña = etConfirmarContraseña.editText?.text?.toString() // captura el texto del et

        if(textoConfirmarContraseña.isNullOrEmpty()){
            errorCondicion(etConfirmarContraseña, true, "Este campo no puede estar vacio")
            return false
        }else if(textoConfirmarContraseña.length > 30){
            errorCondicion(etConfirmarContraseña, true, "La contraseña no puede tener mas de 30 caracteres" )
            return false
        }else if(!textoConfirmarContraseña.equals(textoContraseña)){
            errorCondicion(etConfirmarContraseña, true, "Las contraseñas no coinciden" )
            return false
        }
        else {
            errorCondicion(etConfirmarContraseña, false, "")
            return true
        }
    }
}