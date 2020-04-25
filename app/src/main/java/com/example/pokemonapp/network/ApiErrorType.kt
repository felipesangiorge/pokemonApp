package com.example.pokemonapp.network

import androidx.annotation.StringRes
import com.example.pokemonapp.R

enum class ApiErrorType(val code: String?, @StringRes val stringRes: Int) {

    IOAPI_ERROR(null,R.string.errorcode_generic),
    UNKNOW_ERROR(null, R.string.errorcode_generic_unknow),
    NETWORK_ERROR(null, R.string.errorcode_generic_network),
    EMPTY_BODY(null, R.string.errorcode_generic);

    companion object {
        fun get(code: String): ApiErrorType? = ApiErrorType.values().find { it.code == code }
    }
}