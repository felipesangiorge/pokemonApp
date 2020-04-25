package com.example.pokemonapp

import android.content.Context
import com.example.pokemonapp.data.PokemonRepository
import com.example.pokemonapp.network.PokeApi
import com.example.pokemonapp.network.RetrofitClient

object Injection {


    private var pokeService: PokeApi? = null
    fun providePokeService() = pokeService ?: let {
        pokeService = RetrofitClient().getRetrofit().create(PokeApi::class.java)
        pokeService!!
    }

    private var pokemonRepository: PokemonRepository? = null
    fun providePokemonRepository(context: Context) = pokemonRepository ?: let {
        pokemonRepository = PokemonRepository(
            providePokeService()
        )
        pokemonRepository!!
    }
}