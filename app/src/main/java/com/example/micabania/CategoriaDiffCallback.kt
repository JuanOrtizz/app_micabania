package com.example.micabania

import androidx.recyclerview.widget.DiffUtil

class CategoriaDiffCallback(private val oldList: List<CategoriaItem>, private val newList: List<CategoriaItem>) : DiffUtil.Callback() {

    override fun getOldListSize() = oldList.size
    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldPos: Int, newPos: Int): Boolean {
        val oldItem = oldList[oldPos]
        val newItem = newList[newPos]

        // comparo los id de los elementos
        return when {
            oldItem is CategoriaItem.Elemento && newItem is CategoriaItem.Elemento ->
                oldItem.id == newItem.id
            oldItem::class == newItem::class -> true // Para los otros elementos (Titulo, Agregar y Filtros)
            else -> false
        }
    }

    override fun areContentsTheSame(oldPos: Int, newPos: Int): Boolean {
        val oldItem = oldList[oldPos]
        val newItem = newList[newPos]
        return oldItem == newItem
    }
}