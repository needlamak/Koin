package com.koin.di;

import com.koin.data.user.UserDao;
import com.koin.data.user.UserDatabase;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class UserDatabaseModule_ProvideUserDaoFactory implements Factory<UserDao> {
  private final Provider<UserDatabase> dbProvider;

  public UserDatabaseModule_ProvideUserDaoFactory(Provider<UserDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public UserDao get() {
    return provideUserDao(dbProvider.get());
  }

  public static UserDatabaseModule_ProvideUserDaoFactory create(Provider<UserDatabase> dbProvider) {
    return new UserDatabaseModule_ProvideUserDaoFactory(dbProvider);
  }

  public static UserDao provideUserDao(UserDatabase db) {
    return Preconditions.checkNotNullFromProvides(UserDatabaseModule.INSTANCE.provideUserDao(db));
  }
}
