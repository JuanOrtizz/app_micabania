package com.example.micabania

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout

class TuCuenta : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tu_cuenta)

        /*intent para el boton registrarse*/
        val botonCambiarContraseña = findViewById<Button>(R.id.botonCambiarContraseña)
        botonCambiarContraseña.setOnClickListener(){
            currentFocus?.clearFocus()
            var banderaInputContraseña = verificarInputNuevaContraseña()
            var banderaInputConfirmarContraseña = verificarInputConfirmarNuevaContraseña()

            if (banderaInputContraseña && banderaInputConfirmarContraseña){
                limpiarET()
                val contextView = findViewById<View>(R.id.vista_tu_cuenta)
                Snackbar.make(contextView, "Cambiaste tu contraseña", Snackbar.LENGTH_LONG).show()
            }
        }
    }

    // Funcion para limpiar los campos EditText al cambiar contraseña
    private fun limpiarET(){
        val etNuevaContraseña = findViewById<TextInputLayout>(R.id.etNuevaContraseña)
        val etConfirmarNuevaContraseña = findViewById<TextInputLayout>(R.id.etConfirmarNuevaContraseña)
        etNuevaContraseña.editText?.text?.clear()
        etConfirmarNuevaContraseña.editText?.text?.clear()
    }

    //Funcion para mostrar errores en los et.
    private fun errorCondicion (input: TextInputLayout, condicion:Boolean, mensaje:String){
        if (condicion){ // si es nulo o vacio
            input.boxStrokeWidth = 4 // le agrega el borde para el error
            input.isErrorEnabled = true // habilita el campo
            input.error = mensaje // agrega  el texto
        } else {
            input.error = null  // Si soluciona el error lo borra
            input.isErrorEnabled = false // y desactiva su campo
        }
    }

    // funciones para verificar inputs
    // funcion para verificar input nueva contraseña
    private fun verificarInputNuevaContraseña():Boolean{
        val inputNuevaContraseña = findViewById<TextInputLayout>(R.id.etNuevaContraseña)

        var banderaInternaNuevaContraseña = verificarTextoNuevaContraseña(inputNuevaContraseña)

        inputNuevaContraseña.editText?.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                inputNuevaContraseña.isErrorEnabled = false
            } else{
                verificarTextoNuevaContraseña(inputNuevaContraseña)
            }
        }
        return banderaInternaNuevaContraseña
    }

    // funcion para verificar input nueva contraseña
    private fun verificarInputConfirmarNuevaContraseña():Boolean{
        val inputNuevaContraseña = findViewById<TextInputLayout>(R.id.etNuevaContraseña)
        val inputConfirmarNuevaContraseña = findViewById<TextInputLayout>(R.id.etConfirmarNuevaContraseña)
        var banderaInternaConfirmarNuevaContraseña = verificarTextoConfirmarNuevaContraseña(inputNuevaContraseña, inputConfirmarNuevaContraseña)
        inputConfirmarNuevaContraseña.editText?.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                inputConfirmarNuevaContraseña.isErrorEnabled = false
            } else{
                verificarTextoConfirmarNuevaContraseña(inputNuevaContraseña, inputConfirmarNuevaContraseña)
            }
        }
        return banderaInternaConfirmarNuevaContraseña
    }

    // funciones internas de verificacion de inputs
    // funcion interna con verificaciones para nueva contraseña
    private fun verificarTextoNuevaContraseña(inputNuevaContraseña: TextInputLayout):Boolean{
        val textoNuevaContraseña = inputNuevaContraseña.editText?.text?.toString() // captura el texto del et
        if (textoNuevaContraseña.isNullOrEmpty()) {
            errorCondicion(inputNuevaContraseña, true, "Este campo no puede estar vacío")
            return false
        } else if (textoNuevaContraseña.length > 30) {
            errorCondicion(inputNuevaContraseña, true, "La contraseña no puede tener más de 30 caracteres")
            return false
        } else if (textoNuevaContraseña.length < 8) {
            errorCondicion(inputNuevaContraseña, true, "La contraseña debe tener al menos 8 caracteres")
            return false
        }else if(!textoNuevaContraseña.matches(Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#\\\$%^&*()_\\-+=\\[\\]{};':\"\\\\|,.<>\\/?]).*\$"))) {
            errorCondicion(inputNuevaContraseña, true, "La contraseña debe incluir mayúscula, minúscula, número y carácter especial.")
            return false
            // aca va a ir otro if para comprobar que no coloque la misma contraseña cuando conectemos con DB
        }else {
            errorCondicion(inputNuevaContraseña, false, "")
            return true
        }
    }

    //Funcion interna con verificaciones para confirmar la nueva contraseña
    private fun verificarTextoConfirmarNuevaContraseña(inputNuevaContraseña: TextInputLayout, inputConfirmarNuevaContraseña: TextInputLayout):Boolean{
        val textoNuevaContraseña = inputNuevaContraseña.editText?.text?.toString() // captura el texto del et
        val textoConfirmarNuevaContraseña = inputConfirmarNuevaContraseña.editText?.text?.toString() // captura el texto del et

        if(textoConfirmarNuevaContraseña.isNullOrEmpty()){
            errorCondicion(inputConfirmarNuevaContraseña, true, "Este campo no puede estar vacio")
            return false
        }else if(textoConfirmarNuevaContraseña.length > 30){
            errorCondicion(inputConfirmarNuevaContraseña, true, "La contraseña no puede tener mas de 30 caracteres" )
            return false
        }else if(!textoConfirmarNuevaContraseña.equals(textoNuevaContraseña)){
            errorCondicion(inputConfirmarNuevaContraseña, true, "Las contraseñas no coinciden" )
            return false
        }
        else {
            errorCondicion(inputConfirmarNuevaContraseña, false, "")
            return true
        }
    }
}