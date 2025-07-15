package com.example.micabania

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar

class Categoria : AppCompatActivity() {
    // Declaro variables
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AdapterCategoria
    private lateinit var launcherNuevoElemento: ActivityResultLauncher<Intent> // Lanza Activity y trae datos de vuelta
    private lateinit var btnVolver:ImageButton
    private lateinit var dbHelper: AppDBHelper
    private var idPropiedad:Int = -1
    private var idImagen:Int = -1
    private var idCategoria:Int = -1
    private var nombreCategoria:String = ""
    private var filtroActual: Ordenamiento? = null

    //Funcion OnCreate
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_categoria)

        //Inicializo variables
        recyclerView = findViewById(R.id.recyclerViewCategoria)
        recyclerView.layoutManager = LinearLayoutManager(this)
        launcherNuevoElemento = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            obtenerDatosElementoNuevo(result) // llamo al metodo para obtener datos
        }
        btnVolver = findViewById(R.id.btnVolver)
        dbHelper = DBManager.get()
        //Obtengo los datos del intent de la activity anterior
        idPropiedad = intent.getIntExtra("id_propiedad",-1)
        idImagen = intent.getIntExtra("id_imagen",-1)
        idCategoria = intent.getIntExtra("id_categoria",-1)
        nombreCategoria = intent.getStringExtra("nombre_categoria") ?: "Desconocido"

        //Listener para el boton volver
        btnVolver.setOnClickListener{
            finish()
        }

        //Inicializo el adapter luego de capturar los datos
        adapter = inicializarAdapter()
        //Establezco el adapter al recyclerview
        recyclerView.adapter = adapter

    }

    //Funcion para obtener los datos del nuevo elemento
    private fun obtenerDatosElementoNuevo(result:ActivityResult){
            // si el resultado de la activity es OK (es decir, agrego un elemento) ejecuta esto
            if (result.resultCode == Activity.RESULT_OK) {

                // obtenemos la informacion de la otra activity
                val data = result.data

                //  obtenemos los datos individuales
                val mensaje = data?.getStringExtra("mensaje_snackbar")

                // si el mensaje no es nulo o vacio
                if(!mensaje.isNullOrEmpty()){
                    actualizarAdapterRV()//Actualiza el recyclerView
                    mostrarSnackbar(R.id.vista_categoria, mensaje) // muestra el snackbar
                }
            }
        }

    //Funcion para mostrar el snackbar (Nuevo producto)
    private fun mostrarSnackbar (idVista:Int, mensaje:String){
        // obtengo el contexto donde se va a mostrar el snackbar
        val contextView = findViewById<View>(idVista)
        // Creo el snackbar
        val snackbar = Snackbar.make(contextView, mensaje, Snackbar.LENGTH_LONG)

        // Personalizo el snackbar
        val snackbarView = snackbar.view
        val background = snackbarView.background
        background.setTint(Color.DKGRAY)  // Cambio el color de fondo
        //Capturo el texto
        val textView = snackbarView.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
        textView.setTextColor(Color.WHITE) // Color texto
        //Muestro el snackbar
        snackbar.show()
    }

    //Funcion para mostrar el snackbar con accion (Eliminar Elemento)
    private fun mostrarSnackbarConAccion (idVista:Int, mensaje:String, idElemento: Int){
        // obtengo el contexto donde se va a mostrar el snackbar
        val contextView = findViewById<View>(idVista)
        // Crea el snackbar
        val snackbar = Snackbar.make(contextView, mensaje, Snackbar.LENGTH_LONG)
            .setAction("Cancelar"){
                adapter.recuperarElemento(idElemento) // si cancela recupera el elemento
            }
            .addCallback(object: Snackbar.Callback(){
                override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                    if (event != DISMISS_EVENT_ACTION) {
                        // Si no cancela, elimino el elemento de la DB
                        dbHelper.eliminarElemento(idElemento)
                    }
                }
            })
            .setActionTextColor(Color.RED)

        // Personalizo el snackbar
        val snackbarView = snackbar.view
        val background = snackbarView.background
        background.setTint(Color.DKGRAY)  // Cambio el color de fondo
        //Capturo el texto
        val textView = snackbarView.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
        textView.setTextColor(Color.WHITE) // Color texto
        //Muestro el snackbar
        snackbar.show()
    }

    // Funcion para inicializar el adapter del recycler view
    private fun inicializarAdapter(): AdapterCategoria{
        //obtengo los elementos de la propiedad y la categoria por sus IDs
        val elementos = dbHelper.obtenerElementosCategoria(idPropiedad, idCategoria)

        //Creo una lista de CategoriaItem que esta definido en el adapter
        val items = mutableListOf<CategoriaItem>()
        // Creo una lista de los elementos filtrados, por si aplica filtros a la categoria
        var elementosFiltrados: List<Elemento>

        //Agrego en la lista los valores a mostrar
        items.add(CategoriaItem.CategoriaTitulo(idImagen, nombreCategoria)) //Mando el nombre de la categoria
        items.add(CategoriaItem.FiltrosOrdenamiento(filtroActual)) // Mando los filtros
        items.addAll(elementos.map { CategoriaItem.Elemento(it.id!!, it.stock!!, it.nombre!!) }) //Mando sus elementos
        items.add(CategoriaItem.AgregarElemento) // Mando el boton

        //Creo el adapter con los items y las funciones callback
        adapter = AdapterCategoria(
            items,
            onClickFiltroStock = {
                filtroActual = if (filtroActual == Ordenamiento.STOCK) null else Ordenamiento.STOCK
                val elementosFiltrados = if (filtroActual != null) { // Si el filtro no es null, filtra por ese filtro
                    dbHelper.ordenarElementos(idPropiedad, idCategoria, filtroActual!!)
                } else {
                    dbHelper.obtenerElementosCategoria(idPropiedad, idCategoria) //Sino los carga por id
                }
                actualizarAdapterRV(elementosFiltrados) // actualizo el adapter
            },
            onClickFiltroAlfabetico = {
                filtroActual = if (filtroActual == Ordenamiento.ALFABETICO) null else Ordenamiento.ALFABETICO
                val elementosFiltrados = if (filtroActual != null) { // Si el filtro no es null, filtra por ese filtro
                    dbHelper.ordenarElementos(idPropiedad, idCategoria, filtroActual!!)
                } else {
                    dbHelper.obtenerElementosCategoria(idPropiedad, idCategoria)//Sino los carga por id
                }
                actualizarAdapterRV(elementosFiltrados)// actualizo el adapter
            },
            onClickElemento = { idElemento -> // Listener para el click en el elemento
                val elemento = dbHelper.obtenerDatosElementoCategoria(idElemento) // obtengo los datos del elemento
                // Creo el dialog
                val dialog = DialogElemento(
                    elemento = elemento.nombre!!,
                    stock = elemento.stock.toString(),
                    onSumar = {
                        dbHelper.actualizarStockElemento(idElemento, +1) // actualiza la db
                        actualizarAdapterRV() // actualiza el adapter con nuevo stock
                    },
                    onRestar = {
                        dbHelper.actualizarStockElemento(idElemento, -1) // actualiza la db
                        actualizarAdapterRV() // actualiza el adapter con nuevo stock
                    },
                    onEliminar = {
                        adapter.ocultarElemento(idElemento) // Oculta el elemento del RV
                        mostrarSnackbarConAccion(R.id.vista_categoria, "Elemento Eliminado", idElemento)
                    }
                )
                dialog.show(supportFragmentManager, "ConfirmDialog")
            },
            onClickAgregar = { //Listener para el click en el boton agregar Elemento
                val intent = Intent(this, AgregarElemento::class.java)
                intent.putExtra("id_propiedad", idPropiedad)
                intent.putExtra("id_categoria", idCategoria)
                launcherNuevoElemento.launch(intent)
            }
        )

        //Retorno el adaptador para que el recycler view pueda mostrar los elementos
        return adapter
    }

    //Funcion para actualizar el adapter al agregar o eliminar un elemento
    private fun actualizarAdapterRV(elementosFiltrados: List<Elemento>? = null){
        var elementos:List<Elemento> // creo una lista de elementos
        if (elementosFiltrados != null){ // Si elementos filtrados no es null, muestra los elementos filtrados
            elementos = elementosFiltrados
        }else if (filtroActual != null) { // Si el filtro actual no es null, mantiene el filtro en actualizaciones de stock
            elementos = dbHelper.ordenarElementos(idPropiedad, idCategoria, filtroActual!!)
        }else{ // Sino muestra los elementos por id
            elementos = dbHelper.obtenerElementosCategoria(idPropiedad, idCategoria) //Obtiene de nuevo los elementos
        }
        val items = mutableListOf<CategoriaItem>().apply {
            add(CategoriaItem.CategoriaTitulo(idImagen, nombreCategoria)) //Pasa la imagen y el nombre de la categoria
            add(CategoriaItem.FiltrosOrdenamiento(filtroActual)) // Paso los filtros
            addAll(elementos.map { CategoriaItem.Elemento(it.id!!, it.stock!!, it.nombre!!) }) // Pasa los elementos
            add(CategoriaItem.AgregarElemento)//Pasa el boton de agregar elemento
        }
        adapter.updateItems(items) //Actualiza los items (Layout categoria, filtros, btns elementos y btn agregar elemento)
    }
}