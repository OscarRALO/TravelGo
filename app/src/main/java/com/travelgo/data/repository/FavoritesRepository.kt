package com.travelgo.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.travelgo.data.model.Destination

class FavoritesRepository {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // Referencia a la colección del usuario actual
    private fun getFavoritesCollection() = auth.currentUser?.uid?.let { uid ->
        db.collection("usuarios").document(uid).collection("favoritos")
    }

    /**
     * Guarda un destino en la sub-colección 'favoritos' del usuario
     */
    /*fun addFavorite(destination: Destination, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val collection = getFavoritesCollection() ?: return

        // Creamos un mapa con los datos esenciales para mostrar en la lista de favoritos
        // Nota: Si usas imágenes locales (R.drawable...), guardamos el ID entero.
        // Para una app real en producción, deberías guardar URLs de imágenes subidas a Storage.
        val favoriteData = hashMapOf(
            "id" to destination.id,
            "nombre" to destination.nombre,
            "ciudad" to destination.ciudad,
            "pais" to destination.pais,
            "descripcion" to destination.descripcion,
            "categoria" to destination.categoria,
            "imagenPrincipal" to destination.imagenPrincipal, // Guardamos el ID del recurso
            "rating" to destination.rating,
            "precio" to destination.precio
        )

        collection.document(destination.id)
            .set(favoriteData)
            .addOnSuccessListener {
                Log.d("Firestore", "Favorito guardado: ${destination.nombre}")
                onSuccess()
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error al guardar favorito", e)
                onFailure(e)
            }
    }*/

// En FavoritesRepository.kt

    /**
     * Obtener la lista de favoritos desde Firestore
     */
    fun getFavorites(onSuccess: (List<Destination>) -> Unit, onFailure: (Exception) -> Unit) {
        val uid = auth.currentUser?.uid

        if (uid == null) {
            onFailure(Exception("Usuario no logueado"))
            return
        }

        db.collection("usuarios").document(uid).collection("favoritos")
            .get()
            .addOnSuccessListener { snapshot ->
                // Convertimos los documentos de Firestore a objetos Destination
                val destinos = snapshot.toObjects(Destination::class.java)
                onSuccess(destinos)
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }


    fun addFavorite(destination: Destination, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val uid = auth.currentUser?.uid

        // 1. DIAGNÓSTICO: Validar explícitamente si hay usuario
        if (uid == null) {
            val errorMsg = "ERROR CRÍTICO: Usuario no logueado (UID es null). No se puede guardar en Firestore."
            Log.e("Firestore", errorMsg)
            onFailure(Exception(errorMsg)) // Avisar al fragmento que falló
            return
        }

        // 2. Si hay usuario, procedemos
        db.collection("usuarios").document(uid).collection("favoritos")
            .document(destination.id)
            .set(destination) // Puedes pasar el objeto destination directo si tiene constructor vacío
            .addOnSuccessListener {
                Log.d("Firestore", "¡Éxito! Favorito guardado: ${destination.nombre}")
                onSuccess()
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error de Firebase al escribir: ${e.message}", e)
                onFailure(e)
            }
    }
    /**
     * Elimina un destino de favoritos
     */
    fun removeFavorite(destinationId: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        // 1. Obtenemos el UID explícitamente para validar
        val uid = auth.currentUser?.uid

        // 2. DIAGNÓSTICO: Si es nulo, avisamos del error y NO continuamos
        if (uid == null) {
            val errorMsg = "ERROR CRÍTICO: Usuario no logueado (UID es null). No se puede eliminar de Firestore."
            Log.e("Firestore", errorMsg)
            onFailure(Exception(errorMsg)) // Avisar al fragmento que falló
            return
        }

        // 3. Si hay usuario, procedemos a borrar
        // Nota: Ya no usamos getFavoritesCollection() para evitar la confusión, construimos la ruta directa
        db.collection("usuarios").document(uid).collection("favoritos")
            .document(destinationId)
            .delete()
            .addOnSuccessListener {
                Log.d("Firestore", "Favorito eliminado: $destinationId")
                onSuccess()
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error al eliminar favorito", e)
                onFailure(e)
            }
    }
}