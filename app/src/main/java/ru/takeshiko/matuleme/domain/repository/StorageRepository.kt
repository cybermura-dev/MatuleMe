package ru.takeshiko.matuleme.domain.repository

import io.github.jan.supabase.storage.FileUploadResponse
import io.ktor.http.ContentType
import ru.takeshiko.matuleme.data.remote.SupabaseClientManager

interface StorageRepository {
    suspend fun uploadFile(bucket: String, path: String, file: ByteArray, contentTypeString: String): Result<FileUploadResponse>
}

class StorageRepositoryImpl(
    supabase: SupabaseClientManager
) : StorageRepository {

    private val storage = supabase.storage

    override suspend fun uploadFile(
        bucket: String,
        path: String,
        file: ByteArray,
        contentTypeString: String
    ): Result<FileUploadResponse> =
        runCatching {
            val ktorContentType = ContentType.parse(contentTypeString)

            storage
                .from(bucket)
                .upload(path, file) {
                    upsert = true
                    contentType = ktorContentType
                }
        }
}