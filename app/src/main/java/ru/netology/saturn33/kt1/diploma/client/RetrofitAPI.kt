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

    @DELETE("token/{id}")
    suspend fun unregisterPushToken(@Path("id") id: Long): Response<Void>

    @GET("posts/recent/{userId}/{count}")
    suspend fun getRecentPosts(@Path("userId") userId: Long, @Path("count") pageSize: Long): Response<MutableList<PostResponseDto>>

    @GET("posts/before/{userId}/{id}/{count}")
    suspend fun getPostsBefore(@Path("userId") userId: Long, @Path("id") postId: Long, @Path("count") count: Long): Response<MutableList<PostResponseDto>>

    @POST("posts/{id}/promote")
    suspend fun promote(@Path("id") id: Long): Response<PostResponseDto>

    @POST("posts/{id}/demote")
    suspend fun demote(@Path("id") id: Long): Response<PostResponseDto>

    @GET("posts/reactions/{id}")
    suspend fun getReactions(@Path("id") postId: Long): Response<MutableList<ReactionResponseDto>>

}
