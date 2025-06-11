package com.example.micabania

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar

class MenuPrincipal : AppCompatActivity() {
    // Declaro variables
    private lateinit var btnPropiedad: Button
    private lateinit var btnAgregarPropiedad: Button
    private lateinit var layoutPropiedades: LinearLayout
    private lateinit var launcherNuevaPropiedad: ActivityResultLauncher<Intent>// Lanza Activity y trae datos de vuelta (Solo si se registra la propiedad)
    private lateinit var launcherPropiedad: ActivityResultLauncher<Intent>// Lanza Activity y trae datos de vuelta (Solo si se elimina la propiedad)

    //Funcion OnCreate
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_principal)

        // Inicializo las variables al cargar la activity
        btnPropiedad = findViewById(R.id.botonPropiedadEjemplo)
        btnAgregarPropiedad = findViewById(R.id.botonAgregarPropiedad)
        layoutPropiedades = findViewById(R.id.layoutPropiedades)
        launcherNuevaPropiedad = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            obtenerDatosNuevaPropiedad(result) // llamo al metodo para obtener datos del registro
        }
        launcherPropiedad = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            obtenerDatosEliminarPropiedad(result) // llamo al metodo para obtener datos de la eliminacion
        }

        // Listener para el boton de la propiedad
        btnPropiedad.setOnClickListener{
            val intent = Intent(this, MenuPropiedad::class.java)
            launcherPropiedad.launch(intent)
        }

        //Listener para el boton agregar propiedad
        btnAgregarPropiedad.setOnClickListener{
            val intent = Intent(this, AgregarPropiedad::class.java)
            launcherNuevaPropiedad.launch(intent) // lanzo la activity con el launcher para obtener resultados
        }

    }

    //Funcion para mostrar el snackbar
    private fun mostrarSnackbar (idVista:Int, mensaje:String){
        // obtengo el contexto donde se va a mostrar el snackbar
        val contextView = findViewById<View>(idVista)
        // Crea el snackbar
        Snackbar.make(contextView, mensaje, Snackbar.LENGTH_LONG).show()
    }

    //Funcion para mostrar el snackbar con accion
    private fun mostrarSnackbarConAccion (idVista:Int, mensaje:String){
        // obtengo el contexto donde se va a mostrar el snackbar
        val contextView = findViewById<View>(idVista)
        // Crea el snackbar
        Snackbar.make(contextView, mensaje, Snackbar.LENGTH_LONG)
            .setAction("Cancelar"){
                // aca va a ir la logica para cancelar la operacion
            }
            .setActionTextColor(ContextCompat.getColor(this, R.color.rojo))
            .show()
    }

    //Funcion para obtener los datos de la nueva propiedad
    private fun obtenerDatosNuevaPropiedad(result: ActivityResult){
        // si el resultado de la activity es OK (es decir, agrego un elemento) ejecuta esto
        if (result.resultCode == Activity.RESULT_OK) {
            // obtenemos la informacion de la otra activity
            val data = result.data
            //  obtenemos los datos individuales
            val nombrePropiedad = data?.getStringExtra("propiedad")
            val mensaje = data?.getStringExtra("mensaje_snackbar")
            // si el nombre y el mensaje no es nulo o vacio
            if(!nombrePropiedad.isNullOrEmpty() && !mensaje.isNullOrEmpty()){
                generarBotonPropiedad(nombrePropiedad) // genera el boton
                mostrarSnackbar(R.id.vista_menu_principal, mensaje) // muestra el snackbar
            }
        }
    }

    //Funcion para obtener los datos de la nueva propiedad
    private fun obtenerDatosEliminarPropiedad(result: ActivityResult){
        // si el resultado de la activity es OK (es decir, agrego un elemento) ejecuta esto
        if (result.resultCode == Activity.RESULT_OK) {
            // obtenemos la informacion de la otra activity
            val data = result.data
            //  obtenemos los datos individuales
            val mensaje = data?.getStringExtra("mensaje_snackbar")
            // si el nombre y el mensaje no es nulo o vacio
            if(!mensaje.isNullOrEmpty()){
                mostrarSnackbarConAccion(R.id.vista_menu_principal, mensaje) // muestra el snackbar
            }
        }
    }

    private fun generarBotonPropiedad(texto:String){
        // creo el nuevo boton con su estilo
        val nuevoBoton = MaterialButton(
            ContextThemeWrapper(this, R.style.BotonPropiedad),
            null, R.style.BotonElemento).apply {
            text = "${texto}"
        }
        layoutPropiedades.addView(nuevoBoton) // agrega el boton a la vista
        layoutPropiedades.invalidate()  // pide que la vista se redibuje
        layoutPropiedades.requestLayout() // pide que se recalculen tamaños en la misma
    }
}