package com.example.pokemonapp.livedata

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.example.pokemonapp.network.ApiResponse
import com.example.pokemonapp.network.ApiSuccessResponse

fun <X, Y> LiveData<X>.map(body: (X) -> Y): LiveData<Y> {
    return Transformations.map(this, body)
}

fun <T> liveDataOf(initialValue: T): MutableLiveData<T> {
    return emptyLiveData<T>().apply { value = initialValue }
}

fun <T> LiveData<T>.distinctUntilChanged(): LiveData<T> {
    return Transformations.distinctUntilChanged(this)
}

fun <X, T, Z> combineLatest(first: LiveData<X>, second: LiveData<T>, combineFunction: (X, T) -> Z): LiveData<Z> {
    val finalLiveData: MediatorLiveData<Z> = MediatorLiveData()

    var firstEmitted = false
    var firstValue: X? = null

    var secondEmitted = false
    var secondValue: T? = null
    finalLiveData.addSource(first) { value ->
        firstEmitted = true
        firstValue = value
        if (firstEmitted && secondEmitted) {
            finalLiveData.value = combineFunction(firstValue!!, secondValue!!)
        }
    }
    finalLiveData.addSource(second) { value ->
        secondEmitted = true
        secondValue = value
        if (firstEmitted && secondEmitted) {
            finalLiveData.value = combineFunction(firstValue!!, secondValue!!)
        }
    }
    return finalLiveData
}

fun <T> LiveData<T>.startWithPreEmission(): LiveData<CombineResult<T>> {
    return this.map { CombineResult.Emission(it) as CombineResult<T> }.startWith(CombineResult.ToBeEmitted())
}

fun <T> LiveData<T>.startWith(startingValue: T?): LiveData<T> {
    val finalLiveData: MediatorLiveData<T> = MediatorLiveData()
    finalLiveData.value = startingValue
    finalLiveData.addSource(this) { source ->
        finalLiveData.value = source
    }
    return finalLiveData
}

sealed class CombineResult<T> {
    class ToBeEmitted<T> : CombineResult<T>()
    data class Emission<T>(val data: T) : CombineResult<T>()

    fun orNull(): T? = when (this) {
        is ToBeEmitted -> null
        is Emission -> this.data
    }
}

fun <T> emptyLiveData(): MutableLiveData<T> {
    return MutableLiveData()
}

fun <X, Y> LiveData<X>.mapNotNull(body: (X) -> Y?): LiveData<Y> {
    return this.map(body).filterNotNull()
}

fun <X, Y> LiveData<X>.switchMap(body: (X) -> LiveData<Y>): LiveData<Y> {
    return Transformations.switchMap(this, body)
}

fun <T> LiveData<T?>.filterNotNull(): LiveData<T> {
    val mutableLiveData: MediatorLiveData<T> = MediatorLiveData()
    mutableLiveData.addSource(this) {
        if(it != null)
            mutableLiveData.value = it
    }
    return mutableLiveData
}

fun <T> LiveData<ApiResponse<T>>.filterSuccess(): LiveData<ApiSuccessResponse<T>> {
    val mutableLiveData: MediatorLiveData<ApiSuccessResponse<T>> = MediatorLiveData()
    mutableLiveData.addSource(this) {
        if(it is ApiSuccessResponse)
            mutableLiveData.value = it
    }
    return mutableLiveData
}