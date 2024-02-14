package ru.netology.nmedia.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.netology.nmedia.api.ApiService
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.model.FeedModel
import ru.netology.nmedia.model.FeedModelState
import ru.netology.nmedia.repository.PostRepository
import javax.inject.Inject

@HiltViewModel
class AuthFragmentViewModel @Inject constructor(
    appAuth: AppAuth,
    private val postRepository: PostRepository
)  : ViewModel() {
    private val _state = MutableLiveData(FeedModelState())
    val state: LiveData<FeedModelState>
        get() = _state

    fun signIn(login: String, password: String) {
        viewModelScope.launch {
            _state.value = FeedModelState(loading = true)
            _state.value = try {
                postRepository.signIn(login, password)
                FeedModelState()
            } catch (e: RuntimeException) {
               FeedModelState(error = true)
            }
        }


    }


}
