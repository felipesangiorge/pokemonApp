package com.example.pokemonapp.utils

import androidx.paging.DataSource
import androidx.paging.PositionalDataSource


class SingleItemDataSource<T>(
    val item: T
) : PositionalDataSource<T>() {

    class Factory<T>(val item: T) : DataSource.Factory<Int, T>() {
        override fun create(): DataSource<Int, T> = SingleItemDataSource(item)
    }

    override fun loadRange(params: LoadRangeParams, callback: LoadRangeCallback<T>) {
        callback.onResult(emptyList())
    }

    override fun loadInitial(params: LoadInitialParams, callback: LoadInitialCallback<T>) {
        callback.onResult(listOf(item), 0, 1)
    }
}
