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
public final class CoinRepositoryImpl_Factory implements Factory<CoinRepositoryImpl> {
  private final Provider<CoinGeckoApiService> apiServiceProvider;

  private final Provider<CoinDao> coinDaoProvider;

  private final Provider<NetworkUtil> networkUtilProvider;

  public CoinRepositoryImpl_Factory(Provider<CoinGeckoApiService> apiServiceProvider,
      Provider<CoinDao> coinDaoProvider, Provider<NetworkUtil> networkUtilProvider) {
    this.apiServiceProvider = apiServiceProvider;
    this.coinDaoProvider = coinDaoProvider;
    this.networkUtilProvider = networkUtilProvider;
  }

  @Override
  public CoinRepositoryImpl get() {
    return newInstance(apiServiceProvider.get(), coinDaoProvider.get(), networkUtilProvider.get());
  }

  public static CoinRepositoryImpl_Factory create(Provider<CoinGeckoApiService> apiServiceProvider,
      Provider<CoinDao> coinDaoProvider, Provider<NetworkUtil> networkUtilProvider) {
    return new CoinRepositoryImpl_Factory(apiServiceProvider, coinDaoProvider, networkUtilProvider);
  }

  public static CoinRepositoryImpl newInstance(CoinGeckoApiService apiService, CoinDao coinDao,
      NetworkUtil networkUtil) {
    return new CoinRepositoryImpl(apiService, coinDao, networkUtil);
  }
}
