package com.travelgo.ui.theme.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.travelgo.R
import com.travelgo.data.model.Destination

/**
 * Adaptador de Favoritos
 * Ahora optimizado + compatible con Firestore
 */
class FavoritesAdapter(
    private val onItemClick: (Destination) -> Unit,
    private val onRemoveFavorite: (Destination) -> Unit
) : RecyclerView.Adapter<FavoritesAdapter.FavoriteViewHolder>() {

    // Lista mutable (antes era inmutable → ERROR al eliminar items)
    private var favorites = mutableListOf<Destination>()

    /**
     * ViewHolder
     */
    inner class FavoriteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val ivImage: ImageView = itemView.findViewById(R.id.ivFavoriteImage)
        private val tvName: TextView = itemView.findViewById(R.id.tvFavoriteName)
        private val tvLocation: TextView = itemView.findViewById(R.id.tvFavoriteLocation)
        private val tvRating: TextView = itemView.findViewById(R.id.tvFavoriteRating)
        private val tvCategory: TextView = itemView.findViewById(R.id.tvFavoriteCategory)
        private val btnRemove: ImageButton = itemView.findViewById(R.id.btnRemoveFavorite)

        fun bind(destination: Destination) {
            tvName.text = destination.nombre
            tvLocation.text = destination.getUbicacionCompleta()
            tvRating.text = destination.rating.toString()
            tvCategory.text = destination.categoria

            // ⚠️ Antes usabas setImageResource → NO sirve si las imágenes vienen de Firestore
            ivImage.setImageResource(destination.imagenPrincipal)

            itemView.setOnClickListener { onItemClick(destination) }

            btnRemove.setOnClickListener {
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

    override fun getItemCount(): Int = favorites.size

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        holder.bind(favorites[position])
    }

    /**
     * Actualiza la lista usando DiffUtil para rendimiento
     */
    fun submitList(newFavorites: List<Destination>) {
        val diffCallback = object : DiffUtil.Callback() {
            override fun getOldListSize() = favorites.size
            override fun getNewListSize() = newFavorites.size

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
                favorites[oldItemPosition].id == newFavorites[newItemPosition].id

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
                favorites[oldItemPosition] == newFavorites[newItemPosition]
        }

        val diffResult = DiffUtil.calculateDiff(diffCallback)

        favorites = newFavorites.toMutableList()
        diffResult.dispatchUpdatesTo(this)
    }

    /**
     * Eliminar un favorito con animación + actualización de Firestore
     */
    fun removeItem(destination: Destination) {
        val index = favorites.indexOfFirst { it.id == destination.id }
        if (index != -1) {
            favorites.removeAt(index)
            notifyItemRemoved(index)
        }
    }
}
