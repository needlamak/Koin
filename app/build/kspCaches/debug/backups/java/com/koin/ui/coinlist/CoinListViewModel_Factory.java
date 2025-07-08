package com.koin.ui.coinlist;

import com.koin.domain.coin.CoinRepository;
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
public final class CoinListViewModel_Factory implements Factory<CoinListViewModel> {
  private final Provider<CoinRepository> repositoryProvider;

  public CoinListViewModel_Factory(Provider<CoinRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public CoinListViewModel get() {
    return newInstance(repositoryProvider.get());
  }

  public static CoinListViewModel_Factory create(Provider<CoinRepository> repositoryProvider) {
    return new CoinListViewModel_Factory(repositoryProvider);
  }

  public static CoinListViewModel newInstance(CoinRepository repository) {
    return new CoinListViewModel(repository);
  }
}
