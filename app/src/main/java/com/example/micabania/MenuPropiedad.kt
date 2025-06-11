package com.example.micabania

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity

class MenuPropiedad : AppCompatActivity() {
    // Declaro variables
    private lateinit var btnVolver:ImageButton
    private lateinit var btnCategoriaCocina: Button
    private lateinit var btnCategoriaBlanqueria: Button
    private lateinit var btnCategoriaBaño: Button
    private lateinit var btnCategoriaLimpieza: Button
    private lateinit var btnCategoriaExteriores: Button
    private lateinit var btnEliminar: Button

    // Funcion OnCreate
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_propiedad)

        // Inicializo las variables al cargar la activity
        btnVolver = findViewById(R.id.botonVolver)
        btnCategoriaCocina = findViewById(R.id.botonCategoriaCocina)
        btnCategoriaBlanqueria = findViewById(R.id.botonCategoriaBlanqueria)
        btnCategoriaBaño = findViewById(R.id.botonCategoriaBaño)
        btnCategoriaLimpieza = findViewById(R.id.botonCategoriaLimpieza)
        btnCategoriaExteriores = findViewById(R.id.botonCategoriaExteriores)
        btnEliminar = findViewById(R.id.botonEliminarPropiedad)

        /*Listener para el boton volver*/
        btnVolver.setOnClickListener{
            finish()
        }

        /*Listener para el boton categoria Cocina*/
        btnCategoriaCocina.setOnClickListener{
            cargarActivityCategoria(R.drawable.icon_cocina, "Cocina")
        }

        btnCategoriaBlanqueria.setOnClickListener{
            cargarActivityCategoria(R.drawable.icon_blanqueria, "Blanqueria")
        }

        btnCategoriaBaño.setOnClickListener{
            cargarActivityCategoria(R.drawable.icon_banio, "Baño")
        }

        btnCategoriaLimpieza.setOnClickListener{
            cargarActivityCategoria(R.drawable.icon_limpieza, "Limpieza")
        }

        btnCategoriaExteriores.setOnClickListener{
            cargarActivityCategoria(R.drawable.icon_exteriores, "Exteriores y Otros")
        }

        btnEliminar.setOnClickListener{
            DialogConfirmacion(
               onConfirmar = {
                   //Cuando conecte con la DB, lo va a eliminar de la misma.
                   val resultIntent = Intent().apply {
                       putExtra("mensaje_snackbar", "Eliminaste la propiedad") //devuelve el mensaje para el snackbar
                   }
                   //Metodo setResult para enviar los datos.
                   setResult(Activity.RESULT_OK, resultIntent)
                   finish()// finalizo esta activity asi gestiono la memoria
                }
            ).show(supportFragmentManager, "ConfirmDialog")
        }

    }

    private fun cargarActivityCategoria(imagenId:Int, textoCategoria:String){
        val intent = Intent(this, Categoria::class.java)
        intent.putExtra("imagenId", imagenId)
        intent.putExtra("categoria", textoCategoria)
        startActivity(intent)
    }

}