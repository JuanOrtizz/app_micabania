package com.example.micabania

import android.app.Activity
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
    // Declaro variable
    private lateinit var btnCambiarContraseña:Button
    private lateinit var etNuevaContraseña:TextInputLayout
    private lateinit var etConfirmarNuevaContraseña:TextInputLayout

    //Metodo OnCreate
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tu_cuenta)

        // Inicializo la variable al cargar la activity
        btnCambiarContraseña = findViewById<Button>(R.id.botonCambiarContraseña)
        etNuevaContraseña = findViewById(R.id.etNuevaContraseña)
        etConfirmarNuevaContraseña = findViewById(R.id.etConfirmarNuevaContraseña)

        /*Listener para el boton registrarse*/
        btnCambiarContraseña.setOnClickListener(){
            currentFocus?.clearFocus()// Elimino el foco de los ET
            // Declaro banderas para verificar si los datos ingresados en los ET son validos.
            var banderaInputContraseña = verificarInputNuevaContraseña()
            var banderaInputConfirmarContraseña = verificarInputConfirmarNuevaContraseña()

            // Si las banderas son verdaderas, no hay errores en la verificaciones, ejecuta este if
            if (banderaInputContraseña && banderaInputConfirmarContraseña){
                DialogConfirmacion(
                    onConfirmar = {
                        // Va a ir logica para actualizar la contraseña en la DB
                        limpiarET() //Limpia los ET
                        mostrarSnackbar(R.id.vista_tu_cuenta, "Cambiaste tu contraseña") //Muestra el Snackbar
                    }
                ).show(supportFragmentManager, "ConfirmDialog")
            }
        }
    }

    // Funcion para limpiar los campos EditText al cambiar contraseña
    private fun limpiarET(){
        etNuevaContraseña.editText?.text?.clear()
        etConfirmarNuevaContraseña.editText?.text?.clear()
    }

    //Funcion para mostrar el snackbar
    private fun mostrarSnackbar (idVista:Int, mensaje:String){
        // obtengo el contexto donde se va a mostrar el snackbar
        val contextView = findViewById<View>(idVista)
        // Crea el snackbar
        Snackbar.make(contextView, mensaje, Snackbar.LENGTH_LONG).show()
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
    private fun verificarInputNuevaContraseña():Boolean{
        var banderaInternaNuevaContraseña = verificarTextoNuevaContraseña()
        etNuevaContraseña.editText?.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                etNuevaContraseña.isErrorEnabled = false
            } else{
                verificarTextoNuevaContraseña()
            }
        }
        return banderaInternaNuevaContraseña
    }

    //Funcion para validar Input (ET) Confirmar Nueva Contraseña
    private fun verificarInputConfirmarNuevaContraseña():Boolean{
        var banderaInternaConfirmarNuevaContraseña = verificarTextoConfirmarNuevaContraseña()
        etConfirmarNuevaContraseña.editText?.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                etConfirmarNuevaContraseña.isErrorEnabled = false
            } else{
                verificarTextoConfirmarNuevaContraseña()
            }
        }
        return banderaInternaConfirmarNuevaContraseña
    }

    // Funciones internas para validar el texto (valor) que ingresa el usuario por los Inputs (ETs)
    //Funcion para validar texto (valor) Input (ET) Nueva Contraseña
    private fun verificarTextoNuevaContraseña():Boolean{
        val textoNuevaContraseña = etNuevaContraseña.editText?.text?.toString() // captura el texto del et
        if (textoNuevaContraseña.isNullOrEmpty()) {
            errorCondicion(etNuevaContraseña, true, "Este campo no puede estar vacío")
            return false
        } else if (textoNuevaContraseña.length > 30) {
            errorCondicion(etNuevaContraseña, true, "La contraseña no puede tener más de 30 caracteres")
            return false
        } else if (textoNuevaContraseña.length < 8) {
            errorCondicion(etNuevaContraseña, true, "La contraseña debe tener al menos 8 caracteres")
            return false
        }else if(!textoNuevaContraseña.matches(Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#\\\$%^&*()_\\-+=\\[\\]{};':\"\\\\|,.<>\\/?]).*\$"))) {
            errorCondicion(etNuevaContraseña, true, "La contraseña debe incluir mayúscula, minúscula, número y carácter especial.")
            return false
            //Cuando agregue DB va a ir otro if para comprobar que no coloque la misma contraseña
        }else {
            errorCondicion(etNuevaContraseña, false, "")
            return true
        }
    }

    //Funcion para validar texto (valor) Input (ET) Nueva Contraseña
    private fun verificarTextoConfirmarNuevaContraseña():Boolean{
        val textoNuevaContraseña = etNuevaContraseña.editText?.text?.toString() // captura el texto del et
        val textoConfirmarNuevaContraseña = etConfirmarNuevaContraseña.editText?.text?.toString() // captura el texto del et

        if(textoConfirmarNuevaContraseña.isNullOrEmpty()){
            errorCondicion(etConfirmarNuevaContraseña, true, "Este campo no puede estar vacio")
            return false
        }else if(textoConfirmarNuevaContraseña.length > 30){
            errorCondicion(etConfirmarNuevaContraseña, true, "La contraseña no puede tener mas de 30 caracteres" )
            return false
        }else if(!textoConfirmarNuevaContraseña.equals(textoNuevaContraseña)){
            errorCondicion(etConfirmarNuevaContraseña, true, "Las contraseñas no coinciden" )
            return false
        }
        else {
            errorCondicion(etConfirmarNuevaContraseña, false, "")
            return true
        }
    }
}