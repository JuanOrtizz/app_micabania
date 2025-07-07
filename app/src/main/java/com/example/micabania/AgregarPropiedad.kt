package com.example.micabania

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputLayout

class AgregarPropiedad : AppCompatActivity() {
    // Declaro variables
    private lateinit var btnVolver:ImageButton
    private lateinit var etNombre:TextInputLayout
    private lateinit var etUbicacion:TextInputLayout
    private lateinit var etCantidadHabitantes:TextInputLayout
    private lateinit var btnRegistrarPropiedad:Button
    private lateinit var dbHelper: AppDBHelper
    private var idUsuario:Int = 0

    // Funcion OnCreate
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agregar_propiedad)

        //Inicializo variables al cargar la activity
        btnVolver = findViewById(R.id.btnVolver)
        etNombre = findViewById(R.id.etNombre)
        etUbicacion = findViewById(R.id.etUbicacion)
        etCantidadHabitantes = findViewById(R.id.etCantidadHabitantes)
        btnRegistrarPropiedad = findViewById(R.id.btnRegistrarPropiedad)
        dbHelper = DBManager.get()
        //Capturo el idUsuario del intent
        idUsuario = intent.getIntExtra("id_usuario",-1)
        Log.d("DEBUG", "ID usuario recibido: $idUsuario")

        //Listener para el boton volver
        btnVolver.setOnClickListener{
            finish()
        }

        //Listener para el boton registrar propiedad
        btnRegistrarPropiedad.setOnClickListener{
            currentFocus?.clearFocus() // Elimino el foco de los ET
            // Declaro banderas para verificar si los datos ingresados en los ET son validos.
            val banderaInputNombre = verificarInputNombre()
            val banderaInputUbicacion = verificarInputUbicacion()
            val banderaInputCantidadHabitantes = verificarInputCantidadHabitantes()

            // Si las banderas son verdaderas, no hay errores en la verificaciones, ejecuta este if
            if (banderaInputNombre && banderaInputUbicacion && banderaInputCantidadHabitantes){
                if(insertarPropiedadDB()){
                    val nombrePropiedad = etNombre.editText?.text.toString().trim() // Capturo el nombre de la propiedad
                    // ResultIntent para devolver datos del nuevo elemento al menu principal
                    val resultIntent = Intent().apply {
                        putExtra("mensaje_snackbar", "Registraste  la propiedad ${nombrePropiedad}") //devuelve el mensaje para el snackbar
                    }
                    // Funcion setResult para enviar los datos.
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

    //Funcion para validar Input (ET) Ubicacion
    private fun verificarInputUbicacion():Boolean{
        val banderaInternaUbicacion = verificarTextoUbicacion()

        etUbicacion.editText?.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                etUbicacion.isErrorEnabled = false
            } else{
                verificarTextoUbicacion()
            }
        }
        return banderaInternaUbicacion
    }

    //Funcion para validar Input (ET) Cantidad de Habitantes
    private fun verificarInputCantidadHabitantes():Boolean{
        val banderaInternaCantidadHabitantes = verificarTextoCantidadHabitantes()

        etCantidadHabitantes.editText?.setOnFocusChangeListener{ _, hasFocus ->
            if(hasFocus){
                etCantidadHabitantes.isErrorEnabled = false
            }else{
                verificarTextoCantidadHabitantes()
            }
        }
        return banderaInternaCantidadHabitantes
    }

    // Funciones internas para validar el texto (valor) que ingresa el usuario por los Inputs (ETs)
    //Funcion para validar texto (valor) Input (ET) Nombre
    private fun verificarTextoNombre ():Boolean{
        val textoNombre = etNombre.editText?.text.toString().trim() // captura el texto del et
        if(textoNombre.isNullOrEmpty()){
            errorCondicion(etNombre, true, "Este campo no puede estar vacio")
            return false
        }else if(textoNombre.length > 50){
            errorCondicion(etNombre, true, "El nombre no puede tener mas de 50 caracteres" )
            return false
        }else if(dbHelper.existeNombrePropiedad(textoNombre, idUsuario)){
            errorCondicion(etNombre, true, "Ya existe una propiedad con ese nombre" )
            return false
        }else {
            errorCondicion(etNombre, false, "")
            return true
        }
    }

    //Funcion para validar texto (valor) Input (ET) Ubicacion
    private fun verificarTextoUbicacion ():Boolean{
        val textoUbicacion = etUbicacion.editText?.text.toString().trim() // captura el texto del et
        if(textoUbicacion.isNullOrEmpty()){
            errorCondicion(etUbicacion, true, "Este campo no puede estar vacio")
            return false
        }else if(textoUbicacion.length > 50){
            errorCondicion(etUbicacion, true, "El nombre no puede tener mas de 50 caracteres" )
            return false
        }else {
            errorCondicion(etUbicacion, false, "")
            return true
        }
    }

    //Funcion para validar texto (valor) Input (ET) Cantidad Habitantes
    private fun verificarTextoCantidadHabitantes():Boolean{
        val textoCantidadHabitantes = etCantidadHabitantes.editText?.text.toString().trim()
        if (!textoCantidadHabitantes.isNullOrEmpty()){
            val numeroCH = textoCantidadHabitantes.toInt()
            if (numeroCH > 99){
                errorCondicion(etCantidadHabitantes, true, "El numero maximo de habitantes es de 99" )
                return false
            } else if (numeroCH < 1){
                errorCondicion(etCantidadHabitantes, true, "El numero minimo de habitantes es 1" )
                return false
            } else{
                errorCondicion(etCantidadHabitantes, false, "")
                return true
            }
        }else{
            errorCondicion(etCantidadHabitantes, true, "Este campo no puede estar vacio")
            return false
        }
    }

    //Funcion para insertar la propiedad en la DB
    private fun insertarPropiedadDB():Boolean{
        //Obtengo los valores
        val nombre = etNombre.editText?.text.toString().trim()
        val ubicacion = etUbicacion.editText?.text.toString().trim()
        val cantidadHB = etCantidadHabitantes.editText?.text.toString().trim()
        val cantidadHBNum = cantidadHB.toInt()
        // Capturo si se realizo con exito y retorno el valor
        val ok = dbHelper.insertarPropiedad(idUsuario, nombre, ubicacion, cantidadHBNum)
        return ok
    }
}