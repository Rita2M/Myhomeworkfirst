package ru.netology.nmedia.url

import retrofit2.http.Url
import java.net.URL

object UrlProvider {
    private const val  BASE_URL = "http://10.0.2.2:9999"

    fun getAvatarUrl(authorAvatar: String) : String{
        return "${BASE_URL}/avatars/${authorAvatar}"
    }
    fun getMediaUrl(jj: String) : String{
        return "${BASE_URL}/media/${jj}"
    }

}
