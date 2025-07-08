package com.koin.data.user;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata("javax.inject.Singleton")
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
public final class LocalUserRepository_Factory implements Factory<LocalUserRepository> {
  private final Provider<UserDao> userDaoProvider;

  public LocalUserRepository_Factory(Provider<UserDao> userDaoProvider) {
    this.userDaoProvider = userDaoProvider;
  }

  @Override
  public LocalUserRepository get() {
    return newInstance(userDaoProvider.get());
  }

  public static LocalUserRepository_Factory create(Provider<UserDao> userDaoProvider) {
    return new LocalUserRepository_Factory(userDaoProvider);
  }

  public static LocalUserRepository newInstance(UserDao userDao) {
    return new LocalUserRepository(userDao);
  }
}
