package com.koin.authentication.api

import com.koin.authentication.data.AuthRepository
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val authRepository: AuthRepository
) : Interceptor {
    
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        
        return runBlocking {
            authRepository.getCurrentUserToken()
                .fold(
                    onSuccess = { token ->
                        val authenticatedRequest = originalRequest.newBuilder()
                            .header("Authorization", "Bearer $token")
                            .build()
                        chain.proceed(authenticatedRequest)
                    },
                    onFailure = {
                        chain.proceed(originalRequest)
                    }
                )
        }
    }
}