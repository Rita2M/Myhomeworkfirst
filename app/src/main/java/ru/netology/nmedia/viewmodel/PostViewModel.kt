package ru.netology.nmedia.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.FeedModelState
import ru.netology.nmedia.model.PhotoModel
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.url.UrlProvider
import ru.netology.nmedia.util.SingleLiveEvent
import java.io.File
import javax.inject.Inject


private val empty = Post(
    id = 0,
    authorId = 0,
    content = "",
    author = "",
    likedByMe = false,
    published = "",
    authorAvatar = ""

)

//@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class PostViewModel @Inject constructor(
    private val postRepository: PostRepository,
    appAuth: AppAuth,
) : ViewModel() {
    val data: Flow<PagingData<Post>> = postRepository
        .data.cachedIn(viewModelScope)

    private val _photo = MutableLiveData<PhotoModel?>(null)
    val photo: LiveData<PhotoModel?>
        get() = _photo
    private val _state = MutableLiveData(FeedModelState())
    val state: LiveData<FeedModelState>
        get() = _state
    private val edited = MutableLiveData(empty)
    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated

    fun setPhoto(uri: Uri, file: File) {
        _photo.value = PhotoModel(uri, file)
    }
//    init {
//        loadPosts()
//    }
//    private fun reed(){
//        viewModelScope.launch {
//            postRepository.readAll()
//        }
//    }
//private fun loadPosts() = viewModelScope.launch {
//    try {
//        _state.value = FeedModelState(loading = true)
//        postRepository.data.collectLatest {
//            _state.value = FeedModelState()
//        }
//        _state.value = FeedModelState()
//    } catch (e: Exception) {
//        _state.value = FeedModelState(error = true)
//    }
//}




    fun save() {
        edited.value?.let {
            viewModelScope.launch {
                try {
                    val photoModel = _photo.value
                    if (photoModel == null) {
                        postRepository.save(it)
                    } else {
                        postRepository.saveWithAttachment(it, photoModel)
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
                postRepository.likeById(post)
                FeedModelState()

            } catch (e: RuntimeException) {
                FeedModelState(error = true)

            }
        }
    }

    fun repostById(id: Long) {
        viewModelScope.launch {
            _state.value = try {
                withContext(Dispatchers.IO) {
                    postRepository.repostById(id)
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
                postRepository.removeById(id)
                FeedModelState()

            } catch (e: Exception) {
                FeedModelState(error = true)
            }
        }

    }

    fun clearPhoto() {
        _photo.value = null
    }

    fun getPhoto(string: String) {
        viewModelScope.launch {
            UrlProvider.getMediaUrl(string)
        }
    }


}
