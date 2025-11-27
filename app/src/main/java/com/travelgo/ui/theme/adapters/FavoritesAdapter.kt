package com.travelgo.ui.theme.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.travelgo.R
import com.travelgo.data.model.Destination

/**
 * RecyclerView Adapter
 * Adaptador para la lista de favoritos
 */
class FavoritesAdapter(
    // TEMA: Evento Clic - Callbacks
    private val onItemClick: (Destination) -> Unit,
    private val onRemoveFavorite: (Destination) -> Unit
) : RecyclerView.Adapter<FavoritesAdapter.FavoriteViewHolder>() {

    private var favorites = listOf<Destination>()

    /**
     * RecyclerView ViewHolder
     */
    inner class FavoriteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // TEMA: Referencias en Android
        val ivImage: ImageView = itemView.findViewById(R.id.ivFavoriteImage)
        val tvName: TextView = itemView.findViewById(R.id.tvFavoriteName)
        val tvLocation: TextView = itemView.findViewById(R.id.tvFavoriteLocation)
        val tvRating: TextView = itemView.findViewById(R.id.tvFavoriteRating)
        val tvCategory: TextView = itemView.findViewById(R.id.tvFavoriteCategory)
        val btnRemove: ImageButton = itemView.findViewById(R.id.btnRemoveFavorite)

        fun bind(destination: Destination) {
            tvName.text = destination.nombre
            tvLocation.text = destination.getUbicacionCompleta()
            tvRating.text = destination.rating.toString()
            tvCategory.text = destination.categoria
            ivImage.setImageResource(destination.imagenPrincipal)

            // TEMA: Evento Clic - Click en el item
            itemView.setOnClickListener {
                onItemClick(destination)
            }

            // TEMA: Evento Clic - Click en botón eliminar
            btnRemove.setOnClickListener {
                // Animación antes de eliminar
                animateRemove()
                onRemoveFavorite(destination)
            }
        }

        private fun animateRemove() {
            btnRemove.animate()
                .scaleX(1.3f)
                .scaleY(1.3f)
                .setDuration(150)
                .withEndAction {
                    btnRemove.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(150)
                        .start()
                }
                .start()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_favorite, parent, false)
        return FavoriteViewHolder(view)
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        holder.bind(favorites[position])
    }

    override fun getItemCount(): Int = favorites.size

    /**
     * Actualizar lista de favoritos
     */
    fun submitList(newFavorites: List<Destination>) {
        favorites = newFavorites
        notifyDataSetChanged()
    }

    /**
     * Eliminar un item con animación
     */
    fun removeItem(destination: Destination) {
        val position = favorites.indexOf(destination)
        if (position != -1) {
            favorites = favorites.filterNot { it.id == destination.id }
            notifyItemRemoved(position)
        }
    }
}