package com.example.micabania

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.DialogFragment

class DialogElemento(
    private val elemento:String,
    stock:String,
    private val onSumar: () -> Unit,
    private val onRestar: () -> Unit,
    private val onEliminar: () -> Unit
) : DialogFragment() {

    private var stockNumerico = stock.toInt()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_elemento, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val txtElemento = view.findViewById<TextView>(R.id.txtElemento)
        val txtStockElemento = view.findViewById<TextView>(R.id.txtStockElemento)
        val btnCerrar = view.findViewById<ImageButton>(R.id.btnCerrar)
        val btnEliminar = view.findViewById<Button>(R.id.btnEliminar)
        val btnRestar = view.findViewById<Button>(R.id.btnRestar)
        val btnSumar = view.findViewById<Button>(R.id.btnSumar)

        txtElemento.text = elemento
        txtStockElemento.text = stockNumerico.toString()

        btnCerrar.setOnClickListener {
            dismiss()
        }

        btnEliminar.setOnClickListener {
            onEliminar()
            dismiss()
        }

        btnRestar.setOnClickListener{
            if (stockNumerico > 0) {
                stockNumerico--
                txtStockElemento.text = stockNumerico.toString()
                onRestar()
            }
        }

        btnSumar.setOnClickListener{
            stockNumerico++
            txtStockElemento.text = stockNumerico.toString()
            onSumar()
        }
    }

    override fun onStart() {
        super.onStart()
        // le pongo apariencia trasparente al fondo
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)

        // Fuerzo el ancho del dialog al 90% de la pantalla
        val width = (resources.displayMetrics.widthPixels * 0.90).toInt()
        dialog?.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
    }
}