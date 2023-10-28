package ru.netology.nmedia.repository


import retrofit2.Call
import retrofit2.Response
import ru.netology.nmedia.api.PostApi

import ru.netology.nmedia.dto.Post

class PostRepositoryImpl : PostRepository {


    override fun getAll(): List<Post> {


        return PostApi.retrofitService.getAll().execute()
            .let { it.body() ?: throw RuntimeException("body is null") }

    }

    override fun getAllAsync(callback: PostRepository.Callback<List<Post>>) {

        PostApi.retrofitService.getAll().enqueue(object : retrofit2.Callback<List<Post>> {
            override fun onResponse(call: Call<List<Post>>, response: Response<List<Post>>) {
                if (!response.isSuccessful) {
                    callback.onError(RuntimeException(response.errorBody()?.string()))
                    return
                }
                val body = response.body() ?: run {
                    callback.onError(RuntimeException("response is empty"))
                    return
                }
                callback.onSuccess(body)
            }

            override fun onFailure(call: Call<List<Post>>, t: Throwable) {
                callback.onError(Exception(t))
            }


        })
    }


    override fun saveAsync(post: Post, callback: PostRepository.Callback<Post>) {
        PostApi.retrofitService.savePost(post).enqueue(object : retrofit2.Callback<Post> {
            override fun onResponse(call: Call<Post>, response: Response<Post>) {
                if (response.code() == 200) callback.onSuccess(post) else callback.onError(
                    RuntimeException("Error in save")
                )
            }

            override fun onFailure(call: Call<Post>, t: Throwable) {
                callback.onError(Exception(t))
            }
        })


    }


    override fun likeAsync(id: Long, callback: PostRepository.Callback<Post>) {

        PostApi.retrofitService.likeById(id).enqueue(object : retrofit2.Callback<Post> {

            override fun onResponse(call: Call<Post>, response: Response<Post>) {
                if (!response.isSuccessful) {
                    callback.onError(java.lang.RuntimeException(response.message()))
                    return
                }
                val body = response.body() ?: run {
                    callback.onError(RuntimeException("body is null"))
                    return
                }
                callback.onSuccess(body)

            }

            override fun onFailure(call: Call<Post>, t: Throwable) {
                callback.onError(Exception(t))
            }
        })
    }


    override fun repostByIdAsync(id: Long, callback: PostRepository.Callback<Post>) {
//        PostApi.retrofitService.
//            .enqueue(object : Callback {

//            })
        //TODO
    }


    override fun removeByIdAsync(id: Long, callback: PostRepository.Callback<Unit>) {
        PostApi.retrofitService.removeById(id).enqueue(object : retrofit2.Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                if (response.code() == 200) callback.onSuccess(Unit) else callback.onError(
                    RuntimeException("Error in remove")
                )
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                callback.onError(Exception(t))
            }


        })
    }


    override fun unLikeAsync(id: Long, callback: PostRepository.Callback<Post>) {
        PostApi.retrofitService.dislikeById(id).enqueue(object : retrofit2.Callback<Post> {
            override fun onResponse(call: Call<Post>, response: Response<Post>) {

                if (!response.isSuccessful) {
                    callback.onError(java.lang.RuntimeException(response.message()))
                    return
                }
                val body = response.body() ?: run {
                    callback.onError(RuntimeException("body is null"))
                    return
                }

                callback.onSuccess(body)
            }


            override fun onFailure(call: Call<Post>, t: Throwable) {
                callback.onError(Exception(t))
            }


        })
    }

}
