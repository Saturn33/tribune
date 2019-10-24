package ru.netology.saturn33.kt1.diploma.dto

import ru.netology.saturn33.kt1.diploma.API_BASE_URL

data class AttachmentDto(
    val id: String
) {
    val url
        get() = "${API_BASE_URL}static/$id"
}
