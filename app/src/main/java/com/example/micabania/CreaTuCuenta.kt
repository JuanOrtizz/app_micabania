package com.example.micabania

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout

class CreaTuCuenta : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_crea_tu_cuenta)

        /*intent para el boton volver*/
        val botonVolver = findViewById<ImageButton>(R.id.botonVolver)
        botonVolver.setOnClickListener(){
            finish()
        }

        /*intent para el boton registrarse*/
        val botonRegistrarse = findViewById<Button>(R.id.botonRegistrarse)
        botonRegistrarse.setOnClickListener(){
            currentFocus?.clearFocus()
            var banderaInputNombre = verificarInputNombre()
            var banderaInputEmail = verificarInputEmail()
            var banderaInputContraseña = verificarInputContraseña()
            var banderaInputConfirmarContraseña = verificarInputConfirmarContraseña()

            // Si las banderas son verdaderas, no hay errores en la verificaciones, ejecuta este if
            if (banderaInputNombre && banderaInputEmail && banderaInputContraseña && banderaInputConfirmarContraseña){
                val intent = Intent(this, MainActivity::class.java) // crea un intent para volver a la pantalla login
                intent.putExtra("mensaje_snackbar", "Te registraste con éxito") // el titulo del mensaje y el mensaje que devuelve
                startActivity(intent)// inicio la actitivy
                finish() // finalizo esta activity asi gestiono la memoria
            }
        }
    }

    //Funcion para mostrar errores en los et.
    private fun errorCondicion (input:TextInputLayout, condicion:Boolean, mensaje:String){
        if (condicion){ // si es nulo o vacio
            input.boxStrokeWidth = 4 // le agrega el borde para el error
            input.isErrorEnabled = true // habilita el campo
            input.error = mensaje // agrega  el texto
        } else {
            input.error = null  // Si soluciona el error lo borra
            input.isErrorEnabled = false // y desactiva su campo
        }
    }

    //Funciones para validar inputs
    private fun verificarInputNombre():Boolean{
        val inputNombre = findViewById<TextInputLayout>(R.id.etNombre)
        var banderaInternaNombre = verificarTextoNombre(inputNombre)

        inputNombre.editText?.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                inputNombre.isErrorEnabled = false
            } else{
                verificarTextoNombre(inputNombre)
            }
        }
        return banderaInternaNombre
    }

    private fun verificarInputEmail():Boolean{
        val inputEmail = findViewById<TextInputLayout>(R.id.etEmail)
        var banderaInternaEmail = verificarTextoEmail(inputEmail)
        inputEmail.editText?.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                inputEmail.isErrorEnabled = false
            } else{
                verificarTextoEmail(inputEmail)
            }
        }
        return banderaInternaEmail
    }

    private fun verificarInputContraseña():Boolean{
        val inputContraseña = findViewById<TextInputLayout>(R.id.etContraseña)

        var banderaInternaContraseña = verificarTextoContraseña(inputContraseña)

        inputContraseña.editText?.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                inputContraseña.isErrorEnabled = false
            } else{
                verificarTextoContraseña(inputContraseña)
            }
        }
        return banderaInternaContraseña
    }

    private fun verificarInputConfirmarContraseña():Boolean{
        val inputContraseña = findViewById<TextInputLayout>(R.id.etContraseña)
        val inputConfirmarContraseña = findViewById<TextInputLayout>(R.id.etConfirmarContraseña)
        var banderaInternaConfirmarContraseña = verificarTextoConfirmarContraseña(inputContraseña, inputConfirmarContraseña)
        inputConfirmarContraseña.editText?.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                inputConfirmarContraseña.isErrorEnabled = false
            } else{
                verificarTextoConfirmarContraseña(inputContraseña, inputConfirmarContraseña)
            }
        }
         return banderaInternaConfirmarContraseña
    }

    // Funciones internas para validar el valor que ingresa el usuario por los Inputs
    private fun verificarTextoNombre (inputNombre: TextInputLayout):Boolean{
        val textoNombre = inputNombre.editText?.text?.toString() // captura el texto del et

        if(textoNombre.isNullOrEmpty()){
            errorCondicion(inputNombre, true, "Este campo no puede estar vacio")
            return false
        }else if(textoNombre.length > 100){
            errorCondicion(inputNombre, true, "El nombre no puede tener mas de 100 caracteres" )
            return false
        }else if (!textoNombre.matches(Regex("^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$"))) {
            errorCondicion(inputNombre, true, "El nombre solo puede contener letras")
            return false
        }else {
            errorCondicion(inputNombre, false, "")
            return true
        }
    }

    private fun verificarTextoEmail (inputEmail: TextInputLayout):Boolean{
        val textoEmail = inputEmail.editText?.text?.toString() // captura el texto del et

        if(textoEmail.isNullOrEmpty()){
            errorCondicion(inputEmail, true, "Este campo no puede estar vacio")
            return false
        }else if(textoEmail.length > 320){
            errorCondicion(inputEmail, true, "El email no puede tener mas de 320 caracteres" )
            return false
        }else if (!textoEmail.matches(Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"))) { // en un futuro, se va a solicitar una activacion de la cuenta por mail para verificar existencia del correo
            errorCondicion(inputEmail, true, "El email no es valido")
            return false
        }else {
            errorCondicion(inputEmail, false, "")
            return true
        }
    }

    private fun verificarTextoContraseña(inputContraseña: TextInputLayout):Boolean{
        val textoContraseña = inputContraseña.editText?.text?.toString() // captura el texto del et
        if (textoContraseña.isNullOrEmpty()) {
            errorCondicion(inputContraseña, true, "Este campo no puede estar vacío")
            return false
        } else if (textoContraseña.length > 30) {
            errorCondicion(inputContraseña, true, "La contraseña no puede tener más de 30 caracteres")
            return false
        } else if (textoContraseña.length < 8) {
            errorCondicion(inputContraseña, true, "La contraseña debe tener al menos 8 caracteres")
            return false
        }else if(!textoContraseña.matches(Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#\\\$%^&*()_\\-+=\\[\\]{};':\"\\\\|,.<>\\/?]).*\$"))) {
            errorCondicion(inputContraseña, true, "La contraseña debe incluir mayúscula, minúscula, número y carácter especial.")
            return false
        }else {
            errorCondicion(inputContraseña, false, "")
            return true
        }
    }

    private fun verificarTextoConfirmarContraseña(inputContraseña: TextInputLayout, inputConfirmarContraseña: TextInputLayout):Boolean{
        val textoContraseña = inputContraseña.editText?.text?.toString() // captura el texto del et
        val textoConfirmarContraseña = inputConfirmarContraseña.editText?.text?.toString() // captura el texto del et

        if(textoConfirmarContraseña.isNullOrEmpty()){
            errorCondicion(inputConfirmarContraseña, true, "Este campo no puede estar vacio")
            return false
        }else if(textoConfirmarContraseña.length > 30){
            errorCondicion(inputConfirmarContraseña, true, "La contraseña no puede tener mas de 30 caracteres" )
            return false
        }else if(!textoConfirmarContraseña.equals(textoContraseña)){
            errorCondicion(inputConfirmarContraseña, true, "Las contraseñas no coinciden" )
            return false
        }
        else {
            errorCondicion(inputConfirmarContraseña, false, "")
            return true
        }
    }
}