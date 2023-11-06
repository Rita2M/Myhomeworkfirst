package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import com.bumptech.glide.util.Util
import ru.netology.nmedia.dto.Post

interface PostRepository {
    val data : LiveData<List<Post>>
    suspend fun getAll()
    suspend fun save(post: Post)
    suspend fun likeById(post: Post)
    suspend fun removeById(id:Long)
    fun repostById(id: Long)



}
