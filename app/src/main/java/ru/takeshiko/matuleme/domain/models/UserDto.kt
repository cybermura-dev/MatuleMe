package ru.takeshiko.matuleme.domain.models

data class UserDto(
    val id: String,
    val email: String?,
    val isEmailConfirmed: Boolean,
    val firstName: String?,
    val lastName: String?,
    val avatarUrl: String?,
    val phoneNumber: String?
)