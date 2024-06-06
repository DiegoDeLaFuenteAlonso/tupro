package com.diego.tupro
import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import android.util.Log

class ActualizarMinutosService : Service() {

    private var job: Job? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val idPartido = intent?.getStringExtra("idPartido")
        if (idPartido != null) {
            job = CoroutineScope(Dispatchers.IO).launch {
                while (isActive) {
                    delay(60000)  // Espera un minuto
                    actualizarMinutos(idPartido)
                }
            }
        }
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        job?.cancel()  // Detiene la actualización cuando el servicio se detiene
    }

    suspend fun actualizarMinutos(idPartido: String) {
        // Obtén una referencia a la base de datos de Firestore
        val db = Firebase.firestore

        // Obtén una referencia al documento del partido
        val partidoRef = db.collection("partidos").document(idPartido)

        // Obtén el partido de la base de datos
        val partido = partidoRef.get().await()

        // Comprueba si el partido existe
        if (partido.exists()) {
            // Obtén el valor actual de "minutos"
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
