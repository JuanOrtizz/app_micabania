package com.example.micabania

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat

class MiCabaniaApp : Application() {

    override fun onCreate() {
        super.onCreate()

        // Inicializo el objeto con el patron Singleton para la DB
        DBManager.init(applicationContext)

        // Registro un callback para todas las actividades
        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                // Configuro la barra de estado en todas las activities a negro cuando se activen
                activity.window.statusBarColor = ContextCompat.getColor(activity, R.color.verdeFondoApp)
                WindowInsetsControllerCompat(activity.window, activity.window.decorView)
                    .isAppearanceLightStatusBars = true
            }
            // Todos los metodos
            override fun onActivityStarted(activity: Activity) {}
            override fun onActivityResumed(activity: Activity) {}
            override fun onActivityPaused(activity: Activity) {}
            override fun onActivityStopped(activity: Activity) {}
            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
            override fun onActivityDestroyed(activity: Activity) {}
        })
    }
}