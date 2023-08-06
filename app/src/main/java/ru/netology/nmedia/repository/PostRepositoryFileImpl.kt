package ru.netology.nmedia.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ru.netology.nmedia.dto.Post

class PostRepositoryFileImpl (
    private val context : Context
        ): PostRepository {
    private val gson = Gson()
    private val typeToken = TypeToken.getParameterized(List ::class.java, Post ::class.java).type
    private val fileName = "posts.json"
    private var nextId = 1L
    private var posts = emptyList<Post>()
        set(value){
            field = value
            data.value = posts
            sync()
        }
    private val data = MutableLiveData(posts)
    init {
        val file = context.filesDir.resolve(fileName)
        if (file.exists()){
            context.openFileInput(fileName).bufferedReader().use {
                posts = gson.fromJson(it, typeToken)
                nextId = (posts.maxOfOrNull { it.id } ?: 0) +1
                data.value = posts
            }
        }
    }
    override fun getAll(): LiveData<List<Post>> = data
    override fun save(post: Post) {
        if(post.id == 0L){
            posts = listOf(
                post.copy(
                    id = nextId++,
                    author = "Me",
                    likedByMe = false,
                    published = "now"
                )
            ) + posts
            data.value = posts
            return
        }
        posts = posts.map {
            if (it.id != post.id) it else it.copy(content = post.content)
        }
        data.value = posts
    }


    override fun likeById(id: Long) {
        posts = posts.map {
            if (it.id != id) it else it.copy(
                likedByMe = !it.likedByMe,
                likes = if (it.likedByMe) it.likes - 1 else it.likes + 1
            )
        }
    }

    override fun repostById(id: Long) {
        posts = posts.map {
            if (it.id != id) it else it.copy(
                reposts = it.reposts + 1,
                repostByMe = true
            )
        }
    }

    override fun removeById(id: Long) {
        posts = posts.filter { it.id != id }
    }
    private fun sync(){
        context.openFileOutput(fileName, Context.MODE_PRIVATE).bufferedWriter().use {
            it.write(gson.toJson(posts))
        }
    }



}
