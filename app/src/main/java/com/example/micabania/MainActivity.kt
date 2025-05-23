package com.example.micabania

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout

class MainActivity : AppCompatActivity() {
    private lateinit var etEmail:TextInputLayout
    private lateinit var etContraseña:TextInputLayout
    private lateinit var botonIngresar:Button
    private lateinit var txtBtnCreaTuCuenta:TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Inicializo las variables
        etEmail = findViewById(R.id.etEmail)
        etContraseña = findViewById(R.id.etContraseña)
        botonIngresar = findViewById(R.id.botonIngresar)
        txtBtnCreaTuCuenta = findViewById(R.id.botonCrearCuenta)

        /*intent para el textView de crear cuenta*/
        txtBtnCreaTuCuenta.setOnClickListener(){
            val intent = Intent(this, CreaTuCuenta::class.java)
            startActivity(intent)
        }

        /*intent para el boton iniciar sesion*/
        botonIngresar.setOnClickListener(){
            currentFocus?.clearFocus()
            var banderaInputEmail = verificarInputEmail()
            var banderaInputContraseña = verificarInputContraseña()

            if (banderaInputEmail && banderaInputContraseña){
                val intent = Intent(this, MenuPrincipal::class.java)
                startActivity(intent)
                finish()
            }
        }

        mostrarSnackbar(R.id.vista_main)
    }

    //Funcion para mostrar Snackbar
    private fun mostrarSnackbar (id:Int){
        // Mostrar Snackbar si el usuario se registra con exito.
        val contextView = findViewById<View>(id)
        // recibe el mensaje por el intent que mandamos de activity CreaTuCuenta
        val mensaje = intent.getStringExtra("mensaje_snackbar")
        // Si el mensaje no esta vacio crea el snackbar
        if (!mensaje.isNullOrEmpty()) {
            Snackbar.make(contextView, mensaje, Snackbar.LENGTH_LONG).show()
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

    //Funcion para verificar inputs
    private fun verificarInputEmail():Boolean{
        var banderaInternaEmail = verificarTextoEmail(etEmail)
        etEmail.editText?.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                etEmail.isErrorEnabled = false
            } else{
                verificarTextoEmail(etEmail)
            }
        }
        return banderaInternaEmail
    }

    private fun verificarInputContraseña():Boolean{
        var banderaInternaContraseña = verificarTextoContraseña(etContraseña)

        etContraseña.editText?.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                etContraseña.isErrorEnabled = false
            } else{
                verificarTextoContraseña(etContraseña)
            }
        }
        return banderaInternaContraseña
    }

    //Funciones internas para verificar inputs
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
        }else { // va a ir otra condicion cuando agreguemos DB para verificar que el correo existe en la DB.
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
        }else { // aca va a ir una condicion si la contraseña coincide a la de la guardada en DB
            errorCondicion(inputContraseña, false, "")
            return true
        }
    }
}