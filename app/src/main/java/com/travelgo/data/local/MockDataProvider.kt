package com.travelgo.data.local

import com.travelgo.R
import com.travelgo.data.model.Destination

/**
 * Proveedor de datos LOCAL
 * Contiene todos los destinos turísticos de la app
 *
 */
object MockDataProvider {

    /**
     * Obtener todos los destinos disponibles
     */
    fun getDestinations(): List<Destination> {
        return listOf(
            // 1. Machu Picchu
            Destination(
                id = "1",
                nombre = "Machu Picchu",
                ciudad = "Cusco",
                pais = "Perú",
                descripcion = "Antigua ciudad inca en las montañas de los Andes",
                descripcionDetallada = "Machu Picchu es una antigua ciudad inca situada en la cordillera de los Andes. Construida en el siglo XV, es considerada una de las 7 maravillas del mundo moderno. Esta ciudadela está rodeada de impresionantes montañas y ofrece vistas espectaculares del Valle Sagrado.",
                categoria = "Histórico",
                imagenPrincipal = R.drawable.machupicchu,
                imagenes = listOf(R.drawable.machupicchu),
                latitud = -13.1631,
                longitud = -72.5450,
                rating = 4.9f,
                numeroReviews = 15420,
                precio = "S/. 350 por persona",
                duracion = "Full day (12 horas)",
                horario = "6:00 AM - 5:30 PM",
                popularidad = 100
            ),

            // 2. Máncora
            Destination(
                id = "2",
                nombre = "Máncora",
                ciudad = "Piura",
                pais = "Perú",
                descripcion = "El destino playero más popular del norte peruano",
                descripcionDetallada = "Máncora es el destino playero más popular del norte peruano. Con aguas cálidas todo el año, es ideal para surfear, hacer kitesurf y relajarse. Sus hermosas playas de arena blanca y su vibrante vida nocturna atraen a turistas de todo el mundo.",
                categoria = "Playa",
                imagenPrincipal = R.drawable.mancora,
                imagenes = listOf(R.drawable.mancora),
                latitud = -4.1061,
                longitud = -81.0464,
                rating = 4.7f,
                numeroReviews = 8430,
                precio = "S/. 250 por persona",
                duracion = "3 días / 2 noches",
                horario = "24 horas",
                popularidad = 85
            ),

            // 3. Volcán Misti
            Destination(
                id = "3",
                nombre = "Volcán Misti",
                ciudad = "Arequipa",
                pais = "Perú",
                descripcion = "Estratovolcán activo que domina Arequipa",
                descripcionDetallada = "El Volcán Misti es un estratovolcán activo que domina el paisaje de Arequipa, conocida como la 'Ciudad Blanca'. Con una altura de 5,822 metros, ofrece una experiencia de trekking desafiante con vistas panorámicas increíbles de la ciudad y el Cañón del Colca.",
                categoria = "Montaña",
                imagenPrincipal = R.drawable.arequipa,
                imagenes = listOf(R.drawable.arequipa),
                latitud = -16.2940,
                longitud = -71.4090,
                rating = 4.8f,
                numeroReviews = 5620,
                precio = "S/. 400 por persona",
                duracion = "2 días / 1 noche",
                horario = "5:00 AM - 8:00 PM",
                popularidad = 78
            ),

            // 4. Lima Centro
            Destination(
                id = "4",
                nombre = "Lima Centro",
                ciudad = "Lima",
                pais = "Perú",
                descripcion = "Centro histórico Patrimonio de la Humanidad",
                descripcionDetallada = "El centro histórico de Lima, declarado Patrimonio de la Humanidad por la UNESCO, ofrece una mezcla única de arquitectura colonial y moderna. Visita la Plaza Mayor, el Palacio de Gobierno, la Catedral de Lima y disfruta de la mejor gastronomía del mundo.",
                categoria = "Ciudad",
                imagenPrincipal = R.drawable.centrolima,
                imagenes = listOf(R.drawable.centrolima),
                latitud = -12.0464,
                longitud = -77.0428,
                rating = 4.6f,
                numeroReviews = 12340,
                precio = "S/. 150 por persona",
                duracion = "City tour (6 horas)",
                horario = "9:00 AM - 6:00 PM",
                popularidad = 90
            ),

            // 5. Amazonía Peruana
            Destination(
                id = "5",
                nombre = "Amazonía Peruana",
                ciudad = "Iquitos",
                pais = "Perú",
                descripcion = "Explora la selva amazónica y su biodiversidad",
                descripcionDetallada = "Explora la selva amazónica desde Iquitos, la ciudad más grande de la Amazonía peruana. Navega por el río Amazonas, observa delfines rosados, caimanes y una increíble biodiversidad. Visita comunidades nativas y sumérgete en la naturaleza más salvaje.",
                categoria = "Naturaleza",
                imagenPrincipal = R.drawable.amazonas,
                imagenes = listOf(R.drawable.amazonas),
                latitud = -3.7437,
                longitud = -73.2516,
                rating = 4.8f,
                numeroReviews = 6780,
                precio = "S/. 600 por persona",
                duracion = "4 días / 3 noches",
                horario = "Todo el día",
                popularidad = 82
            ),

            // 6. Reserva Nacional de Paracas
            Destination(
                id = "6",
                nombre = "Reserva Nacional de Paracas",
                ciudad = "Ica",
                pais = "Perú",
                descripcion = "Paraíso costero con islas y fauna marina",
                descripcionDetallada = "Paracas es un paraíso costero donde el desierto se encuentra con el mar. Visita las Islas Ballestas conocidas como las 'Galápagos Peruanas', hogar de lobos marinos, pingüinos de Humboldt y miles de aves. Disfruta de playas de arena roja y formaciones rocosas únicas.",
                categoria = "Naturaleza",
                imagenPrincipal = R.drawable.paracas,
                imagenes = listOf(R.drawable.paracas),
                latitud = -13.8318,
                longitud = -76.2775,
                rating = 4.7f,
                numeroReviews = 4590,
                precio = "S/. 280 por persona",
                duracion = "2 días / 1 noche",
                horario = "8:00 AM - 5:00 PM",
                popularidad = 75
            )
        )
    }

    /**
     * Obtener un destino por su ID
     */
    fun getDestinationById(id: String): Destination? {
        return getDestinations().find { it.id == id }
    }

    /**
     * Filtrar destinos por categoría
     */
    fun getDestinationsByCategory(category: String): List<Destination> {
        if (category == "Todos") return getDestinations()
        return getDestinations().filter { it.categoria == category }
    }

    /**
     * Buscar destinos por nombre, ciudad o descripción
     */
    fun searchDestinations(query: String): List<Destination> {
        if (query.isBlank()) return getDestinations()

        val lowerQuery = query.lowercase()
        return getDestinations().filter {
            it.nombre.lowercase().contains(lowerQuery) ||
                    it.ciudad.lowercase().contains(lowerQuery) ||
                    it.descripcion.lowercase().contains(lowerQuery) ||
                    it.categoria.lowercase().contains(lowerQuery)
        }
    }

    /**
     * Obtener destinos por IDs (para favoritos)
     */
    fun getDestinationsByIds(ids: Set<String>): List<Destination> {
        return getDestinations().filter { it.id in ids }
    }

    /**
     * Obtener destinos populares (top 3)
     */
    fun getPopularDestinations(): List<Destination> {
        return getDestinations()
            .sortedByDescending { it.popularidad }
            .take(3)
    }

    /**
     * Obtener todas las categorías únicas
     */
    fun getCategories(): List<String> {
        return listOf("Todos") + getDestinations()
            .map { it.categoria }
            .distinct()
            .sorted()
    }
}