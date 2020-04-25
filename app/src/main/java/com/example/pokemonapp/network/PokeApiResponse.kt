/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.pokemonapp.network

import com.example.pokemonapp.core.Resource
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import java.io.IOException
import java.lang.reflect.Type

/**
 * Common class used by API responses.
 * @param <T> the type of the response object
</T> */
@Suppress("unused") // T is used in extending classes
sealed class PokeApiResponse<T> {

    fun toResource(): Resource<T> = when (this) {
        is PokeApiSuccessResponse -> Resource.Success(this.data)
        is PokeApiErrorResponse -> Resource.Failure(null, Resource.Error(this.apiError.message))
        is PokeErrorResponse -> Resource.Failure(null, Resource.Error(this.apiError.message))
    }

    companion object {
        fun <T> create(error: Throwable, call: Call<BaseResponse<T>>, responseType: Type, retrofit: Retrofit): PokeErrorResponse<T> {
            return when (error) {
                is IOException -> PokeErrorResponse(ApiError.Normal("ERRIO", ApiErrorType.IOAPI_ERROR))
                else -> PokeErrorResponse(ApiError.Normal("ERR", ApiErrorType.UNKNOW_ERROR))
            }
        }

        fun <T> create(response: Response<BaseResponse<T>>, responseType: Type, retrofit: Retrofit): PokeApiResponse<T> {
            return if (response.isSuccessful) {
                val body = response.body()
                processResponse(body, response)
            } else {
                return try {
                    val body = response.errorBody()?.let {
                        retrofit.responseBodyConverter<BaseResponse<T>>(responseType, emptyArray()).convert(it)
                    }
                    processResponse(body, response)
                } catch (ex: Exception) {
                    processResponse(null, response)
                }
            }
        }

        private fun <T> processResponse(body: BaseResponse<T>?, response: Response<BaseResponse<T>>): PokeApiResponse<T> = when {
            response.code() == 401 -> PokeApiErrorResponse(ApiError.Normal("ERR401", ApiErrorType.UNKNOW_ERROR))
            response.code() == 500 -> {
                val errorResponse = response.errorBody()?.string()
                PokeErrorResponse(ApiError.Normal("ERR500", ApiErrorType.UNKNOW_ERROR))
            }
            body == null -> {
                PokeErrorResponse(ApiError.Normal("EMPTY", ApiErrorType.EMPTY_BODY))
            }
            body.status.status == "NOK" -> PokeErrorResponse(ApiError.Normal("NOK", ApiErrorType.UNKNOW_ERROR))
            else -> PokeApiSuccessResponse(
                data = body.data!!,
                meta = body.meta
            )
        }
    }
}

data class PokeApiSuccessResponse<T>(
    val data: T,
    val meta: BaseResponse.Meta?
) : PokeApiResponse<T>()

data class PokeApiErrorResponse<T>(
    val apiError: ApiError
) : PokeApiResponse<T>()

data class PokeErrorResponse<T>(
    val apiError: ApiError
) : PokeApiResponse<T>()
