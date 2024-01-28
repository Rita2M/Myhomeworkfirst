package ru.netology.nmedia.viewmodel

import android.app.Application
import android.net.Uri
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.util.Util
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.FeedModel
import ru.netology.nmedia.model.FeedModelState
import ru.netology.nmedia.model.PhotoModel
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryImpl
import ru.netology.nmedia.url.UrlProvider
import ru.netology.nmedia.util.SingleLiveEvent
import java.io.File


private val empty = Post(
    id = 0, content = "", author = "", likedByMe = false, published = "", authorAvatar = ""

)

class PostViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: PostRepository =
        PostRepositoryImpl(AppDb.getInstance(application).postDao())
    val data: LiveData<FeedModel> = repository.data.map {
        FeedModel(posts = it, empty = it.isEmpty())
    }.asLiveData(Dispatchers.Default)
    val newerCount : LiveData<Int> = data.switchMap {
        val firstId = it.posts.firstOrNull()?.id ?: 0L
            repository.getNewerCount(firstId).asLiveData(Dispatchers.Default)

    }
    private val _photo = MutableLiveData<PhotoModel?>(null)
    val photo : LiveData<PhotoModel?>
        get() = _photo
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
    fun setPhoto(uri: Uri, file: File){
        _photo.value = PhotoModel(uri, file)
    }
  fun readdd(){
      viewModelScope.launch{
          repository.readAll()
      }
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
                    try {
                        val photoModel = _photo.value
                        if (photoModel == null) {
                            repository.save(it)
                        }else{
                            repository.saveWithAttachment(it, photoModel)
                        }

                        _state.value = FeedModelState()
                        _postCreated.value = Unit
                    } catch (e: Exception) {
                        _state.value = FeedModelState(error = true)
                    }
                }


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
    fun changePhoto(uri: Uri, file: File) {
        _photo.value = PhotoModel(uri, file)
    }

    fun edit(post: Post) {
        edited.value = post
    }

    fun likeById(post: Post) {
        viewModelScope.launch {
            _state.value = try {
                repository.likeById(post)
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
    fun clearPhoto(){
        _photo.value = null
    }
    fun getPhoto(string: String){
        viewModelScope.launch {
            UrlProvider.getMediaUrl(string)
        }
    }




}
