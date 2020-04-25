package com.example.pokemonapp.extensions

import android.os.Handler
import java.util.concurrent.Executor

fun Handler.toExecutor(): Executor = Executor {
    this.post(it)
}