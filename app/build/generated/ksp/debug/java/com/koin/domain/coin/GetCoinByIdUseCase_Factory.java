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
public final class GetCoinByIdUseCase_Factory implements Factory<GetCoinByIdUseCase> {
  private final Provider<CoinRepository> repositoryProvider;

  public GetCoinByIdUseCase_Factory(Provider<CoinRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public GetCoinByIdUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static GetCoinByIdUseCase_Factory create(Provider<CoinRepository> repositoryProvider) {
    return new GetCoinByIdUseCase_Factory(repositoryProvider);
  }

  public static GetCoinByIdUseCase newInstance(CoinRepository repository) {
    return new GetCoinByIdUseCase(repository);
  }
}
