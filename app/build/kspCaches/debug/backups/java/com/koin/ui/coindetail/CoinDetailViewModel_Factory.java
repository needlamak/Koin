package com.koin.ui.coindetail;

import androidx.lifecycle.SavedStateHandle;
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
public final class CoinDetailViewModel_Factory implements Factory<CoinDetailViewModel> {
  private final Provider<CoinRepository> repositoryProvider;

  private final Provider<SavedStateHandle> savedStateHandleProvider;

  public CoinDetailViewModel_Factory(Provider<CoinRepository> repositoryProvider,
      Provider<SavedStateHandle> savedStateHandleProvider) {
    this.repositoryProvider = repositoryProvider;
    this.savedStateHandleProvider = savedStateHandleProvider;
  }

  @Override
  public CoinDetailViewModel get() {
    return newInstance(repositoryProvider.get(), savedStateHandleProvider.get());
  }

  public static CoinDetailViewModel_Factory create(Provider<CoinRepository> repositoryProvider,
      Provider<SavedStateHandle> savedStateHandleProvider) {
    return new CoinDetailViewModel_Factory(repositoryProvider, savedStateHandleProvider);
  }

  public static CoinDetailViewModel newInstance(CoinRepository repository,
      SavedStateHandle savedStateHandle) {
    return new CoinDetailViewModel(repository, savedStateHandle);
  }
}
