package ru.takeshiko.matuleme.domain.repository

import ru.takeshiko.matuleme.data.remote.SupabaseClientManager
import ru.takeshiko.matuleme.domain.models.UserPaymentDto

interface PaymentRepository {
    suspend fun getPayments(userId: String): Result<List<UserPaymentDto>>
    suspend fun getPaymentById(paymentId: String): Result<UserPaymentDto?>
    suspend fun addPayment(payment: UserPaymentDto): Result<UserPaymentDto>
    suspend fun updatePayment(payment: UserPaymentDto): Result<UserPaymentDto>
    suspend fun deletePayment(paymentId: String): Result<Unit>
    suspend fun setDefaultPayment(userId: String, paymentId: String): Result<Unit>
}

class PaymentRepositoryImpl(
    supabase: SupabaseClientManager
) : PaymentRepository {

    private val postgrest = supabase.postgrest

    private val tableName = "user_payments"

    override suspend fun getPayments(userId: String): Result<List<UserPaymentDto>> =
        runCatching {
            postgrest
                .from(tableName)
                .select {
                    filter { eq("user_id", userId) }
                }
                .decodeList<UserPaymentDto>()
        }

    override suspend fun getPaymentById(paymentId: String): Result<UserPaymentDto?> =
        runCatching {
            postgrest
                .from(tableName)
                .select {
                    filter { eq("id", paymentId) }
                    limit(1)
                }
                .decodeSingleOrNull<UserPaymentDto>()
        }

    override suspend fun addPayment(payment: UserPaymentDto): Result<UserPaymentDto> =
        runCatching {
            postgrest
                .from(tableName)
                .insert(payment) { select() }
                .decodeSingle<UserPaymentDto>()
        }

    override suspend fun updatePayment(payment: UserPaymentDto): Result<UserPaymentDto> =
        runCatching {
            postgrest
                .from(tableName)
                .update(payment) {
                    select()
                    filter { eq("id", payment.id!!) }
                }
                .decodeSingle<UserPaymentDto>()
        }

    override suspend fun deletePayment(paymentId: String): Result<Unit> =
        runCatching {
            postgrest
                .from(tableName)
                .delete {
                    filter { eq("id", paymentId) }
                }
        }

    override suspend fun setDefaultPayment(userId: String, paymentId: String): Result<Unit> =
        runCatching {
            postgrest
                .from(tableName)
                .update(mapOf("is_default" to false)) {
                    filter {
                        eq("user_id", userId)
                        neq("id", paymentId)
                    }
                }

            postgrest
                .from(tableName)
                .update(mapOf("is_default" to true)) {
                    filter { eq("id", paymentId) }
                }
        }
}