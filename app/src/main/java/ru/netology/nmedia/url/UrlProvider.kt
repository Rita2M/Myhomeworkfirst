package ru.netology.nmedia.url

object UrlProvider {
    private const val  BASE_URL = "http://10.0.2.2:9999"

    fun getAvatarUrl(authorAvatar: String) : String{
        return "${BASE_URL}/avatars/${authorAvatar}"
    }

}
