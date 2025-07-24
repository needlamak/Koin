package com.koin.authentication.data

import com.koin.data.user.UserEntity

interface AuthRepository  {
    suspend fun signUp(email: String, password: String): Result<String>
    suspend fun confirmSignUp(email: String, confirmationCode: String): Result<Unit>
    suspend fun signIn(email: String, password: String): Result<String>
    suspend fun getCurrentUser(): Result<String>
    suspend fun signOut(): Result<Unit>
    suspend fun isUserSignedIn(): Boolean
    suspend fun getCurrentUserToken(): Result<String>

    // Local user management
    suspend fun upsertUser(user: UserEntity): Long
    suspend fun getUser(userId: Long): UserEntity?
    suspend fun deleteUser(user: UserEntity)
}