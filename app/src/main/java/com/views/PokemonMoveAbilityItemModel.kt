package com.views

import android.widget.TextView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.example.pokemonapp.R
import com.example.pokemonapp.network.model.PokemonAbility
import com.example.pokemonapp.network.model.PokemonMove

@EpoxyModelClass(layout = R.layout.item_pokemon_move_ability_list)
abstract class PokemonMoveAbilityItemModel : EpoxyModelWithHolder<PokemonMoveAbilityItemModel.Holder>() {

    @EpoxyAttribute
    var pokemonMove: PokemonMove? = null

    @EpoxyAttribute
    var pokemonAbility: PokemonAbility? = null


    override fun bind(holder: Holder): Unit = with(holder) {
        pokemonMove?.let {
            tv_pokemon_move_ability.text = it.move.name
        }
        pokemonAbility?.let {
            tv_pokemon_move_ability.text = it.ability.name
        }
    }

    class Holder : KotlinEpoxyHolder() {
        val tv_pokemon_move_ability by bind<TextView>(R.id.tv_pokemon_move_ability)
    }
}