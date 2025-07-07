package com.example.micabania
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteOpenHelper
import android.database.sqlite.SQLiteDatabase
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import android.util.Base64
import javax.crypto.spec.PBEKeySpec

// Clase para el objeto Usuario
data class Usuario(
    val nombre: String? = null,
    val email: String? = null
)

// Clase para el objeto Propiedad
data class Propiedad(
    val id: Int? = null,
    val nombre: String? =null,
    val ubicacion: String? = null,
    val cantidadHabitantes: Int? = null
)

//Clase para el objeto Elemento
data class Elemento(
    val id: Int? = null,
    val stock: Int? = null,
    val nombre: String? = null
)

data class CategoriaElemento(val id: Int, val nombre: String)

//Clase para el DBHelper de SQLiteOpenHelper
class AppDBHelper(context: Context): SQLiteOpenHelper(context, "MiCabaniaDB", null, 1) {

    //Funcion On Create
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("PRAGMA foreign_keys=ON;") // Activo las claves foraneas para las relacioens

        //Creo tabla usuarios
        db.execSQL(
            """
           CREATE TABLE usuarios (
               id INTEGER PRIMARY KEY AUTOINCREMENT,
               nombre TEXT,
               email TEXT UNIQUE,
               contrasenia TEXT
           )""".trimIndent()
        )

        //Creo tabla categorias elementos
        db.execSQL(
            """
           CREATE TABLE categorias_elementos(
               id INTEGER PRIMARY KEY AUTOINCREMENT,
               nombre TEXT
           )""".trimIndent()
        )

        //Creo la tabla propiedades
        db.execSQL(
            """
           CREATE TABLE propiedades(
               id INTEGER PRIMARY KEY AUTOINCREMENT,
               id_usuario INTEGER, 
               nombre TEXT,
               ubicacion TEXT,
               cantidad_habitantes INTEGER,
               FOREIGN KEY (id_usuario) REFERENCES usuarios(id)
           )""".trimIndent()
        )

        //Creo la tabla elementos
        db.execSQL(
            """
           CREATE TABLE elementos(
               id INTEGER PRIMARY KEY AUTOINCREMENT,
               id_propiedad INTEGER,
               id_categoria INTEGER,
               nombre TEXT,
               stock INT,
               FOREIGN KEY (id_propiedad) REFERENCES propiedades(id),
               FOREIGN KEY (id_categoria) REFERENCES categorias_elementos(id)
           )""".trimIndent()
        )

        //Inserto las categorias en la DB
        db.execSQL("INSERT INTO categorias_elementos (nombre) VALUES ('Cocina')")
        db.execSQL("INSERT INTO categorias_elementos (nombre) VALUES ('Blanqueria')")
        db.execSQL("INSERT INTO categorias_elementos (nombre) VALUES ('Baño')")
        db.execSQL("INSERT INTO categorias_elementos (nombre) VALUES ('Limpieza')")
        db.execSQL("INSERT INTO categorias_elementos (nombre) VALUES ('Exteriores y Otros')")

    }

    //Funcion OnUpgrade
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {

    }

    //FUNCIONES USUARIO
    // Funcion para insertar/registrar un nuevo usuario
    fun insertarUsuario(nombre: String, email: String, contrasenia: String): Boolean {
        val db = writableDatabase

        // Hasheo contraseña
        val salt = generateSalt()
        val hashedContrasenia = hashContrasenia(contrasenia, salt)

        val valores = ContentValues().apply {
            put("nombre", nombre)
            put("email", email)
            put("contrasenia", hashedContrasenia) // Guardamos el hash
        }

        val resultado = db.insert("usuarios", null, valores)

        return resultado != -1L
    }

    // Funcion para verificar que no exista un usuario con el mismo email en la db
    fun existeEmail(email: String): Boolean {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM usuarios WHERE email = ? LIMIT 1",
            arrayOf(email)
        )
        val existe = cursor.moveToFirst()
        cursor.close()
        return existe
    }

    // Funcion para login que devuelve un par (id y nombre)
    fun login(email: String, contrasenia: String): Pair<Int, String>? {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM usuarios WHERE email = ?",
            arrayOf(email)
        )

        // Si encuentra un usuario ejecuta este if
        if (cursor.moveToFirst()) {
            val idUsuario = cursor.getInt(0)
            val nombreUsuario = cursor.getString(1)
            val contraseniaGuardada = cursor.getString(3) // Obtengo el salt y el hash

            // Separo el salt y el hash
            val parts = contraseniaGuardada.split(":")
            // Si se dividio bien ejecuta este bloque
            if (parts.size == 2) {
                val saltGuardado = Base64.decode(parts[0], Base64.NO_WRAP)

                // Hasheo la contraseña ingresada con el salt almacenado
                val contraseniaIngresadaHasheada = hashContrasenia(contrasenia, saltGuardado)

                // Comparo el hash de la contraseña ingresada con el almacenado
                if (contraseniaIngresadaHasheada == contraseniaGuardada) {
                    cursor.close()
                    return Pair(idUsuario, nombreUsuario)
                }
            }
        }
        // si no encuentra nada, cierra el cursor y retorna false
        cursor.close()
        return null
    }

    // Funcion para obtener datos del usuario
    fun obtenerDatosUsuario(idUsuario: Int): Usuario{
        var usuario = Usuario()
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT nombre, email FROM usuarios WHERE id = ?",
            arrayOf(idUsuario.toString())
        )
        if (cursor.moveToFirst()) {
            do {
                val nombre = cursor.getString(0)
                val email = cursor.getString(1)
                usuario = Usuario(
                    nombre = nombre,
                    email = email,
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        return usuario
    }

    // Funcion para realizar un Update de la contraseña del usuario
    fun actualizarContraseniaUsuario(idUsuario: Int, nuevaContrasenia:String): Boolean{
        val db = writableDatabase

        // Hasheo contraseña
        val salt = generateSalt()
        val hashedContrasenia = hashContrasenia(nuevaContrasenia, salt)

        //valores a actualizar en este caso contrasenia
        val valores = ContentValues().apply {
            put("contrasenia", hashedContrasenia)
        }

        //Actualizo
        db.update("usuarios", valores, "id = ?", arrayOf(idUsuario.toString()))
        return true
    }

    // Funcion para verificar que el usuario no coloque una contraseña nueva igual a la actual
    fun verificarContrasenia(idUsuario: Int, nuevaContrasenia: String): Boolean {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT contrasenia FROM usuarios WHERE id = ?", arrayOf(idUsuario.toString()))
        var mismaContrasenia = false

        if (cursor.moveToFirst()) {
            val contraseniaActual = cursor.getString(0)
            val partes = contraseniaActual.split(":")
            val salt = Base64.decode(partes[0], Base64.NO_WRAP)
            val hashContraseniaActual = partes[1]
            val hashNuevaContrasenia = hashContrasenia(nuevaContrasenia, salt).split(":")[1]

            // verifica si la contraseña es la misma
            if(hashNuevaContrasenia == hashContraseniaActual) {
                mismaContrasenia = true
            }
            else{
                mismaContrasenia = false
            }
        }
        cursor.close()

        return mismaContrasenia
    }

    // FUNCIONES PROPIEDAD
    // Funcion para insertar una nueva propiedad
    fun insertarPropiedad(idUsuario: Int, nombre: String, ubicacion: String, cantidadHabitantes: Int): Boolean {
        val db = writableDatabase
        val valores = ContentValues().apply {
            put("id_usuario", idUsuario)
            put("nombre", nombre)
            put("ubicacion", ubicacion)
            put("cantidad_habitantes", cantidadHabitantes)
        }
        val resultado = db.insert("propiedades", null, valores)

        return resultado != -1L
    }

    // Funcion para verificar que no exista un nombre de propiedad en la DB (Utilizado para Insert y Update)
    fun existeNombrePropiedad(nombre: String, idUsuario:Int, idPropiedadModificar: Int? = null): Boolean {
        val db = readableDatabase
        val cursor = if (idPropiedadModificar != null) { // Si le pasa id (Funciona cuando se realiza una modificacion)
            db.rawQuery(
                "SELECT 1 FROM propiedades WHERE nombre = ? AND id_usuario = ? AND id != ? LIMIT 1",
                arrayOf(nombre,idUsuario.toString(), idPropiedadModificar.toString())
            )
        } else {
            db.rawQuery( // Se ejecuta cuando es un Insert (No se pasa idPropiedadModificar en parametros)
                "SELECT 1 FROM propiedades WHERE nombre COLLATE NOCASE = ? AND id_usuario = ? LIMIT 1",
                arrayOf(nombre, idUsuario.toString())
            )
        }

        val existe = cursor.moveToFirst()
        cursor.close()
        return existe
    }

    // Funcion para cargar todas las propiedades de un usuario
    fun obtenerPropiedades(idUsuario: Int): List<Propiedad> {
        val propiedades = mutableListOf<Propiedad>()
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT id, nombre FROM propiedades WHERE id_usuario = ?",
            arrayOf(idUsuario.toString())
        )

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(0)
                val nombre = cursor.getString(1)
                propiedades.add(Propiedad(id = id, nombre = nombre))
            } while (cursor.moveToNext())
        }

        cursor.close()
        return propiedades
    }

    // Funcion para obtener los datos de una propiedad especifica
    fun obtenerDatosPropiedad(idPropiedad: Int): Propiedad {
        var propiedad = Propiedad()
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT nombre, ubicacion, cantidad_habitantes FROM propiedades WHERE id = ?",
            arrayOf(idPropiedad.toString())
        )
        if (cursor.moveToFirst()) {
            do {
                val nombre = cursor.getString(0)
                val ubicacion = cursor.getString(1)
                val cantidadHabitantes = cursor.getInt(2)
                propiedad = Propiedad(
                    nombre = nombre,
                    ubicacion = ubicacion,
                    cantidadHabitantes = cantidadHabitantes
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        return propiedad
    }

    // Funcion para actualizar los datos de una propiedad
    fun actualizarDatosPropiedad(idPropiedad: Int, nuevoNombre: String?, nuevaUbicacion: String?, nuevaCantidadHb: Int?): Boolean {
        val db = writableDatabase
        // capturo los datos actuales de la propiedad
        val datosActualesPropiedad = obtenerDatosPropiedad(idPropiedad)

        // Creo una variable valores(cambios) y hayCambio(Mostrar snackbar informando que no se realizaron modificaciones)
        val valores = ContentValues()
        var hayCambio = false

        // Validaciones para comprobar que campo/s se modificaron o no
        if (nuevoNombre != null && nuevoNombre != datosActualesPropiedad.nombre ) {
            valores.put("nombre", nuevoNombre)
            hayCambio = true
        }
        if (nuevaUbicacion != null && nuevaUbicacion != datosActualesPropiedad.ubicacion) {
            valores.put("ubicacion", nuevaUbicacion)
            hayCambio = true
        }
        if (nuevaCantidadHb != null && nuevaCantidadHb != datosActualesPropiedad.cantidadHabitantes) {
            valores.put("cantidad_habitantes", nuevaCantidadHb)
            hayCambio = true
        }

        if (!hayCambio) {
            return false // Si no hubo cambios retorno false
        }else{
            // Si hay cambios ejecuto el Update en la tabla y retorno true
            db.update("propiedades", valores, "id = ?", arrayOf(idPropiedad.toString()))
            return true
        }
    }

    // Funcion para eliminar una propiedad
    fun eliminarPropiedad(idPropiedad: Int) {
        val db = writableDatabase
        db.delete("elementos", "id_propiedad = ?", arrayOf(idPropiedad.toString())) //Elimina todos sus elementos
        db.delete("propiedades", "id = ?", arrayOf(idPropiedad.toString())) // Elimina la propiedad
    }

    // FUNCIONES ELEMENTO
    // Funcion para insertar un elemento
    fun insertarElemento(idPropiedad:Int, idCategoria:Int, nombre: String, stock: Int): Boolean {
        val db = writableDatabase
        val valores = ContentValues().apply {
            put("id_propiedad", idPropiedad)
            put("id_categoria", idCategoria)
            put("nombre", nombre)
            put("stock", stock)
        }
        val resultado = db.insert("elementos", null, valores)

        return resultado != -1L
    }

    // Funcion para verificar que no exista un elemento con un mismo nombre en la DB
    fun existeNombreElemento(nombre: String, idPropiedad: Int, idCategoria: Int): Boolean {
        val db = readableDatabase
        val cursor = db.rawQuery( // Ignora mayusculas y minusculas en el nombre
            "SELECT * FROM elementos WHERE nombre COLLATE NOCASE = ? AND id_propiedad = ? AND id_categoria = ? LIMIT 1",
            arrayOf(nombre, idPropiedad.toString(), idCategoria.toString())
        )
        val existe = cursor.moveToFirst()
        cursor.close()
        return existe
    }

    // Funcion para obtener todos los elementos de una propiedad en una categoria
    fun obtenerElementosCategoria(idPropiedad: Int, idCategoria: Int): List<Elemento> {
        val elementos = mutableListOf<Elemento>()
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT id, stock, nombre FROM elementos WHERE id_propiedad = ? AND id_categoria = ?",
            arrayOf(idPropiedad.toString(), idCategoria.toString())
        )

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(0)
                val stock = cursor.getInt(1)
                val nombre = cursor.getString(2)
                elementos.add(Elemento(id = id, stock = stock, nombre = nombre))
            } while (cursor.moveToNext())
        }

        cursor.close()
        return elementos
    }

    // Funcion para obtener los datos de un elemento
    fun obtenerDatosElementoCategoria(idElemento: Int): Elemento {
        var elemento = Elemento()
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT stock, nombre FROM elementos WHERE id = ?", arrayOf(idElemento.toString())
        )
        if (cursor.moveToFirst()) {
            do {
                val stock = cursor.getInt(0)
                val nombre = cursor.getString(1)
                elemento = Elemento(stock = stock, nombre = nombre)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return elemento
    }

    // Funcion para actualizar el stock de un elemento mediante un Dialog
    fun actualizarStockElemento(idElemento: Int, cantidad: Int) {
        val db = writableDatabase

        // Obtengo el stock actual del elemento
        val cursor = db.rawQuery("SELECT stock FROM elementos WHERE id = ?", arrayOf(idElemento.toString()))
        var stockActual = 0
        if (cursor.moveToFirst()) {
            stockActual = cursor.getInt(0)
        }
        cursor.close()

        // calculo el nuevo stock
        val nuevoStock = (stockActual + cantidad).coerceAtLeast(0)

        val valores = ContentValues().apply {
            put("stock", nuevoStock)
        }

        // Realizo el update
        db.update("elementos", valores, "id = ?", arrayOf(idElemento.toString()))
    }

    // Funcion para eliminar un elemento
    fun eliminarElemento(idElemento: Int) {
        val db = writableDatabase
        db.delete("elementos", "id = ?", arrayOf(idElemento.toString())) //Elimina el elemento
    }

    //Funcion para Categorias
    // Funcion para obtener las categorias de los elementos e imprimir en el PDF
    fun obtenerCategorias(): List<CategoriaElemento> {
        val categorias = mutableListOf<CategoriaElemento>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT id, nombre FROM categorias_elementos", null)
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(0)
                val nombre = cursor.getString(1)
                categorias.add(CategoriaElemento(id = id, nombre = nombre))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return categorias
    }

    // Funcion para Hashear la contraseña
    private fun hashContrasenia(contrasenia: String, salt: ByteArray = generateSalt()): String {
        val iterations = 10000
        val keyLength = 256
        val spec = PBEKeySpec(contrasenia.toCharArray(), salt, iterations, keyLength)
        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
        val hash = factory.generateSecret(spec).encoded

        val saltBase64 = Base64.encodeToString(salt, Base64.NO_WRAP)
        val hashBase64 = Base64.encodeToString(hash, Base64.NO_WRAP)

        return "$saltBase64:$hashBase64" // devuelvo el salt con el hash juntos
    }

    // Funcion para generar el salt
    private fun generateSalt(): ByteArray {
        val salt = ByteArray(16)
        SecureRandom().nextBytes(salt)
        return salt
    }

}
