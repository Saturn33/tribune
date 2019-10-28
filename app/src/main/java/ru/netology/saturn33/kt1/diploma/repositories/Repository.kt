package ru.netology.saturn33.kt1.diploma.repositories

import android.graphics.Bitmap
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.netology.saturn33.kt1.diploma.API_BASE_URL
import ru.netology.saturn33.kt1.diploma.PAGE_SIZE
import ru.netology.saturn33.kt1.diploma.client.InjectAuthTokenInterceptor
import ru.netology.saturn33.kt1.diploma.client.RetrofitAPI
import ru.netology.saturn33.kt1.diploma.dto.*
import java.io.ByteArrayOutputStream

object Repository {
    private var retrofit: Retrofit = createRetrofit(returnInstance = true)

    private var API: RetrofitAPI = retrofit.create(RetrofitAPI::class.java)

    fun createRetrofit(authToken: String? = null, returnInstance: Boolean = false): Retrofit {
        val httpLoggerInterceptor = HttpLoggingInterceptor()
        httpLoggerInterceptor.level = HttpLoggingInterceptor.Level.BODY
        val clientBuilder = OkHttpClient.Builder()
        if (authToken != null)
            clientBuilder.addInterceptor(InjectAuthTokenInterceptor(authToken))
        clientBuilder.addInterceptor(httpLoggerInterceptor)
        val client = clientBuilder.build()

        retrofit = Retrofit.Builder()
            .client(client)
            .baseUrl(API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        if (returnInstance)
            return retrofit
        API = retrofit.create(RetrofitAPI::class.java)

        return retrofit
    }

    suspend fun auth(login: String, password: String) = API.authenticate(
        AuthenticationRequestDto(login, password)
    )

    suspend fun register(login: String, password: String) = API.register(
        RegistrationRequestDto(login, password)
    )

    suspend fun getRecentPosts(userId: Long = 0) = API.getRecentPosts(userId, PAGE_SIZE)

    suspend fun getPostsBefore(userId: Long, id: Long) = API.getPostsBefore(userId, id, PAGE_SIZE)

    suspend fun getProfile() = API.getProfile()

    suspend fun saveProfile(avatar: AttachmentDto?) = API.saveProfile(
        ProfileRequestDto(avatar)
    )

    suspend fun addPost(text: String, link: String?, image: AttachmentDto) = API.addPost(
        PostRequestDto(text, link, image)
    )

    suspend fun promote(id: Long) = API.promote(id)

    suspend fun demote(id: Long) = API.demote(id)

    suspend fun getReactions(postId: Long) = API.getReactions(postId)

    suspend fun registerPushToken(token: String) = API.registerPushToken(PushTokenRequestDto(token))

    suspend fun unregisterPushToken(id: Long) = API.unregisterPushToken(id)

    suspend fun upload(bitmap: Bitmap): Response<AttachmentDto> {
        val bos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos)
        val reqFile = bos.toByteArray().toRequestBody("image/jpeg".toMediaTypeOrNull())
        val body = MultipartBody.Part.createFormData("file", "image.jpg", reqFile)
        return API.uploadImage(body)
    }

}
