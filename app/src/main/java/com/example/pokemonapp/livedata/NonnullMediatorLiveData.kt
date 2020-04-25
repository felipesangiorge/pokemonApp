package com.example.pokemonapp.livedata

import android.os.Looper
import androidx.lifecycle.MediatorLiveData

@Suppress("RedundantOverride") // We actually need this to force value to be NonNull
class NonnullMediatorLiveData<T : Any>(
    private val data: T
) : MediatorLiveData<T>() {
    init {
        // use post instead of set since this can be created on any thread
        if (Looper.myLooper() == Looper.getMainLooper()) {
            value = data
        } else {
            postValue(data)
        }
    }

    override fun postValue(value: T) {
        super.postValue(value)
    }

    override fun setValue(value: T) {
        super.setValue(value)
    }

    override fun getValue(): T {
        return super.getValue() ?: data
    }
}