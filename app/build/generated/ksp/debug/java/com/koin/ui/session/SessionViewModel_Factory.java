package com.koin.ui.session;

import com.koin.data.session.SessionManager;
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
public final class SessionViewModel_Factory implements Factory<SessionViewModel> {
  private final Provider<SessionManager> sessionManagerProvider;

  public SessionViewModel_Factory(Provider<SessionManager> sessionManagerProvider) {
    this.sessionManagerProvider = sessionManagerProvider;
  }

  @Override
  public SessionViewModel get() {
    return newInstance(sessionManagerProvider.get());
  }

  public static SessionViewModel_Factory create(Provider<SessionManager> sessionManagerProvider) {
    return new SessionViewModel_Factory(sessionManagerProvider);
  }

  public static SessionViewModel newInstance(SessionManager sessionManager) {
    return new SessionViewModel(sessionManager);
  }
}
