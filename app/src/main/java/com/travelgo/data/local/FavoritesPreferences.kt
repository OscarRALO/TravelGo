package com.travelgo.data.local

import android.content.Context
import android.content.SharedPreferences

/**
 * Gestor de favoritos usando SharedPreferences
 * Guarda los favoritos LOCALMENTE en el dispositivo
 */
class FavoritesPreferences(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(
        PREFS_NAME,
        Context.MODE_PRIVATE
    )

    companion object {
        private const val PREFS_NAME = "favorites_prefs"
        private const val KEY_FAVORITES = "favorite_destinations"

        // Singleton
        @Volatile
        private var INSTANCE: FavoritesPreferences? = null

        fun getInstance(context: Context): FavoritesPreferences {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: FavoritesPreferences(context.applicationContext).also {
                    INSTANCE = it
                }
            }
        }
    }

    /**
     * Obtener todos los IDs de favoritos
     * @return Set de IDs (ej: ["1", "3", "5"])
     */
    fun getFavoriteIds(): Set<String> {
        return prefs.getStringSet(KEY_FAVORITES, emptySet()) ?: emptySet()
    }

    /**
     * Verificar si un destino es favorito
     * @param destinoId ID del destino
     * @return true si es favorito, false si no
     */
    fun isFavorite(destinoId: String): Boolean {
        return getFavoriteIds().contains(destinoId)
    }

    /**
     * Agregar un destino a favoritos
     * @param destinoId ID del destino
     * @return true si se agregó, false si ya existía
     */
    fun addFavorite(destinoId: String): Boolean {
        val currentFavorites = getFavoriteIds().toMutableSet()
        val wasAdded = currentFavorites.add(destinoId)

        if (wasAdded) {
            prefs.edit()
                .putStringSet(KEY_FAVORITES, currentFavorites)
                .apply()
        }

        return wasAdded
    }

    /**
     * Quitar un destino de favoritos
     * @param destinoId ID del destino
     * @return true si se eliminó, false si no existía
     */
    fun removeFavorite(destinoId: String): Boolean {
        val currentFavorites = getFavoriteIds().toMutableSet()
        val wasRemoved = currentFavorites.remove(destinoId)

        if (wasRemoved) {
            prefs.edit()
                .putStringSet(KEY_FAVORITES, currentFavorites)
                .apply()
        }

        return wasRemoved
    }

    /**
     * Toggle: agregar si no existe, quitar si existe
     * @param destinoId ID del destino
     * @return true si ahora ES favorito, false si ahora NO es favorito
     */
    fun toggleFavorite(destinoId: String): Boolean {
        return if (isFavorite(destinoId)) {
            removeFavorite(destinoId)
            false // Ya no es favorito
        } else {
            addFavorite(destinoId)
            true // Ahora es favorito
        }
    }

    /**
     * Obtener cantidad de favoritos
     */
    fun getFavoritesCount(): Int {
        return getFavoriteIds().size
    }

    /**
     * Limpiar todos los favoritos
     */
    fun clearAllFavorites() {
        prefs.edit()
            .remove(KEY_FAVORITES)
            .apply()
    }

    /**
     * Verificar si hay favoritos
     */
    fun hasFavorites(): Boolean {
        return getFavoriteIds().isNotEmpty()
    }
}