package com.koin;

import android.app.Activity;
import android.app.Service;
import android.view.View;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;
import com.google.gson.Gson;
import com.koin.data.coin.CoinDao;
import com.koin.data.coin.CoinDatabase;
import com.koin.data.coin.CoinGeckoApiService;
import com.koin.data.coin.CoinRepositoryImpl;
import com.koin.data.coin.NetworkUtil;
import com.koin.data.session.SessionManager;
import com.koin.data.user.LocalUserRepository;
import com.koin.data.user.UserDao;
import com.koin.data.user.UserDatabase;
import com.koin.di.DatabaseModule_ProvideCoinDaoFactory;
import com.koin.di.DatabaseModule_ProvideCoinDatabaseFactory;
import com.koin.di.NetworkModule_ProvideCoinGeckoApiServiceFactory;
import com.koin.di.NetworkModule_ProvideGsonFactory;
import com.koin.di.NetworkModule_ProvideOkHttpClientFactory;
import com.koin.di.NetworkModule_ProvideRetrofitFactory;
import com.koin.di.UserDatabaseModule_ProvideUserDaoFactory;
import com.koin.di.UserDatabaseModule_ProvideUserDatabaseFactory;
import com.koin.di.UtilModule_ProvideNetworkUtilFactory;
import com.koin.ui.auth.AuthViewModel;
import com.koin.ui.auth.AuthViewModel_HiltModules;
import com.koin.ui.auth.AuthViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.koin.ui.auth.AuthViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.koin.ui.coindetail.CoinDetailViewModel;
import com.koin.ui.coindetail.CoinDetailViewModel_HiltModules;
import com.koin.ui.coindetail.CoinDetailViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.koin.ui.coindetail.CoinDetailViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.koin.ui.coinlist.CoinListViewModel;
import com.koin.ui.coinlist.CoinListViewModel_HiltModules;
import com.koin.ui.coinlist.CoinListViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.koin.ui.coinlist.CoinListViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.koin.ui.profile.ProfileViewModel;
import com.koin.ui.profile.ProfileViewModel_HiltModules;
import com.koin.ui.profile.ProfileViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.koin.ui.profile.ProfileViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.koin.ui.session.SessionViewModel;
import com.koin.ui.session.SessionViewModel_HiltModules;
import com.koin.ui.session.SessionViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.koin.ui.session.SessionViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import dagger.hilt.android.ActivityRetainedLifecycle;
import dagger.hilt.android.ViewModelLifecycle;
import dagger.hilt.android.internal.builders.ActivityComponentBuilder;
import dagger.hilt.android.internal.builders.ActivityRetainedComponentBuilder;
import dagger.hilt.android.internal.builders.FragmentComponentBuilder;
import dagger.hilt.android.internal.builders.ServiceComponentBuilder;
import dagger.hilt.android.internal.builders.ViewComponentBuilder;
import dagger.hilt.android.internal.builders.ViewModelComponentBuilder;
import dagger.hilt.android.internal.builders.ViewWithFragmentComponentBuilder;
import dagger.hilt.android.internal.lifecycle.DefaultViewModelFactories;
import dagger.hilt.android.internal.lifecycle.DefaultViewModelFactories_InternalFactoryFactory_Factory;
import dagger.hilt.android.internal.managers.ActivityRetainedComponentManager_LifecycleModule_ProvideActivityRetainedLifecycleFactory;
import dagger.hilt.android.internal.managers.SavedStateHandleHolder;
import dagger.hilt.android.internal.modules.ApplicationContextModule;
import dagger.hilt.android.internal.modules.ApplicationContextModule_ProvideContextFactory;
import dagger.internal.DaggerGenerated;
import dagger.internal.DoubleCheck;
import dagger.internal.LazyClassKeyMap;
import dagger.internal.MapBuilder;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

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
public final class DaggerCryptoApp_HiltComponents_SingletonC {
  private DaggerCryptoApp_HiltComponents_SingletonC() {
  }

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder {
    private ApplicationContextModule applicationContextModule;

    private Builder() {
    }

    public Builder applicationContextModule(ApplicationContextModule applicationContextModule) {
      this.applicationContextModule = Preconditions.checkNotNull(applicationContextModule);
      return this;
    }

    public CryptoApp_HiltComponents.SingletonC build() {
      Preconditions.checkBuilderRequirement(applicationContextModule, ApplicationContextModule.class);
      return new SingletonCImpl(applicationContextModule);
    }
  }

  private static final class ActivityRetainedCBuilder implements CryptoApp_HiltComponents.ActivityRetainedC.Builder {
    private final SingletonCImpl singletonCImpl;

    private SavedStateHandleHolder savedStateHandleHolder;

    private ActivityRetainedCBuilder(SingletonCImpl singletonCImpl) {
      this.singletonCImpl = singletonCImpl;
    }

    @Override
    public ActivityRetainedCBuilder savedStateHandleHolder(
        SavedStateHandleHolder savedStateHandleHolder) {
      this.savedStateHandleHolder = Preconditions.checkNotNull(savedStateHandleHolder);
      return this;
    }

    @Override
    public CryptoApp_HiltComponents.ActivityRetainedC build() {
      Preconditions.checkBuilderRequirement(savedStateHandleHolder, SavedStateHandleHolder.class);
      return new ActivityRetainedCImpl(singletonCImpl, savedStateHandleHolder);
    }
  }

  private static final class ActivityCBuilder implements CryptoApp_HiltComponents.ActivityC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private Activity activity;

    private ActivityCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
    }

    @Override
    public ActivityCBuilder activity(Activity activity) {
      this.activity = Preconditions.checkNotNull(activity);
      return this;
    }

    @Override
    public CryptoApp_HiltComponents.ActivityC build() {
      Preconditions.checkBuilderRequirement(activity, Activity.class);
      return new ActivityCImpl(singletonCImpl, activityRetainedCImpl, activity);
    }
  }

  private static final class FragmentCBuilder implements CryptoApp_HiltComponents.FragmentC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private Fragment fragment;

    private FragmentCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
    }

    @Override
    public FragmentCBuilder fragment(Fragment fragment) {
      this.fragment = Preconditions.checkNotNull(fragment);
      return this;
    }

    @Override
    public CryptoApp_HiltComponents.FragmentC build() {
      Preconditions.checkBuilderRequirement(fragment, Fragment.class);
      return new FragmentCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, fragment);
    }
  }

  private static final class ViewWithFragmentCBuilder implements CryptoApp_HiltComponents.ViewWithFragmentC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl;

    private View view;

    private ViewWithFragmentCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        FragmentCImpl fragmentCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
      this.fragmentCImpl = fragmentCImpl;
    }

    @Override
    public ViewWithFragmentCBuilder view(View view) {
      this.view = Preconditions.checkNotNull(view);
      return this;
    }

    @Override
    public CryptoApp_HiltComponents.ViewWithFragmentC build() {
      Preconditions.checkBuilderRequirement(view, View.class);
      return new ViewWithFragmentCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, fragmentCImpl, view);
    }
  }

  private static final class ViewCBuilder implements CryptoApp_HiltComponents.ViewC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private View view;

    private ViewCBuilder(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
    }

    @Override
    public ViewCBuilder view(View view) {
      this.view = Preconditions.checkNotNull(view);
      return this;
    }

    @Override
    public CryptoApp_HiltComponents.ViewC build() {
      Preconditions.checkBuilderRequirement(view, View.class);
      return new ViewCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, view);
    }
  }

  private static final class ViewModelCBuilder implements CryptoApp_HiltComponents.ViewModelC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private SavedStateHandle savedStateHandle;

    private ViewModelLifecycle viewModelLifecycle;

    private ViewModelCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
    }

    @Override
    public ViewModelCBuilder savedStateHandle(SavedStateHandle handle) {
      this.savedStateHandle = Preconditions.checkNotNull(handle);
      return this;
    }

    @Override
    public ViewModelCBuilder viewModelLifecycle(ViewModelLifecycle viewModelLifecycle) {
      this.viewModelLifecycle = Preconditions.checkNotNull(viewModelLifecycle);
      return this;
    }

    @Override
    public CryptoApp_HiltComponents.ViewModelC build() {
      Preconditions.checkBuilderRequirement(savedStateHandle, SavedStateHandle.class);
      Preconditions.checkBuilderRequirement(viewModelLifecycle, ViewModelLifecycle.class);
      return new ViewModelCImpl(singletonCImpl, activityRetainedCImpl, savedStateHandle, viewModelLifecycle);
    }
  }

  private static final class ServiceCBuilder implements CryptoApp_HiltComponents.ServiceC.Builder {
    private final SingletonCImpl singletonCImpl;

    private Service service;

    private ServiceCBuilder(SingletonCImpl singletonCImpl) {
      this.singletonCImpl = singletonCImpl;
    }

    @Override
    public ServiceCBuilder service(Service service) {
      this.service = Preconditions.checkNotNull(service);
      return this;
    }

    @Override
    public CryptoApp_HiltComponents.ServiceC build() {
      Preconditions.checkBuilderRequirement(service, Service.class);
      return new ServiceCImpl(singletonCImpl, service);
    }
  }

  private static final class ViewWithFragmentCImpl extends CryptoApp_HiltComponents.ViewWithFragmentC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl;

    private final ViewWithFragmentCImpl viewWithFragmentCImpl = this;

    ViewWithFragmentCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        FragmentCImpl fragmentCImpl, View viewParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
      this.fragmentCImpl = fragmentCImpl;


    }
  }

  private static final class FragmentCImpl extends CryptoApp_HiltComponents.FragmentC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl = this;

    FragmentCImpl(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl, Fragment fragmentParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;


    }

    @Override
    public DefaultViewModelFactories.InternalFactoryFactory getHiltInternalFactoryFactory() {
      return activityCImpl.getHiltInternalFactoryFactory();
    }

    @Override
    public ViewWithFragmentComponentBuilder viewWithFragmentComponentBuilder() {
      return new ViewWithFragmentCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl, fragmentCImpl);
    }
  }

  private static final class ViewCImpl extends CryptoApp_HiltComponents.ViewC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final ViewCImpl viewCImpl = this;

    ViewCImpl(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl, View viewParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;


    }
  }

  private static final class ActivityCImpl extends CryptoApp_HiltComponents.ActivityC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl = this;

    ActivityCImpl(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        Activity activityParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;


    }

    @Override
    public void injectMainActivity(MainActivity mainActivity) {
    }

    @Override
    public DefaultViewModelFactories.InternalFactoryFactory getHiltInternalFactoryFactory() {
      return DefaultViewModelFactories_InternalFactoryFactory_Factory.newInstance(getViewModelKeys(), new ViewModelCBuilder(singletonCImpl, activityRetainedCImpl));
    }

    @Override
    public Map<Class<?>, Boolean> getViewModelKeys() {
      return LazyClassKeyMap.<Boolean>of(MapBuilder.<String, Boolean>newMapBuilder(5).put(AuthViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, AuthViewModel_HiltModules.KeyModule.provide()).put(CoinDetailViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, CoinDetailViewModel_HiltModules.KeyModule.provide()).put(CoinListViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, CoinListViewModel_HiltModules.KeyModule.provide()).put(ProfileViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, ProfileViewModel_HiltModules.KeyModule.provide()).put(SessionViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, SessionViewModel_HiltModules.KeyModule.provide()).build());
    }

    @Override
    public ViewModelComponentBuilder getViewModelComponentBuilder() {
      return new ViewModelCBuilder(singletonCImpl, activityRetainedCImpl);
    }

    @Override
    public FragmentComponentBuilder fragmentComponentBuilder() {
      return new FragmentCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl);
    }

    @Override
    public ViewComponentBuilder viewComponentBuilder() {
      return new ViewCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl);
    }
  }

  private static final class ViewModelCImpl extends CryptoApp_HiltComponents.ViewModelC {
    private final SavedStateHandle savedStateHandle;

    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ViewModelCImpl viewModelCImpl = this;

    Provider<AuthViewModel> authViewModelProvider;

    Provider<CoinDetailViewModel> coinDetailViewModelProvider;

    Provider<CoinListViewModel> coinListViewModelProvider;

    Provider<ProfileViewModel> profileViewModelProvider;

    Provider<SessionViewModel> sessionViewModelProvider;

    ViewModelCImpl(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        SavedStateHandle savedStateHandleParam, ViewModelLifecycle viewModelLifecycleParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.savedStateHandle = savedStateHandleParam;
      initialize(savedStateHandleParam, viewModelLifecycleParam);

    }

    @SuppressWarnings("unchecked")
    private void initialize(final SavedStateHandle savedStateHandleParam,
        final ViewModelLifecycle viewModelLifecycleParam) {
      this.authViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 0);
      this.coinDetailViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 1);
      this.coinListViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 2);
      this.profileViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 3);
      this.sessionViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 4);
    }

    @Override
    public Map<Class<?>, javax.inject.Provider<ViewModel>> getHiltViewModelMap() {
      return LazyClassKeyMap.<javax.inject.Provider<ViewModel>>of(MapBuilder.<String, javax.inject.Provider<ViewModel>>newMapBuilder(5).put(AuthViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) (authViewModelProvider))).put(CoinDetailViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) (coinDetailViewModelProvider))).put(CoinListViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) (coinListViewModelProvider))).put(ProfileViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) (profileViewModelProvider))).put(SessionViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) (sessionViewModelProvider))).build());
    }

    @Override
    public Map<Class<?>, Object> getHiltViewModelAssistedMap() {
      return Collections.<Class<?>, Object>emptyMap();
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final ActivityRetainedCImpl activityRetainedCImpl;

      private final ViewModelCImpl viewModelCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
          ViewModelCImpl viewModelCImpl, int id) {
        this.singletonCImpl = singletonCImpl;
        this.activityRetainedCImpl = activityRetainedCImpl;
        this.viewModelCImpl = viewModelCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // com.koin.ui.auth.AuthViewModel
          return (T) new AuthViewModel(singletonCImpl.localUserRepositoryProvider.get(), singletonCImpl.sessionManagerProvider.get());

          case 1: // com.koin.ui.coindetail.CoinDetailViewModel
          return (T) new CoinDetailViewModel(singletonCImpl.coinRepositoryImplProvider.get(), viewModelCImpl.savedStateHandle);

          case 2: // com.koin.ui.coinlist.CoinListViewModel
          return (T) new CoinListViewModel(singletonCImpl.coinRepositoryImplProvider.get());

          case 3: // com.koin.ui.profile.ProfileViewModel
          return (T) new ProfileViewModel(singletonCImpl.localUserRepositoryProvider.get(), singletonCImpl.sessionManagerProvider.get());

          case 4: // com.koin.ui.session.SessionViewModel
          return (T) new SessionViewModel(singletonCImpl.sessionManagerProvider.get());

          default: throw new AssertionError(id);
        }
      }
    }
  }

  private static final class ActivityRetainedCImpl extends CryptoApp_HiltComponents.ActivityRetainedC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl = this;

    Provider<ActivityRetainedLifecycle> provideActivityRetainedLifecycleProvider;

    ActivityRetainedCImpl(SingletonCImpl singletonCImpl,
        SavedStateHandleHolder savedStateHandleHolderParam) {
      this.singletonCImpl = singletonCImpl;

      initialize(savedStateHandleHolderParam);

    }

    @SuppressWarnings("unchecked")
    private void initialize(final SavedStateHandleHolder savedStateHandleHolderParam) {
      this.provideActivityRetainedLifecycleProvider = DoubleCheck.provider(new SwitchingProvider<ActivityRetainedLifecycle>(singletonCImpl, activityRetainedCImpl, 0));
    }

    @Override
    public ActivityComponentBuilder activityComponentBuilder() {
      return new ActivityCBuilder(singletonCImpl, activityRetainedCImpl);
    }

    @Override
    public ActivityRetainedLifecycle getActivityRetainedLifecycle() {
      return provideActivityRetainedLifecycleProvider.get();
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final ActivityRetainedCImpl activityRetainedCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
          int id) {
        this.singletonCImpl = singletonCImpl;
        this.activityRetainedCImpl = activityRetainedCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // dagger.hilt.android.ActivityRetainedLifecycle
          return (T) ActivityRetainedComponentManager_LifecycleModule_ProvideActivityRetainedLifecycleFactory.provideActivityRetainedLifecycle();

          default: throw new AssertionError(id);
        }
      }
    }
  }

  private static final class ServiceCImpl extends CryptoApp_HiltComponents.ServiceC {
    private final SingletonCImpl singletonCImpl;

    private final ServiceCImpl serviceCImpl = this;

    ServiceCImpl(SingletonCImpl singletonCImpl, Service serviceParam) {
      this.singletonCImpl = singletonCImpl;


    }
  }

  private static final class SingletonCImpl extends CryptoApp_HiltComponents.SingletonC {
    private final ApplicationContextModule applicationContextModule;

    private final SingletonCImpl singletonCImpl = this;

    Provider<UserDatabase> provideUserDatabaseProvider;

    Provider<LocalUserRepository> localUserRepositoryProvider;

    Provider<SessionManager> sessionManagerProvider;

    Provider<OkHttpClient> provideOkHttpClientProvider;

    Provider<Gson> provideGsonProvider;

    Provider<Retrofit> provideRetrofitProvider;

    Provider<CoinGeckoApiService> provideCoinGeckoApiServiceProvider;

    Provider<CoinDatabase> provideCoinDatabaseProvider;

    Provider<NetworkUtil> provideNetworkUtilProvider;

    Provider<CoinRepositoryImpl> coinRepositoryImplProvider;

    SingletonCImpl(ApplicationContextModule applicationContextModuleParam) {
      this.applicationContextModule = applicationContextModuleParam;
      initialize(applicationContextModuleParam);

    }

    UserDao userDao() {
      return UserDatabaseModule_ProvideUserDaoFactory.provideUserDao(provideUserDatabaseProvider.get());
    }

    CoinDao coinDao() {
      return DatabaseModule_ProvideCoinDaoFactory.provideCoinDao(provideCoinDatabaseProvider.get());
    }

    @SuppressWarnings("unchecked")
    private void initialize(final ApplicationContextModule applicationContextModuleParam) {
      this.provideUserDatabaseProvider = DoubleCheck.provider(new SwitchingProvider<UserDatabase>(singletonCImpl, 1));
      this.localUserRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<LocalUserRepository>(singletonCImpl, 0));
      this.sessionManagerProvider = DoubleCheck.provider(new SwitchingProvider<SessionManager>(singletonCImpl, 2));
      this.provideOkHttpClientProvider = DoubleCheck.provider(new SwitchingProvider<OkHttpClient>(singletonCImpl, 6));
      this.provideGsonProvider = DoubleCheck.provider(new SwitchingProvider<Gson>(singletonCImpl, 7));
      this.provideRetrofitProvider = DoubleCheck.provider(new SwitchingProvider<Retrofit>(singletonCImpl, 5));
      this.provideCoinGeckoApiServiceProvider = DoubleCheck.provider(new SwitchingProvider<CoinGeckoApiService>(singletonCImpl, 4));
      this.provideCoinDatabaseProvider = DoubleCheck.provider(new SwitchingProvider<CoinDatabase>(singletonCImpl, 8));
      this.provideNetworkUtilProvider = DoubleCheck.provider(new SwitchingProvider<NetworkUtil>(singletonCImpl, 9));
      this.coinRepositoryImplProvider = DoubleCheck.provider(new SwitchingProvider<CoinRepositoryImpl>(singletonCImpl, 3));
    }

    @Override
    public void injectCryptoApp(CryptoApp cryptoApp) {
    }

    @Override
    public Set<Boolean> getDisableFragmentGetContextFix() {
      return Collections.<Boolean>emptySet();
    }

    @Override
    public ActivityRetainedComponentBuilder retainedComponentBuilder() {
      return new ActivityRetainedCBuilder(singletonCImpl);
    }

    @Override
    public ServiceComponentBuilder serviceComponentBuilder() {
      return new ServiceCBuilder(singletonCImpl);
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, int id) {
        this.singletonCImpl = singletonCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // com.koin.data.user.LocalUserRepository
          return (T) new LocalUserRepository(singletonCImpl.userDao());

          case 1: // com.koin.data.user.UserDatabase
          return (T) UserDatabaseModule_ProvideUserDatabaseFactory.provideUserDatabase(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 2: // com.koin.data.session.SessionManager
          return (T) new SessionManager(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 3: // com.koin.data.coin.CoinRepositoryImpl
          return (T) new CoinRepositoryImpl(singletonCImpl.provideCoinGeckoApiServiceProvider.get(), singletonCImpl.coinDao(), singletonCImpl.provideNetworkUtilProvider.get());

          case 4: // com.koin.data.coin.CoinGeckoApiService
          return (T) NetworkModule_ProvideCoinGeckoApiServiceFactory.provideCoinGeckoApiService(singletonCImpl.provideRetrofitProvider.get());

          case 5: // retrofit2.Retrofit
          return (T) NetworkModule_ProvideRetrofitFactory.provideRetrofit(singletonCImpl.provideOkHttpClientProvider.get(), singletonCImpl.provideGsonProvider.get());

          case 6: // okhttp3.OkHttpClient
          return (T) NetworkModule_ProvideOkHttpClientFactory.provideOkHttpClient();

          case 7: // com.google.gson.Gson
          return (T) NetworkModule_ProvideGsonFactory.provideGson();

          case 8: // com.koin.data.coin.CoinDatabase
          return (T) DatabaseModule_ProvideCoinDatabaseFactory.provideCoinDatabase(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 9: // com.koin.data.coin.NetworkUtil
          return (T) UtilModule_ProvideNetworkUtilFactory.provideNetworkUtil(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          default: throw new AssertionError(id);
        }
      }
    }
  }
}
