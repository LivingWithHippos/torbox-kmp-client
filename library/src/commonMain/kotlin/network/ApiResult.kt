package com.github.livingwithhippos.torbox.kmp.client.network

sealed interface ApiResult<out T> {
    data class Success<T>(val value: T) : ApiResult<T>
    sealed interface Failure : ApiResult<Nothing> {
        data class Http(val code: Int, val body: String?) : Failure
        data class BadRequest(val cause: Throwable) : Failure
        data class Network(val cause: Throwable) : Failure
        data class Serialization(val cause: Throwable) : Failure
        data class Unknown(val cause: Throwable) : Failure
    }
}