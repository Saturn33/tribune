package ru.netology.saturn33.kt1.diploma.dto

data class PostResponseDto(
    val id: Long,
    val date: Long,
    val author: UserResponseDto,
    var text: String,
    var promotes: Int = 0,
    var promotedByMe: Boolean = false,
    var demotes: Int = 0,
    var demotedByMe: Boolean = false,
    val attachment: AttachmentDto
)
