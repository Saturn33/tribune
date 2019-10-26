package ru.netology.saturn33.kt1.diploma.helpers

import ru.netology.saturn33.kt1.diploma.dto.AttachmentDto
import ru.netology.saturn33.kt1.diploma.dto.PostResponseDto
import ru.netology.saturn33.kt1.diploma.dto.UserResponseDto
import ru.netology.saturn33.kt1.diploma.model.UserBadge
import java.util.*

object Stub {
    fun getPosts(): List<PostResponseDto> {
        return listOf(
            PostResponseDto(
                4,
                Date().time,
                UserResponseDto(
                    1,
                    "Kirill",
                    null,
                    null
                ),
                null,
                "Проверка текста без аватара",
                1, false,
                100, false,
                AttachmentDto("post-4.jpg")
            ),
            PostResponseDto(
                3,
                Date().time,
                UserResponseDto(
                    1,
                    "Vasya",
                    null,
                    AttachmentDto("avatar-1.jpg")
                ),
                null,
                "Проверка текста",
                5, false,
                10, true,
                AttachmentDto("post-3.jpg")
            ),
            PostResponseDto(
                2,
                Date().time - 86400 * 1000,
                UserResponseDto(
                    2,
                    "Petya",
                    UserBadge.HATER,
                    AttachmentDto("avatar-2.jpg")
                ),
                null,
                "Проверка текста подлиннее, чтобы он на несколько строк перелетел, а то как-то неинтересно",
                0, false,
                0, false,
                AttachmentDto("post-2.jpg")
            ),
            PostResponseDto(
                1,
                Date().time - 86400 * 1000 * 3,
                UserResponseDto(
                    3,
                    "Anton",
                    UserBadge.PROMOTER,
                    AttachmentDto("avatar-3.jpg")
                ),
                "https://google.com/search?q=android",
                "Проверка текста со ссылкой, должно переходить на гугл-поиск",
                100, true,
                10, false,
                AttachmentDto("post-1.jpg")
            )
        )
    }
}
