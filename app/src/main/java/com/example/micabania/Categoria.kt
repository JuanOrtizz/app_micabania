package com.example.micabania

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.View
import android.view.View.TEXT_ALIGNMENT_TEXT_START
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar

class CategoriaCocina : AppCompatActivity() {
    private lateinit var btnVolver:ImageButton
    private lateinit var imgCategoria:ImageView
    private lateinit var txtCategoria:TextView
    private lateinit var layoutElementos:LinearLayout
    private lateinit var btnElemento:Button
    private lateinit var btnAgregarElemento:Button
    private lateinit var launcherNuevoElemento: ActivityResultLauncher<Intent> // Lanza Activity y trae datos de vuelta
    //Funcion OnCreate
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_categoria_cocina)

        //Inicializo variables
        btnVolver = findViewById(R.id.botonVolver)
        imgCategoria = findViewById(R.id.imgCategoria)
        txtCategoria = findViewById(R.id.txtCategoria)
        layoutElementos = findViewById(R.id.layoutElementos)
        btnElemento = findViewById(R.id.btnElemento)
        btnAgregarElemento = findViewById(R.id.botonAgregarElemento)
        launcherNuevoElemento = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            obtenerDatosElementoNuevo(result) // llamo al metodo para obtener datos
        }

        // Cargo el nombre de la categoria y en el futuro cargará los elementos para esa categoria desde DB
        val imagenId = intent.getIntExtra("imagenId", 0)
        imgCategoria.setImageResource(imagenId)
        txtCategoria.text = intent.getStringExtra("categoria")

        /*Listener para el boton volver*/
        btnVolver.setOnClickListener(){
            finish()
        }

        // Listener para el boton del elemento
        btnElemento.setOnClickListener {
            //Obtengo el texto del boton
            val textoBoton = btnElemento.text.toString()

            // El texto al tener siempre el formato xStock NombreElemento, ponemos que los delimiter van a ser los "" y tiene como limite 2 partes de split, porque si el usuario pone elementos con espacios no siga dividiendo el string
            val partes = textoBoton.split(" ", limit = 2)

            // Obtengo el stock en numero sacandole la X
            var stockElemento = ""
            // Si las aprtes no estan vacias y el primer string empieza con el prefijo x, obtiene el stock
            if (partes.isNotEmpty() && partes[0].startsWith("x")) {
                stockElemento = partes[0].substring(1)  // Saltea la X
            }

            // Obtengo el elemento
            var nombreElemento = ""
            // Si el tamaño del array (partes) es mayor a 1, captura el nombre del elemento.
            if (partes.size > 1){
               nombreElemento = partes[1]
            }

            // Creo el dialog
            val dialog = DialogElemento(
                elemento = nombreElemento,
                stock = stockElemento,
                // Cuando agregue DB voy a empezar a actualizar el boton con su stock o eliminarlo en caso de que el stock sea 0 o se elimine ese elemento
                onSumar = {},
                onRestar = {},
                onEliminar = {}
            )
            dialog.show(supportFragmentManager, "ConfirmDialog")
        }

        /*Listener para el boton agregar elemento*/
        btnAgregarElemento.setOnClickListener(){
            val intent = Intent(this, AgregarElemento::class.java)
            launcherNuevoElemento.launch(intent) // lanzo la activity con el launcher para obtener resultados
        }

    }

    //Funcion para generar el boton y agregarlo al layout correspondiente
    private fun generarBotonElemento(stock:Int, texto:String){
        // creo el nuevo boton con su estilo
        val nuevoBoton = MaterialButton(ContextThemeWrapper(this, R.style.BotonElemento),
            null, R.style.BotonElemento).apply {
            text = "x${stock} ${texto}" // Aplico el texto del boton
        }
        layoutElementos.addView(nuevoBoton) // agrega el boton a la vista
        layoutElementos.invalidate()  // pide que la vista se redibuje
        layoutElementos.requestLayout() // pide que se recalculen tamaños en la misma
    }

    //Funcion para obtener los datos del nuevo elemento
    private fun obtenerDatosElementoNuevo(result:ActivityResult){
            // si el resultado de la activity es OK (es decir, agrego un elemento) ejecuta esto
            if (result.resultCode == Activity.RESULT_OK) {

                // obtenemos la informacion de la otra activity
                val data = result.data

                //  obtenemos los datos individuales
                val nombreElemento = data?.getStringExtra("nombreElemento")
                val stock = data?.getIntExtra("stockElemento", 0)
                val mensaje = data?.getStringExtra("mensaje_snackbar")

                // si es el stock no es 0 o nulo, el nombre y el mensaje no es nulo o vacio
                if(stock != 0 && stock != null && !nombreElemento.isNullOrEmpty() && !mensaje.isNullOrEmpty()){
                    generarBotonElemento(stock, nombreElemento) // genera el boton
                    mostrarSnackbar(R.id.vista_categoria_cocina, mensaje) // muesta el snackbar
                }
            }
        }

    //Funcion para mostrar el snackbar
    private fun mostrarSnackbar (idVista:Int, mensaje:String){
        // obtengo el contexto donde se va a mostrar el snackbar
        val contextView = findViewById<View>(idVista)
        // Crea el snackbar
        Snackbar.make(contextView, mensaje, Snackbar.LENGTH_LONG).show()
    }
}