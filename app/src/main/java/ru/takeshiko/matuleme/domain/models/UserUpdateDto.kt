package ru.takeshiko.matuleme.domain.models

data class UserUpdateDto(
    val email: String? = null,
    val password: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val avatarUrl: String? = null,
    val phoneNumber: String? = null
)