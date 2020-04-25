package com.example.pokemonapp.ui.home

import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import com.example.pokemonapp.core.Resource
import com.example.pokemonapp.network.model.PokemonListResult
import java.lang.Error

interface HomeContract{

    interface ViewModel: ViewState, ViewAction

    interface ViewState{
        val error: LiveData<Resource.Error>
        val navigation: LiveData<ViewInstructions>
        val pokemonList : LiveData<List<PokemonListResult>>
        val isPokemonListLoading: LiveData<Boolean>
    }

    interface ViewAction{
        fun pokemonClicked(name: String)
    }

    sealed class ViewInstructions{
        data class NavigateToPokemonDetails(val name: String): ViewInstructions()
    }
}