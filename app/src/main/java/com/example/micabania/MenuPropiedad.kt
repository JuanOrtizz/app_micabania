package com.example.micabania

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Layout
import android.text.Spannable
import android.text.SpannableString
import android.text.StaticLayout
import android.text.TextPaint
import android.text.style.LeadingMarginSpan
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class MenuPropiedad : AppCompatActivity() {
    // Declaro variables
    private lateinit var btnVolver:ImageButton
    private lateinit var txtNombrePropiedad:TextView
    private lateinit var txtUbicacionPropiedad:TextView
    private lateinit var txtCHPropiedad:TextView
    private lateinit var btnExportarPDF:Button
    private lateinit var btnModificarPropiedad: Button
    private lateinit var btnCategoriaCocina: Button
    private lateinit var btnCategoriaBlanqueria: Button
    private lateinit var btnCategoriaBanio: Button
    private lateinit var btnCategoriaLimpieza: Button
    private lateinit var btnCategoriaExteriores: Button
    private lateinit var btnEliminar: Button
    private lateinit var dbHelper: AppDBHelper
    private lateinit var launcherModificarPropiedad: ActivityResultLauncher<Intent>// Lanza Activity y trae datos de vuelta (Solo si se modifica la propiedad)
    private var idPropiedad: Int = -1
    private var idUsuario:Int = -1
    private var cambios:Boolean = false

    // Funcion OnCreate
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_propiedad)

        // Inicializo las variables al cargar la activity
        btnVolver = findViewById(R.id.btnVolver)
        txtNombrePropiedad = findViewById(R.id.txtNombrePropiedad)
        txtUbicacionPropiedad = findViewById(R.id.txtUbicacionPropiedad)
        txtCHPropiedad = findViewById(R.id.txtCHPropiedad)
        btnExportarPDF = findViewById(R.id.btnExportarPDF)
        btnModificarPropiedad = findViewById(R.id.btnModificarPropiedad)
        btnCategoriaCocina = findViewById(R.id.btnCategoriaCocina)
        btnCategoriaBlanqueria = findViewById(R.id.btnCategoriaBlanqueria)
        btnCategoriaBanio = findViewById(R.id.btnCategoriaBaño)
        btnCategoriaLimpieza = findViewById(R.id.btnCategoriaLimpieza)
        btnCategoriaExteriores = findViewById(R.id.btnCategoriaExteriores)
        btnEliminar = findViewById(R.id.btnEliminarPropiedad)
        dbHelper = DBManager.get()
        launcherModificarPropiedad = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            obtenerDatosModificarPropiedad(result) // llamo al metodo para obtener datos del registro
        }
        idPropiedad = intent.getIntExtra("id_propiedad", -1) // capturo el id de la propiedad para mostrar los datos
        idUsuario = intent.getIntExtra("id_usuario",-1) // Capturo el id del usuario

        // cargo los datos de la propiedad
        cargarDatosPropiedad()

        //Listener para el boton volver
        btnVolver.setOnClickListener{
            val resultIntent = Intent().apply {
                // manda los datos para ocultar la propiedad desde el adapter de Menu Principal
                putExtra("cambios_propiedad", cambios) //devuelve si hubo cambios o no
            }
            // Funcion setResult para enviar los datos.
            setResult(Activity.RESULT_OK, resultIntent)
            finish()// finalizo esta activity asi gestiono la memoria
        }

        //Listener para el boton exportar inventario a PDF
        btnExportarPDF.setOnClickListener{
            exportarInventarioAPdf() // exporta PDF
            mostrarSnackbar(R.id.vista_menu_propiedad, "PDF guardado con éxito" )//Muestra snackbar
        }


        //Listener para el boton modificar propiedad
        btnModificarPropiedad.setOnClickListener{
            val intent = Intent(this, ModificarPropiedad::class.java)
            // paso el id
            intent.putExtra("id_propiedad", idPropiedad)
            intent.putExtra("id_usuario", idUsuario)
            launcherModificarPropiedad.launch(intent)
        }

        //Listener para el boton categoria Cocina
        btnCategoriaCocina.setOnClickListener{
            cargarActivityCategoria(R.drawable.icon_cocina, 1, "Cocina")
        }

        //Listener para el boton categoria Blanqueria
        btnCategoriaBlanqueria.setOnClickListener{
            cargarActivityCategoria(R.drawable.icon_blanqueria, 2, "Blanquería")
        }

        //Listener para el boton categoria Baño
        btnCategoriaBanio.setOnClickListener{
            cargarActivityCategoria(R.drawable.icon_banio, 3, "Baño")
        }

        //Listener para el boton categoria Limpieza
        btnCategoriaLimpieza.setOnClickListener{
            cargarActivityCategoria(R.drawable.icon_limpieza, 4, "Limpieza")
        }

        //Listener para el boton categoria Exteriores y otros
        btnCategoriaExteriores.setOnClickListener{
            cargarActivityCategoria(R.drawable.icon_exteriores, 5, "Exteriores y otros")
        }

        //Listener para el boton eliminar propiedad
        btnEliminar.setOnClickListener{
            DialogConfirmacion( // abre el dialog de confirmacion
               onConfirmar = { // funcion onConfirmar
                   val resultIntent = Intent().apply {
                       // manda los datos para ocultar la propiedad desde el adapter de Menu Principal
                       putExtra("mensaje_snackbar", "Eliminaste la propiedad") //devuelve el mensaje para el snackbar
                       putExtra("id_propiedad", idPropiedad) //Devuelve el id de la propiedad para luego eliminarla en MenuPrincipal.kt
                   }
                   // Funcion setResult para enviar los datos.
                   setResult(Activity.RESULT_OK, resultIntent)
                   finish()// finalizo esta activity asi gestiono la memoria
                }
            ).show(supportFragmentManager, "ConfirmDialog")
        }
    }

    // Funcion para cargar los datos de la propiedad
    private fun cargarDatosPropiedad(){
        val propiedad = dbHelper.obtenerDatosPropiedad(idPropiedad)
        txtNombrePropiedad.text = propiedad.nombre
        txtUbicacionPropiedad.text = propiedad.ubicacion
        txtCHPropiedad.text = propiedad.cantidadHabitantes.toString()
    }

    //Funcion para mostrar el snackbar (Modificar propiedad)
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

    //Funcion para obtener los datos de la nueva propiedad
    private fun obtenerDatosModificarPropiedad(result: ActivityResult){
        // si el resultado de la activity es OK (es decir, modifico una propiedad) ejecuta esto
        if (result.resultCode == Activity.RESULT_OK) {
            // obtenemos la informacion de la otra activity
            val data = result.data
            //  obtenemos los datos individuales
            val mensaje = data?.getStringExtra("mensaje_snackbar")
            cambios = true
            // si el nombre y el mensaje no es nulo o vacio
            if(!mensaje.isNullOrEmpty()){
                cargarDatosPropiedad()
                mostrarSnackbar(R.id.vista_menu_propiedad, mensaje) // muestra el snackbar
            }
        }
    }

    // Funcion para cargar la activity Categoria con los datos necesarios en esta
    private fun cargarActivityCategoria(idImagen:Int, idCategoria:Int, nombreCategoria: String){
        val intent = Intent(this, Categoria::class.java)
        intent.putExtra("id_propiedad", idPropiedad)
        intent.putExtra("id_imagen", idImagen)
        intent.putExtra("id_categoria", idCategoria)
        intent.putExtra("nombre_categoria", nombreCategoria)
        startActivity(intent)
    }

    // Funcion para exportar el inventario de una propiedad a un archivo PDF en la carpeta Descargas
    private fun exportarInventarioAPdf() {
        val document = PdfDocument() // Documento PDF
        val paint = Paint() // Paint para escribir texto en el PDF
        val marginLeft = 40 // Margen izquierdo
        var y = 50 // Posicion vertical inicial
        var pageNumber = 1 // Contador de paginas

        // Creo la primer pagina del PDF
        var pageInfo = PdfDocument.PageInfo.Builder(595, 842, pageNumber).create() //A4
        var page = document.startPage(pageInfo)
        var canvas = page.canvas // Canvas para escribir contenido

        // Obtengo los datos de la propiedad
        val propiedad = dbHelper.obtenerDatosPropiedad(idPropiedad)

        // Funcion interna que revisa si se necesita una nueva pagina
        fun verificarPaginaNueva() {
            if (y > 800) { // Si se supera el limite vertical de la pagina crea una nueva pagina
                document.finishPage(page) // termina la pagina actual
                pageNumber++ // sumo 1 en la variable numero de pagina
                // Creo la nueva pagina
                pageInfo = PdfDocument.PageInfo.Builder(595, 842, pageNumber).create()
                page = document.startPage(pageInfo)
                canvas = page.canvas
                y = 50 // Reinicio la posicion vertical
            }
        }

        //Titulo del PDF
        paint.textSize = 22f
        paint.isFakeBoldText = true
        paint.color = Color.BLACK
        val titulo = "INVENTARIO DE PROPIEDAD"
        val tituloWidth = paint.measureText(titulo)
        val tituloX = (595 - tituloWidth) / 2 // pongo el texto al medio del PDF
        canvas.drawText(titulo, tituloX, y.toFloat(), paint)
        y += 30

        //Escribo una linea abajo del titulo
        dibujarLineaPDF(canvas, paint, y)
        y += 20

        //Datos de la propiedad
        paint.textSize = 16f
        paint.isFakeBoldText = true
        canvas.drawText("Datos de la propiedad:", marginLeft.toFloat(), y.toFloat(), paint)
        y += 25

        paint.textSize = 14f
        paint.isFakeBoldText = false
        // Escribo cada dato de la propiedad
        y += escribirItemPDF(canvas, "Nombre: ${propiedad.nombre}", marginLeft, y, paint, bullet = "• ")
        y += escribirItemPDF(canvas, "Ubicación: ${propiedad.ubicacion}", marginLeft, y, paint, bullet = "• ")
        y += escribirItemPDF(canvas, "Cantidad de Habitantes: ${propiedad.cantidadHabitantes}", marginLeft, y, paint, bullet = "• ")
        y += 10

        verificarPaginaNueva() // Verifica si es necesario crear una pagina mas

        //Dibujo de nuevo una linea
        dibujarLineaPDF(canvas, paint, y)
        y += 20

        //Lista de las categorias y elementos de cada una de estas
        val categorias = dbHelper.obtenerCategorias() // Obtengo todas las categorias
        paint.textSize = 16f
        paint.isFakeBoldText = true
        canvas.drawText("Categorías y elementos:", marginLeft.toFloat(), y.toFloat(), paint) //Titulo de la seccion
        y += 25

        paint.textSize = 14f

        // Bucle for para cada categoria
        for (categoria in categorias) {
            verificarPaginaNueva() //Verifico si necesita una pagina neuva
            paint.isFakeBoldText = true
            // Escribo el nombre de la categoria
            y += escribirItemPDF(canvas, "${categoria.nombre}:", marginLeft, y, paint, bullet = "• ")

            val elementos = dbHelper.obtenerElementosCategoria(idPropiedad, categoria.id) // obtengo los elementos de esa categoria
            paint.isFakeBoldText = false

            // Escribo cada elemento dentro de la categoria
            for (elemento in elementos) {
                verificarPaginaNueva() //Verifico si necesita una pagina nueva
                y += escribirItemPDF(canvas, "${elemento.nombre} (x${elemento.stock})", marginLeft + 20, y, paint, bullet = "- ")
            }
            y += 15 // Espacio entre cada categoria
        }

        document.finishPage(page) // Finaliza la ultima pagina una vez que ya escribio todos los items del PDF

        // Guarda el archivo PDF en la carpeta de descargas con el nombre de la propiedad
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // Android 10 y superiores: uso MediaStore para guardar en Descargas
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, "Inventario_${propiedad.nombre}.pdf")
                    put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                }
                val resolver = applicationContext.contentResolver
                val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)

                if (uri != null) {
                    resolver.openOutputStream(uri).use { outputStream ->
                        if (outputStream != null) {
                            document.writeTo(outputStream)
                        }
                    }
                }
            } else {
                // Android 9 y anteriores: guardo directamente en el directorio Descargas (con permiso)
                val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                if (!downloadsDir.exists()) downloadsDir.mkdirs()
                val file = File(downloadsDir, "Inventario_${propiedad.nombre}.pdf")
                FileOutputStream(file).use { fos ->
                    document.writeTo(fos)
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            document.close()
        }
    }

    // Funcion que Dibuja una linea en el pdf
    private fun dibujarLineaPDF(canvas: Canvas, paint: Paint, y: Int) {
        paint.strokeWidth = 2f // Grosor de la linea
        canvas.drawLine(40f, y.toFloat(), 555f, y.toFloat(), paint)
    }

    //Funcion para escribir un Item en el PDF (Datos propiedad, Categorias y elementos)
    private fun escribirItemPDF(canvas: Canvas, text: String, x: Int, yStart: Int, paint: Paint, bullet: String = "- "): Int {
        val bulletWidth = paint.measureText(bullet).toInt() // Calcula el ancho del simbolo del inicio del texto

        // Escribe el simbolo al inicio del texto
        canvas.drawText(bullet, x.toFloat(), yStart.toFloat() + paint.textSize, paint)

        val textPaint = TextPaint(paint) // Usa el mismo estilo de texto que paint
        val spannable = SpannableString(text) // Texto que se puede formatear
        spannable.setSpan(
            LeadingMarginSpan.Standard(0, bulletWidth), // Defino el margen para la alineacion del texto
            0,
            text.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        val maxWidth = 515 - bulletWidth // Defino el ancho disponible para el texto
        val staticLayout = StaticLayout.Builder.obtain(spannable, 0, spannable.length, textPaint, maxWidth)
            .setAlignment(Layout.Alignment.ALIGN_NORMAL) // Alineacion a la izquierda del texto
            .setLineSpacing(0f, 1f) // Espaciado entre  las lineas
            .build()

        // Escribo el texto en el canvas
        canvas.save()
        canvas.translate((x + bulletWidth).toFloat(), yStart.toFloat()) // Ajusto la posicion del texto
        staticLayout.draw(canvas)
        canvas.restore()

        return staticLayout.height // Devuelvo la altura usada por el bloque de texto
    }

}