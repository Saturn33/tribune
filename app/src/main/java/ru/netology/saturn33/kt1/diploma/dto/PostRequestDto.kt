package ru.netology.saturn33.kt1.diploma.dto

data class PostRequestDto(
    val text: String,
    val link: String?,
    val attachment: AttachmentDto
)
