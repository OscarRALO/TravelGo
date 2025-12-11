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
import com.travelgo.data.repository.FavoritesRepository
import java.util.Locale

class DestinationDetailFragment : Fragment(), TextToSpeech.OnInitListener {

    private var currentDestination: Destination? = null
    private var isFavorite = false
    private val favoritesRepository = FavoritesRepository()
    private lateinit var tts: TextToSpeech

    companion object {
        private const val TAG = "DestinationDetail"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_destination_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tts = TextToSpeech(requireContext(), this)

        // Verificar autenticación
        if (FirebaseAuth.getInstance().currentUser == null) {
            Log.e(TAG, "❌ Usuario NO autenticado")
            Toast.makeText(requireContext(), "Debes iniciar sesión", Toast.LENGTH_LONG).show()
            parentFragmentManager.popBackStack()
            return
        }

        Log.d(TAG, "✅ Usuario: ${FirebaseAuth.getInstance().currentUser?.email}")

        // Obtener destino
        val destinationId = arguments?.getString("destinationId")
        currentDestination = destinationId?.let { MockDataProvider.getDestinationById(it) }

        if (currentDestination == null) {
            Log.e(TAG, "❌ Destino no encontrado")
            Toast.makeText(requireContext(), "Error al cargar destino", Toast.LENGTH_SHORT).show()
            parentFragmentManager.popBackStack()
            return
        }

        Log.d(TAG, "✅ Destino: ${currentDestination!!.nombre} (ID: ${currentDestination!!.id})")

        setupViews(view)
        checkIfFavorite()
    }

    private fun checkIfFavorite() {
        val destinationId = currentDestination?.id ?: return

        favoritesRepository.getFavorites(
            onSuccess = { favoritos ->
                isFavorite = favoritos.any { it.id == destinationId }
                view?.let { updateFavoriteIcon(it) }
                Log.d(TAG, "Estado favorito: $isFavorite")
            },
            onFailure = { e ->
                Log.e(TAG, "Error verificando favoritos: ${e.message}")
            }
        )
    }

    private fun setupViews(view: View) {
        val destination = currentDestination ?: return

        view.findViewById<ImageView>(R.id.ivDestinationImage)?.setImageResource(destination.imagenPrincipal)
        view.findViewById<TextView>(R.id.tvDestinationName)?.text = destination.nombre
        view.findViewById<TextView>(R.id.tvLocation)?.text = destination.getUbicacionCompleta()
        view.findViewById<TextView>(R.id.tvRating)?.text = "⭐ ${destination.rating}"
        view.findViewById<TextView>(R.id.tvCategory)?.text = destination.categoria
        view.findViewById<TextView>(R.id.tvPrice)?.text = "Desde ${destination.precio}"
        view.findViewById<TextView>(R.id.tvDuration)?.text = destination.duracion
        view.findViewById<TextView>(R.id.tvDescription)?.text = destination.descripcionDetallada

        // Botón atrás
        view.findViewById<ImageButton>(R.id.btnBack)?.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        // Botón favorito
        view.findViewById<ImageButton>(R.id.btnFavorite)?.setOnClickListener {
            toggleFavorite()
        }

        // Botón reservar
        view.findViewById<Button>(R.id.btnReserve)?.setOnClickListener {
            Toast.makeText(requireContext(), "Próximamente 🚀", Toast.LENGTH_SHORT).show()
        }

        // Botón TTS
        view.findViewById<Button>(R.id.btnSpeak)?.setOnClickListener {
            speak(destination.descripcionDetallada)
        }

        updateFavoriteIcon(view)
    }

    private fun toggleFavorite() {
        val destination = currentDestination ?: return
        val view = view ?: return

        if (isFavorite) {
            Log.d("DestinationDetail", "toggleFavorite: eliminando ${destination.id}")
            favoritesRepository.removeFavorite(
                destination.id,
                onSuccess = {
                    isFavorite = false
                    updateFavoriteIcon(view)
                    Toast.makeText(requireContext(), "Eliminado de favoritos", Toast.LENGTH_SHORT).show()
                },
                onFailure = { e ->
                    Toast.makeText(requireContext(), "Error al eliminar: ${e.message}", Toast.LENGTH_LONG).show()
                    Log.e("DestinationDetail", "removeFavorite error: ${e.message}", e)
                }
            )
        } else {
            Log.d("DestinationDetail", "toggleFavorite: agregando ${destination.id}")
            // Chequeos antes de guardar
            val user = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
            if (user == null) {
                Toast.makeText(requireContext(), "Debes iniciar sesión", Toast.LENGTH_SHORT).show()
                return
            }
            if (destination.id.isEmpty()) {
                Toast.makeText(requireContext(), "Destino inválido (id vacío)", Toast.LENGTH_LONG).show()
                Log.e("DestinationDetail", "Destino tiene id vacío: $destination")
                return
            }

            favoritesRepository.addFavorite(
                destination,
                onSuccess = {
                    isFavorite = true
                    updateFavoriteIcon(view)
                    Toast.makeText(requireContext(), "Agregado a favoritos", Toast.LENGTH_SHORT).show()
                },
                onFailure = { e ->
                    Toast.makeText(requireContext(), "Error al guardar favorito: ${e.message}", Toast.LENGTH_LONG).show()
                    Log.e("DestinationDetail", "addFavorite error: ${e.message}", e)
                }
            )
        }
    }



    private fun updateFavoriteIcon(view: View) {
        view.findViewById<ImageButton>(R.id.btnFavorite)?.setImageResource(
            if (isFavorite) R.drawable.ic_favorite_filled else R.drawable.ic_favorite_border
        )
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts.language = Locale("es", "ES")
            tts.setPitch(0.95f)
            tts.setSpeechRate(0.87f)
        }
    }

    private fun speak(text: String) {
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
    }

    override fun onDestroy() {
        super.onDestroy()
        tts.stop()
        tts.shutdown()
    }
}