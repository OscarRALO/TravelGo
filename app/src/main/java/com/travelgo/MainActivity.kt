package com.travelgo

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.travelgo.work.DailyRecommendationWorker
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Tu layout
        setContentView(R.layout.activity_home)

        // Pedir permiso para notificaciones
        pedirPermisoNotificaciones()

        // Programar el Worker que enviará notificaciones cada 24 horas
        programarWorkerDiario()
    }

    private fun pedirPermisoNotificaciones() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 100)
        }
    }

    private fun programarWorkerDiario() {

        // Worker cada 24 horas
        val workRequest =
            PeriodicWorkRequestBuilder<DailyRecommendationWorker>(
                15, TimeUnit.MINUTES
            ).build()

        // Evita crear múltiples workers duplicados
        WorkManager.getInstance(this)
            .enqueueUniquePeriodicWork(
                "daily_travel_recommendations",
                ExistingPeriodicWorkPolicy.UPDATE,
                workRequest
            )
    }
}
