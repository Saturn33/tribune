package ru.netology.saturn33.kt1.diploma.helpers

import ru.netology.saturn33.kt1.diploma.dto.AttachmentDto
import ru.netology.saturn33.kt1.diploma.dto.PostResponseDto
import ru.netology.saturn33.kt1.diploma.dto.ReactionResponseDto
import ru.netology.saturn33.kt1.diploma.dto.UserResponseDto
import ru.netology.saturn33.kt1.diploma.model.ReactionType
import ru.netology.saturn33.kt1.diploma.model.UserBadge
import java.util.*
import kotlin.random.Random

object Stub {
    fun getUsers(): List<UserResponseDto> {
        return listOf(
            UserResponseDto(
                1,
                "Vasya",
                null,
                AttachmentDto("avatar-1.jpg")
            ),
            UserResponseDto(
                2,
                "Petya",
                UserBadge.HATER,
                AttachmentDto("avatar-2.jpg")
            ),
            UserResponseDto(
                3,
                "Anton",
                UserBadge.PROMOTER,
                AttachmentDto("avatar-3.jpg")
            ),
            UserResponseDto(
                4,
                "Kirill",
                null,
                null
            )
        )
    }

    fun getPosts(): List<PostResponseDto> {
        val users = getUsers()
        return listOf(
            PostResponseDto(
                4,
                Date().time,
                users.find { it.id == 4L }!!,
                null,
                "Проверка текста без аватара",
                1, false,
                100, false,
                AttachmentDto("post-4.jpg")
            ),
            PostResponseDto(
                3,
                Date().time,
                users.find { it.id == 1L }!!,
                null,
                "Проверка текста",
                5, false,
                10, true,
                AttachmentDto("post-3.jpg")
            ),
            PostResponseDto(
                2,
                Date().time - 86400 * 1000,
                users.find { it.id == 2L }!!,
                null,
                "Проверка текста подлиннее, чтобы он на несколько строк перелетел, а то как-то неинтересно",
                0, false,
                0, false,
                AttachmentDto("post-2.jpg")
            ),
            PostResponseDto(
                1,
                Date().time - 86400 * 1000 * 3,
                users.find { it.id == 3L }!!,
                "https://google.com/search?q=android",
                "Проверка текста со ссылкой, должно переходить на гугл-поиск",
                100, true,
                10, false,
                AttachmentDto("post-1.jpg")
            )
        )
    }

    fun getReactions(count: Long): List<ReactionResponseDto> {
        val users = getUsers()
        val reactions: MutableList<ReactionResponseDto> = mutableListOf()
        for (i in 1L..count) {
            reactions.add(
                ReactionResponseDto(
                    i,
                    Date().time - 86400 * 1000 * i,
                    users.get(Random.nextInt(users.size)),
                    if (Random.nextBoolean()) ReactionType.PROMOTE else ReactionType.DEMOTE
                )
            )
        }

        return reactions
    }
}
