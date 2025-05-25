package ru.takeshiko.matuleme.domain.repository

import io.github.jan.supabase.auth.OtpType
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.user.UserInfo
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive
import ru.takeshiko.matuleme.data.local.AppPreferencesManager
import ru.takeshiko.matuleme.data.remote.SupabaseClientManager
import ru.takeshiko.matuleme.domain.models.UserDto
import ru.takeshiko.matuleme.domain.models.UserUpdateDto

interface UserRepository {
    suspend fun isFirstLaunch(): Boolean
    suspend fun completeOnboarding()

    suspend fun register(email: String, password: String): Result<UserDto>
    suspend fun login(email: String, password: String): Result<Unit>
    suspend fun logout(): Result<Unit>

    suspend fun sendVerificationEmail(email: String): Result<Unit>
    suspend fun verifyEmail(email: String, otp: String): Result<Unit>

    suspend fun sendPasswordResetEmail(email: String): Result<Unit>
    suspend fun confirmPasswordReset(email: String, otp: String): Result<Unit>

    suspend fun isUserAuthenticated(): Result<Boolean>
    suspend fun getCurrentUser(): Result<UserDto?>

    suspend fun updateUserData(update: UserUpdateDto): Result<UserDto>
}

class UserRepositoryImpl(
    private val prefs: AppPreferencesManager,
    supabase: SupabaseClientManager
) : UserRepository {

    private val auth = supabase.auth

    private var cachedUser: UserDto? = null

    override suspend fun isFirstLaunch(): Boolean =
        prefs.isFirstLaunch

    override suspend fun completeOnboarding() {
        prefs.setFirstLaunchCompleted()
    }

    private fun mapUser(user: UserInfo): UserDto {
        val metadata = user.userMetadata?.jsonObject
        return UserDto(
            id = user.id,
            email = user.email,
            isEmailConfirmed = user.emailConfirmedAt != null,
            firstName = metadata?.get("first_name")?.jsonPrimitive?.contentOrNull,
            lastName = metadata?.get("last_name")?.jsonPrimitive?.contentOrNull,
            avatarUrl = metadata?.get("avatar_url")?.jsonPrimitive?.contentOrNull,
            phoneNumber = metadata?.get("phone_number")?.jsonPrimitive?.contentOrNull
        )
    }

    override suspend fun register(email: String, password: String): Result<UserDto> =
        runCatching {
            val userInfo = auth.signUpWith(Email) {
                this.email = email
                this.password = password
            } ?: throw IllegalStateException("Sign-up returned null user")
            mapUser(userInfo).also {
                cachedUser = it
            }
        }


    override suspend fun login(email: String, password: String): Result<Unit> =
        runCatching {
            auth.signInWith(Email) {
                this.email = email
                this.password = password
            }
            auth.currentSessionOrNull()?.user?.let {
                cachedUser = mapUser(it)
            }
        }

    override suspend fun logout(): Result<Unit> =
        runCatching {
            auth.signOut()
            cachedUser = null
        }

    override suspend fun sendVerificationEmail(email: String): Result<Unit> =
        runCatching {
            auth.resendEmail(
                type = OtpType.Email.EMAIL,
                email = email
            )
        }

    override suspend fun verifyEmail(email: String, otp: String): Result<Unit> =
        runCatching {
            auth.verifyEmailOtp(
                type = OtpType.Email.EMAIL,
                email = email,
                token = otp
            )
            auth.currentSessionOrNull()?.user?.let {
                cachedUser = mapUser(it)
            }
        }

    override suspend fun sendPasswordResetEmail(email: String): Result<Unit> =
        runCatching {
            auth.resetPasswordForEmail(email)
        }

    override suspend fun confirmPasswordReset(email: String, otp: String): Result<Unit> =
        runCatching {
            auth.verifyEmailOtp(
                type = OtpType.Email.RECOVERY,
                email = email,
                token = otp
            )
            auth.currentSessionOrNull()?.user?.let {
                cachedUser = mapUser(it)
            }
        }

    override suspend fun isUserAuthenticated(): Result<Boolean> =
        runCatching {
            auth.currentSessionOrNull() != null
        }

    override suspend fun getCurrentUser(): Result<UserDto?> =
        runCatching {
            cachedUser ?: run {
                val userFromAuth = auth.currentSessionOrNull()?.user
                if (userFromAuth != null) {
                    mapUser(userFromAuth).also {
                        cachedUser = it
                    }
                } else {
                    cachedUser = null
                    null
                }
            }
        }

    override suspend fun updateUserData(update: UserUpdateDto): Result<UserDto> =
        runCatching {
            val updated = auth.updateUser {
                update.email?.let { this.email = it }
                update.password?.let { this.password = it }
                data {
                    update.firstName?.let { put("first_name", JsonPrimitive(it)) }
                    update.lastName?.let { put("last_name", JsonPrimitive(it)) }
                    update.avatarUrl?.let { put("avatar_url", JsonPrimitive(it)) }
                    update.phoneNumber?.let { put("phone_number", JsonPrimitive(it)) }
                }
            }
            mapUser(updated).also {
                cachedUser = it
            }
        }
}