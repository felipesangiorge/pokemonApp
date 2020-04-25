package com.example.pokemonapp.ui.home

import com.airbnb.epoxy.EpoxyAsyncUtil
import com.airbnb.epoxy.EpoxyController
import com.example.pokemonapp.network.model.Pokemon
import com.example.pokemonapp.network.model.PokemonListResult
import com.views.EpoxyModelProperty
import com.views.headerToKeepEpoxyScrollOnTop
import com.views.pokemonItem

class HomeController(
    private val pokemonClicked: (name: String) -> Unit
) : EpoxyController(
    EpoxyAsyncUtil.getAsyncBackgroundHandler(),
    EpoxyAsyncUtil.getAsyncBackgroundHandler()
) {

    var pokemonListResult: List<PokemonListResult> by EpoxyModelProperty { emptyList<PokemonListResult>() }

    override fun buildModels() {
        headerToKeepEpoxyScrollOnTop {
            id("headerToKeepScrollOnTop")
        }

        pokemonListResult.forEach {
            pokemonItem {
                id(it.name)
                pokemonItem(it)
                pokemonClicked {
                    pokemonClicked.invoke(it)
                }
            }
        }
    }
}