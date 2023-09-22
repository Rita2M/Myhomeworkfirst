package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import ru.netology.nmedia.dto.Post

interface PostRepository {
    fun getAll(): List<Post>
    fun save(post: Post)
    fun likeById(id: Long) : Post
    fun repostById(id: Long)
    fun removeById(id:Long)
    fun unLikeById(id: Long) : Post
}
