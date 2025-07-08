package com.koin.ui.profile;

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
public final class ProfileViewModel_Factory implements Factory<ProfileViewModel> {
  private final Provider<UserRepository> repositoryProvider;

  private final Provider<SessionManager> sessionManagerProvider;

  public ProfileViewModel_Factory(Provider<UserRepository> repositoryProvider,
      Provider<SessionManager> sessionManagerProvider) {
    this.repositoryProvider = repositoryProvider;
    this.sessionManagerProvider = sessionManagerProvider;
  }

  @Override
  public ProfileViewModel get() {
    return newInstance(repositoryProvider.get(), sessionManagerProvider.get());
  }

  public static ProfileViewModel_Factory create(Provider<UserRepository> repositoryProvider,
      Provider<SessionManager> sessionManagerProvider) {
    return new ProfileViewModel_Factory(repositoryProvider, sessionManagerProvider);
  }

  public static ProfileViewModel newInstance(UserRepository repository,
      SessionManager sessionManager) {
    return new ProfileViewModel(repository, sessionManager);
  }
}
