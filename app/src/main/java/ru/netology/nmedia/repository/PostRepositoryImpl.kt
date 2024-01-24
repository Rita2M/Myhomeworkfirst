package ru.netology.nmedia.repository


import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.Response
import ru.netology.nmedia.api.PostApi
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dto.Attachment
import ru.netology.nmedia.dto.AttachmentType
import ru.netology.nmedia.dto.Media

import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.entity.PostEntity
import ru.netology.nmedia.entity.toEntity
import ru.netology.nmedia.error.ApiError
import ru.netology.nmedia.error.NetworkError
import ru.netology.nmedia.model.PhotoModel
import ru.netology.nmedia.url.UrlProvider
import java.io.File
import java.io.IOException

class PostRepositoryImpl(private val dao: PostDao) : PostRepository {

    override val data: Flow<List<Post>> = dao.getAll()
        .map {
            it.map(PostEntity::toDto)
        }.flowOn(Dispatchers.Default)

    override fun getNewerCount(postId: Long): Flow<Int> =
        flow {
            while (true) {
                try {
                    delay(10_000)
                    val postsResponse = PostApi.retrofitService.getNever(postId)

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

    override suspend fun getAll() {
        val postsResponse = PostApi.retrofitService.getAll()
        if (!postsResponse.isSuccessful) {
            throw RuntimeException(postsResponse.errorBody()?.toString())
        }
        val posts = postsResponse.body() ?: throw java.lang.RuntimeException("body is null")


         dao.insert(posts.map(PostEntity::fromDto))




    }


    override suspend fun save(post: Post) {
        try {
            val response = PostApi.retrofitService.savePost(post)
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
                PostApi.retrofitService.dislikeById(post.id)
            } else {
                PostApi.retrofitService.likeById(post.id)
            }
            if (!postLikeResponse.isSuccessful) {
                throw ApiError(postLikeResponse.code(), postLikeResponse.message())
            }
            val body = postLikeResponse.body() ?: throw ApiError(
                postLikeResponse.code(),
                postLikeResponse.message()
            )
            dao.insert(PostEntity.fromDto(body))

        } catch (e: IOException) {
            dao.likeById(post.id)
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError()
        }


    }

    override suspend fun readAll() {
        dao.readAll()
    }


    override suspend fun removeById(id: Long) {
        try {
            dao.removeById(id)
            val response = PostApi.retrofitService.removeById(id)
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

            val response = PostApi.retrofitService.savePost(
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

    private suspend fun saveMedia(file: File): retrofit2.Response<Media> {
        val part = MultipartBody.Part.createFormData("file", file.name, file.asRequestBody())

        return PostApi.retrofitService.saveMedia(part)
    }
}
