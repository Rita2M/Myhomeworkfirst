package ru.netology.nmedia.dto


data class Post(
    val id: Long,
    val author: String,
    val authorAvatar: String,
    val content: String,
    val published: String,
    val likes: Int = 0,
    val likedByMe: Boolean = false,
    val attachment: Attachment? = null,
) {
    val reposts: Int = 0
    val repostByMe: Boolean = false
    val linkVideo: String? = null
}
data class Attachment(
    val url: String,
    val type: AttachmentType
)
