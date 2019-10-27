package ru.netology.saturn33.kt1.diploma.helpers

import android.content.Context
import androidx.core.content.edit
import ru.netology.saturn33.kt1.diploma.API_SHARED_FILE
import ru.netology.saturn33.kt1.diploma.AUTHENTICATED_SHARED_KEY
import ru.netology.saturn33.kt1.diploma.READONLY_SHARED_KEY

object SPref {
    fun removeUserAuth(ctx: Context) =
        ctx.getSharedPreferences(API_SHARED_FILE, Context.MODE_PRIVATE).edit {
            remove(AUTHENTICATED_SHARED_KEY)
        }


    fun setUserAuth(ctx: Context, token: String) =
        ctx.getSharedPreferences(API_SHARED_FILE, Context.MODE_PRIVATE).edit {
            putString(AUTHENTICATED_SHARED_KEY, token)
        }

    fun setReadOnly(ctx: Context, readOnly: Boolean) =
        ctx.getSharedPreferences(API_SHARED_FILE, Context.MODE_PRIVATE).edit {
            putBoolean(READONLY_SHARED_KEY, readOnly)
        }

    fun getToken(ctx: Context) =
        ctx.getSharedPreferences(API_SHARED_FILE, Context.MODE_PRIVATE)
            .getString(AUTHENTICATED_SHARED_KEY, null)

    fun isAuthenticated(token: String?) = token?.isNotEmpty() ?: false

    fun isReadOnly(ctx: Context): Boolean =
        ctx.getSharedPreferences(API_SHARED_FILE, Context.MODE_PRIVATE)
            .getBoolean(READONLY_SHARED_KEY, false)
}
