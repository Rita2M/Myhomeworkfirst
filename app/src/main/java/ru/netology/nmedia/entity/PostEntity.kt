package ru.netology.nmedia.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.nmedia.dto.Post

@Entity
data class PostEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val author: String,
    val authorAvatar: String,
    val content: String,
    val published: String,
    val likedByMe: Boolean,
    val likes: Int = 0,
    val reposts: Int = 0,
    val repostByMe: Boolean = false,
    val linkVideo: String? = null
) {
    fun toDto() = Post(id, author, authorAvatar,content, published, likes, likedByMe )
    //который преобразует объект PostEntity в объект Post. Этот метод используется, для преобразования данных из базы данных в формат

    companion object { //выполняет обратное преобразование, т.е. преобразует объект Post в объект PostEntity
        fun fromDto(dto: Post) =
            PostEntity(dto.id, dto.author,dto.authorAvatar, dto.content, dto.published, dto.likedByMe, dto.likes)

    }
}
