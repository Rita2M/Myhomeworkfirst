package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import ru.netology.nmedia.dto.Post

interface PostRepository {
    fun getAll(): List<Post>



    fun getAllAsync(callback: GetAllCallback)
    fun saveAsync(post: Post,callback: SaveCallback)
    fun likeAsync(id: Long, callback: LikeCallback)
    fun unLikeAsync(id: Long, callback: LikeCallback)
    fun removeByIdAsync(id:Long, callback: RemoveCallback)
    fun repostByIdAsync(id: Long,callback: RemoveCallback)

    interface GetAllCallback {
        fun onSuccess(posts: List<Post>) {}
        fun onError(e: Exception) {}
    }
    interface SaveCallback{
        fun onSuccess(post: Post)
        fun onError(e: Exception)
    }
    interface LikeCallback{
        fun onSuccess(post: Post)
        fun onError(e: Exception)
    }
    interface RemoveCallback{
        fun onSuccess(id: Long)
        fun onError(e: Exception)
    }


}
