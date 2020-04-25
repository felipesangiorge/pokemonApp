package com.views

import android.widget.TextView
import androidx.cardview.widget.CardView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.example.pokemonapp.R
import com.example.pokemonapp.network.model.PokemonListResult

@EpoxyModelClass(layout = R.layout.item_pokemon_list)
abstract class PokemonItemModel : EpoxyModelWithHolder<PokemonItemModel.Holder>() {

    @EpoxyAttribute
    lateinit var pokemonItem: PokemonListResult

    @EpoxyAttribute
    lateinit var pokemonClicked: (String) -> Unit

    override fun bind(holder: Holder): Unit = with(holder) {
        tv_pokemon_name.text = pokemonItem.name
        cv_pokemon.setOnClickListener {
            pokemonClicked.invoke(pokemonItem.name)
        }
    }

    class Holder : KotlinEpoxyHolder() {
        val tv_pokemon_name by bind<TextView>(R.id.tv_pokemon_name)
        val cv_pokemon by bind<CardView>(R.id.cv_pokemon)
    }
}