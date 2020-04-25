package com.example.pokemonapp.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.airbnb.epoxy.EpoxyAsyncUtil
import com.example.pokemonapp.Injection
import com.example.pokemonapp.R
import com.example.pokemonapp.extensions.reObserve
import com.example.pokemonapp.utils.toVisibility
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment() {

    private lateinit var navController: NavController

    private val viewModel by viewModels<HomeViewModel> {
        HomeViewModel.Factory(
            Injection.providePokemonRepository(requireContext())
        )
    }

    private val homeController: HomeController by lazy {
        HomeController(
            viewModel::pokemonClicked
        )
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        navController = findNavController()

        rv_pokemon_item.setController(homeController)

        viewModel.navigation.reObserve(this, Observer {
            when (it) {
                is HomeContract.ViewInstructions.NavigateToPokemonDetails -> navigateToPokemonDetails(it.name)
            }
        })

        viewModel.isPokemonListLoading.reObserve(this, Observer {
            rv_pokemon_item.toVisibility = !it
            cl_progress_bar.toVisibility = it
        })

        viewModel.pokemonList.reObserve(this, Observer {
            homeController.pokemonListResult = it
        })

        viewModel.error.reObserve(this, Observer {
            Toast.makeText(requireContext(), it.errorMessage, Toast.LENGTH_LONG).show()
        })
    }

    private fun navigateToPokemonDetails(name: String) {
        val bundle = Bundle()
        bundle.putString("pokemonName", name)

        if (navController.currentDestination?.id == R.id.navigation_home) {
            navController.navigate(R.id.action_navigation_home_to_pokemon_details_fragment,bundle)
        }
    }
}