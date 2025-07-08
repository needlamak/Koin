package com.koin.di;

import android.content.Context;
import com.koin.data.coin.NetworkUtil;
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
public final class UtilModule_ProvideNetworkUtilFactory implements Factory<NetworkUtil> {
  private final Provider<Context> contextProvider;

  public UtilModule_ProvideNetworkUtilFactory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public NetworkUtil get() {
    return provideNetworkUtil(contextProvider.get());
  }

  public static UtilModule_ProvideNetworkUtilFactory create(Provider<Context> contextProvider) {
    return new UtilModule_ProvideNetworkUtilFactory(contextProvider);
  }

  public static NetworkUtil provideNetworkUtil(Context context) {
    return Preconditions.checkNotNullFromProvides(UtilModule.INSTANCE.provideNetworkUtil(context));
  }
}
