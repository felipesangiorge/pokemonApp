package com.example.pokemonapp.network


data class BaseResponse<T>(
    val data: T?,
    val meta: Meta?,
    val status: Status
) {

    class Meta(
        val pagination: Pagination
    ) {
        class Pagination(
            val total: Int,
            val count: Int,
            val per_page: Int,
            val current_page: Int,
            val total_pages: Int,
            val links: Links?
        ) {
            class Links(
                val prev: String?,
                val next: String?
            )
        }
    }

    data class Status(
        val status: String,
        val code: String,
        val msg: ErrorMessage
    ) {

        sealed class ErrorMessage {
            class Normal(val msg: String) : ErrorMessage()
            class Fields(val fieldsToListOfErrorsString: Map<String, List<String>>) : ErrorMessage()
        }
    }
}