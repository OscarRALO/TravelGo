package com.travelgo.ui.theme.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.travelgo.R
import com.travelgo.data.model.Destination

/**
 * RecyclerView Adapter
 * Adaptador para mostrar lista de destinos turísticos
 */
class DestinationsAdapter(
    // Evento Clic - Callbacks para manejar clicks
    private val onItemClick: (Destination) -> Unit,
    private val onFavoriteClick: (Destination) -> Unit
) : RecyclerView.Adapter<DestinationsAdapter.DestinationViewHolder>() {

    private var destinations = listOf<Destination>()
    private var favoriteIds = setOf<String>()

    /**
     * RecyclerView ViewHolder
     * Contiene las referencias a las vistas de cada item
     */
    inner class DestinationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Referencias en Android - findViewById para obtener vistas
        val ivImage: ImageView = itemView.findViewById(R.id.ivDestinationImage)
        val tvName: TextView = itemView.findViewById(R.id.tvDestinationName)
        val tvLocation: TextView = itemView.findViewById(R.id.tvLocation)
        val tvDescription: TextView = itemView.findViewById(R.id.tvDescription)
        val tvRating: TextView = itemView.findViewById(R.id.tvRating)
        val tvPrice: TextView = itemView.findViewById(R.id.tvPrice)
        val chipCategory: Chip = itemView.findViewById(R.id.chipCategory)
        val fabFavorite: FloatingActionButton = itemView.findViewById(R.id.fabFavorite)

        fun bind(destination: Destination) {
            // Asignar datos a las vistas
            tvName.text = destination.nombre
            tvLocation.text = destination.getUbicacionCompleta()
            tvDescription.text = destination.descripcion
            tvRating.text = destination.rating.toString()
            tvPrice.text = destination.getPrecioFormateado()
            chipCategory.text = getCategoryEmoji(destination.categoria) + " " + destination.categoria

            // Cargar imagen
            ivImage.setImageResource(destination.imagenPrincipal)

            // Actualizar estado del favorito
            val isFavorite = favoriteIds.contains(destination.id)
            updateFavoriteButton(isFavorite)

            // Evento Clic - Click en el item completo
            itemView.setOnClickListener {
                onItemClick(destination)
            }

            // Evento Clic - Click en el botón de favorito
            fabFavorite.setOnClickListener {
                // Animación del botón
                animateFavoriteButton()
                onFavoriteClick(destination)
            }
        }

        private fun updateFavoriteButton(isFavorite: Boolean) {
            if (isFavorite) {
                fabFavorite.setImageResource(R.drawable.ic_favorite_filled)
            } else {
                fabFavorite.setImageResource(R.drawable.ic_favorite_border)
            }
        }

        private fun animateFavoriteButton() {
            // Animación simple de escala
            fabFavorite.animate()
                .scaleX(0.7f)
                .scaleY(0.7f)
                .setDuration(100)
                .withEndAction {
                    fabFavorite.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(100)
                        .start()
                }
                .start()
        }

        private fun getCategoryEmoji(category: String): String {
            return when (category) {
                "Playa" -> "🏖️"
                "Montaña" -> "⛰️"
                "Ciudad" -> "🏙️"
                "Histórico" -> "🏛️"
                "Naturaleza" -> "🌿"
                "Aventura" -> "🚵"
                else -> "📍"
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DestinationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_destination, parent, false)
        return DestinationViewHolder(view)
    }

    override fun onBindViewHolder(holder: DestinationViewHolder, position: Int) {
        holder.bind(destinations[position])
    }

    override fun getItemCount(): Int = destinations.size

    /**
     * Actualizar lista de destinos
     */
    fun submitList(newDestinations: List<Destination>) {
        destinations = newDestinations
        notifyDataSetChanged()
    }

    /**
     * Actualizar IDs de favoritos
     */
    fun updateFavorites(favoriteDestinationIds: Set<String>) {
        favoriteIds = favoriteDestinationIds
        notifyDataSetChanged()
    }
}