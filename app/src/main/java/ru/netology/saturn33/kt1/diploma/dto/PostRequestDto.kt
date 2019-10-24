package ru.netology.saturn33.kt1.diploma.dto

data class PostRequestDto(
    val id: Long = 0,
    val text: String,
    val attachment: AttachmentDto
)
