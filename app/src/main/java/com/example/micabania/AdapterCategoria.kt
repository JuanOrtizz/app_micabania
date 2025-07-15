package com.example.micabania

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.DiffUtil

//Clase sellada para representar los elementos del recycler view
sealed class CategoriaItem {
    data class CategoriaTitulo(val imgId:Int, val nombre: String) : CategoriaItem()
    data class FiltrosOrdenamiento(val filtro: Ordenamiento?) : CategoriaItem()
    data class Elemento(val id:Int, val stock: Int, val nombre: String) : CategoriaItem()
    object AgregarElemento : CategoriaItem()
}

//Clase Adapter para el recyclyer view de la activity Categoria
class AdapterCategoria(
    private val items: MutableList<CategoriaItem>, // Elementos dentro del menu
    private val onClickFiltroStock: () -> Unit, // Funcion callback para manejar el click en el filtro stock
    private val onClickFiltroAlfabetico: () -> Unit, // Funcion callback para manejar el click en el filtro alfabetico
    private val onClickElemento: (Int) -> Unit, //Funcion callback para manejar click en el boton del elemento
    private val onClickAgregar: () -> Unit // Funcion callback para manejar click en el boton agregar elemento
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    // Lista de elementos ocultos momentaneamente por si el usuario se arrepiente de una eliminacion y cancela con snackbar
    private val elementosOcultos = mutableListOf<CategoriaItem.Elemento>()
    // Filtro actual de ordenamiento
    var filtroActual: Ordenamiento? = null

    // Funcion para retornar un tipo de vista dependiendo la posicion en la que se encuentre, para permitir al adapter inflar el layout especifico
    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is CategoriaItem.CategoriaTitulo -> 0
            is CategoriaItem.FiltrosOrdenamiento -> 1
            is CategoriaItem.Elemento -> 2
            is CategoriaItem.AgregarElemento -> 3
        }
    }

    // Funcion para inflar un layout dependiento el tipo de elemento (viewtype) y crea y deveulve un ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            0 -> {
                val view = inflater.inflate(R.layout.item_titulo_categoria, parent, false)
                CategoriaTituloViewHolder(view)
            }
            1 -> {
                val view = inflater.inflate(R.layout.item_ordenar_categoria, parent, false)
                FiltrosOrdenamientoViewHolder(view)
            }
            2 -> {
                val view = inflater.inflate(R.layout.item_btn_elemento_categoria, parent, false)
                ElementoViewHolder(view)
            }
            else -> {
                val view = inflater.inflate(R.layout.item_btn_agregar_elemento_categoria, parent, false)
                AgregarElementoViewHolder(view)
            }
        }
    }

    // Funcion que devuelve la cantidad total de elementos a mostrar en el RecyclerView
    override fun getItemCount() = items.size

    // Funcion que asigna los datos a cada viewHolder segun el tipo de elemento, llamando al metodo Bind de cada ViewHolder para configurar el contenido de cada vista
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is CategoriaItem.CategoriaTitulo -> (holder as CategoriaTituloViewHolder).bind(item)
            is CategoriaItem.FiltrosOrdenamiento -> (holder as FiltrosOrdenamientoViewHolder).bind(onClickFiltroStock, onClickFiltroAlfabetico, item.filtro)
            is CategoriaItem.Elemento -> (holder as ElementoViewHolder).bind(item, onClickElemento)
            is CategoriaItem.AgregarElemento -> (holder as AgregarElementoViewHolder).bind(onClickAgregar)
        }
    }

    //Funcion para actualizar el adapter
    fun updateItems(nuevosItems: List<CategoriaItem>) {
        // constantes para las animaciones
        val diffCallback = CategoriaDiffCallback(items, nuevosItems)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        // actualizo el adapter
        items.clear()
        items.addAll(nuevosItems)
        diffResult.dispatchUpdatesTo(this)
    }

    //ViewHolder del titulo de la categoria
    class CategoriaTituloViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imgCategoria: ImageView = itemView.findViewById(R.id.imgCategoria)
        private val txtCategoria: TextView = itemView.findViewById(R.id.txtCategoria)
        fun bind(item: CategoriaItem.CategoriaTitulo) {
            imgCategoria.setImageResource(item.imgId)
            txtCategoria.text = item.nombre
        }
    }

    //ViewHolder de los filtros
    class FiltrosOrdenamientoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val btnFiltroStock:Button = itemView.findViewById(R.id.btnOrdenarStock)
        private val btnFiltroAlfabetico:Button  = itemView.findViewById(R.id.btnOrdenarAlfabetico)
        fun bind(onClickStock: () -> Unit, onClickAlfabetico: () -> Unit, filtroActual: Ordenamiento?) {
            //capturo los colores
            val colorOriginal = ContextCompat.getColor(itemView.context, R.color.celeste)
            val colorSeleccionado = ContextCompat.getColor(itemView.context, R.color.azul)

            // establezco colores en los filtros si estan activos o no
            btnFiltroStock.backgroundTintList = ColorStateList.valueOf(
                if (filtroActual == Ordenamiento.STOCK) colorSeleccionado else colorOriginal
            )
            btnFiltroAlfabetico.backgroundTintList = ColorStateList.valueOf(
                if (filtroActual == Ordenamiento.ALFABETICO) colorSeleccionado else colorOriginal
            )

            //Listener onclicks en los botones
            btnFiltroStock.setOnClickListener { onClickStock() }
            btnFiltroAlfabetico.setOnClickListener { onClickAlfabetico() }
        }
    }

    //ViewHolder de cada btn de elemento
    class ElementoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val btnElemento:Button = itemView.findViewById(R.id.btnElemento)
        fun bind(item: CategoriaItem.Elemento, onClick: (Int) -> Unit) {
            btnElemento.text = "x${item.stock} ${item.nombre}"
            btnElemento.setOnClickListener {
                onClick(item.id)
            }
        }
    }

    //ViewHolder del btn para agregar un elemento
    class AgregarElementoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val btnRegistrarElemento:Button = itemView.findViewById(R.id.btnAgregarElemento)
        fun bind(onClick: () -> Unit) {
            btnRegistrarElemento.setOnClickListener {
                onClick()
            }
        }
    }

    // Funcion para ocultar un elemento del recycler view en una eliminacion
    fun ocultarElemento(id: Int) {
        val index = items.indexOfFirst { it is CategoriaItem.Elemento && it.id == id }
        if (index != -1) {
            val elemento = items.removeAt(index) as CategoriaItem.Elemento
            elementosOcultos.add(elemento)
            notifyItemRemoved(index)
        }
    }

    // Funcion para recuperar un elemento en el recycler view si el usuario se arrepiente de una eliminacion
    fun recuperarElemento(id: Int) {
        val indexOculta = elementosOcultos.indexOfFirst { it.id == id }
        if (indexOculta != -1) {
            val elemento = elementosOcultos.removeAt(indexOculta)

            // Busco donde esta el btn "AgregarElemento" para insertarlo justo antes al elemento
            val indexAgregarElemento = items.indexOfFirst { it is CategoriaItem.AgregarElemento }
            val insertIndex = if (indexAgregarElemento != -1) indexAgregarElemento else items.size

            items.add(insertIndex, elemento)
            notifyItemInserted(insertIndex)
        }
    }
}
