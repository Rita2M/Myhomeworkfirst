package ru.netology.nmedia.viewmodel

import android.app.Application
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.bumptech.glide.util.Util
import com.google.android.material.snackbar.Snackbar
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.FeedModel
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryImpl
import ru.netology.nmedia.util.SingleLiveEvent


private val empty = Post(
    id = 0, content = "", author = "", likedByMe = false, published = "", authorAvatar = ""

)

class PostViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: PostRepository = PostRepositoryImpl()


    private val _data = MutableLiveData(FeedModel())
    val data: LiveData<FeedModel> get() = _data
    val edited = MutableLiveData(empty)
    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated

    init {
        loadPosts()
    }

    //    fun loadPosts() {
//        thread {
//            // Начинаем загрузку
//            _data.postValue(FeedModel(loading = true))
//            try {
//                // Данные успешно получены
//                val posts = repository.getAll()
//                FeedModel(posts = posts, empty = posts.isEmpty())
//            } catch (e: IOException) {
//                // Получена ошибка
//                FeedModel(error = true)
//            }.also(_data::postValue)
//        }
//    }
    fun loadPosts() {
        _data.postValue(FeedModel(loading = true))
        repository.getAllAsync(object : PostRepository.Callback<List<Post>> {
            override fun onSuccess(posts: List<Post>) {
                _data.value = FeedModel(posts = posts, empty = posts.isEmpty())
            }

            override fun onError(e: Exception) {
                _data.value = FeedModel(error = true)
            }
        })
    }


    fun save() {
        edited.value?.let {
            repository.saveAsync(it, object : PostRepository.Callback<Post> {
                override fun onSuccess(post: Post) {
                    val updatedPosts = _data.value?.posts?.map { editeee ->
                        if (editeee.id == post.id) post else editeee

                    }.orEmpty()

                    _data.value = _data.value?.copy(posts = updatedPosts)
                    _postCreated.value = Unit
                }


                override fun onError(e: Exception) {
                    _data.value = _data.value?.copy(errorCRUD = true)
                    _data.value = FeedModel(posts = _data.value?.posts ?: emptyList())
                }
            })


        }
        edited.value = empty
    }

    fun changeContent(content: String) {
        val text = content.trim()
        if (edited.value?.content == text) {
            return
        }
        edited.value = edited.value?.copy(content = text)
    }

    fun edit(post: Post) {
        edited.value = post
    }

    fun likeById(post: Post) {
        if (post.likedByMe) {
            repository.unLikeAsync(post.id, object : PostRepository.Callback<Post> {
                override fun onSuccess(post: Post) {
                    val updatedPosts = _data.value?.posts?.map {
                        if (it.id == post.id) post else it
                    }.orEmpty()

                    _data.value = FeedModel(posts = updatedPosts)

                }

                override fun onError(e: Exception) {
                    _data.value = _data.value?.copy(errorCRUD = true)
                    _data.value = FeedModel(posts = _data.value?.posts ?: emptyList())
                }
            })
        } else {
            repository.likeAsync(post.id, object : PostRepository.Callback<Post> {
                override fun onSuccess(post: Post) {
                    val updatedPosts = _data.value?.posts?.map {
                        if (it.id == post.id) post else it
                    }.orEmpty()

                    _data.value = FeedModel(posts = updatedPosts)

                }

                override fun onError(e: Exception) {
                    _data.value = _data.value?.copy(errorCRUD = true)
                    _data.value = FeedModel(posts = _data.value?.posts ?: emptyList())
                }
            })
        }

    }

    fun repostById(id: Long) {
        repository.repostByIdAsync(id, object : PostRepository.Callback<Post> {
            override fun onSuccess(post: Post) {
            }

            override fun onError(e: Exception) {
                _data.value = FeedModel(errorCRUD = true)
            }
        })
    }

    fun removeById(id: Long) {
        //val rr = _data.value?.posts.orEmpty()
        repository.removeByIdAsync(id, object : PostRepository.Callback<Unit> {
            override fun onSuccess(unit: Unit) {
                _data.value =
                    _data.value?.copy(posts = _data.value?.posts.orEmpty().filter { it.id != id })
            }

            override fun onError(e: Exception) {
                _data.value = _data.value?.copy(errorCRUD = true)
                _data.value = FeedModel(posts = _data.value?.posts ?: emptyList())
            }
        })
    }


}
