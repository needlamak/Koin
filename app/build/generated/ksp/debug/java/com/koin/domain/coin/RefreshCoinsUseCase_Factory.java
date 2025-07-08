package com.koin.domain.coin;

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
public final class RefreshCoinsUseCase_Factory implements Factory<RefreshCoinsUseCase> {
  private final Provider<CoinRepository> repositoryProvider;

  public RefreshCoinsUseCase_Factory(Provider<CoinRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public RefreshCoinsUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static RefreshCoinsUseCase_Factory create(Provider<CoinRepository> repositoryProvider) {
    return new RefreshCoinsUseCase_Factory(repositoryProvider);
  }

  public static RefreshCoinsUseCase newInstance(CoinRepository repository) {
    return new RefreshCoinsUseCase(repository);
  }
}
