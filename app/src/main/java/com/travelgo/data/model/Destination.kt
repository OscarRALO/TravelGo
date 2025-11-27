package com.travelgo.data.model

/**
 * Modelo de datos para un destino turístico
 * Representa toda la información necesaria de un lugar
 */
data class Destination(
    val id: String = "",                    // ID único del destino
    val nombre: String = "",                // Nombre del destino
    val ciudad: String = "",                // Ciudad donde está ubicado
    val pais: String = "Perú",             // País
    val descripcion: String = "",           // Descripción corta (para cards)
    val descripcionDetallada: String = "",  // Descripción completa (para detalles)
    val categoria: String = "",             // Playa, Montaña, Ciudad, etc.
    val imagenPrincipal: Int = 0,          // Imagen principal (drawable resource)
    val imagenes: List<Int> = emptyList(), // Lista de imágenes (para galería)
    val latitud: Double = 0.0,             // Para mapas
    val longitud: Double = 0.0,            // Para mapas
    val rating: Float = 0f,                // Calificación (0-5)
    val numeroReviews: Int = 0,            // Número de reseñas
    val precio: String = "",               // Precio en formato texto ("S/. 350")
    val duracion: String = "",             // Duración de la visita
    val horario: String = "",              // Horario de atención
    val popularidad: Int = 0               // Para ordenar por popularidad
) {
    // Función auxiliar para verificar si está vacío
    fun isEmpty(): Boolean {
        return id.isEmpty() || nombre.isEmpty()
    }

    // Función para obtener el precio formateado
    fun getPrecioFormateado(): String {
        return if (precio.isNotEmpty()) "Desde $precio" else "Consultar precio"
    }

    // Función para obtener la ubicación completa
    fun getUbicacionCompleta(): String {
        return "$ciudad, $pais"
    }
}