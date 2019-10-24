package ru.netology.saturn33.kt1.diploma.client

import okhttp3.Interceptor
import okhttp3.Response
import ru.netology.saturn33.kt1.diploma.AUTH_TOKEN_HEADER

class InjectAuthTokenInterceptor(private val authToken: String) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val requestWithToken = originalRequest.newBuilder()
            .header(AUTH_TOKEN_HEADER, "Bearer $authToken")
            .build()
        return chain.proceed(requestWithToken)
    }
}
