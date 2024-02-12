package ru.netology.nmedia.api

import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
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
import ru.netology.nmedia.BuildConfig
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.dto.Auth
import ru.netology.nmedia.dto.Media
import ru.netology.nmedia.dto.Post
import java.util.concurrent.TimeUnit

private val logging = HttpLoggingInterceptor().apply{
   if (BuildConfig.DEBUG){
       level = HttpLoggingInterceptor.Level.BODY
   }
}
private val client = OkHttpClient.Builder()
    .connectTimeout(30, TimeUnit.SECONDS)
    .addInterceptor{chain->
        AppAuth.getInstance().authState.value.token?.let {token->
            val  newRequest =chain.request().newBuilder()
                .addHeader("Authorization", token)
                .build()
            return@addInterceptor  chain.proceed(newRequest)

        }
        chain.proceed(chain.request())
    }
    .addInterceptor(logging)
    .build()
private val retrofit = Retrofit.Builder()
    .addConverterFactory(GsonConverterFactory.create())
    .baseUrl(BuildConfig.BASE_URL)
    .client(client)
    .build()


interface PostsApiService {
    @GET("posts")
    suspend fun getAll(): Response<List<Post>>
    @GET("posts/{id}/newer")
    suspend fun getNever(@Path("id")id: Long) : Response<List<Post>>

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

}

object PostApi {
    val retrofitService: PostsApiService by lazy {
        retrofit.create(PostsApiService::class.java)
    }
}
