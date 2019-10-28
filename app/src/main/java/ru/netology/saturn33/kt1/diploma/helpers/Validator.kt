package ru.netology.saturn33.kt1.diploma.helpers

object Validator {
    fun isPasswordValid(password: String): Pair<Boolean, String> {
        val min = 6
        val max = 15

        if (password.length < min) return false to "Минимальное количество символов для пароля - $min"
        if (password.length > max) return false to "Максимальное количество символов для пароля - $max"
        if (!Regex("^[a-zA-Z_\\d]+$").matches(password)) return false to "Пароль должен состоять только из латинских символов и цифр"

        return true to ""
    }

    fun isLoginValid(login: String): Pair<Boolean, String> {
        val min = 1
        val max = 10

        if (login.length < min) return false to "Минимальное количество символов для логина - $min"
        if (login.length > max) return false to "Максмиальное количество символов для логина - $max"

        return true to ""
    }

    fun isPostTextValid(text: String): Pair<Boolean, String> {
        val limit = 100
        if (text.isEmpty()) return false to "Текст идеи должен быть заполнен"
        if (text.length > limit) return false to "Текст идеи должен быть менее $limit символов"
        return true to ""
    }

    fun isPostLinkValid(link: String?): Pair<Boolean, String> {
        if (link == null) return true to ""
        if (!Regex("^https?://.+").matches(link)) return false to "Ссылка должна начинаться с http(s)://"
        return true to ""
    }
}
