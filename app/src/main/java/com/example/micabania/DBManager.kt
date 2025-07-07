package com.example.micabania
import android.content.Context

// Objeto para la DB, utiliza patron singleton para evitar varias instancias de la misma DB
object DBManager {
    private var dbHelper: AppDBHelper? = null // Inicializo en null

    // Funcion para inicializar
    fun init(context: Context) {
        if (dbHelper == null) { // Si es null
            dbHelper = AppDBHelper(context.applicationContext) // Crea esto
        }
    }

    // Funcion para obtener el objeto de la DB
    fun get(): AppDBHelper {
        //Retorna el dbHelper o una exception si no esta inicializado
        return dbHelper ?: throw IllegalStateException("DBManager no inicializado. Usa init(context) primero.")
    }
}
