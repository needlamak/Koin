package com.koin.di;

import com.koin.data.coin.CoinDao;
import com.koin.data.coin.CoinDatabase;
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
public final class DatabaseModule_ProvideCoinDaoFactory implements Factory<CoinDao> {
  private final Provider<CoinDatabase> databaseProvider;

  public DatabaseModule_ProvideCoinDaoFactory(Provider<CoinDatabase> databaseProvider) {
    this.databaseProvider = databaseProvider;
  }

  @Override
  public CoinDao get() {
    return provideCoinDao(databaseProvider.get());
  }

  public static DatabaseModule_ProvideCoinDaoFactory create(
      Provider<CoinDatabase> databaseProvider) {
    return new DatabaseModule_ProvideCoinDaoFactory(databaseProvider);
  }

  public static CoinDao provideCoinDao(CoinDatabase database) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideCoinDao(database));
  }
}
