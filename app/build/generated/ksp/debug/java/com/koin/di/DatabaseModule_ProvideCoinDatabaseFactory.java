package com.koin.di;

import android.content.Context;
import com.koin.data.coin.CoinDatabase;
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
public final class DatabaseModule_ProvideCoinDatabaseFactory implements Factory<CoinDatabase> {
  private final Provider<Context> contextProvider;

  public DatabaseModule_ProvideCoinDatabaseFactory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public CoinDatabase get() {
    return provideCoinDatabase(contextProvider.get());
  }

  public static DatabaseModule_ProvideCoinDatabaseFactory create(
      Provider<Context> contextProvider) {
    return new DatabaseModule_ProvideCoinDatabaseFactory(contextProvider);
  }

  public static CoinDatabase provideCoinDatabase(Context context) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideCoinDatabase(context));
  }
}
