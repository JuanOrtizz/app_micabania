package com.example.micabania

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputLayout

class AgregarElemento : AppCompatActivity() {
    // Declaro variables
    private lateinit var btnVolver: ImageButton
    private lateinit var etNombre: TextInputLayout
    private lateinit var etStock: TextInputLayout
    private lateinit var btnRegistrarElemento: Button


    //Funcion OnCreate
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agregar_elemento)

        // Inicializo las variables al cargar la activity
        btnVolver = findViewById(R.id.botonVolver)
        etNombre = findViewById(R.id.etNombre)
        etStock = findViewById(R.id.etStock)
        btnRegistrarElemento = findViewById(R.id.botonRegistrarElemento)


        /*Listener para el boton volver (ImageButton)*/
        btnVolver.setOnClickListener() {
            finish()
        }

        /*Listener para el boton Registrar elemento*/
        btnRegistrarElemento.setOnClickListener() {
            currentFocus?.clearFocus() // Elimino el foco de los ET
            // Declaro banderas para verificar si los datos ingresados en los ET son validos.
            var banderaInputNombre = verificarInputNombre()
            var banderaInputStock = verificarInputStock()

            // Si las banderas son verdaderas, no hay errores en la verificaciones, ejecuta este if
            if (banderaInputNombre && banderaInputStock) {
                val nombreElemento = etNombre.editText?.text.toString().trim() // Captura el nombre del elemento
                val stock = etStock.editText?.text.toString().trim()// Captura el stock del elemento
                val stockNumerico = stock.toInt() // Lo paso a Int

                // ResultIntent para devolver datos del nuevo elemento a la categoria
                val resultIntent = Intent().apply {
                    putExtra("mensaje_snackbar", "Registraste ${nombreElemento}") // Pasa el mensaje para el snackbar
                    putExtra("stockElemento", stockNumerico) // Pasa el stock para crear el elemento en esa activity
                    putExtra("nombreElemento", nombreElemento) // Pasa el nombre del elemento
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
    private fun verificarInputNombre(): Boolean {
        var banderaInternaNombre = verificarTextoNombre()

        etNombre.editText?.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                etNombre.isErrorEnabled = false
            } else {
                verificarTextoNombre()
            }
        }
        return banderaInternaNombre
    }

    //Funcion para validar Input (ET) Stock
    private fun verificarInputStock(): Boolean {
        var banderaInternaStock = verificarTextoStock()

        etStock.editText?.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                etStock.isErrorEnabled = false
            } else {
                verificarTextoStock()
            }
        }
        return banderaInternaStock
    }

    // Funciones internas para validar el texto (valor) que ingresa el usuario por los Inputs (ETs)
    //Funcion para validar texto (valor) Input (ET) Nombre
    private fun verificarTextoNombre(): Boolean {
        val textoNombre = etNombre.editText?.text.toString().trim() // captura el texto del et
        if (textoNombre.isNullOrEmpty()) {
            errorCondicion(etNombre, true, "Este campo no puede estar vacio")
            return false
        } else if (textoNombre.length > 100) {
            errorCondicion(etNombre, true, "El nombre no puede tener mas de 100 caracteres")
            return false
        } else { // va a hacer una verificacion para ver si ya existe un elemento con ese nombre para evitar duplicados
            errorCondicion(etNombre, false, "")
            return true
        }
    }

    //Funcion para validar texto (valor) Input (ET) Stock
    private fun verificarTextoStock(): Boolean {
        val textoStock = etStock.editText?.text.toString().trim()
        if (!textoStock.isNullOrEmpty()) {
            val numeroStock = textoStock.toInt()
            if (numeroStock > 999) {
                errorCondicion(etStock, true, "El numero maximo de Stock es 999")
                return false
            } else if (numeroStock < 1) {
                errorCondicion(etStock, true, "El numero minimo de stock es 1")
                return false
            } else {
                errorCondicion(etStock, false, "")
                return true
            }
        } else {
            errorCondicion(etStock, true, "Este campo no puede estar vacio")
            return false
        }
    }
}