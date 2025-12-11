    package com.travelgo.ui.theme.home

    import android.os.Bundle
    import android.view.LayoutInflater
    import android.view.View
    import android.view.ViewGroup
    import android.widget.Button
    import android.widget.LinearLayout
    import android.widget.ProgressBar
    import android.widget.TextView
    import androidx.fragment.app.Fragment
    import androidx.recyclerview.widget.LinearLayoutManager
    import androidx.recyclerview.widget.RecyclerView
    import com.google.android.material.snackbar.Snackbar
    import com.travelgo.R
    import com.travelgo.data.local.FavoritesPreferences
    import com.travelgo.data.model.Destination
    import com.travelgo.data.repository.FavoritesRepository
    import com.travelgo.ui.theme.adapters.FavoritesAdapter
    import android.widget.Toast

    class FavoritesFragment : Fragment() {
        private lateinit var rvFavorites: RecyclerView
        private lateinit var tvFavoritesCount: TextView
        private lateinit var emptyState: LinearLayout
        private lateinit var btnExplore: Button
        private lateinit var progressBar: ProgressBar
        // Adapter y datos
        private lateinit var adapter: FavoritesAdapter
        private lateinit var favoritesPreferences: FavoritesPreferences
        private var favoriteDestinations = listOf<Destination>()
        private val favoritesRepository = FavoritesRepository()




        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            return inflater.inflate(R.layout.fragment_favorites, container, false)
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)

            // TEMA: Context Activity
            favoritesPreferences = FavoritesPreferences.getInstance(requireContext())

            initViews(view)
            setupRecyclerView()
            setupButtons()
            loadFavorites()
        }


        private fun initViews(view: View) {
            rvFavorites = view.findViewById(R.id.rvFavorites)
            tvFavoritesCount = view.findViewById(R.id.tvFavoritesCount)
            emptyState = view.findViewById(R.id.emptyStateFavorites)
            btnExplore = view.findViewById(R.id.btnExplore)
            progressBar = view.findViewById(R.id.progressBarFavorites)
        }


        private fun setupRecyclerView() {
            // TEMA: Evento Clic - Callbacks del adapter
            adapter = FavoritesAdapter(
                onItemClick = { destination ->
                    openDestinationDetail(destination)
                },
                onRemoveFavorite = { destination ->
                    removeFavorite(destination)
                }
            )

            rvFavorites.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = this@FavoritesFragment.adapter
                setHasFixedSize(true)
            }
        }


        private fun setupButtons() {
            btnExplore.setOnClickListener {
                // Navegar al HomeFragment
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, HomeFragment())
                    .commit()
            }
        }


    // Asegúrate de tener instanciado el repositorio arriba:
        // private val favoritesRepository = FavoritesRepository()

        private fun loadFavorites() {
            showLoading(true)

            favoritesRepository.getFavorites(
                onSuccess = { list ->
                    favoriteDestinations = list
                    adapter.submitList(list)
                    updateFavoritesCount()
                    showEmptyState(list.isEmpty())
                    showLoading(false)
                },
                onFailure = {
                    showLoading(false)
                    showEmptyState(true)
                    Toast.makeText(requireContext(), "Error: ${it.message}", Toast.LENGTH_LONG).show()
                }
            )
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

            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }



        private fun removeFavorite(destination: Destination) {

            favoritesRepository.removeFavorite(
                destination.id,
                onSuccess = {
                    favoriteDestinations = favoriteDestinations.filter { it.id != destination.id }
                    adapter.submitList(favoriteDestinations)
                    updateFavoritesCount()
                    showEmptyState(favoriteDestinations.isEmpty())
                },
                onFailure = {
                    Toast.makeText(requireContext(), "Error: ${it.message}", Toast.LENGTH_LONG).show()
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
        }


        private fun showLoading(show: Boolean) {
            progressBar.visibility = if (show) View.VISIBLE else View.GONE
            rvFavorites.visibility = if (show) View.GONE else View.VISIBLE
        }


        private fun showEmptyState(show: Boolean) {
            emptyState.visibility = if (show) View.VISIBLE else View.GONE
            rvFavorites.visibility = if (show) View.GONE else View.VISIBLE
        }


        override fun onResume() {
            super.onResume()
            loadFavorites()
        }
    }