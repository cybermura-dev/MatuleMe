package ru.takeshiko.matuleme.presentation.utils

import io.github.jan.supabase.exceptions.BadRequestRestException
import io.github.jan.supabase.exceptions.HttpRequestException
import io.github.jan.supabase.exceptions.NotFoundRestException
import io.github.jan.supabase.exceptions.RestException
import io.github.jan.supabase.exceptions.SupabaseEncodingException
import io.github.jan.supabase.exceptions.UnauthorizedRestException
import io.github.jan.supabase.exceptions.UnknownRestException
import io.ktor.client.plugins.HttpRequestTimeoutException
import kotlinx.coroutines.TimeoutCancellationException
import ru.takeshiko.matuleme.R
import ru.takeshiko.matuleme.domain.provider.StringResourceProvider
import java.net.SocketTimeoutException

suspend fun <T> StringResourceProvider.safeSupabaseCall(
    call: suspend () -> T
): Result<T> = runCatching {
    var lastException: Throwable? = null
    val retryCount = 2

    for (attempt in 0..retryCount) {
        try {
            return@runCatching call()
        } catch (e: Exception) {
            if (e is BadRequestRestException ||
                e is UnauthorizedRestException ||
                e is SupabaseEncodingException ||
                e is NotFoundRestException
            ) {
                throw e
            }

            lastException = e
            if (attempt >= retryCount) {
                throw e
            }

            kotlinx.coroutines.delay((attempt + 1) * 500L)
        }
    }

    throw lastException ?: RuntimeException("Unknown error occurred after retries")

}.mapError { throwable ->
    val errorMessage = when (throwable) {
        is BadRequestRestException ->
            getString(R.string.error_bad_request)
        is UnauthorizedRestException ->
            getString(R.string.error_unauthorized)
        is NotFoundRestException ->
            getString(R.string.error_not_found)
        is UnknownRestException ->
            getString(R.string.error_unknown_rest)
        is SupabaseEncodingException ->
            getString(R.string.error_encoding)
        is RestException ->
            getString(R.string.error_rest_generic)
        is HttpRequestException ->
            getString(R.string.error_network)
        is HttpRequestTimeoutException,
        is TimeoutCancellationException,
        is SocketTimeoutException ->
            getString(R.string.error_timeout)
        else ->
            throwable.localizedMessage ?: throwable.message ?: getString(R.string.error_generic)
    }
    IllegalArgumentException(errorMessage, throwable)
}

fun <T> Result<T>.mapError(mapper: (Throwable) -> Throwable): Result<T> =
    fold(
        onSuccess = { Result.success(it) },
        onFailure = { Result.failure(mapper(it)) }
    )