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
import com.travelgo.R
import com.travelgo.data.local.FavoritesPreferences
import com.travelgo.data.local.MockDataProvider
import com.travelgo.data.model.Destination
import com.travelgo.data.repository.FavoritesRepository
import java.util.Locale

class DestinationDetailFragment : Fragment(), TextToSpeech.OnInitListener {

    private var destinationId: String? = null
    private lateinit var favoritesPreferences: FavoritesPreferences
    private var isFavorite = false
    private val favoritesRepository = FavoritesRepository()

    // ---- TTS ----
    private lateinit var tts: TextToSpeech
    private var descriptionText: String = ""
    // --------------

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_destination_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inicializar TTS
        tts = TextToSpeech(requireContext(), this)

        favoritesPreferences = FavoritesPreferences.getInstance(requireContext())

        destinationId = arguments?.getString("destinationId")
        val name = arguments?.getString("name") ?: "Destino"
        val location = arguments?.getString("location") ?: ""
        val description = arguments?.getString("description") ?: ""
        val rating = arguments?.getFloat("rating") ?: 0f
        val price = arguments?.getString("price") ?: ""
        val duration = arguments?.getString("duration") ?: ""
        val imageRes = arguments?.getInt("image") ?: R.drawable.ic_launcher_background
        val category = arguments?.getString("category") ?: ""

        // Guardamos la descripción para leerla luego
        descriptionText = description

        // Verificar si es favorito
        destinationId?.let {
            isFavorite = favoritesPreferences.isFavorite(it)
        }

        setupViews(view, name, location, description, rating, price, duration, imageRes, category)
        setupButtons(view)

        setupTtsButton(view)
    }

    private fun setupTtsButton(view: View) {
        val btnSpeak = view.findViewById<Button>(R.id.btnSpeak)
        btnSpeak.setOnClickListener {
            speak(descriptionText)
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts.language = Locale("es", "ES")
            tts.setPitch(0.95f)       // 1.0 = normal
            tts.setSpeechRate(0.87f)  // 1.0 = normal
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


    // ---------------- TUS FUNCIONES ORIGINALES ----------------

    private fun setupViews(
        view: View,
        name: String,
        location: String,
        description: String,
        rating: Float,
        price: String,
        duration: String,
        imageRes: Int,
        category: String
    ) {
        view.findViewById<ImageView>(R.id.ivDestinationImage)?.setImageResource(imageRes)
        view.findViewById<TextView>(R.id.tvDestinationName)?.text = name
        view.findViewById<TextView>(R.id.tvLocation)?.text = location
        view.findViewById<TextView>(R.id.tvRating)?.text = "⭐ $rating"
        view.findViewById<TextView>(R.id.tvCategory)?.text = category
        view.findViewById<TextView>(R.id.tvPrice)?.text = "Desde $price"
        view.findViewById<TextView>(R.id.tvDuration)?.text = duration
        view.findViewById<TextView>(R.id.tvDescription)?.text = description

        updateFavoriteIcon(view)
    }

    private fun setupButtons(view: View) {
        view.findViewById<ImageButton>(R.id.btnBack)?.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        view.findViewById<ImageButton>(R.id.btnFavorite)?.setOnClickListener {
            toggleFavorite(view)
        }

        view.findViewById<Button>(R.id.btnReserve)?.setOnClickListener {
            Toast.makeText(
                requireContext(),
                "Funcionalidad de reserva próximamente 🚀",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun toggleFavorite(view: View) {
        val destination = getDestinationFromArguments() // Asegúrate de tener el objeto Destination completo
// LOG DE DEPURACIÓN
        Log.d("DEBUG_APP", "Intentando guardar: ID='${destination.id}', Nombre='${destination.nombre}'")
        if (isFavorite) {
            // Eliminar de Firebase
            favoritesRepository.removeFavorite(destination.id,
                onSuccess = {
                    isFavorite = false
                    updateFavoriteIcon(view)
                    Toast.makeText(requireContext(), "Eliminado de favoritos", Toast.LENGTH_SHORT).show()
                },
                onFailure = {
                    Toast.makeText(requireContext(), "Error al eliminar", Toast.LENGTH_SHORT).show()
                }
            )
        } else {
            // Guardar en Firebase
            favoritesRepository.addFavorite(destination,
                onSuccess = {
                    isFavorite = true
                    updateFavoriteIcon(view)
                    Toast.makeText(requireContext(), "¡Agregado a favoritos! ❤️", Toast.LENGTH_SHORT).show()
                },
                onFailure = {
                    Toast.makeText(requireContext(), "Error al guardar", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }

    // Función auxiliar para reconstruir el objeto Destination desde los argumentos (Bundle)
    private fun getDestinationFromArguments(): Destination {
        return Destination(
            id = arguments?.getString("destinationId") ?: "",
            nombre = arguments?.getString("name") ?: "",
            ciudad = arguments?.getString("location")?.split(",")?.get(0)?.trim() ?: "", // Ejemplo simple
            descripcion = arguments?.getString("description") ?: "",
            imagenPrincipal = arguments?.getInt("image") ?: 0,
            rating = arguments?.getFloat("rating") ?: 0f,
            precio = arguments?.getString("price") ?: "",
            // Completa los campos necesarios...
        )
    }


    private fun updateFavoriteIcon(view: View) {
        val btnFavorite = view.findViewById<ImageButton>(R.id.btnFavorite)
        if (isFavorite) {
            btnFavorite?.setImageResource(R.drawable.ic_favorite_filled)
        } else {
            btnFavorite?.setImageResource(R.drawable.ic_favorite_border)
        }
    }
}
