package ru.netology.saturn33.kt1.diploma.client

import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*
import ru.netology.saturn33.kt1.diploma.dto.*

interface RetrofitAPI {
    @POST("authentication")
    suspend fun authenticate(@Body authRequestParams: AuthenticationRequestDto): Response<AuthenticationResponseDto>

    @POST("registration")
    suspend fun register(@Body authRequestParams: RegistrationRequestDto): Response<RegistrationResponseDto>

    @GET("profile")
    suspend fun getProfile(): Response<ProfileResponseDto>

    @POST("profile")
    suspend fun saveProfile(@Body profileParams: ProfileRequestDto): Response<ProfileResponseDto>

    @Multipart
    @POST("media")
    suspend fun uploadImage(@Part file: MultipartBody.Part): Response<AttachmentDto>

    @POST("token")
    suspend fun registerPushToken(@Body pushToken: PushTokenRequestDto): Response<Void>

}
