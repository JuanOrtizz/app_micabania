package com.example.micabania

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton


//Clase sellada para representar los elementos del recycler view
sealed class MenuItem {
    data class Bienvenida(val nombre: String) : MenuItem()
    data class Propiedad(val id:Int, val nombre: String) : MenuItem()
    object AgregarPropiedad : MenuItem()
}

//Clase Adapter para el recyclyer view de la activity MenuPrincipal
class AdapterMenuPrincipal(
    private val items: MutableList<MenuItem>, // Elementos dentro del menu
    private val onClickPropiedad: (Int) -> Unit, //Funcion callback para manejar click en el boton de la propiedad
    private val onClickAgregar: () -> Unit // Funcion callback para manejar click en el boton agregar propiedad
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    // Lista de propiedades ocultas momentaneamente por si el usuario se arrepiente de una eliminacion y cancela con snackbar
    private val propiedadesOcultas = mutableListOf<MenuItem.Propiedad>()

    // Funcion para retornar un tipo de vista dependiendo la posicion en la que se encuentre, para permitir al adapter inflar el layout especifico
    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is MenuItem.Bienvenida -> 0
            is MenuItem.Propiedad -> 1
            is MenuItem.AgregarPropiedad -> 2
        }
    }

    // Funcion para inflar un layout dependiento el tipo de elemento (viewtype) y crea y deveulve un ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            0 -> {
                val view = inflater.inflate(R.layout.item_titulo_menu_principal, parent, false)
                BienvenidaViewHolder(view)
            }
            1 -> {
                val view = inflater.inflate(R.layout.item_btn_propiedad_menu_principal, parent, false)
                PropiedadViewHolder(view)
            }
            else -> {
                val view = inflater.inflate(R.layout.item_btn_agregar_propiedad_menu_principal, parent, false)
                AgregarPropiedadViewHolder(view)
            }
        }
    }

    // Funcion que devuelve la cantidad total de elementos a mostrar en el RecyclerView
    override fun getItemCount() = items.size

    // Funcion que asigna los datos a cada viewHolder segun el tipo de elemento, llamando al metodo Bind de cada ViewHolder para configurar el contenido de cada vista
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is MenuItem.Bienvenida -> (holder as BienvenidaViewHolder).bind(item)
            is MenuItem.Propiedad -> (holder as PropiedadViewHolder).bind(item, onClickPropiedad)
            is MenuItem.AgregarPropiedad -> (holder as AgregarPropiedadViewHolder).bind(onClickAgregar)
        }
    }

    //Funcion para actualizar el adapter
    fun updateItems(nuevosItems: List<MenuItem>) {
        items.clear()
        items.addAll(nuevosItems)
        notifyDataSetChanged()
    }

    //ViewHolder del TextView de bienvenida
    class BienvenidaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val txtBienvenida:TextView = itemView.findViewById(R.id.txtBienvenida)
        fun bind(item: MenuItem.Bienvenida) {
            txtBienvenida.text = "¡Hola ${item.nombre}!"
        }
    }

    //ViewHolder de cada btn de propiedad
    class PropiedadViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val btnPropiedad:Button = itemView.findViewById(R.id.btnPropiedad)
        fun bind(item: MenuItem.Propiedad, onClick: (Int) -> Unit) {
            btnPropiedad.text = item.nombre
            btnPropiedad.setOnClickListener {
                onClick(item.id)
            }
        }
    }

    //ViewHolder del btn para agregar una propiedad
    class AgregarPropiedadViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val btnAgregarPropiedad:MaterialButton = itemView.findViewById(R.id.btnAgregarPropiedad)
        fun bind(onClick: () -> Unit) {
            btnAgregarPropiedad.setOnClickListener {
                onClick()
            }
        }
    }

    // Funcion para ocultar un elemento del recycler view en una eliminacion
    fun ocultarPropiedad(id: Int) {
        val index = items.indexOfFirst { it is MenuItem.Propiedad && it.id == id }
        if (index != -1) {
            val propiedad = items.removeAt(index) as MenuItem.Propiedad
            propiedadesOcultas.add(propiedad)
            notifyItemRemoved(index)
        }
    }

    // Funcion para recuperar una propiedad en el recycler view si el usuario se arrepiente de una eliminacion
    fun recuperarPropiedad(id: Int) {
        val indexOculta = propiedadesOcultas.indexOfFirst { it.id == id }
        if (indexOculta != -1) {
            val propiedad = propiedadesOcultas.removeAt(indexOculta)
            items.add(items.size - 1, propiedad) // antes del botón AgregarPropiedad
            notifyItemInserted(items.size - 2)
        }
    }

}
