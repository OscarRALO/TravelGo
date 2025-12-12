package com.travelgo.ui.theme.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.ValueEventListener
import com.travelgo.R
import com.travelgo.data.local.MockDataProvider
import com.travelgo.data.model.Destination
import com.travelgo.data.repository.FavoritesRealtimeRepository
import com.travelgo.ui.theme.adapters.FavoritesAdapter

class FavoritesFragment : Fragment() {

    private lateinit var rvFavorites: RecyclerView
    private lateinit var tvFavoritesCount: TextView
    private lateinit var emptyState: LinearLayout
    private lateinit var btnExplore: Button
    private lateinit var progressBar: ProgressBar

    private lateinit var adapter: FavoritesAdapter
    private var favoriteDestinations = listOf<Destination>()

    private val realtimeRepo = FavoritesRealtimeRepository()
    private var valueEventListener: ValueEventListener? = null

    companion object {
        private const val TAG = "FavoritesFragment"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d(TAG, "onCreateView")
        return inflater.inflate(R.layout.fragment_favorites, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated")

        initViews(view)
        setupRecyclerView()
        setupButtons()
        observeFavoritesRealtime()
    }

    private fun initViews(view: View) {
        rvFavorites = view.findViewById(R.id.rvFavorites)
        tvFavoritesCount = view.findViewById(R.id.tvFavoritesCount)
        emptyState = view.findViewById(R.id.emptyStateFavorites)
        btnExplore = view.findViewById(R.id.btnExplore)
        progressBar = view.findViewById(R.id.progressBarFavorites)
    }

    private fun setupRecyclerView() {
        adapter = FavoritesAdapter(
            onItemClick = { destination -> openDestinationDetail(destination) },
            onRemoveFavorite = { destination -> removeFavorite(destination) }
        )
        rvFavorites.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@FavoritesFragment.adapter
            setHasFixedSize(true)
        }
        Log.d(TAG, "RecyclerView configurado")
    }

    private fun setupButtons() {
        btnExplore.setOnClickListener {
            Log.d(TAG, "btnExplore clicked")
            parentFragmentManager.beginTransaction().replace(R.id.fragment_container, HomeFragment()).commit()
        }
    }

    private fun observeFavoritesRealtime() {
        showLoading(true)
        Log.d(TAG, "observeFavoritesRealtime: Iniciando...")

        valueEventListener = realtimeRepo.observeFavorites(
            onDataChange = { favorites ->
                Log.d(TAG, "observeFavoritesRealtime: Datos recibidos - ${favorites.size} favoritos")

                val enrichedFavorites = favorites.map { favorite ->
                    val localDestination = MockDataProvider.getDestinationById(favorite.id)
                    if (localDestination != null) {
                        favorite.apply {
                            imagenPrincipal = localDestination.imagenPrincipal
                            imagenes = localDestination.imagenes
                        }
                    } else {
                        Log.w(TAG, "No se encontro destino local para id=${favorite.id}")
                        favorite
                    }
                }

                favoriteDestinations = enrichedFavorites
                adapter.submitList(enrichedFavorites)
                updateFavoritesCount()
                showEmptyState(enrichedFavorites.isEmpty())
                showLoading(false)
            },
            onError = { exception ->
                Log.e(TAG, "observeFavoritesRealtime: ERROR - ${exception.message}", exception)
                showLoading(false)
                showEmptyState(true)
                Toast.makeText(requireContext(), "Error: ${exception.message}", Toast.LENGTH_LONG).show()
            }
        )

        if (valueEventListener == null) {
            Log.e(TAG, "No se pudo crear el listener")
            showLoading(false)
            showEmptyState(true)
        }
    }

    private fun openDestinationDetail(destination: Destination) {
        Log.d(TAG, "openDestinationDetail: ${destination.nombre}")
        val bundle = Bundle().apply { putString("destinationId", destination.id) }
        val fragment = DestinationDetailFragment()
        fragment.arguments = bundle
        parentFragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack(null).commit()
    }

    private fun removeFavorite(destination: Destination) {
        Log.d(TAG, "removeFavorite: ${destination.nombre} (${destination.id})")
        realtimeRepo.removeFavorite(
            destination.id,
            onSuccess = {
                Log.d(TAG, "Favorito eliminado")
                Toast.makeText(requireContext(), "Eliminado de favoritos", Toast.LENGTH_SHORT).show()
            },
            onFailure = { exception ->
                Log.e(TAG, "Error eliminando: ${exception.message}", exception)
                Toast.makeText(requireContext(), "Error: ${exception.message}", Toast.LENGTH_LONG).show()
            }
        )
    }

    private fun updateFavoritesCount() {
        val count = favoriteDestinations.size
        tvFavoritesCount.text = when (count) {
            0 -> "No tienes favoritos"
            1 -> "1 destino guardado"
            else -> "$count destinos guardados"
        }
        Log.d(TAG, "updateFavoritesCount: $count")
    }

    private fun showLoading(show: Boolean) {
        progressBar.visibility = if (show) View.VISIBLE else View.GONE
        rvFavorites.visibility = if (show) View.GONE else View.VISIBLE
    }

    private fun showEmptyState(show: Boolean) {
        emptyState.visibility = if (show) View.VISIBLE else View.GONE
        rvFavorites.visibility = if (show) View.GONE else View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(TAG, "onDestroyView: Limpiando listener")
        valueEventListener?.let { listener ->
            try {
                val uid = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid
                if (uid != null) {
                    com.google.firebase.database.FirebaseDatabase.getInstance().reference.child("users").child(uid).child("favoritos").removeEventListener(listener)
                    Log.d(TAG, "Listener removido")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error removiendo listener: ${e.message}", e)
            }
        }
        valueEventListener = null
    }
}