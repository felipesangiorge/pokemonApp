package com.example.pokemonapp.livedata

import android.os.Looper
import androidx.lifecycle.MediatorLiveData

@Suppress("RedundantOverride")
class NonnullMediatorLiveData<T : Any>(
    private val data: T
) : MediatorLiveData<T>() {
    init {
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