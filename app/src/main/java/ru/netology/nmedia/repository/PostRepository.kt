package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import com.bumptech.glide.util.Util
import ru.netology.nmedia.dto.Post

interface PostRepository {
    fun getAll(): List<Post>



    fun getAllAsync(callback: Callback<List<Post>>)
    fun saveAsync(post: Post,callback: Callback<Post>)
    fun likeAsync(id: Long, callback: Callback<Post>)
    fun removeByIdAsync(id:Long, callback: Callback<Unit>)
    fun repostByIdAsync(id: Long,callback: Callback<Post>)
    fun unLikeAsync(id: Long,callback: Callback<Post>)


    interface Callback<T> {
        fun onSuccess(posts: T) {}
        fun onError(e: Exception) {}
    }




}
