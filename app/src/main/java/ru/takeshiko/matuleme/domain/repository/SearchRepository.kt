package ru.takeshiko.matuleme.domain.repository

import io.github.jan.supabase.postgrest.query.Order
import ru.takeshiko.matuleme.data.remote.SupabaseClientManager
import ru.takeshiko.matuleme.domain.models.SearchQueryDto

interface SearchRepository {
    suspend fun logQuery(query: SearchQueryDto): Result<SearchQueryDto>
    suspend fun updateQuery(query: SearchQueryDto): Result<SearchQueryDto>
    suspend fun deleteQuery(userId: String, query: String): Result<String>
    suspend fun getRecentQueriesByUser(userId: String, limit: Int): Result<List<SearchQueryDto>>
}

class SearchRepositoryImpl(
    supabase: SupabaseClientManager
) : SearchRepository {

    private val postgrest = supabase.postgrest

    private val tableName = "search_queries"

    override suspend fun logQuery(query: SearchQueryDto): Result<SearchQueryDto> =
        runCatching {
            postgrest
                .from(tableName)
                .insert(query) { select() }
                .decodeSingle<SearchQueryDto>()
            }

    override suspend fun updateQuery(query: SearchQueryDto): Result<SearchQueryDto> =
        runCatching {
            postgrest
                .from(tableName)
                .update(query) { select() }
                .decodeSingle<SearchQueryDto>()
        }

    override suspend fun deleteQuery(userId: String, query: String): Result<String> =
        runCatching {
            postgrest
                .from(tableName)
                .delete {
                    filter {
                        eq("user_id", userId)
                        eq("query", query)
                    }
                }
            query
        }

    override suspend fun getRecentQueriesByUser(userId: String, limit: Int): Result<List<SearchQueryDto>> =
        runCatching {
            postgrest
                .from(tableName)
                .select {
                    filter { eq("user_id", userId) }
                    order("searched_at", Order.DESCENDING)
                    limit(limit.toLong())
                }
                .decodeList<SearchQueryDto>()
        }
}