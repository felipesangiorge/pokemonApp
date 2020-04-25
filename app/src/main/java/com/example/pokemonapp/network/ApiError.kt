package com.example.pokemonapp.network


sealed class ApiError(
    open val message: String?,
    open val type: ApiErrorType
) {

    data class Normal(override val message: String?, override val type: ApiErrorType) : ApiError(message, type)
    data class Fields(val fieldsToListOfErrorsString: Map<String, List<String>>) : ApiError(null, ApiErrorType.UNKNOW_ERROR)
}