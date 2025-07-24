package com.koin.authentication.data // Ensure this matches your actual package

import com.amplifyframework.auth.AuthException
import com.amplifyframework.auth.AuthUserAttributeKey
import com.amplifyframework.auth.cognito.AWSCognitoAuthSession
import com.amplifyframework.auth.options.AuthSignUpOptions
import com.amplifyframework.auth.result.AuthSignInResult
import com.amplifyframework.auth.result.AuthSignUpResult
import com.amplifyframework.core.Amplify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.resume
/**
 * Concrete implementation of the AuthRepository interface using AWS Amplify.
 *
 * @Inject constructor() tells Hilt how to create an instance of this class.
 */
class AuthRepositoryImpl @Inject constructor() : AuthRepository { // Add @Inject to the constructor

    override suspend fun signUp(email: String, password: String): Result<String> =
        withContext(Dispatchers.IO) {
            suspendCancellableCoroutine { continuation ->
                val options = AuthSignUpOptions.builder()
                    .userAttribute(AuthUserAttributeKey.email(), email)
                    .build()

                Amplify.Auth.signUp(
                    email,
                    password,
                    options,
                    { result: AuthSignUpResult ->
                        if (result.isSignUpComplete) {
                            continuation.resume(Result.success("Sign up successful"))
                        } else {
                            continuation.resume(Result.success("Confirmation required"))
                        }
                    },
                    { error: AuthException ->
                        continuation.resume(
                            Result.failure(
                                Exception(
                                    error.message ?: "Sign up failed"
                                )
                            )
                        )
                    }
                )
            }
        }

    override suspend fun confirmSignUp(email: String, confirmationCode: String): Result<Unit> =
        withContext(Dispatchers.IO) {
            suspendCancellableCoroutine { continuation ->
                Amplify.Auth.confirmSignUp(
                    email,
                    confirmationCode,
                    {
                        continuation.resume(Result.success(Unit))
                    },
                    { error: AuthException ->
                        continuation.resume(
                            Result.failure(
                                Exception(
                                    error.message ?: "Confirmation failed"
                                )
                            )
                        )
                    }
                )
            }
        }

    override suspend fun signIn(email: String, password: String): Result<String> =
        withContext(Dispatchers.IO) {
            suspendCancellableCoroutine { continuation ->
                Amplify.Auth.signIn(
                    email,
                    password,
                    { result: AuthSignInResult ->
                        if (result.isSignedIn) {
                            continuation.resume(Result.success("Sign in successful"))
                        } else {
                            continuation.resume(Result.failure(Exception("Sign in not complete")))
                        }
                    },
                    { error: AuthException ->
                        continuation.resume(
                            Result.failure(
                                Exception(
                                    error.message ?: "Sign in failed"
                                )
                            )
                        )
                    }
                )
            }
        }

    override suspend fun getCurrentUser(): Result<String> =
        withContext(Dispatchers.IO) {
            suspendCancellableCoroutine { continuation ->
                Amplify.Auth.getCurrentUser(
                    { user ->
                        continuation.resume(Result.success(user.username))
                    },
                    { error: AuthException ->
                        continuation.resume(
                            Result.failure(
                                Exception(
                                    error.message ?: "Get current user failed"
                                )
                            )
                        )
                    }
                )
            }
        }

    override suspend fun getCurrentUserToken(): Result<String> =
        withContext(Dispatchers.IO) {
            try {
                suspendCancellableCoroutine { continuation ->
                    Amplify.Auth.fetchAuthSession(
                        { session ->
                            try {
                                val cognitoSession = session as AWSCognitoAuthSession
                                val tokenResult = cognitoSession.userPoolTokensResult
                                val idToken = tokenResult.value?.idToken

                                if (idToken != null && tokenResult.type.name == "SUCCESS") {
                                    continuation.resume(Result.success(idToken) as Result<String>)
                                } else {
                                    continuation.resume(Result.failure(Exception("Token unavailable or fetch failed")))
                                }
                            } catch (e: Exception) {
                                continuation.resume(Result.failure(e))
                            }
                        },
                        { error: AuthException ->
                            continuation.resume(
                                Result.failure(
                                    Exception(
                                        error.message ?: "Session fetch failed"
                                    )
                                )
                            )
                        }
                    )
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    override suspend fun signOut(): Result<Unit> =
        withContext(Dispatchers.IO) {
            suspendCancellableCoroutine { continuation ->
                Amplify.Auth.signOut(
                    {
                        continuation.resume(Result.success(Unit))
                    }
                )
            }
        }

    override suspend fun isUserSignedIn(): Boolean =
        withContext(Dispatchers.IO) {
            suspendCancellableCoroutine { continuation ->
                Amplify.Auth.fetchAuthSession(
                    { session ->
                        continuation.resume(session.isSignedIn)
                    },
                    { _: AuthException ->
                        continuation.resume(false)
                    }
                )
            }
        }
}


//package com.koin.authentication.data
//
//import com.amplifyframework.auth.AuthException
//import com.amplifyframework.auth.AuthUserAttributeKey
//import com.amplifyframework.auth.cognito.AWSCognitoAuthSession
//import com.amplifyframework.auth.options.AuthSignUpOptions
//import com.amplifyframework.auth.result.AuthSignInResult
//import com.amplifyframework.auth.result.AuthSignUpResult
//import com.amplifyframework.core.Amplify
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.suspendCancellableCoroutine
//import kotlinx.coroutines.withContext
//import kotlin.coroutines.resume
//
//sealed class SessionResult<out T> {
//    data class Success<T>(val value: T) : SessionResult<T>()
//    data class Failure(val error: AuthException) : SessionResult<Nothing>()
//}
//
//class AuthRepositoryImpl : AuthRepository {
//
//    override suspend fun signUp(email: String, password: String): Result<String> =
//        withContext(Dispatchers.IO) {
//            suspendCancellableCoroutine { continuation ->
//                val options = AuthSignUpOptions.builder()
//                    .userAttribute(AuthUserAttributeKey.email(), email)
//                    .build()
//
//                Amplify.Auth.signUp(
//                    email,
//                    password,
//                    options,
//                    { result: AuthSignUpResult ->
//                        if (result.isSignUpComplete) {
//                            continuation.resume(Result.success("Sign up successful"))
//                        } else {
//                            continuation.resume(Result.success("Confirmation required"))
//                        }
//                    },
//                    { error: AuthException ->
//                        continuation.resume(
//                            Result.failure(
//                                Exception(
//                                    error.message ?: "Sign up failed"
//                                )
//                            )
//                        )
//                    }
//                )
//            }
//        }
//
//    override suspend fun confirmSignUp(email: String, confirmationCode: String): Result<Unit> =
//        withContext(Dispatchers.IO) {
//            suspendCancellableCoroutine { continuation ->
//                Amplify.Auth.confirmSignUp(
//                    email,
//                    confirmationCode,
//                    {
//                        continuation.resume(Result.success(Unit))
//                    },
//                    { error: AuthException ->
//                        continuation.resume(
//                            Result.failure(
//                                Exception(
//                                    error.message ?: "Confirmation failed"
//                                )
//                            )
//                        )
//                    }
//                )
//            }
//        }
//
//    override suspend fun signIn(email: String, password: String): Result<String> =
//        withContext(Dispatchers.IO) {
//            suspendCancellableCoroutine { continuation ->
//                Amplify.Auth.signIn(
//                    email,
//                    password,
//                    { result: AuthSignInResult ->
//                        if (result.isSignedIn) {
//                            continuation.resume(Result.success("Sign in successful"))
//                        } else {
//                            continuation.resume(Result.failure(Exception("Sign in not complete")))
//                        }
//                    },
//                    { error: AuthException ->
//                        continuation.resume(
//                            Result.failure(
//                                Exception(
//                                    error.message ?: "Sign in failed"
//                                )
//                            )
//                        )
//                    }
//                )
//            }
//        }
//
//    override suspend fun getCurrentUser(): Result<String> =
//        withContext(Dispatchers.IO) {
//            suspendCancellableCoroutine { continuation ->
//                Amplify.Auth.getCurrentUser(
//                    { user ->
//                        continuation.resume(Result.success(user.username))
//                    },
//                    { error: AuthException ->
//                        continuation.resume(
//                            Result.failure(
//                                Exception(
//                                    error.message ?: "Get current user failed"
//                                )
//                            )
//                        )
//                    }
//                )
//            }
//        }
//
//    override suspend fun getCurrentUserToken(): Result<String> =
//        withContext(Dispatchers.IO) {
//            try {
//                suspendCancellableCoroutine { continuation ->
//                    Amplify.Auth.fetchAuthSession(
//                        { session ->
//                            try {
//                                val cognitoSession = session as AWSCognitoAuthSession
//                                val tokenResult = cognitoSession.userPoolTokensResult
//                                val idToken = tokenResult.value?.idToken
//
//                                if (idToken != null && tokenResult.type.name == "SUCCESS") {
//                                    continuation.resume(Result.success(idToken))
//                                } else {
//                                    continuation.resume(Result.failure(Exception("Token unavailable or fetch failed")))
//                                }
//                            } catch (e: Exception) {
//                                continuation.resume(Result.failure(e))
//                            }
//                        },
//                        { error: AuthException ->
//                            continuation.resume(
//                                Result.failure(
//                                    Exception(
//                                        error.message ?: "Session fetch failed"
//                                    )
//                                )
//                            )
//                        }
//                    )
//                }
//            } catch (e: Exception) {
//                Result.failure(e)
//            }
//        }
//
//    override suspend fun signOut(): Result<Unit> =
//        withContext(Dispatchers.IO) {
//            suspendCancellableCoroutine { continuation ->
//                Amplify.Auth.signOut(
//                    {
//                        continuation.resume(Result.success(Unit))
//                    }
//                )
//            }
//        }
//
//    override suspend fun isUserSignedIn(): Boolean =
//        withContext(Dispatchers.IO) {
//            suspendCancellableCoroutine { continuation ->
//                Amplify.Auth.fetchAuthSession(
//                    { session ->
//                        continuation.resume(session.isSignedIn)
//                    },
//                    { _: AuthException ->
//                        continuation.resume(false)
//                    }
//                )
//            }
//        }
//}

