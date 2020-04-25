package com.example.pokemonapp.ui.pokemon_details

import android.content.Context
import com.airbnb.epoxy.EpoxyAsyncUtil
import com.airbnb.epoxy.EpoxyController
import com.example.pokemonapp.network.model.PokemonAbility
import com.example.pokemonapp.network.model.PokemonMove
import com.views.EpoxyModelProperty
import com.views.headerToKeepEpoxyScrollOnTop
import com.views.pokemonMoveAbilityItem

class PokemonDetailsController(
    private val context: Context
) : EpoxyController(
    EpoxyAsyncUtil.getAsyncBackgroundHandler(),
    EpoxyAsyncUtil.getAsyncBackgroundHandler()
) {
    var selectedTabView: PokemonDetailsContract.ViewState.TabViewSelected by EpoxyModelProperty { PokemonDetailsContract.ViewState.TabViewSelected.MOVES }

    var pokemonMoveResult: List<PokemonMove> by EpoxyModelProperty { emptyList<PokemonMove>() }

    var pokemonAbility: List<PokemonAbility> by EpoxyModelProperty { emptyList<PokemonAbility>() }

    override fun buildModels() {

        when (selectedTabView) {
            PokemonDetailsContract.ViewState.TabViewSelected.MOVES -> {
                pokemonMoveResult.forEach {
                    pokemonMoveAbilityItem {
                        id(it.move.name)
                        pokemonMove(it)
                    }
                }
            }
            PokemonDetailsContract.ViewState.TabViewSelected.ABILITIES -> {
                pokemonAbility.forEach {
                    pokemonMoveAbilityItem {
                        id(it.ability.name)
                        pokemonAbility(it)
                    }
                }
            }
        }
    }
}