package com.example.pokemonapp.core


sealed class Resource<out DataType>(
    open val data: DataType?
) {
    data class Loading<out DataType>(override val data: DataType?) : Resource<DataType>(data)

    data class Success<out DataType>(override val data: DataType) : Resource<DataType>(data)

    data class Failure<out DataType>(override val data: DataType?, val error: Error) : Resource<DataType>(data)

    //    class Error(val errorMessage: String) {
    //        var exception: Throwable? = null
    //        // FIXME: We should have a more meaningful default message
    //        constructor(throwable: Throwable?) : this(throwable?.localizedMessage ?: "Unknown error") {
    //            exception = throwable
    //        }
    //    }

    fun <T> mapResourceData(mapper: (DataType) -> T): Resource<T> = when (this) {
        is Loading -> Loading(data?.let { mapper(it) })
        is Success -> Success(mapper(data))
        is Failure -> Failure(data?.let { mapper(it) }, error)
    }

    open class Error(val errorMessage: String?) {

        class Fields(val fieldsToListOfErrorsString: Map<String, List<String>>) : Resource.Error(null)

        // FIXME: Callers of these constructors should be replaced to call primary constructor by creating new ApiErrorType

        override fun toString(): String {
            return "Error(errorMessage=$errorMessage)"
        }
    }
}