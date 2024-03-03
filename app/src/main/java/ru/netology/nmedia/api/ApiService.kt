package ru.netology.nmedia.api

import okhttp3.Interceptor
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query
import ru.netology.nmedia.BuildConfig
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.dto.Auth
import ru.netology.nmedia.dto.Media
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.dto.PushToken
import java.util.concurrent.TimeUnit


//fun client(vararg interceptors: Interceptor): OkHttpClient = OkHttpClient.Builder()
//    .apply {   interceptors.forEach {
//        this.addInterceptor(it)
//    } }.build()
//fun retrofit(client: OkHttpClient): Retrofit  = Retrofit.Builder()
//    .addConverterFactory(GsonConverterFactory.create())
//    .baseUrl(BuildConfig.BASE_URL)
//    .client(client)
//    .build()


interface ApiService {
    @POST("users/push-tokens")
    suspend fun save(@Body pushToken: PushToken): Response<Unit>
    @GET("posts")
    suspend fun getAll(): Response<List<Post>>
    @GET("posts/latest")
    suspend fun getLatest(@Query("count") count: Int): Response<List<Post>>
    @GET("posts/{id}/newer")
    suspend fun getNever(@Path("id")id: Long) : Response<List<Post>>
    @GET("posts/{id}/before")
    suspend fun getBefore(@Path("id")id: Long,@Query("count") count: Int) : Response<List<Post>>
    @GET("posts/{id}/after")
    suspend fun getAfter(@Path("id")id: Long,@Query("count")count: Int) : Response<List<Post>>

    @GET("posts/{id}")
    suspend fun getById(@Path("id") id: Long): Response<Post>

    @POST("posts")
    suspend fun savePost(@Body post: Post): Response<Post>

    @DELETE("posts/{id}")
    suspend fun removeById(@Path("id") id: Long): Response<Unit>

    @POST("posts/{id}/likes")
    suspend fun likeById(@Path("id") id: Long): Response<Post>

    @DELETE("posts/{id}/likes")
    suspend fun dislikeById(@Path("id") id: Long): Response<Post>

    @Multipart
    @POST("media")
    suspend fun saveMedia(@Part part: MultipartBody.Part): Response<Media>
    @FormUrlEncoded
    @POST("users/authentication")
    suspend fun updateUser(@Field("login") login: String, @Field("pass") pass: String): Response<Auth>

    @POST("users/push-tokens")
    suspend fun sendPushToken(@Body token: PushToken): Response<Unit>

}
