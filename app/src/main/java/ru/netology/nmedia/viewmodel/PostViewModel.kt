package ru.netology.nmedia.viewmodel

import android.app.Application
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.util.Util
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.FeedModel
import ru.netology.nmedia.model.FeedModelState
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryImpl
import ru.netology.nmedia.util.SingleLiveEvent


private val empty = Post(
    id = 0, content = "", author = "", likedByMe = false, published = "", authorAvatar = ""

)

class PostViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: PostRepository =
        PostRepositoryImpl(AppDb.getInstance(application).postDao())
    val data: LiveData<FeedModel> = repository.data.map {
        FeedModel(posts = it, empty = it.isEmpty())
    }
    private val _state = MutableLiveData(FeedModelState())
    val state: LiveData<FeedModelState>
        get() = _state
    private val edited = MutableLiveData(empty)
    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated

    init {
        loadPosts()
    }

    fun loadPosts() {
        viewModelScope.launch {
            _state.value = FeedModelState(loading = true)
            _state.value = try {
                repository.getAll()
                FeedModelState()
            } catch (e: Exception) {
                FeedModelState(error = true)
            }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _state.value = FeedModelState(refreshing = true)
            _state.value = try {
                repository.getAll()
                FeedModelState()
            } catch (e: Exception) {
                FeedModelState(error = true)
            }
        }
    }


    fun save() {
        edited.value?.let {
            viewModelScope.launch {

                _postCreated.value = Unit
                viewModelScope.launch {
                    try {
                        repository.save(it)
                        _state.value = FeedModelState()
                    } catch (e: Exception) {
                        _state.value = FeedModelState(error = true)
                    }
                }

            }
        }
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

    fun likeById(id: Long) {
        viewModelScope.launch {
            _state.value = try {
                repository.likeById(id)
                FeedModelState()

            } catch (e: Exception) {
                FeedModelState(error = true)
            }
        }
    }
    fun repostById(id: Long) {
        viewModelScope.launch {
            _state.value = try {
                withContext(Dispatchers.IO) {
                    repository.repostById(id)
                }
                FeedModelState()
            } catch (e: Exception) {
                FeedModelState(error = true)
            }

        }
    }

    fun removeById(id: Long) {
        viewModelScope.launch {
            _state.value = try {
                repository.removeById(id)
                FeedModelState()

            } catch (e: Exception) {
                FeedModelState(error = true)
            }
        }

    }


}
