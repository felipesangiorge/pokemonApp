package com.example.pokemonapp.extensions

import android.os.Handler
import android.os.Looper
import androidx.annotation.MainThread
import androidx.lifecycle.*
import androidx.lifecycle.Observer
import com.example.pokemonapp.core.Resource
import com.snakydesign.livedataextensions.*
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.Executor
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import kotlin.properties.Delegates


// https://medium.com/@BladeCoder/architecture-components-pitfalls-part-1-9300dd969808
@MainThread
fun <T> LiveData<T>.reObserve(owner: LifecycleOwner, observer: Observer<T>) {
    removeObserver(observer)
    observe(owner, observer)
}

fun <T> MutableLiveData<T>.setValueIfNew(newValue: T): Boolean = if (this.value != newValue) {
    value = newValue
    true
} else {
    false
}

fun <T> MutableLiveData<T>.postValueIfNew(newValue: T): Boolean = if (this.value != newValue) {
    postValue(newValue)
    true
} else {
    false
}

@MainThread
fun <T> LiveData<T>.observeOnce(observer: Observer<T>) {
    observeForever(object : Observer<T> {
        override fun onChanged(t: T?) {
            observer.onChanged(t)
            removeObserver(this)
        }
    })
}

@MainThread
fun <T> MediatorLiveData<*>.addSourceOnce(source: LiveData<T>, body: (T?) -> Unit) {
    this.addSource(source) {
        this.removeSource(source)
        body(it)
    }
}

@MainThread
fun <T> LiveData<Resource<T>>.asCompletable(): LiveData<Resource<T>> {
    val mediator = MediatorLiveData<Resource<T>>()

    mediator.addSource(this) {
        when (it) {
            is Resource.Loading -> mediator.value = it
            else -> {
                mediator.removeSource(this)
                mediator.value = it
            }
        }
    }

    return mediator
}


@MainThread
fun <T : Any> LiveData<T>.throttleWithTimeout(
    delay: Long,
    timeUnit: TimeUnit,
    handler: Handler
): LiveData<T> {
    val mediator = MediatorLiveData<T>()

    var lastValue: T by Delegates.notNull()

    val callback = Runnable {
        mediator.value = lastValue
    }

    mediator.addSource(this) {
        handler.removeCallbacks(callback)

        lastValue = it

        val delayMillis = timeUnit.toMillis(delay)
        handler.postDelayed(callback, delayMillis)
    }

    return mediator
}

@MainThread
fun <T : Any> LiveData<T>.throttleWithTimeoutOld(
    delay: Long,
    timeUnit: TimeUnit,
    handler: Handler
): LiveData<T> {
    val mediator = MediatorLiveData<T>()

    val lastEmissionTimeUnit = TimeUnit.MILLISECONDS
    var lastEmission: Long = 0
    var lastValue: T by Delegates.notNull()

    val callback = Runnable {
        lastEmission = System.currentTimeMillis()
        mediator.value = lastValue
    }

    mediator.addSource(this) {
        handler.removeCallbacks(callback)

        lastValue = it

        val delayMillis = timeUnit.toMillis(delay)
        val timeElapsed = System.currentTimeMillis() - lastEmissionTimeUnit.toMillis(lastEmission)
        if (timeElapsed > delayMillis) {
            handler.post(callback)
        } else {
            handler.postDelayed(callback, delayMillis - timeElapsed)
        }
    }

    return mediator
}

//@MainThread
//fun <T : Any> LiveData<T>.throttleWithTimeout(
//    delay: Long,
//    timeUnit: TimeUnit,
//    handler: Handler
//): LiveData<T> {
//    val mediator = MediatorLiveData<T>()
//
//    val lastEmissionTimeUnit = TimeUnit.MILLISECONDS
//    var lastEmission: Long = 0
//    var currentValue: Optional<T> by Delegates.notNull()
//
//    val callback = Runnable {
//        val value = currentValue.orNull() ?: return@Runnable
//
//        lastEmission = System.currentTimeMillis()
//        mediator.value = value
//    }
//
//    mediator.addSource(this) {
//        handler.removeCallbacks(callback)
//
//        currentValue = Optional.ofNullable(it)
//
//        val delayMillis = timeUnit.toMillis(delay)
//        val timeElapsed = System.currentTimeMillis() - lastEmissionTimeUnit.toMillis(lastEmission)
//        if (timeElapsed > delayMillis) {
//            handler.post(callback)
//        } else {
//            handler.postDelayed(callback, delayMillis - timeElapsed)
//        }
//    }
//
//    return mediator
//}

@MainThread
fun <T> List<LiveData<T>>.waitForAllOnce(observer: Observer<List<T?>>) {
    val remainingCount = AtomicInteger(this.size)
    val results = CopyOnWriteArrayList<T?>()

    this.forEach {
        it.observeOnce(Observer {
            results.add(it)

            if (remainingCount.decrementAndGet() == 0) {
                observer.onChanged(results.toList())
            }
        })
    }
}

@MainThread
fun <T> MediatorLiveData<*>.waitForAllOnce(sources: List<LiveData<T>>, body: (List<T?>) -> Unit) {
    val remainingCount = AtomicInteger(sources.size)
    val results = CopyOnWriteArrayList<T?>()

    sources.forEach {
        this.addSourceOnce(it) {
            results.add(it)

            if (remainingCount.decrementAndGet() == 0) {
                body(results.toList())
            }
        }
    }
}

@MainThread
fun <T> LiveData<T>.debounce(duration: Long = 1000L) = MediatorLiveData<T>().also { mld ->
    val source = this
    val handler = Handler(Looper.getMainLooper())

    val runnable = Runnable {
        mld.value = source.value
    }

    mld.addSource(source) {
        handler.removeCallbacks(runnable)
        handler.postDelayed(runnable, duration)
    }
}

@MainThread
fun <T> LiveData<T>.conditionalDebounce(
    debounce: Long = 1000L,
    debounceItem: (T) -> Boolean
): LiveData<T> =
    MediatorLiveData<T>().also { output ->
        val source = this
        val handler = Handler(Looper.getMainLooper())

        val runnable = Runnable {
            output.value = source.value
        }

        output.addSource(source) {
            handler.removeCallbacks(runnable)
            handler.postDelayed(runnable, if (debounceItem(it)) debounce else 0)
        }
    }


fun <T> LiveData<Resource<T>>.filterSuccess(): LiveData<Resource.Success<T>> {
    val mutableLiveData: MediatorLiveData<Resource.Success<T>> = MediatorLiveData()
    mutableLiveData.addSource(this) {
        if(it is Resource.Success)
            mutableLiveData.value = it
    }
    return mutableLiveData
}

fun <T> LiveData<Resource<T>>.filterFailure(): LiveData<Resource.Failure<T>> {
    val mutableLiveData: MediatorLiveData<Resource.Failure<T>> = MediatorLiveData()
    mutableLiveData.addSource(this) {
        if(it is Resource.Failure)
            mutableLiveData.value = it
    }
    return mutableLiveData
}


fun <T : Any> LiveData<T>.scanWithFirstValue(accumulator: (accumulatedValue: T, currentValue: T) -> T): MutableLiveData<T> {
    var accumulatedValue: T by Delegates.notNull<T>()
    var hasEmittedFirst = false
    return MediatorLiveData<T>().apply {
        addSource(this@scanWithFirstValue) { emittedValue ->
            if (!hasEmittedFirst) {
                hasEmittedFirst = true
                accumulatedValue = accumulator(emittedValue, emittedValue)
            } else {
                accumulatedValue = accumulator(accumulatedValue, emittedValue)
            }
            value = accumulatedValue
        }
    }
}

fun <T> LiveData<T>.ignoreFirstValueWhen(predicate: (T) -> Boolean): LiveData<T> {
    val mediator = MediatorLiveData<T>()
    var isFirstEmission = true

    mediator.addSource(this) {
        if (!isFirstEmission) {
            mediator.value = it
            return@addSource
        }

        if (!predicate(it)) {
            mediator.value = it
        }

        isFirstEmission = false
    }

    return mediator
}


//fun <T> LiveData<Resource<T>>.doOnSuccess(
//    executor: Executor? = null,
//    predicate: (Resource.Success<T>) -> Unit
//): LiveData<Resource<T>> = when (executor) {
//    null -> this.doOnNext { if (it is Resource.Success) predicate(it) }
//    else -> this.switchMap {
//        when (it) {
//            is Resource.Success -> MutableLiveData<Resource<T>>().apply {
//                executor.execute {
//                    predicate(it)
//                    postValue(it)
//                }
//            }
//            else -> liveDataOf(it)
//        }
//    }
//}



