package com.example.pokemonapp.ui.pokemon_details

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import com.example.pokemonapp.core.Resource
import com.example.pokemonapp.network.model.Language
import com.example.pokemonapp.network.model.Pokemon
import com.example.pokemonapp.network.model.PokemonAbility
import com.example.pokemonapp.network.model.PokemonMove

interface PokemonDetailsContract {

    interface ViewModel : ViewState, ViewActions

    interface ViewState {
        val error: LiveData<Resource.Error>
        val navigation: LiveData<ViewInstructions>
        val pokemonDetail: LiveData<Pokemon>
        val pokemonMoves: LiveData<List<PokemonMove>>
        val pokemonAbilities: LiveData<List<PokemonAbility>>
        val isPokemonDetailLoading: LiveData<Boolean>
        val tabViewSelected: LiveData<TabViewSelected>

        enum class TabViewSelected{
            MOVES,ABILITIES
        }
    }

    interface ViewActions {
        fun selectedTabChanged(position: ViewState.TabViewSelected)
    }

    sealed class ViewInstructions {

    }
}