package com.travelgo.work

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.travelgo.R

class DailyRecommendationWorker(
    val context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    override fun doWork(): Result {

        // Crear canal de notificación (solo 1 vez)
        createNotificationChannel()

        // Lista de recomendaciones
        val recomendaciones = listOf(
            "Visita Machu Picchu, una de las maravillas del mundo.",
            "Conoce el Valle Sagrado y sus hermosos paisajes.",
            "Explora el Centro Histórico de Lima.",
            "Disfruta del Lago Titicaca en Puno.",
            "Descubre la Huacachina y practica sandboarding."
        )
        Log.d("WORKER", "Notificación enviada correctamente")

        // Seleccionar una recomendación aleatoria
        val recomendacion = recomendaciones.random()

        // Crear notificación
        val notification = NotificationCompat.Builder(context, "travel_recommendations")
            .setContentTitle("Recomendación del día ✈️")
            .setContentText(recomendacion)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        // Mostrar notificación
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify((1..9999).random(), notification)

        return Result.success()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "travel_recommendations",
                "Recomendaciones de viaje",
                NotificationManager.IMPORTANCE_HIGH
            )
            val manager = context.getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }
}
