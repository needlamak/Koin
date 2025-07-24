package com.koin.authentication.data


interface AuthRepository  {
    suspend fun signUp(email: String, password: String): Result<String>
    suspend fun confirmSignUp(email: String, confirmationCode: String): Result<Unit>
    suspend fun signIn(email: String, password: String): Result<String>
    suspend fun getCurrentUser(): Result<String>
    suspend fun signOut(): Result<Unit>
    suspend fun isUserSignedIn(): Boolean
    suspend fun getCurrentUserToken(): Result<String>
}