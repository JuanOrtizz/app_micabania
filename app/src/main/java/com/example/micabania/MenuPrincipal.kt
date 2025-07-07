package com.example.micabania

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.view.View
import android.widget.ImageButton
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.widget.TextView


class MenuPrincipal : AppCompatActivity() {

    // Declaro variables
    private lateinit var drawerLayout: DrawerLayout // drawerLayout del menu hamburguesa
    private lateinit var btnMenuHamburguesa: ImageButton // boton menu hamburguesa
    private lateinit var navView:NavigationView // Nav View del menu hamburguesa
    private lateinit var launcherNuevaPropiedad: ActivityResultLauncher<Intent>// Lanza Activity y trae datos de vuelta (Solo si se registra la propiedad)
    private lateinit var launcherPropiedad: ActivityResultLauncher<Intent>// Lanza Activity y trae datos de vuelta (Solo si se elimina la propiedad)
    private lateinit var recyclerView: RecyclerView// Recycler view
    private lateinit var adapter: AdapterMenuPrincipal // Adapter del recycler view
    private lateinit var dbHelper: AppDBHelper
    private lateinit var sharedPref: SharedPreferences
    private var idUsuarioSharedPref: Int = 0
    private var nombreUsuarioSharedPref: String = ""

    //Funcion OnCreate
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_principal)

        // Inicializo las variables al cargar la activity
        drawerLayout = findViewById(R.id.drawer_layout)
        btnMenuHamburguesa = findViewById(R.id.btnMenuHamburguesa)
        navView = findViewById(R.id.nav_view)
        launcherNuevaPropiedad = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            obtenerDatosNuevaPropiedad(result) // llamo al metodo para obtener datos del registro
        }
        launcherPropiedad = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            obtenerDatosPropiedad(result)
        }
        recyclerView = findViewById(R.id.recyclerViewMenuPrincipal)
        recyclerView.layoutManager = LinearLayoutManager(this)
        dbHelper = DBManager.get()
        sharedPref = getSharedPreferences("MiCabaniaPrefs", Context.MODE_PRIVATE)
        //Obtengo datos de shared preferences
        idUsuarioSharedPref = sharedPref.getInt("id_usuario", -1)
        nombreUsuarioSharedPref = sharedPref.getString("nombre_usuario", "") ?: ""

        //Inicializo el adapter luego de capturar los datos de sharedpref
        adapter = inicializarAdapter()
        //Establezco el adapter al recyclerview
        recyclerView.adapter = adapter

        // Agrego el listener al btn menu hamburguesa
        btnMenuHamburguesa.setOnClickListener {
            if (drawerLayout.isDrawerOpen(navView)) { //Si esta abierto lo cierra (al menu)
                drawerLayout.closeDrawer(navView)
            } else {
                drawerLayout.openDrawer(navView)// Sino lo abre (al menu)
            }
        }

        //Cambio los colores del menú
        cambiarColoresElementosMenu()

        // Listeners para cada elemento del menu y realizar la navegacion
        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_cuenta -> { // Listener para el item Cuenta
                    val intent = Intent(this, TuCuenta::class.java)
                    startActivity(intent)
                    true
                }
                R.id.nav_propiedades -> { // Listener para el item Propiedades
                    true
                }
                R.id.nav_faqs -> { //Listener para el item FAQs
                    val intent = Intent(this, FAQs::class.java)
                    startActivity(intent)
                    true
                }
                R.id.nav_soporte -> { // Listener para el item Soporte
                    val intent = Intent(this, Soporte::class.java)
                    startActivity(intent)
                    true
                }
                R.id.nav_logout -> { // Listener para el item Logout
                    // Limpio sharedPreferences al cerrar sesion
                    with(sharedPref.edit()) {
                        clear()
                        apply()
                    }
                    val intent = Intent(this, MainActivity::class.java)
                    // Para evitar que el usuario pueda volver para atras
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                    true
                }
                else -> false
            }.also {
                drawerLayout.closeDrawers()
            }
        }
    }

    //Funcion para cambiar los colores de los items del menu
    private fun cambiarColoresElementosMenu(){
        navView.itemIconTintList = null // Saco el color gris de los iconos que trae por default
        val menu = navView.menu // capturo el menu
        val logoutItem = menu.findItem(R.id.nav_logout) //capturo el elemento logout (CERRAR SESION)

        // Creo un Spannable para cambiar el color del item CERRAR SESION
        val spannableTitle = SpannableString(logoutItem.title)
        val rojo = ContextCompat.getColor(this, R.color.rojo)
        spannableTitle.setSpan(
            ForegroundColorSpan(rojo),
            0,
            spannableTitle.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        // Asigno el titulo con el nuevo color a CERRAR SESION
        logoutItem.title = spannableTitle
    }

    //Funcion para mostrar el snackbar (crear propiedad)
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

    //Funcion para mostrar el snackbar con accion (Eliminar Propiedad)
    private fun mostrarSnackbarConAccion (idVista:Int, mensaje:String, idPropiedad: Int){
        // obtengo el contexto donde se va a mostrar el snackbar
        val contextView = findViewById<View>(idVista)
        // Crea el snackbar
        val snackbar = Snackbar.make(contextView, mensaje, Snackbar.LENGTH_LONG)
            .setAction("Cancelar"){
                adapter.recuperarPropiedad(idPropiedad)
            }
            .addCallback(object: Snackbar.Callback(){
                override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                    if (event != DISMISS_EVENT_ACTION) {
                        // Si no cancela, elimino la propiedad de la DB
                        dbHelper.eliminarPropiedad(idPropiedad)
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

    // Funcion para inicializar el adapter del Recycler View
    private fun inicializarAdapter(): AdapterMenuPrincipal{
        //obtengo las propiedades del usuario por su ID
        val propiedades = dbHelper.obtenerPropiedades(idUsuarioSharedPref)

        //Creo una lista de MenuItem que esta definido en el adapter
        val items = mutableListOf<MenuItem>()

        //Agrego en la lista los valores a mostrar
        items.add(MenuItem.Bienvenida(nombreUsuarioSharedPref)) //Mando el nombre
        items.addAll(propiedades.map { MenuItem.Propiedad(it.id!!, it.nombre!!) }) //Mando sus propiedades
        items.add(MenuItem.AgregarPropiedad) // Mando el boton

        //Creo el adapter con los items y las funciones callback
        val adapter = AdapterMenuPrincipal(
            items,
            onClickPropiedad = { idPropiedad -> // Listener para el click en la propiedad
                val intent = Intent(this, MenuPropiedad::class.java)
                // paso el id de la propiedad a la activity Menu Propiedad
                intent.putExtra("id_propiedad", idPropiedad)
                intent.putExtra("id_usuario", idUsuarioSharedPref)
                launcherPropiedad.launch(intent)
            },
            onClickAgregar = { //Listener para el click en el boton agregar propiedad
                val intent = Intent(this, AgregarPropiedad::class.java)
                intent.putExtra("id_usuario", idUsuarioSharedPref)
                launcherNuevaPropiedad.launch(intent)
            }
        )

        //Retorno el adapter para que el recycler view pueda mostrar los elementos
        return adapter
    }

    //Funcion para actualizar el adapter al agregar o eliminar una propiedad
    private fun actualizarAdapterRV(){
        val propiedades = dbHelper.obtenerPropiedades(idUsuarioSharedPref) //Obtiene de nuevo las propiedades
        val items = mutableListOf<MenuItem>().apply {
            add(MenuItem.Bienvenida(nombreUsuarioSharedPref)) //Pasa el nombre del usuario por parametros
            addAll(propiedades.map { MenuItem.Propiedad(it.id!!, it.nombre!!) }) // Pasa las propiedades
            add(MenuItem.AgregarPropiedad)//Pasa el boton de agregar propiedad
        }
        Log.d("DEBUG", "Propiedades después de crear: ${propiedades.map { it.nombre }}")
        adapter.updateItems(items) //Actualiza los items (textview bienvenida, btns propiedades y btn agregar propiedad)
    }

    //Funcion para obtener los datos de la nueva propiedad
    private fun obtenerDatosNuevaPropiedad(result: ActivityResult){
        // si el resultado de la activity es OK (es decir, agrego una propiedad) ejecuta esto
        if (result.resultCode == Activity.RESULT_OK) {
            // obtenemos la informacion de la otra activity
            val data = result.data
            //  obtenemos los datos individuales
            val mensaje = data?.getStringExtra("mensaje_snackbar")
            // si el mensaje no es nulo o vacio
            if(!mensaje.isNullOrEmpty()){
                actualizarAdapterRV() // actualizo el recycler view para mostrar la nueva propiedad
                mostrarSnackbar(R.id.vista_menu_principal, mensaje) // muestra el snackbar
            }
        }
    }

    //Funcion para obtener los datos de la propiedad
    private fun obtenerDatosPropiedad(result: ActivityResult){
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            data?.let {
                val cambiosPropiedad = it.getBooleanExtra("cambios_propiedad", false)
                val mensaje = it.getStringExtra("mensaje_snackbar")
                val idPropiedad = it.getIntExtra("id_propiedad", -1)

                if (cambiosPropiedad) {
                    actualizarAdapterRV()
                }
                if (!mensaje.isNullOrEmpty() && idPropiedad != -1) {
                    actualizarAdapterRV()
                    adapter.ocultarPropiedad(idPropiedad)
                    mostrarSnackbarConAccion(R.id.vista_menu_principal, mensaje, idPropiedad)
                }
            }
        }
    }

}