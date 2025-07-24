package com.koin.authentication.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.koin.authentication.data.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject

public sealed class AuthState {
    object Initial : AuthState()
    object SignedOut : AuthState()
    object Loading : AuthState()
    object Idle: AuthState()
    object Success: AuthState()

    data class SignUpSuccess(val message: String) : AuthState()
    object SignUpConfirmed : AuthState()
    data class SignedIn(val username: String) : AuthState()
    data class Error(val message: String) : AuthState()
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    
    private val _authState = MutableStateFlow<AuthState>(AuthState.Initial)
    private val _uiState: MutableStateFlow<AuthState> = MutableStateFlow(AuthState.Initial)
    val uiState: StateFlow<AuthState> = _uiState


    val authState: StateFlow<AuthState> = _authState.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    fun signUp(username: String, email: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            authRepository.signUp(email, password)
                .onSuccess { message ->
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
                .onSuccess { message ->
                    _authState.value = AuthState.SignedIn(message)
                }
                .onFailure { error ->
                    _authState.value = AuthState.Error(error.message ?: "Sign in failed")
                }
            _isLoading.value = false
        }
    }
    
    fun signOut() {
        viewModelScope.launch {
            authRepository.signOut()
                .onSuccess {
                    _authState.value = AuthState.SignedOut
                }
                .onFailure { error ->
                    _authState.value = AuthState.Error(error.message ?: "Sign out failed")
                }
        }
    }
    
    fun checkAuthStatus() {
        viewModelScope.launch {
            if (authRepository.isUserSignedIn()) {
                authRepository.getCurrentUser()
                    .onSuccess { username ->
                        _authState.value = AuthState.SignedIn(username)
                    }
                    .onFailure {
                        _authState.value = AuthState.SignedOut
                    }
            } else {
                _authState.value = AuthState.SignedOut
            }
        }
    }
    
    fun getCurrentToken(): Flow<String?> = flow {
        authRepository.getCurrentUserToken()
            .onSuccess { token ->
                emit(token)
            }
            .onFailure {
                emit(null)
            }
    }
}

