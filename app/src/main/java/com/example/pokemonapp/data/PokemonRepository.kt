package com.example.pokemonapp.data

import androidx.lifecycle.LiveData
import com.example.pokemonapp.livedata.NonnullMediatorLiveData
import com.example.pokemonapp.network.*
import com.example.pokemonapp.network.model.Pokemon
import com.example.pokemonapp.network.model.PokemonList


class PokemonRepository(
    private val pokeService: PokeApi
) {

    fun getPokemon(id: Int): LiveData<ApiResponse<Pokemon>> {
        val response = pokeService.getPokemon(id)

        val result = NonnullMediatorLiveData<ApiResponse<Pokemon>>(ApiResponse.createLoading())

        result.addSource(response) {
            when (it) {
                is ApiEmptyResponse -> result.value = ApiResponse.create(1, Throwable())
                is ApiErrorResponse -> result.value = ApiResponse.create(it.errorCode, Throwable(it.errorMessage))
                is ApiSuccessResponse -> result.postValue(it)
                is ApiLoadingResponse -> result.value = ApiResponse.createLoading()
            }
        }

        return result
    }

    fun getPokemonByName(name: String): LiveData<ApiResponse<Pokemon>> {
        val response = pokeService.getPokemonByName(name)

        val result = NonnullMediatorLiveData<ApiResponse<Pokemon>>(ApiResponse.createLoading())

        result.addSource(response) {
            when (it) {
                is ApiEmptyResponse -> result.value = ApiResponse.create(1, Throwable())
                is ApiErrorResponse -> result.value = ApiResponse.create(it.errorCode, Throwable(it.errorMessage))
                is ApiSuccessResponse -> result.postValue(it)
                is ApiLoadingResponse -> result.value = ApiResponse.createLoading()
            }
        }

        return result
    }

    fun getPokemonList(): LiveData<ApiResponse<PokemonList>> {
        val response = pokeService.getPokemonList()

        val result = NonnullMediatorLiveData<ApiResponse<PokemonList>>(ApiResponse.createLoading())

        result.addSource(response) {
            when (it) {
                is ApiEmptyResponse -> result.value = ApiResponse.create(1, Throwable())
                is ApiErrorResponse -> result.value = ApiResponse.create(it.errorCode, Throwable(it.errorMessage))
                is ApiSuccessResponse -> result.postValue(it)
                is ApiLoadingResponse -> result.value = ApiResponse.createLoading()
            }
        }

        return result
    }
}