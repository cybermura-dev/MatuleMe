package ru.takeshiko.matuleme.domain.usecase

import io.github.jan.supabase.storage.FileUploadResponse
import ru.takeshiko.matuleme.domain.provider.StringResourceProvider
import ru.takeshiko.matuleme.domain.repository.StorageRepository
import ru.takeshiko.matuleme.presentation.utils.safeSupabaseCall

class UploadFileUseCase(
    private val repository: StorageRepository,
    private val stringResourceProvider: StringResourceProvider
) {
    suspend operator fun invoke(bucket: String, path: String, file: ByteArray, contentType: String) : Result<FileUploadResponse> {
        return stringResourceProvider.safeSupabaseCall {
            repository.uploadFile(bucket, path, file, contentType).getOrThrow()
        }
    }
}