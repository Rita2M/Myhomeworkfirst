package ru.netology.nmedia.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.netology.nmedia.api.PostApi
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.model.FeedModelState


class AuthFragmentViewModel : ViewModel() {
    private val _state = MutableLiveData(FeedModelState())
    val state: LiveData<FeedModelState>
        get() = _state

    suspend fun signIn(login: String, password: String) {
        try {
            val response = PostApi.retrofitService.updateUser(login, password)
            if (response.code() == 404) {
                _state.value = FeedModelState(error = true)
            }
            if (response.isSuccessful) {
                val authState = response.body()
                if (authState != null) {
                    AppAuth.getInstance().setAuth(authState.id, authState.token)
                    FeedModelState()
                } else throw java.lang.RuntimeException("body is null")

            }

        } catch (e: RuntimeException) {
            throw java.lang.RuntimeException(e)

        }


    }


}
