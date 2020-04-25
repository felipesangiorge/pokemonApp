package com.views

import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.example.pokemonapp.R

@EpoxyModelClass(layout = R.layout.item_header_epoxy_scroll_top)
abstract class HeaderToKeepEpoxyScrollOnTop : EpoxyModelWithHolder<HeaderToKeepEpoxyScrollOnTop.Holder>() {
    class Holder : KotlinEpoxyHolder()
}