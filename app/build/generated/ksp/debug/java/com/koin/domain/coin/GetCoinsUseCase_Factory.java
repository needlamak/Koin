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
public final class GetCoinsUseCase_Factory implements Factory<GetCoinsUseCase> {
  private final Provider<CoinRepository> repositoryProvider;

  public GetCoinsUseCase_Factory(Provider<CoinRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public GetCoinsUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static GetCoinsUseCase_Factory create(Provider<CoinRepository> repositoryProvider) {
    return new GetCoinsUseCase_Factory(repositoryProvider);
  }

  public static GetCoinsUseCase newInstance(CoinRepository repository) {
    return new GetCoinsUseCase(repository);
  }
}
