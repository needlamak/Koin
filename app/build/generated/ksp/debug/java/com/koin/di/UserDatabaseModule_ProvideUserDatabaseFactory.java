package com.koin.di;

import android.content.Context;
import com.koin.data.user.UserDatabase;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class UserDatabaseModule_ProvideUserDatabaseFactory implements Factory<UserDatabase> {
  private final Provider<Context> contextProvider;

  public UserDatabaseModule_ProvideUserDatabaseFactory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public UserDatabase get() {
    return provideUserDatabase(contextProvider.get());
  }

  public static UserDatabaseModule_ProvideUserDatabaseFactory create(
      Provider<Context> contextProvider) {
    return new UserDatabaseModule_ProvideUserDatabaseFactory(contextProvider);
  }

  public static UserDatabase provideUserDatabase(Context context) {
    return Preconditions.checkNotNullFromProvides(UserDatabaseModule.INSTANCE.provideUserDatabase(context));
  }
}
