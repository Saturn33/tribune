package ru.netology.saturn33.kt1.diploma.dto

import ru.netology.saturn33.kt1.diploma.model.UserBadge

data class ProfileResponseDto(
    val username: String,
    val isReadOnly: Boolean,
    val badge: UserBadge?,
    val avatar: AttachmentDto?
)
