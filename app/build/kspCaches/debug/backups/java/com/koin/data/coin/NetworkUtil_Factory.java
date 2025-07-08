package com.koin.data.coin;

import android.content.Context;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
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
public final class NetworkUtil_Factory implements Factory<NetworkUtil> {
  private final Provider<Context> contextProvider;

  public NetworkUtil_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public NetworkUtil get() {
    return newInstance(contextProvider.get());
  }

  public static NetworkUtil_Factory create(Provider<Context> contextProvider) {
    return new NetworkUtil_Factory(contextProvider);
  }

  public static NetworkUtil newInstance(Context context) {
    return new NetworkUtil(context);
  }
}
