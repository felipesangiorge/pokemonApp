package com.example.pokemonapp.core


sealed class Resource<out DataType>(
    open val data: DataType?
) {
    data class Loading<out DataType>(override val data: DataType?) : Resource<DataType>(data)

    data class Success<out DataType>(override val data: DataType) : Resource<DataType>(data)

    data class Failure<out DataType>(override val data: DataType?, val error: Error) : Resource<DataType>(data)

    fun <T> mapResourceData(mapper: (DataType) -> T): Resource<T> = when (this) {
        is Loading -> Loading(data?.let { mapper(it) })
        is Success -> Success(mapper(data))
        is Failure -> Failure(data?.let { mapper(it) }, error)
    }

    open class Error(val errorMessage: String?) {

        class Fields(val fieldsToListOfErrorsString: Map<String, List<String>>) : Resource.Error(null)

        override fun toString(): String {
            return "Error(errorMessage=$errorMessage)"
        }
    }
}