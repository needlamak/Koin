package com.koin.ui.localauth

import com.koin.data.session.SessionManager
import com.koin.domain.user.User
import com.koin.domain.user.UserRepository
import com.koin.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class LocalAuthViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val sessionManager: SessionManager
) : BaseViewModel<AuthUiState, AuthUiEvent>() {

    override val _uiState: MutableStateFlow<AuthUiState> = MutableStateFlow(AuthUiState())

    override fun handleEvent(event: AuthUiEvent) {
        when (event) {
            is AuthUiEvent.Register -> register(event.name, event.email)
        }
    }

    private fun register(name: String, email: String) {
        execute(onError = { e ->
            _uiState.update { it.copy(error = e.message) }
        }) {
            val user = User(username = name, email = email)
            val insertedId = userRepository.upsert(user)
            sessionManager.login(insertedId)
            _uiState.update { it.copy(isRegistered = true) }
        }
    }
}

data class AuthUiState(
    val isRegistered: Boolean = false,
    val error: String? = null
)

sealed class AuthUiEvent {
    data class Register(val name: String, val email: String) : AuthUiEvent()
}