package com.travelgo.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ValueEventListener
import com.travelgo.data.model.Destination

class FavoritesRealtimeRepository {

    private val database = FirebaseDatabase.getInstance()
    private val auth = FirebaseAuth.getInstance()

    companion object {
        private const val TAG = "FavoritesRealtimeRepo"
        private const val USERS_PATH = "users"
        private const val FAVORITES_PATH = "favoritos"
    }

    private fun getUserFavoritesRef(): DatabaseReference? {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            Log.e(TAG, "Usuario no autenticado")
            return null
        }
        return database.reference.child(USERS_PATH).child(uid).child(FAVORITES_PATH)
    }

    fun addFavorite(destination: Destination, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val favoritesRef = getUserFavoritesRef()
        if (favoritesRef == null) {
            onFailure(Exception("Usuario no autenticado"))
            return
        }
        if (destination.id.isEmpty()) {
            onFailure(Exception("ID vacio"))
            return
        }
        val favoriteData = hashMapOf("id" to destination.id, "nombre" to destination.nombre, "ciudad" to destination.ciudad, "pais" to destination.pais, "descripcion" to destination.descripcion, "categoria" to destination.categoria, "latitud" to destination.latitud, "longitud" to destination.longitud, "rating" to destination.rating, "precio" to destination.precio, "timestamp" to ServerValue.TIMESTAMP)
        favoritesRef.child(destination.id).setValue(favoriteData).addOnSuccessListener { Log.d(TAG, "Favorito guardado"); onSuccess() }.addOnFailureListener { ex -> Log.e(TAG, "Error: " + ex.message); onFailure(ex) }
    }

    fun removeFavorite(destinationId: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val favoritesRef = getUserFavoritesRef()
        if (favoritesRef == null) {
            onFailure(Exception("Usuario no autenticado"))
            return
        }
        favoritesRef.child(destinationId).removeValue().addOnSuccessListener { onSuccess() }.addOnFailureListener { ex -> onFailure(ex) }
    }

    fun observeFavorites(onDataChange: (List<Destination>) -> Unit, onError: (Exception) -> Unit): ValueEventListener? {
        val favoritesRef = getUserFavoritesRef()
        if (favoritesRef == null) {
            onError(Exception("Usuario no autenticado"))
            return null
        }
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val favorites = mutableListOf<Destination>()
                for (childSnapshot in snapshot.children) {
                    try {
                        val destination = childSnapshot.getValue(Destination::class.java)
                        if (destination != null) {
                            favorites.add(destination)
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Error: " + e.message)
                    }
                }
                onDataChange(favorites)
            }
            override fun onCancelled(error: DatabaseError) {
                onError(error.toException())
            }
        }
        favoritesRef.addValueEventListener(listener)
        return listener
    }

    fun isFavorite(destinationId: String, onResult: (Boolean) -> Unit) {
        val favoritesRef = getUserFavoritesRef()
        if (favoritesRef == null) {
            onResult(false)
            return
        }
        favoritesRef.child(destinationId).get().addOnSuccessListener { snapshot -> onResult(snapshot.exists()) }.addOnFailureListener { onResult(false) }
    }
}