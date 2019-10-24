package ru.netology.saturn33.kt1.diploma.dto

import ru.netology.saturn33.kt1.diploma.model.UserBadge

data class UserResponseDto(
    val id: Long,
    val username: String,
    val badge: UserBadge?,
    val avatar: AttachmentDto?
)
