package com.example.pokemonapp.ui.pokemon_details

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.pokemonapp.core.Resource
import com.example.pokemonapp.data.PokemonRepository
import com.example.pokemonapp.livedata.filterSuccess
import com.example.pokemonapp.livedata.map
import com.example.pokemonapp.network.ApiErrorResponse
import com.example.pokemonapp.network.ApiLoadingResponse
import com.example.pokemonapp.network.model.Pokemon
import com.example.pokemonapp.network.model.PokemonAbility
import com.example.pokemonapp.network.model.PokemonMove

class PokemonDetailsViewModel (
    pokemonName: String,
    pokemonRepository: PokemonRepository
): ViewModel(), PokemonDetailsContract.ViewModel{

    private val _navigation = MediatorLiveData<PokemonDetailsContract.ViewInstructions>()
    override val navigation: LiveData<PokemonDetailsContract.ViewInstructions>
        get() = _navigation

    private val _error = MediatorLiveData<Resource.Error>()
    override val error: LiveData<Resource.Error>
        get() = _error

    private val getPokemonDetailData = pokemonRepository.getPokemonByName(pokemonName)
    override val pokemonDetail: LiveData<Pokemon> = getPokemonDetailData.filterSuccess().map {
        it.body
    }

    override val pokemonMoves: LiveData<List<PokemonMove>> = getPokemonDetailData.filterSuccess().map {
        it.body.moves
    }

    override val pokemonAbilities: LiveData<List<PokemonAbility>> = getPokemonDetailData.filterSuccess().map {
        it.body.abilities
    }

    override val isPokemonDetailLoading: LiveData<Boolean> = getPokemonDetailData.map { it is ApiLoadingResponse }

    private val _tabViewSelectedAction = MediatorLiveData<PokemonDetailsContract.ViewState.TabViewSelected>()
    override val tabViewSelected: LiveData<PokemonDetailsContract.ViewState.TabViewSelected>
        get() = _tabViewSelectedAction

    init {
        _error.addSource(getPokemonDetailData){
            if(it is ApiErrorResponse)
                _error.value = Resource.Error(it.errorMessage)
        }
    }

    override fun selectedTabChanged(position: PokemonDetailsContract.ViewState.TabViewSelected) {
        _tabViewSelectedAction.value = position
    }

    class Factory(
        private val pokemonName: String,
        private val pokemonRepository:PokemonRepository
    ): ViewModelProvider.Factory{
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return PokemonDetailsViewModel(
                pokemonName,
                pokemonRepository
            ) as T
        }
    }
}