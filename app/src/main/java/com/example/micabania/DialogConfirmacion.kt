package com.example.micabania

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.DialogFragment

class DialogConfirmacion(
    private val onConfirmar: () -> Unit // Funcion callback para la accion confirmar
) : DialogFragment() {

    // Funcion para crear el layout (Dialog)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_confirmacion, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // captura los botones del dialog
        val btnCancelar = view.findViewById<Button>(R.id.btnCancelar)
        val btnConfirmar = view.findViewById<Button>(R.id.btnConfirmar)

        // listener del boton al cancelar
        btnCancelar.setOnClickListener {
            dismiss() // cierra el dialog
        }

        // listener del boton confirmar
        btnConfirmar.setOnClickListener {
            onConfirmar() // ejecuta la funcion callback
            dismiss() // cierra el dialog
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