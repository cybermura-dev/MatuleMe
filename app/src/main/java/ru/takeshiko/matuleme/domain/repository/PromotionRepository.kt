package ru.takeshiko.matuleme.domain.repository

import ru.takeshiko.matuleme.data.remote.SupabaseClientManager
import ru.takeshiko.matuleme.domain.models.PromotionDto

interface PromotionRepository {
    suspend fun getPromotions(): Result<List<PromotionDto>>
    suspend fun getActivePromotions(): Result<List<PromotionDto>>
}

class PromotionRepositoryImpl(
    supabase: SupabaseClientManager
) : PromotionRepository {

    private val postgrest = supabase.postgrest

    private val tableName = "promotions"

    override suspend fun getPromotions(): Result<List<PromotionDto>> =
        runCatching {
            postgrest
                .from(tableName)
                .select()
                .decodeList<PromotionDto>()
        }

    override suspend fun getActivePromotions(): Result<List<PromotionDto>> =
        runCatching {
            postgrest
                .from(tableName)
                .select()
                .decodeList<PromotionDto>()
                .filter { it.isActive }
        }
}