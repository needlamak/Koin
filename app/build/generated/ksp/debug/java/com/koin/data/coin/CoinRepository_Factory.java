package com.koin.data.coin;

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
public final class CoinRepository_Factory implements Factory<CoinRepository> {
  private final Provider<CoinGeckoApiService> apiProvider;

  public CoinRepository_Factory(Provider<CoinGeckoApiService> apiProvider) {
    this.apiProvider = apiProvider;
  }

  @Override
  public CoinRepository get() {
    return newInstance(apiProvider.get());
  }

  public static CoinRepository_Factory create(Provider<CoinGeckoApiService> apiProvider) {
    return new CoinRepository_Factory(apiProvider);
  }

  public static CoinRepository newInstance(CoinGeckoApiService api) {
    return new CoinRepository(api);
  }
}
