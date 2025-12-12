package com.travelgo.ui.theme.home

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.travelgo.R
import com.travelgo.data.local.MockDataProvider
import com.travelgo.data.model.Destination
import com.travelgo.data.repository.FavoritesRealtimeRepository
import java.util.Locale

class DestinationDetailFragment : Fragment(), TextToSpeech.OnInitListener {

    private var currentDestination: Destination? = null
    private var isFavorite = false
    private lateinit var tts: TextToSpeech
    private var ttsReady = false
    private val realtimeRepo = FavoritesRealtimeRepository()

    companion object {
        private const val TAG = "DestinationDetail"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d(TAG, "onCreateView")
        return inflater.inflate(R.layout.fragment_destination_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated")

        tts = TextToSpeech(requireContext(), this)

        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            Log.e(TAG, "Usuario NO autenticado")
            Toast.makeText(requireContext(), "Debes iniciar sesion", Toast.LENGTH_LONG).show()
            parentFragmentManager.popBackStack()
            return
        }

        Log.d(TAG, "Usuario: ${currentUser.email} (uid=${currentUser.uid})")

        val destinationId = arguments?.getString("destinationId")
        Log.d(TAG, "destinationId=$destinationId")

        if (destinationId == null) {
            Log.e(TAG, "destinationId es null")
            Toast.makeText(requireContext(), "Error: No se especifico destino", Toast.LENGTH_SHORT).show()
            parentFragmentManager.popBackStack()
            return
        }

        currentDestination = MockDataProvider.getDestinationById(destinationId)

        if (currentDestination == null) {
            Log.e(TAG, "Destino no encontrado")
            Toast.makeText(requireContext(), "Destino no encontrado", Toast.LENGTH_SHORT).show()
            parentFragmentManager.popBackStack()
            return
        }

        Log.d(TAG, "Destino: ${currentDestination!!.nombre} (${currentDestination!!.id})")

        setupViews(view)
        checkIfFavorite()
    }

    private fun checkIfFavorite() {
        val destinationId = currentDestination?.id
        if (destinationId == null || destinationId.isEmpty()) {
            Log.w(TAG, "checkIfFavorite: destinationId invalido")
            return
        }
        Log.d(TAG, "checkIfFavorite: Verificando si es favorito: $destinationId")
        realtimeRepo.isFavorite(destinationId) { isFav ->
            isFavorite = isFav
            updateFavoriteIcon()
            Log.d(TAG, "checkIfFavorite: Es favorito: $isFavorite")
        }
    }

    private fun setupViews(view: View) {
        val destination = currentDestination ?: return

        val ivDestinationImage = view.findViewById<ImageView>(R.id.ivDestinationImage)
        val tvDestinationName = view.findViewById<TextView>(R.id.tvDestinationName)
        val tvLocation = view.findViewById<TextView>(R.id.tvLocation)
        val tvRating = view.findViewById<TextView>(R.id.tvRating)
        val tvCategory = view.findViewById<TextView>(R.id.tvCategory)
        val tvPrice = view.findViewById<TextView>(R.id.tvPrice)
        val tvDuration = view.findViewById<TextView>(R.id.tvDuration)
        val tvDescription = view.findViewById<TextView>(R.id.tvDescription)
        val btnBack = view.findViewById<ImageButton>(R.id.btnBack)
        val btnFavorite = view.findViewById<ImageButton>(R.id.btnFavorite)
        val btnReserve = view.findViewById<Button>(R.id.btnReserve)
        val btnSpeak = view.findViewById<Button>(R.id.btnSpeak)

        ivDestinationImage?.setImageResource(destination.imagenPrincipal)
        tvDestinationName?.text = destination.nombre
        tvLocation?.text = destination.getUbicacionCompleta()
        tvRating?.text = "⭐ ${destination.rating}"
        tvCategory?.text = destination.categoria
        tvPrice?.text = "Desde ${destination.precio}"
        tvDuration?.text = destination.duracion
        tvDescription?.text = destination.descripcionDetallada

        btnBack?.setOnClickListener {
            Log.d(TAG, "btnBack clicked")
            parentFragmentManager.popBackStack()
        }

        btnFavorite?.setOnClickListener {
            Log.d(TAG, "btnFavorite CLICKED!")
            toggleFavorite()
        }

        btnReserve?.setOnClickListener {
            Log.d(TAG, "btnReserve clicked")
            Toast.makeText(requireContext(), "Reserva proximamente", Toast.LENGTH_SHORT).show()
        }

        btnSpeak?.setOnClickListener {
            if (ttsReady) {
                speak(destination.descripcionDetallada)
            } else {
                Toast.makeText(requireContext(), "Espera, inicializando...", Toast.LENGTH_SHORT).show()
            }
        }

        updateFavoriteIcon()
    }

    private fun toggleFavorite() {
        Log.d(TAG, "toggleFavorite: INICIADO - isFavorite=$isFavorite")

        val destination = currentDestination
        if (destination == null) {
            Log.e(TAG, "toggleFavorite: destination es null")
            return
        }

        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            Log.e(TAG, "toggleFavorite: Usuario no autenticado")
            Toast.makeText(requireContext(), "Debes iniciar sesion", Toast.LENGTH_SHORT).show()
            return
        }

        if (destination.id.isEmpty()) {
            Log.e(TAG, "toggleFavorite: destination.id vacio")
            Toast.makeText(requireContext(), "Error: Destino invalido", Toast.LENGTH_LONG).show()
            return
        }

        if (isFavorite) {
            Log.d(TAG, "toggleFavorite: ELIMINANDO favorito: ${destination.nombre} (${destination.id})")
            realtimeRepo.removeFavorite(
                destination.id,
                onSuccess = {
                    isFavorite = false
                    updateFavoriteIcon()
                    Toast.makeText(requireContext(), "Eliminado de favoritos", Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "toggleFavorite: EXITO - Favorito eliminado")
                },
                onFailure = { exception ->
                    Toast.makeText(requireContext(), "Error: ${exception.message}", Toast.LENGTH_LONG).show()
                    Log.e(TAG, "toggleFavorite: ERROR eliminando: ${exception.message}", exception)
                }
            )
        } else {
            Log.d(TAG, "toggleFavorite: AGREGANDO favorito: ${destination.nombre} (${destination.id})")
            realtimeRepo.addFavorite(
                destination,
                onSuccess = {
                    isFavorite = true
                    updateFavoriteIcon()
                    Toast.makeText(requireContext(), "Agregado a favoritos", Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "toggleFavorite: EXITO - Favorito agregado")
                },
                onFailure = { exception ->
                    Toast.makeText(requireContext(), "Error: ${exception.message}", Toast.LENGTH_LONG).show()
                    Log.e(TAG, "toggleFavorite: ERROR agregando: ${exception.message}", exception)
                }
            )
        }
    }

    private fun updateFavoriteIcon() {
        val btnFavorite = view?.findViewById<ImageButton>(R.id.btnFavorite)
        btnFavorite?.setImageResource(if (isFavorite) R.drawable.ic_favorite_filled else R.drawable.ic_favorite_border)
        Log.d(TAG, "updateFavoriteIcon: isFavorite=$isFavorite")
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts.setLanguage(Locale("es", "ES"))
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e(TAG, "TTS: Idioma no soportado")
                ttsReady = false
            } else {
                tts.setPitch(0.95f)
                tts.setSpeechRate(0.87f)
                ttsReady = true
                Log.d(TAG, "TTS: Inicializado")
            }
        } else {
            Log.e(TAG, "TTS: Error inicializacion")
            ttsReady = false
        }
    }

    private fun speak(text: String) {
        if (!ttsReady || text.isEmpty()) return
        Log.d(TAG, "speak: ${text.length} caracteres")
        if (text.length > 4000) {
            val chunks = text.chunked(4000)
            chunks.forEachIndexed { index, chunk ->
                tts.speak(chunk, if (index == 0) TextToSpeech.QUEUE_FLUSH else TextToSpeech.QUEUE_ADD, null, "utterance_$index")
            }
        } else {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
        }
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy")
        if (::tts.isInitialized) {
            tts.stop()
            tts.shutdown()
        }
        super.onDestroy()
    }
}