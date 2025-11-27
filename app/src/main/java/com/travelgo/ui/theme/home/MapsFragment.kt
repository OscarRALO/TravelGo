package com.travelgo.ui.theme.home

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.travelgo.R

class MapsFragment : Fragment(), OnMapReadyCallback {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var googleMap: GoogleMap? = null

    private val REQUEST_LOCATION_PERM = 100

    // Destinos cercanos (mock)
    private val destinosCercanos = listOf(
        // --- Destinos Originales ---
        LatLng(-13.5150, -71.9780), // Cusco - Plaza de Armas
        LatLng(-13.1631, -72.5450), // Machu Picchu
        LatLng(-12.0464, -77.0428), // Lima - Centro Histórico (Plaza Mayor)

        // --- Más destinos en Lima y Callao ---
        LatLng(-12.1215, -77.0305), // Miraflores - Parque Kennedy
        LatLng(-12.1474, -77.0208), // Barranco - Puente de los Suspiros
        LatLng(-12.0553, -77.0802), // Callao - Fortaleza del Real Felipe
        LatLng(-12.0838, -77.0090), // San Borja - Museo de la Nación

        // --- Destinos en el Sur ---
        LatLng(-16.3989, -71.5350), // Arequipa - Plaza de Armas
        LatLng(-15.6100, -71.9110), // Cañón del Colca (Mirador Cruz del Cóndor)
        LatLng(-15.8402, -70.0219), // Puno - Plaza de Armas
        LatLng(-15.8190, -69.9700), // Lago Titicaca - Islas Uros (Aprox.)
        LatLng(-14.0875, -75.7631), // Ica - Huacachina
        LatLng(-13.8630, -76.3250), // Paracas - Reserva Nacional (Centro de Visitantes)
        LatLng(-14.7017, -75.1307), // Líneas de Nazca (Mirador)

        // --- Destinos en el Norte ---
        LatLng(-8.1119, -79.0286),  // Trujillo - Plaza de Armas
        LatLng(-8.1080, -79.0750),  // Chan Chan (Cerca a Trujillo)
        LatLng(-6.7714, -79.8409),  // Chiclayo - Plaza de Armas
        LatLng(-6.8010, -79.6010),  // Señor de Sipán (Huaca Rajada)
        LatLng(-5.1945, -80.6328),  // Piura - Plaza de Armas
        LatLng(-4.1000, -81.0500),  // Máncora (Playa)
        LatLng(-9.5260, -77.5280),  // Huaraz - Plaza de Armas (Callejón de Huaylas)
        LatLng(-9.0742, -77.6125),  // Chavín de Huántar

        // --- Destinos en la Selva ---
        LatLng(-3.7491, -73.2538),  // Iquitos - Plaza de Armas
        LatLng(-12.5959, -69.1891), // Puerto Maldonado - (Referencia)
        LatLng(-8.3791, -74.5539),  // Pucallpa - (Referencia)
        LatLng(-7.1687, -78.5123)   // Cajamarca - Plaza de Armas
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        view.findViewById<Button>(R.id.btnMyLocation).setOnClickListener {
            // Centrar en la ubicación actual (solo si permisos están ok)
            if (tienePermisos()) mostrarUbicacionUsuario()
            else pedirPermisos()
        }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        // Comprueba permisos; si no están pide y sale (configurarMapa se llamará después)
        if (!tienePermisos()) {
            pedirPermisos()
            return
        }

        configurarMapa()
    }

    // ------------------- Permisos -------------------

    private fun tienePermisos(): Boolean =
        ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

    private fun pedirPermisos() {
        requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_LOCATION_PERM)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_LOCATION_PERM) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // permiso otorgado: configurar mapa si ya está listo
                try {
                    configurarMapa()
                } catch (e: SecurityException) {
                    // por si acaso, aunque ya verificamos permiso
                    Toast.makeText(requireContext(), "No se pudo activar la ubicación: permiso.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "Permiso de ubicación denegado.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // ------------------- Configuración del mapa -------------------

    private fun configurarMapa() {
        // Seguridad: revisa permiso justo antes de llamar a propiedades que lo requieren
        if (!tienePermisos()) {
            pedirPermisos()
            return
        }

        try {
            googleMap?.apply {
                isMyLocationEnabled = true                // requiere permiso
                uiSettings.isMyLocationButtonEnabled = true
                uiSettings.isZoomControlsEnabled = true
            }
        } catch (se: SecurityException) {
            // Si por alguna razón no tenemos permiso (protección extra)
            se.printStackTrace()
            return
        }

        mostrarUbicacionUsuario()
        mostrarDestinosCercanos()
    }

    // ------------------- Ubicación del usuario -------------------

    private fun mostrarUbicacionUsuario() {
        // verificar permiso justo antes de usar fusedLocationClient
        if (!tienePermisos()) {
            pedirPermisos()
            return
        }

        try {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    val miPos = LatLng(location.latitude, location.longitude)
                    googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(miPos, 15f))

                    // Marcador opcional (no duplicar marcadores: opcionalmente limpiar primero)
                    googleMap?.addMarker(
                        MarkerOptions()
                            .position(miPos)
                            .title("Estás aquí")
                    )
                } else {
                    Toast.makeText(requireContext(), "No se pudo obtener la ubicación", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener { ex ->
                ex.printStackTrace()
                Toast.makeText(requireContext(), "Error obteniendo ubicación: ${ex.message}", Toast.LENGTH_SHORT).show()
            }
        } catch (se: SecurityException) {
            se.printStackTrace()
            Toast.makeText(requireContext(), "Permiso no disponible para obtener ubicación", Toast.LENGTH_SHORT).show()
        }
    }

    // ------------------- Destinos cercanos (mock) -------------------

    private fun mostrarDestinosCercanos() {
        googleMap ?: return

        destinosCercanos.forEachIndexed { index, latLng ->
            googleMap?.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .title("Destino turístico ${index + 1}")
            )
        }
    }

    // ------------------- (Opcional) Abrir navegación -------------------

    private fun abrirGoogleMaps(lat: Double, lon: Double) {
        val uri = Uri.parse("google.navigation:q=$lat,$lon")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        intent.setPackage("com.google.android.apps.maps")
        if (intent.resolveActivity(requireContext().packageManager) != null) {
            startActivity(intent)
        } else {
            Toast.makeText(requireContext(), "Google Maps no está instalado", Toast.LENGTH_SHORT).show()
        }
    }
}
