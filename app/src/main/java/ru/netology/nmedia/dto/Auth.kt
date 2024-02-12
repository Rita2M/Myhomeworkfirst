package ru.netology.nmedia.dto

import android.media.session.MediaSession.Token


data class Auth(
    val id: Long,
    val token: String

)
