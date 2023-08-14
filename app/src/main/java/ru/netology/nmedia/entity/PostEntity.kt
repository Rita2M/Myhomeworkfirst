package ru.netology.nmedia.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.nmedia.dto.Post

@Entity
class PostEntity (
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val author: String,
    val content: String,
    val published: String,
    val likes: Int = 0,
    val reposts: Int = 0,
    val likedByMe: Boolean = false,
    val repostByMe: Boolean = false,
    val linkVideo: String? = null
){
    fun toDto() = Post(id, author, content, published, likes, reposts,likedByMe, repostByMe,linkVideo)

    companion object {
        fun fromDto(dto: Post) =
            PostEntity(dto.id, dto.author, dto.content, dto.published, dto.likes,dto.reposts,  dto.likedByMe, dto.likedByMe, dto.linkVideo)

    }
}
