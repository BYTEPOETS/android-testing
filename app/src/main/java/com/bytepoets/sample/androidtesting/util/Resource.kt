package com.bytepoets.sample.androidtesting.util

enum class ResourceState {
    SUCCESS,
    ERROR,
    LOADING
}

data class Resource<out T>(
    val status: ResourceState,
    val data: T? = null,
    val errorMessage: String? = null
) {
    companion object {
        fun <T> success(data: T?): Resource<T> = Resource(ResourceState.SUCCESS, data, null)

        fun <T> error(message: String, data: T? = null): Resource<T> =
            Resource(ResourceState.ERROR, data, message)

        fun <T> loading(data: T? = null): Resource<T> = Resource(ResourceState.LOADING, data, null)
    }
}
