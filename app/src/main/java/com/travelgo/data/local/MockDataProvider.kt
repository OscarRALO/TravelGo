package com.travelgo.data.local

import com.travelgo.R
import com.travelgo.data.model.Destination

/**
 * Proveedor de datos LOCAL
 * Contiene todos los destinos turísticos de la app
 */
object MockDataProvider {

    fun getDestinations(): List<Destination> {

        val d1 = Destination(
            id = "1",
            nombre = "Machu Picchu",
            ciudad = "Cusco",
            pais = "Perú",
            descripcion = "Antigua ciudad inca en las montañas de los Andes",
            descripcionDetallada = "Machu Picchu es una antigua ciudad inca situada en la cordillera de los Andes...",
            categoria = "Histórico",
            imagenPrincipalUrl = "",
            latitud = -13.1631,
            longitud = -72.5450,
            rating = 4.9f,
            numeroReviews = 15420,
            precio = "S/. 350 por persona",
            duracion = "Full day (12 horas)",
            horario = "6:00 AM - 5:30 PM",
            popularidad = 100
        )
        d1.imagenPrincipal = R.drawable.machupicchu
        d1.imagenes = listOf(R.drawable.machupicchu)

        val d2 = Destination(
            id = "2",
            nombre = "Máncora",
            ciudad = "Piura",
            pais = "Perú",
            descripcion = "El destino playero más popular del norte peruano",
            descripcionDetallada = "Máncora es el destino playero más popular del norte peruano...",
            categoria = "Playa",
            imagenPrincipalUrl = "",
            latitud = -4.1061,
            longitud = -81.0464,
            rating = 4.7f,
            numeroReviews = 8430,
            precio = "S/. 250 por persona",
            duracion = "3 días / 2 noches",
            horario = "24 horas",
            popularidad = 85
        )
        d2.imagenPrincipal = R.drawable.mancora
        d2.imagenes = listOf(R.drawable.mancora)

        val d3 = Destination(
            id = "3",
            nombre = "Volcán Misti",
            ciudad = "Arequipa",
            pais = "Perú",
            descripcion = "Estratovolcán activo que domina Arequipa",
            descripcionDetallada = "El Volcán Misti es un estratovolcán activo...",
            categoria = "Montaña",
            imagenPrincipalUrl = "",
            latitud = -16.2940,
            longitud = -71.4090,
            rating = 4.8f,
            numeroReviews = 5620,
            precio = "S/. 400 por persona",
            duracion = "2 días / 1 noche",
            horario = "5:00 AM - 8:00 PM",
            popularidad = 78
        )
        d3.imagenPrincipal = R.drawable.arequipa
        d3.imagenes = listOf(R.drawable.arequipa)

        val d4 = Destination(
            id = "4",
            nombre = "Lima Centro",
            ciudad = "Lima",
            pais = "Perú",
            descripcion = "Centro histórico Patrimonio de la Humanidad",
            descripcionDetallada = "El centro histórico de Lima...",
            categoria = "Ciudad",
            imagenPrincipalUrl = "",
            latitud = -12.0464,
            longitud = -77.0428,
            rating = 4.6f,
            numeroReviews = 12340,
            precio = "S/. 150 por persona",
            duracion = "City tour (6 horas)",
            horario = "9:00 AM - 6:00 PM",
            popularidad = 90
        )
        d4.imagenPrincipal = R.drawable.centrolima
        d4.imagenes = listOf(R.drawable.centrolima)

        val d5 = Destination(
            id = "5",
            nombre = "Amazonía Peruana",
            ciudad = "Iquitos",
            pais = "Perú",
            descripcion = "Explora la selva amazónica y su biodiversidad",
            descripcionDetallada = "Explora la selva amazónica desde Iquitos...",
            categoria = "Naturaleza",
            imagenPrincipalUrl = "",
            latitud = -3.7437,
            longitud = -73.2516,
            rating = 4.8f,
            numeroReviews = 6780,
            precio = "S/. 600 por persona",
            duracion = "4 días / 3 noches",
            horario = "Todo el día",
            popularidad = 82
        )
        d5.imagenPrincipal = R.drawable.amazonas
        d5.imagenes = listOf(R.drawable.amazonas)

        val d6 = Destination(
            id = "6",
            nombre = "Reserva Nacional de Paracas",
            ciudad = "Ica",
            pais = "Perú",
            descripcion = "Paraíso costero con islas y fauna marina",
            descripcionDetallada = "Paracas es un paraíso costero donde el desierto se encuentra con el mar...",
            categoria = "Naturaleza",
            imagenPrincipalUrl = "",
            latitud = -13.8318,
            longitud = -76.2775,
            rating = 4.7f,
            numeroReviews = 4590,
            precio = "S/. 280 por persona",
            duracion = "2 días / 1 noche",
            horario = "8:00 AM - 5:00 PM",
            popularidad = 75
        )
        d6.imagenPrincipal = R.drawable.paracas
        d6.imagenes = listOf(R.drawable.paracas)

        return listOf(d1, d2, d3, d4, d5, d6)
    }

    fun getDestinationById(id: String): Destination? =
        getDestinations().find { it.id == id }

    fun getDestinationsByCategory(category: String): List<Destination> =
        if (category == "Todos") getDestinations()
        else getDestinations().filter { it.categoria == category }

    fun searchDestinations(query: String): List<Destination> {
        if (query.isBlank()) return getDestinations()
        val q = query.lowercase()

        return getDestinations().filter {
            it.nombre.lowercase().contains(q) ||
                    it.ciudad.lowercase().contains(q) ||
                    it.descripcion.lowercase().contains(q) ||
                    it.categoria.lowercase().contains(q)
        }
    }

    fun getDestinationsByIds(ids: Set<String>): List<Destination> =
        getDestinations().filter { it.id in ids }

    fun getPopularDestinations(): List<Destination> =
        getDestinations().sortedByDescending { it.popularidad }.take(3)

    fun getCategories(): List<String> =
        listOf("Todos") + getDestinations()
            .map { it.categoria }
            .distinct()
            .sorted()
}
