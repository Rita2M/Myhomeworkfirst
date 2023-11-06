package ru.netology.nmedia.repository


import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import ru.netology.nmedia.api.PostApi
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.entity.PostEntity
import ru.netology.nmedia.error.ApiError
import ru.netology.nmedia.error.NetworkError
import java.io.IOException

class PostRepositoryImpl(private val dao: PostDao) : PostRepository {

    override val data: LiveData<List<Post>> = dao.getAll()
        .map {
            it.map(PostEntity::toDto)
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

    override suspend fun likeById(id: Long) {
        try {
            dao.likeById(id)

            val post = PostApi.retrofitService.getById(id).body() ?: throw RuntimeException("null")
            val postLikeResponse = if (post.likedByMe) {
                PostApi.retrofitService.dislikeById(id)
            } else {
                PostApi.retrofitService.likeById(id)
            }
            if (!postLikeResponse.isSuccessful) {
                throw ApiError(postLikeResponse.code(), postLikeResponse.message())
            }

        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError()
        }


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
