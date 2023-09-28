package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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
        repository.getAllAsync(object : PostRepository.GetAllCallback {
            override fun onSuccess(posts: List<Post>) {
                _data.postValue(FeedModel(posts = posts, empty = posts.isEmpty()))
                }

            override fun onError(e: Exception) {
                _data.postValue(FeedModel(error = true))
            }
        })
    }


    fun save() {
        edited.value?.let {
            repository.saveAsync(it, object : PostRepository.SaveCallback {
                override fun onSuccess(post: Post) {
                    _postCreated.postValue(Unit)
                }

                override fun onError(e: Exception) {
                    _data.postValue(FeedModel(error = true))
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
            repository.unLikeAsync(post.id, object : PostRepository.LikeCallback {
                override fun onSuccess(post: Post) {
                    val updatedPosts = _data.value?.posts?.map {
                        if (it.id == post.id) post else it
                    }.orEmpty()
                    _data.postValue(FeedModel(posts = updatedPosts))
                }

                override fun onError(e: Exception) {
                    e.printStackTrace()

                }
            })
        } else {
            repository.likeAsync(post.id, object : PostRepository.LikeCallback {
                override fun onSuccess(post: Post) {
                    val updatedPosts = _data.value?.posts?.map {
                        if (it.id == post.id) post else it
                    }.orEmpty()
                    _data.postValue(FeedModel(posts = updatedPosts))

                }

                override fun onError(e: Exception) {
                    e.printStackTrace()

                }
            })
        }
    }

    fun repostById(id: Long) {
         repository.repostByIdAsync(id, object : PostRepository.RemoveCallback {
             override fun onSuccess(id: Long) {
             }

             override fun onError(e: Exception) {
                 _data.postValue(FeedModel(error = true))
             }
         })
    }

//    fun removeById(id: Long) {
//        thread {
//            // Оптимистичная модель
//            val old = _data.value?.posts.orEmpty()
//            _data.postValue(_data.value?.copy(
//                posts = _data.value?.posts.orEmpty().filter { it.id != id }))
//            try {
//                repository.removeById(id)
//            } catch (e: IOException) {
//                _data.postValue(_data.value?.copy(posts = old))
//            }
//        }
//    }
    fun removeById(id: Long){
       val rr =  _data.value?.posts.orEmpty()
    repository.removeByIdAsync(id, object : PostRepository.RemoveCallback{
        override fun onSuccess(id: Long) {
            _data.postValue(_data.value?.copy(posts = _data.value?.posts.orEmpty().filter { it.id != id }))
        }

        override fun onError(e: Exception) {
            _data.postValue(_data.value?.copy(posts = rr))
        }
    })
    }


}
