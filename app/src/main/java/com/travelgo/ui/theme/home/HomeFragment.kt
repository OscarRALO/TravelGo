package com.travelgo.ui.theme.home

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.snackbar.Snackbar
import com.travelgo.R
import com.travelgo.data.local.FavoritesPreferences
import com.travelgo.data.local.MockDataProvider
import com.travelgo.data.model.Destination
import com.travelgo.ui.theme.adapters.DestinationsAdapter


class HomeFragment : Fragment() {
    private lateinit var rvDestinations: RecyclerView
    private lateinit var etSearch: EditText
    private lateinit var chipGroupCategories: ChipGroup
    private lateinit var progressBar: ProgressBar
    private lateinit var emptyState: LinearLayout
    private lateinit var adapter: DestinationsAdapter
    private lateinit var favoritesPreferences: FavoritesPreferences
    private var allDestinations = listOf<Destination>()
    private var filteredDestinations = listOf<Destination>()
    private var currentCategory = "Todos"

    /**
     * Fragment - Ciclo de vida
     * onCreateView: Inflar el layout del fragment
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        favoritesPreferences = FavoritesPreferences.getInstance(requireContext())
        initViews(view)
        setupRecyclerView()
        setupSearchView()
        setupCategoryChips()
        loadDestinations()
    }


    private fun initViews(view: View) {
        rvDestinations = view.findViewById(R.id.rvDestinations)
        etSearch = view.findViewById(R.id.etSearch)
        chipGroupCategories = view.findViewById(R.id.chipGroupCategories)
        progressBar = view.findViewById(R.id.progressBar)
        emptyState = view.findViewById(R.id.emptyState)
    }

    /**
     * RecyclerView
     */
    private fun setupRecyclerView() {

        adapter = DestinationsAdapter(
            onItemClick = { destination ->
                openDestinationDetail(destination)
            },
            onFavoriteClick = { destination ->
                toggleFavorite(destination)
            }
        )

        // RecyclerView - Configurar LayoutManager y Adapter
        rvDestinations.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@HomeFragment.adapter
            setHasFixedSize(true)
        }
    }


    private fun setupSearchView() {
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterDestinations(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }


    private fun setupCategoryChips() {

        chipGroupCategories.setOnCheckedStateChangeListener { group, checkedIds ->
            if (checkedIds.isEmpty()) {
                // Si no hay nada seleccionado, seleccionar "Todos"
                currentCategory = "Todos"
                view?.findViewById<Chip>(R.id.chipAll)?.isChecked = true
            } else {
                // Obtener el chip seleccionado
                val selectedChip = view?.findViewById<Chip>(checkedIds.first())
                currentCategory = when (selectedChip?.id) {
                    R.id.chipAll -> "Todos"
                    R.id.chipPlaya -> "Playa"
                    R.id.chipMontana -> "Montaña"
                    R.id.chipCiudad -> "Ciudad"
                    R.id.chipHistorico -> "Histórico"
                    R.id.chipNaturaleza -> "Naturaleza"
                    else -> "Todos"
                }

                filterByCategory(currentCategory)
            }
        }
    }


    private fun loadDestinations() {
        showLoading(true)


        view?.postDelayed({
            // Cargar datos desde MockDataProvider
            allDestinations = MockDataProvider.getDestinations()
            filteredDestinations = allDestinations

            // Actualizar adapter
            adapter.submitList(filteredDestinations)

            // Actualizar favoritos
            updateFavorites()

            // Ocultar loading y verificar si hay datos
            showLoading(false)
            showEmptyState(filteredDestinations.isEmpty())

        }, 500)
    }


    private fun filterDestinations(query: String) {
        if (query.isBlank()) {

            filterByCategory(currentCategory)
            return
        }

        // Buscar en MockDataProvider
        filteredDestinations = MockDataProvider.searchDestinations(query)


        if (currentCategory != "Todos") {
            filteredDestinations = filteredDestinations.filter {
                it.categoria == currentCategory
            }
        }

        adapter.submitList(filteredDestinations)
        showEmptyState(filteredDestinations.isEmpty())
    }


    private fun filterByCategory(category: String) {
        filteredDestinations = MockDataProvider.getDestinationsByCategory(category)


        val searchQuery = etSearch.text.toString()
        if (searchQuery.isNotBlank()) {
            filteredDestinations = filteredDestinations.filter {
                it.nombre.contains(searchQuery, ignoreCase = true) ||
                        it.ciudad.contains(searchQuery, ignoreCase = true) ||
                        it.descripcion.contains(searchQuery, ignoreCase = true)
            }
        }

        adapter.submitList(filteredDestinations)
        showEmptyState(filteredDestinations.isEmpty())
    }


    private fun openDestinationDetail(destination: Destination) {

        val bundle = Bundle().apply {
            putString("destinationId", destination.id)
            putString("name", destination.nombre)
            putString("location", destination.getUbicacionCompleta())
            putString("description", destination.descripcionDetallada)
            putFloat("rating", destination.rating)
            putString("price", destination.precio)
            putString("duration", destination.duracion)
            putInt("image", destination.imagenPrincipal)
            putString("category", destination.categoria)
        }


        val fragment = DestinationDetailFragment()
        fragment.arguments = bundle

        // Fragment - Transición entre fragments
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }


    private fun toggleFavorite(destination: Destination) {
        val isFavorite = favoritesPreferences.toggleFavorite(destination.id)


        updateFavorites()

        val message = if (isFavorite) {
            "✅ Agregado a favoritos"
        } else {
            "❌ Eliminado de favoritos"
        }

        // TEMA: Context Activity - Usar requireView() para SnackBar
        Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT)
            .setAction("Ver") {
                // Navegar a FavoritesFragment
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, FavoritesFragment())
                    .addToBackStack(null)
                    .commit()
            }
            .show()
    }


    private fun updateFavorites() {
        val favoriteIds = favoritesPreferences.getFavoriteIds()
        adapter.updateFavorites(favoriteIds)
    }


    private fun showLoading(show: Boolean) {
        progressBar.visibility = if (show) View.VISIBLE else View.GONE
        rvDestinations.visibility = if (show) View.GONE else View.VISIBLE
    }


    private fun showEmptyState(show: Boolean) {
        emptyState.visibility = if (show) View.VISIBLE else View.GONE
        rvDestinations.visibility = if (show) View.GONE else View.VISIBLE
    }


    override fun onResume() {
        super.onResume()
        updateFavorites()
    }
}