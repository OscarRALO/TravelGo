package com.travelgo.data.model

import com.google.firebase.firestore.Exclude

data class Destination(
    val id: String = "",
    val nombre: String = "",
    val ciudad: String = "",
    val pais: String = "Perú",
    val descripcion: String = "",
    val descripcionDetallada: String = "",
    val categoria: String = "",
    val imagenPrincipalUrl: String = "",
    val latitud: Double = 0.0,
    val longitud: Double = 0.0,
    val rating: Float = 0f,
    val numeroReviews: Int = 0,
    val precio: String = "",
    val duracion: String = "",
    val horario: String = "",
    val popularidad: Int = 0
) {

    // Drawables solo para mock / local → Firestore NO los guarda
    @get:Exclude
    var imagenPrincipal: Int = 0

    @get:Exclude
    var imagenes: List<Int> = emptyList()

    fun getUbicacionCompleta(): String = "$ciudad, $pais"

    fun getPrecioFormateado(): String {
        return if (precio.isNotEmpty()) "Desde $precio" else "Consultar precio"
    }

    fun isEmpty(): Boolean {
        return id.isEmpty() || nombre.isEmpty()
    }
}
