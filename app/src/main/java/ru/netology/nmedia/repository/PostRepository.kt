package ru.netology.nmedia.repository


import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.PhotoModel

interface PostRepository {
    val data : Flow<PagingData<Post>>
    fun getNewerCount(postId: Long) : Flow<Int>
    suspend fun getAll()
    suspend fun save(post: Post)
    suspend fun likeById(post: Post)
    suspend fun readAll()
    suspend fun removeById(id:Long)
    fun repostById(id: Long)
    suspend fun saveWithAttachment(post: Post, photoModel: PhotoModel)
    suspend fun signIn(login: String, password: String)

}
