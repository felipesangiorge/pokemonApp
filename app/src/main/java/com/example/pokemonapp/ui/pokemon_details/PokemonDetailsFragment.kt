package com.example.pokemonapp.ui.pokemon_details

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.pokemonapp.Injection
import com.example.pokemonapp.MainActivity
import com.example.pokemonapp.R
import com.example.pokemonapp.extensions.reObserve
import com.example.pokemonapp.utils.DownloadImageTask
import com.example.pokemonapp.utils.addOnTabSelectedListener
import kotlinx.android.synthetic.main.fragment_pokemon_details.*
import java.util.*


class PokemonDetailsFragment() : Fragment() {
    private lateinit var navController: NavController

    private val pokemonDetailsController: PokemonDetailsController by lazy {
        PokemonDetailsController(
            requireContext()
        )
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_pokemon_details, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        navController = findNavController()

        val pokemonName = arguments?.getString("pokemonName") ?: resources.getString(R.string.default_pokemon)

        val viewModel: PokemonDetailsViewModel by viewModels {
            PokemonDetailsViewModel.Factory(
                pokemonName,
                Injection.providePokemonRepository(requireContext())
            )
        }

        rv_moves_ability.setController(pokemonDetailsController)
        rv_moves_ability.layoutManager = GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)

        ll_tab_layout.addOnTabSelectedListener {
            when (it.position) {
                0 -> viewModel.selectedTabChanged(PokemonDetailsContract.ViewState.TabViewSelected.MOVES)
                1 -> viewModel.selectedTabChanged(PokemonDetailsContract.ViewState.TabViewSelected.ABILITIES)
            }
        }

        viewModel.pokemonDetail.reObserve(this, Observer {
            tv_pokemon_name.text = it.name
            tv_weight.text = it.weight.toString()
            tv_height.text = it.height.toString()
            tv_base_experience.text = it.base_experience.toString()
            cb_default_specie.isChecked = it.is_default

            DownloadImageTask(iv_pokemon_image).execute(it.sprites.front_default)
        })

        viewModel.pokemonMoves.reObserve(this, Observer {
            pokemonDetailsController.pokemonMoveResult = it
        })

        viewModel.pokemonAbilities.reObserve(this, Observer {
            pokemonDetailsController.pokemonAbility = it
        })

        viewModel.isPokemonDetailLoading.reObserve(this, Observer {
            if (it) cl_progress_bar.show() else cl_progress_bar.hide()
            if (it) {
                iv_pokemon_image.visibility = View.INVISIBLE
            } else {
                iv_pokemon_image.visibility = View.VISIBLE
            }
        })

        viewModel.tabViewSelected.reObserve(this, Observer {
            pokemonDetailsController.selectedTabView = it
        })

        viewModel.error.reObserve(this, Observer {
            Toast.makeText(requireContext(), it.errorMessage, Toast.LENGTH_LONG).show()
        })
    }
}