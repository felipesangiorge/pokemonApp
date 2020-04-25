package com.views

import android.widget.TextView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.example.pokemonapp.R

@EpoxyModelClass(layout = R.layout.item_header_title)
abstract class PokemonHeaderTitle : EpoxyModelWithHolder<PokemonHeaderTitle.Holder>() {
    @EpoxyAttribute
    lateinit var headerTitle: String

    override fun bind(holder: Holder) = with(holder) {
        tv_header_title.text = headerTitle
    }

    class Holder : KotlinEpoxyHolder(){
        val tv_header_title by bind<TextView>(R.id.tv_header_title)
    }
}