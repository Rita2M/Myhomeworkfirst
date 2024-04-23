package ru.netology.nmedia.repository


import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.insertSeparators
import androidx.paging.map
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import ru.netology.nmedia.api.ApiService
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dao.PostRemoteKeyDao
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.dto.Ad
import ru.netology.nmedia.dto.Attachment
import ru.netology.nmedia.dto.AttachmentType
import ru.netology.nmedia.dto.FeedItem
import ru.netology.nmedia.dto.Media
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.entity.PostEntity
import ru.netology.nmedia.entity.toEntity
import ru.netology.nmedia.error.ApiError
import ru.netology.nmedia.error.NetworkError
import ru.netology.nmedia.model.PhotoModel
import java.io.File
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class PostRepositoryImpl @Inject constructor(
    private val dao: PostDao,
    private val apiService: ApiService,
    private val appAuth: AppAuth,
     postRemoteKeyDao: PostRemoteKeyDao,
    appDb: AppDb,
) : PostRepository {
    @OptIn(ExperimentalPagingApi::class)
    override val data: Flow<PagingData<FeedItem>> = Pager(
        config = PagingConfig(pageSize = 10, enablePlaceholders = true),
        pagingSourceFactory =  dao ::getPagingSource ,
        remoteMediator = PostRemoteMediator(
            apiService = apiService,
            postDao = dao,
            postRemoteKeyDao = postRemoteKeyDao,
            appDb = appDb
        )
    ).flow
        .map { it.map(PostEntity ::toDto)
            .insertSeparators { previous,next ->
                if(previous?.id?.rem(5)==0L){
                    Ad(Random.nextLong(),"figma.jpg")

                }else{
                    null
                }
            }}
        


    override suspend fun getNewerCount(postId: Long): Flow<Int> =
        flow {
            while (true) {
                try {
                    delay(10_000)
                    val postsResponse = apiService.getNewerCount(postId)

                    val body = postsResponse.body().orEmpty()
                    val post = dao.getById(postId)
                    val newPosts = body.filter { newPost ->
                        post.id != newPost.id
                    }

                    if (newPosts.isNotEmpty()) {
                        dao.insert(newPosts.toEntity().map {
                            it.copy(hidden = true)
                        })


                    }
                    val numberOfUnread = dao.unreadCount()
                    emit(numberOfUnread)


                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }


    override suspend fun save(post: Post) {
        try {
            val response = apiService.savePost(post)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            val body = response.body() ?: throw ApiError(response.code(), response.message())
            dao.insert(PostEntity.fromDto(body))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError()
        }

    }

    override suspend fun likeById(post: Post) {
        try {
            dao.likeById(post.id)

            val postLikeResponse = if (post.likedByMe) {
                apiService.dislikeById(post.id)
            } else {
                apiService.likeById(post.id)
            }
            Log.d("repositLike", "${postLikeResponse.body()}")
            if (!postLikeResponse.isSuccessful) {
                throw ApiError(postLikeResponse.code(), postLikeResponse.message())
            }
            val body = postLikeResponse.body() ?: throw ApiError(
                postLikeResponse.code(),
                postLikeResponse.message()
            )
            dao.insert(PostEntity.fromDto(body))

        } catch (e: IOException) {
            // dao.likeById(post.id)
            throw NetworkError
        } catch (e: Exception) {
            e.printStackTrace()
        }


    }

//    override suspend fun readAll() {
//        dao.readAll()
//    }


    override suspend fun removeById(id: Long) {
        try {
            dao.removeById(id)
            val response = apiService.removeById(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError()
        }

    }

    override fun repostById(id: Long) {
        dao.repostById(id)
    }

    override suspend fun saveWithAttachment(post: Post, photoModel: PhotoModel) {
        try {
            val mediaResponse = saveMedia(photoModel.file)
            if (!mediaResponse.isSuccessful) {
                throw ApiError(mediaResponse.code(), mediaResponse.message())

            }
            val media = mediaResponse.body() ?: throw ApiError(
                mediaResponse.code(),
                mediaResponse.message()
            )

            val response = apiService.savePost(
                post.copy(
                    attachment = Attachment(
                        media.id,
                        AttachmentType.IMAGE
                    )
                )
            )
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            val body = response.body() ?: throw ApiError(response.code(), response.message())
            dao.insert(PostEntity.fromDto(body))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError()
        }
    }

    override suspend fun signIn(login: String, password: String) {
        try {
            val response = apiService.updateUser(login, password)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            appAuth.setAuth(body.id, body.token)

        } catch (e: RuntimeException) {
            throw java.lang.RuntimeException(e)

        }

    }

    private suspend fun saveMedia(file: File): retrofit2.Response<Media> {
        val part = MultipartBody.Part.createFormData("file", file.name, file.asRequestBody())

        return apiService.saveMedia(part)
    }
}
