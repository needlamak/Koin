package com.koin.ui.profile

import androidx.lifecycle.viewModelScope
import com.koin.data.session.SessionManager
import com.koin.domain.user.User
import com.koin.domain.user.UserRepository
import com.koin.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: UserRepository,
    private val sessionManager: SessionManager
) : BaseViewModel<ProfileUiState, ProfileUiEvent>() {

    override val _uiState: MutableStateFlow<ProfileUiState> = MutableStateFlow(ProfileUiState())

    init {
        loadUser()
    }

    private fun loadUser() {
        // Assume single user with id = 1 for now
        viewModelScope.launch {
            repository.user(1L)
                .catch { e ->
                    _uiState.update { it.copy(error = e.message, isLoading = false) }
                }
                .collectLatest { user ->
                    _uiState.update { it.copy(user = user, isLoading = false) }
                }
        }
    }

    override fun handleEvent(event: ProfileUiEvent) {
        when (event) {
            is ProfileUiEvent.Save -> save(event.name, event.email, event.bio, event.avatarUri)
            ProfileUiEvent.Refresh -> loadUser()
        }
    }

    private fun save(name: String, email: String, bio: String?, avatarUri: String?) {
        viewModelScope.launch {
            try {
                val user = User(
                    id = _uiState.value.user?.id ?: 0,
                    name = name,
                    email = email,
                    bio = bio,
                    avatarUri = avatarUri
                )
                repository.upsert(user)
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            sessionManager.logout()
            _uiState.update { it.copy(loggedOut = true) }
        }
    }
}

data class ProfileUiState(
    val user: User? = null,
    val isLoading: Boolean = true,
    val error: String? = null,
    val loggedOut: Boolean = false
)


sealed class ProfileUiEvent {
    data class Save(
        val name: String,
        val email: String,
        val bio: String?,
        val avatarUri: String?
    ) : ProfileUiEvent()

    object Refresh : ProfileUiEvent()
}
