package com.travelgo.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.travelgo.data.model.Destination

class FavoritesRepository {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    companion object {
        private const val TAG = "FavoritesRepository"
    }

    fun addFavorite(
        destination: Destination,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val user = auth.currentUser
        if (user == null) {
            val e = Exception("Usuario no autenticado (auth.currentUser == null)")
            Log.e(TAG, "addFavorite FAILED: user null. Destino: ${destination.id}")
            onFailure(e)
            return
        }
        val uid = user.uid

        if (destination.id.isEmpty()) {
            val e = Exception("ID de destino vacío. No se puede guardar.")
            Log.e(TAG, "addFavorite FAILED: id vacío. Destination: $destination")
            onFailure(e)
            return
        }

        // Prepara objeto para guardar: Firestore ignorará @Exclude
        try {
            Log.d(TAG, "addFavorite: intentando guardar destino '${destination.nombre}' (id=${destination.id}) para uid=$uid")
            db.collection("users")
                .document(uid)
                .collection("favoritos")
                .document(destination.id)
                .set(destination)
                .addOnSuccessListener {
                    Log.d(TAG, "addFavorite: ÉXITO guardado destinoId=${destination.id} para uid=$uid")
                    onSuccess()
                }
                .addOnFailureListener { ex ->
                    Log.e(TAG, "addFavorite: ERROR al guardar destinoId=${destination.id} para uid=$uid -> ${ex.message}", ex)
                    onFailure(ex)
                }
        } catch (ex: Exception) {
            Log.e(TAG, "addFavorite: excepción al preparar set() -> ${ex.message}", ex)
            onFailure(ex)
        }
    }

    fun removeFavorite(
        destinationId: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val user = auth.currentUser
        if (user == null) {
            val e = Exception("Usuario no autenticado (auth.currentUser == null)")
            Log.e(TAG, "removeFavorite FAILED: user null. destinationId=$destinationId")
            onFailure(e)
            return
        }
        val uid = user.uid

        if (destinationId.isEmpty()) {
            val e = Exception("destinationId vacío")
            Log.e(TAG, "removeFavorite FAILED: destinationId vacío")
            onFailure(e)
            return
        }

        Log.d(TAG, "removeFavorite: intentando eliminar $destinationId para uid=$uid")
        db.collection("users")
            .document(uid)
            .collection("favoritos")
            .document(destinationId)
            .delete()
            .addOnSuccessListener {
                Log.d(TAG, "removeFavorite: ÉXITO eliminado $destinationId")
                onSuccess()
            }
            .addOnFailureListener { ex ->
                Log.e(TAG, "removeFavorite: ERROR al eliminar $destinationId -> ${ex.message}", ex)
                onFailure(ex)
            }
    }

    fun getFavorites(
        onSuccess: (List<Destination>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val user = auth.currentUser
        if (user == null) {
            val e = Exception("Usuario no autenticado")
            Log.e(TAG, "getFavorites FAILED: user null")
            onFailure(e)
            return
        }
        val uid = user.uid

        Log.d(TAG, "getFavorites: obteniendo favoritos para uid=$uid")
        db.collection("users")
            .document(uid)
            .collection("favoritos")
            .get()
            .addOnSuccessListener { snapshot ->
                Log.d(TAG, "getFavorites: snapshot.size=${snapshot.size()}")
                val list = snapshot.toObjects(Destination::class.java)
                onSuccess(list)
            }
            .addOnFailureListener { ex ->
                Log.e(TAG, "getFavorites: ERROR -> ${ex.message}", ex)
                onFailure(ex)
            }
    }
}
