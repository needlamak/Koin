package com.koin.ui.session

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.koin.authentication.data.AuthRepository
import com.koin.data.session.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SessionViewModel @Inject constructor(
    private val sessionManager: SessionManager,
    private val authRepository: AuthRepository
) : ViewModel() {

    val isLoggedIn: StateFlow<Boolean> = sessionManager.isLoggedIn
        .stateIn(viewModelScope, SharingStarted.Lazily, false)

    val userId: StateFlow<Long?> = sessionManager.userId
        .stateIn(viewModelScope, SharingStarted.Lazily, null)

    init {
        checkAuthOnStartup()
    }

    private fun checkAuthOnStartup() {
        viewModelScope.launch {
            // Check if user is signed in with Cognito
            if (authRepository.isUserSignedIn()) {
                // Verify local session matches
                val localLoggedIn = sessionManager.isLoggedIn.first()
                if (!localLoggedIn) {
                    // Sync local session with Cognito state
                    authRepository.getCurrentUser()
                        .onSuccess { username ->
                            // Find user in local DB and login
                            // This would need UserRepository injection
                        }
                        .onFailure {
                            // Cognito says signed in but can't get user - sign out
                            authRepository.signOut()
                            sessionManager.logout()
                        }
                }
            } else {
                // Not signed in with Cognito - clear local session
                sessionManager.logout()
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.signOut()
            sessionManager.logout()
        }
    }
}

//@HiltViewModel
//class SessionViewModel @Inject constructor(
//    private val sessionManager: SessionManager
//) : ViewModel() {
//    val isLoggedIn: StateFlow<Boolean> = sessionManager.isLoggedIn.stateIn(
//        viewModelScope,
//        SharingStarted.WhileSubscribed(5_000),
//        false
//    )
//
//    suspend fun logout() = sessionManager.logout()
//}
