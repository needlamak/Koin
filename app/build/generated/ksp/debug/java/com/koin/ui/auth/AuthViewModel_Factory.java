package com.koin.ui.auth;

import com.koin.data.session.SessionManager;
import com.koin.domain.user.UserRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast",
    "deprecation",
    "nullness:initialization.field.uninitialized"
})
public final class AuthViewModel_Factory implements Factory<AuthViewModel> {
  private final Provider<UserRepository> userRepositoryProvider;

  private final Provider<SessionManager> sessionManagerProvider;

  public AuthViewModel_Factory(Provider<UserRepository> userRepositoryProvider,
      Provider<SessionManager> sessionManagerProvider) {
    this.userRepositoryProvider = userRepositoryProvider;
    this.sessionManagerProvider = sessionManagerProvider;
  }

  @Override
  public AuthViewModel get() {
    return newInstance(userRepositoryProvider.get(), sessionManagerProvider.get());
  }

  public static AuthViewModel_Factory create(Provider<UserRepository> userRepositoryProvider,
      Provider<SessionManager> sessionManagerProvider) {
    return new AuthViewModel_Factory(userRepositoryProvider, sessionManagerProvider);
  }

  public static AuthViewModel newInstance(UserRepository userRepository,
      SessionManager sessionManager) {
    return new AuthViewModel(userRepository, sessionManager);
  }
}
