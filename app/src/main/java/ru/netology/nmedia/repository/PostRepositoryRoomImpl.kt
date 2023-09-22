/*
package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.OkHttpClient
import okhttp3.Request
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.entity.PostEntity
import java.lang.reflect.Type
import java.util.concurrent.TimeUnit

class PostRepositoryRoomImpl(
    private val dao: PostDao,
) : PostRepository {
    private val client = OkHttpClient.Builder()
        .callTimeout(30, TimeUnit.SECONDS)
        .build()
    private val type = object : TypeToken<List<Post>>() {}.type
    private val gson = Gson()

    private companion object {
        const val BASE_URL = "http://10.0.2.2:9999/"
    }

    override fun getAll(): List<Post> {
        val request = Request.Builder()
            .url("${BASE_URL}api/slow")
            .build()
            return client.newCall(request)
                .execute()
                .let { it.body?.string() ?: error("Body us null") }
                .let { gson.fromJson(it, type) }
    }
//    override fun getAll(): LiveData<List<Post>> = dao.getAll().map { list ->
//        list.map {
//            it.toDto()
//        }
//    }

    override fun likeById(id: Long) {
        dao.likeById(id)
    }

    override fun repostById(id: Long) {
        dao.repostById(id)
    }

    override fun save(post: Post) {
        dao.save(PostEntity.fromDto(post))
    }

    override fun removeById(id: Long) {
        dao.removeById(id)
    }
}
*/
