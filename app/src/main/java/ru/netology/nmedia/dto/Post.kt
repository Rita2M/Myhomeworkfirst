package ru.netology.nmedia.dto

import android.provider.MediaStore.Video


data class Post(
    val id: Long,
    val author: String,
    val content: String,
    val published: String,
    val likes: Int = 0,
    val reposts: Int = 0,
    val likedByMe: Boolean = false,
    val repostByMe: Boolean = false,
    val linkVideo: String? = null
)
