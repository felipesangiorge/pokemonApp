package com.example.pokemonapp.network

import androidx.lifecycle.LiveData
import com.example.pokemonapp.core.Resource
import com.example.pokemonapp.network.model.*
import retrofit2.http.Field
import retrofit2.http.GET
import retrofit2.http.Path


interface PokeApi {

    companion object{
        const val POKEMON_PAGE_SIZE: Int = 20
        const val API_LIMIT = 20
        const val API_OFFSET = 20
    }

    @GET("pokemon/{id}")
    fun getPokemon(@Path("id") id: Int): LiveData<ApiResponse<Pokemon>>

    @GET("pokemon/{name}")
    fun getPokemonByName(@Path("name") name: String): LiveData<ApiResponse<Pokemon>>

    @GET("pokemon/{name}")
    fun getPokemonByNameAsync(@Path("name") name: String): LiveData<Pokemon>

    @GET("pokemon/?limit=${POKEMON_PAGE_SIZE}&offset=${POKEMON_PAGE_SIZE}\"")
    fun getPokemonList(): LiveData<ApiResponse<PokemonList>>

    @GET("pokemon/?limit={limit}&offset={limit}\"")
    fun getPokemonListSync(@Field("limit") limit: Int): ApiResponse<List<PokemonListResult>>

}