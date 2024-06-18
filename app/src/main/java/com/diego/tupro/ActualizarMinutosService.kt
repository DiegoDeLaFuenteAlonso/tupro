package com.diego.tupro
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ActualizarMinutosService : Service() {

    private var job: Job? = null

    @SuppressLint("ForegroundServiceType")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val idPartido = intent?.getStringExtra("idPartido")
        if (idPartido != null) {
            // Crea la notificación
            val channelId = "actualizar_minutos_service_channel"
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val name = "Mi Canal"
                val descriptionText = "Canal para ActualizarMinutosService"
                val importance = NotificationManager.IMPORTANCE_DEFAULT
                val channel = NotificationChannel(channelId, name, importance).apply {
                    description = descriptionText
                }
                val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(channel)
            }

            val notification: Notification = NotificationCompat.Builder(this, channelId)
                .setContentTitle("Partido en juego")
                .setContentText("El contador de tiempo de tu partido está en ejecución")
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .build()

            // Inicia el servicio en primer plano
            startForeground(1, notification)


            job = CoroutineScope(Dispatchers.IO).launch {
                while (isActive) {
                    actualizarMinutos(idPartido)
                    delay(60000)  // Espera un minuto
                }
            }
        }
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        job?.cancel()  // Detiene la actualización cuando el servicio se detiene
        stopForeground(true)  // Detiene el servicio en primer plano
    }

    suspend fun actualizarMinutos(idPartido: String) {
        // referencia a la base de datos de Firestore
        val db = Firebase.firestore

        // referencia al documento del partido
        val partidoRef = db.collection("partidos").document(idPartido)

        // partido de la base de datos
        val partido = partidoRef.get().await()

        // Comprueba si el partido existe
        if (partido.exists()) {
            // valor actual de "minutos"
            val minutosActuales = partido.getLong("minutos") ?: 0

            // Incrementa "minutos" en 1
            val nuevosMinutos = minutosActuales + 1

            // Actualiza "minutos" en la base de datos
            partidoRef.update("minutos", nuevosMinutos).await()

            // Si los minutos llegan a 45 o 90, detén el servicio
            if (nuevosMinutos.toInt() == 45 || nuevosMinutos.toInt() == 90) {
                stopSelf()
            }
        } else {
            // El partido no existe
            Log.w("Error en el hilo actualizar minutos", "No such document")
        }
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }
}
