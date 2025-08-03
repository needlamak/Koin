package com.koin.ui.authentication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.koin.data.auth.data.AuthRepository
import com.koin.data.session.SessionManager
import com.koin.data.user.UserEntity
import com.koin.data.user.toDomain
import com.koin.domain.user.User
import com.koin.domain.user.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class AuthState {
    object Initial : AuthState()
    data class SignUpSuccess(val message: String) : AuthState()
    object SignUpConfirmed : AuthState()
    data class SignedIn(val username: String) : AuthState()
    data class Error(val message: String) : AuthState()
}

@HiltViewModel
class CognitoAuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Initial)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun signUp(username: String, email: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            authRepository.signUp(email, password)
                .onSuccess { message ->
                    // Create user locally but don't login yet (needs confirmation)
                    val user = UserEntity(username = username, email = email)
                    userRepository.upsert(user.toDomain())
                    _authState.value = AuthState.SignUpSuccess(message)
                }
                .onFailure { error ->
                    _authState.value = AuthState.Error(error.message ?: "Sign up failed")
                }
            _isLoading.value = false
        }
    }

    fun confirmSignUp(email: String, code: String) {
        viewModelScope.launch {
            _isLoading.value = true
            authRepository.confirmSignUp(email, code)
                .onSuccess {
                    _authState.value = AuthState.SignUpConfirmed
                }
                .onFailure { error ->
                    _authState.value = AuthState.Error(error.message ?: "Confirmation failed")
                }
            _isLoading.value = false
        }
    }

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            authRepository.signIn(email, password)
                .onSuccess { username ->
                    // Find or create local user
                    val localUser = findOrCreateLocalUser(username, email)
                    localUser?.let { user ->
                        sessionManager.login(user.id)
                        _authState.value = AuthState.SignedIn(username)
                    } ?: run {
                        _authState.value = AuthState.Error("Failed to create local user")
                    }
                }
                .onFailure { error ->
                    _authState.value = AuthState.Error(error.message ?: "Sign in failed")
                }
            _isLoading.value = false
        }
    }

    private suspend fun findOrCreateLocalUser(username: String, email: String): User? {
        return try {
            val existingUser = userRepository.users().first()
                .find { it.username == username || it.email == email }

            existingUser ?: run {
                val newUser = User(username = username, email = email)
                val userId = userRepository.upsert(newUser)
                newUser.copy(id = userId)
            }
        } catch (_: Exception) {
            null
        }
    }

    fun getCurrentToken(): Flow<String?> = flow {
        authRepository.getCurrentUserToken()
            .onSuccess { token -> emit(token) }
            .onFailure { emit(null) }
    }
}

