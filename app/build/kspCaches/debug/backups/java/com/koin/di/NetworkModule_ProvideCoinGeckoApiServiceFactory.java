package com.koin.di;

import com.koin.data.coin.CoinGeckoApiService;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import retrofit2.Retrofit;

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
public final class NetworkModule_ProvideCoinGeckoApiServiceFactory implements Factory<CoinGeckoApiService> {
  private final Provider<Retrofit> retrofitProvider;

  public NetworkModule_ProvideCoinGeckoApiServiceFactory(Provider<Retrofit> retrofitProvider) {
    this.retrofitProvider = retrofitProvider;
  }

  @Override
  public CoinGeckoApiService get() {
    return provideCoinGeckoApiService(retrofitProvider.get());
  }

  public static NetworkModule_ProvideCoinGeckoApiServiceFactory create(
      Provider<Retrofit> retrofitProvider) {
    return new NetworkModule_ProvideCoinGeckoApiServiceFactory(retrofitProvider);
  }

  public static CoinGeckoApiService provideCoinGeckoApiService(Retrofit retrofit) {
    return Preconditions.checkNotNullFromProvides(NetworkModule.INSTANCE.provideCoinGeckoApiService(retrofit));
  }
}
