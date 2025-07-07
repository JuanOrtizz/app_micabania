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
    private val elemento:String, // nombre elemento
    stock:String, // stock
    private val onSumar: () -> Unit, // funcion callback para sumar stock
    private val onRestar: () -> Unit, // funcion callback para restar stock
    private val onEliminar: () -> Unit // funcion callback para eliminar el elemento
) : DialogFragment() {

    // capturo el stock numerico
    private var stockNumerico = stock.toInt()

    // Funcion para crear el layout (Dialog)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_elemento, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        //capturo los botones dle dialog
        val btnCerrar = view.findViewById<ImageButton>(R.id.btnCerrar)
        val txtElemento = view.findViewById<TextView>(R.id.txtElemento)
        val txtStockElemento = view.findViewById<TextView>(R.id.txtStockElemento)
        val btnEliminar = view.findViewById<Button>(R.id.btnEliminar)
        val btnRestar = view.findViewById<Button>(R.id.btnRestar)
        val btnSumar = view.findViewById<Button>(R.id.btnSumar)

        txtElemento.text = elemento // le asigno el texto
        txtStockElemento.text = stockNumerico.toString() // le asigno el stock

        // listener para el boton cerrar
        btnCerrar.setOnClickListener {
            dismiss() //cierra el dialog
        }

        //Listener para el boton eliminar
        btnEliminar.setOnClickListener {
            onEliminar() // ejecuta la funcion eliminar
            dismiss() // cierra el dialog
        }

        //Listener para el boton restar
        btnRestar.setOnClickListener{
            // Si stock numerico es mayor a 1 actualiza la UI con la resta
            if (stockNumerico > 1) {
                stockNumerico--
                txtStockElemento.text = stockNumerico.toString()
                onRestar()// ejecuta la funcion restar
            }
        }

        //Listener para el boton sumar
        btnSumar.setOnClickListener{
            //Si el stock numerico es menor a 999 actualiza la UI con la suma
            if(stockNumerico < 999){
                stockNumerico++
                txtStockElemento.text = stockNumerico.toString()
                onSumar() // ejecuta la funcion sumar
            }
        }
    }

    // Funcion onStart para mostrar el dialog en las proporciones justas
    override fun onStart() {
        super.onStart()
        // le pongo apariencia trasparente al fondo
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)

        // Fuerzo el ancho del dialog al 90% de la pantalla
        val width = (resources.displayMetrics.widthPixels * 0.90).toInt()
        dialog?.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
    }
}