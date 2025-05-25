package ru.takeshiko.matuleme.domain.repository

import ru.takeshiko.matuleme.data.remote.SupabaseClientManager
import ru.takeshiko.matuleme.domain.models.UserDeliveryAddressDto

interface DeliveryAddressRepository {
    suspend fun getAddresses(userId: String): Result<List<UserDeliveryAddressDto>>
    suspend fun getAddressById(addressId: String): Result<UserDeliveryAddressDto?>
    suspend fun addAddress(address: UserDeliveryAddressDto): Result<UserDeliveryAddressDto>
    suspend fun updateAddress(address: UserDeliveryAddressDto): Result<UserDeliveryAddressDto>
    suspend fun deleteAddress(addressId: String): Result<Unit>
    suspend fun setDefaultAddress(userId: String, addressId: String): Result<Unit>
}

class DeliveryAddressRepositoryImpl(
    supabase: SupabaseClientManager
) : DeliveryAddressRepository {

    private val postgrest = supabase.postgrest

    private val tableName = "user_delivery_addresses"

    override suspend fun getAddresses(userId: String): Result<List<UserDeliveryAddressDto>> =
        runCatching {
            postgrest
                .from(tableName)
                .select {
                    filter { eq("user_id", userId) }
                }
                .decodeList()
        }

    override suspend fun getAddressById(addressId: String): Result<UserDeliveryAddressDto?> =
        runCatching {
            postgrest
                .from(tableName)
                .select {
                    filter { eq("id", addressId) }
                    limit(1)
                }
                .decodeSingleOrNull<UserDeliveryAddressDto>()
        }

    override suspend fun addAddress(address: UserDeliveryAddressDto): Result<UserDeliveryAddressDto> =
        runCatching {
            postgrest
                .from(tableName)
                .insert(address) { select() }
                .decodeSingle()
        }

    override suspend fun updateAddress(address: UserDeliveryAddressDto): Result<UserDeliveryAddressDto> =
        runCatching {
            postgrest
                .from(tableName)
                .update(address) {
                    select()
                    filter { eq("id", address.id!!) }
                }
                .decodeSingle()
        }

    override suspend fun deleteAddress(addressId: String): Result<Unit> =
        runCatching {
            postgrest
                .from(tableName)
                .delete {
                    filter { eq("id", addressId) }
                }
        }

    override suspend fun setDefaultAddress(userId: String, addressId: String): Result<Unit> =
        runCatching {
            postgrest
                .from(tableName)
                .update(mapOf("is_default" to false)) {
                    filter {
                        eq("user_id", userId)
                        neq("id", addressId)
                    }
                }

            postgrest
                .from(tableName)
                .update(mapOf("is_default" to true)) {
                    filter { eq("id", addressId) }
                }
        }
}