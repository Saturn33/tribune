package ru.netology.saturn33.kt1.diploma.dto

import ru.netology.saturn33.kt1.diploma.model.ReactionType

data class ReactionResponseDto(
    val date: Long,
    val user: UserResponseDto,
    val type: ReactionType
)
