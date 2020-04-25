package com.example.pokemonapp.ui.home

import androidx.lifecycle.*
import com.example.pokemonapp.core.Resource
import com.example.pokemonapp.data.PokemonRepository
import com.example.pokemonapp.livedata.filterSuccess
import com.example.pokemonapp.livedata.map
import com.example.pokemonapp.livedata.switchMap
import com.example.pokemonapp.network.ApiEmptyResponse
import com.example.pokemonapp.network.ApiErrorResponse
import com.example.pokemonapp.network.ApiLoadingResponse
import com.example.pokemonapp.network.ApiSuccessResponse
import com.example.pokemonapp.network.model.PokemonListResult

class HomeViewModel(
    private val pokemonRepository: PokemonRepository
) : ViewModel(), HomeContract.ViewModel {

    private val _navigation = MediatorLiveData<HomeContract.ViewInstructions>()
    override val navigation: LiveData<HomeContract.ViewInstructions>
        get() = _navigation

    private val _error = MediatorLiveData<Resource.Error>()
    override val error: LiveData<Resource.Error>
        get() = _error

    private val getPokemonByIdAction = MutableLiveData<Int>()
    private val getPokemonByIdData = getPokemonByIdAction.switchMap {
        pokemonRepository.getPokemon(it)
    }

    private val getPokemonByNameAction = MutableLiveData<String>()
    private val getPokemonByNameData = getPokemonByNameAction.switchMap {
        pokemonRepository.getPokemonByName(it)
    }

    private val pokemonListData = pokemonRepository.getPokemonList()
    override val pokemonList: LiveData<List<PokemonListResult>> = pokemonListData.filterSuccess().map {
        it.body.results
    }

    override val isPokemonListLoading: LiveData<Boolean> = pokemonListData.map {
        it is ApiLoadingResponse
    }


    init {
        _error.addSource(getPokemonByIdData) {
            if (it is ApiErrorResponse)
                _error.value = Resource.Error(it.errorMessage)
        }

        _error.addSource(getPokemonByNameData) {
            when (it) {
                is ApiEmptyResponse -> TODO()
                is ApiLoadingResponse -> TODO()
                is ApiErrorResponse -> TODO()
                is ApiSuccessResponse -> TODO()
            }
        }

        _error.addSource(pokemonListData) {
            if (it is ApiErrorResponse)
                _error.value = Resource.Error(it.errorMessage)
        }
    }

    override fun pokemonClicked(name: String) {
        _navigation.value = HomeContract.ViewInstructions.NavigateToPokemonDetails(name)
    }

    @Suppress("UNCHECKED_CAST")
    class Factory(
        private val pokemonRepository: PokemonRepository
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return HomeViewModel(
                pokemonRepository
            ) as T
        }
    }
}