package com.koin.startup

import android.content.Context
import androidx.startup.Initializer
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent

// Create this file: app/src/main/java/com/koin/startup/DependencyGraphInitializer.kt

//class DependencyGraphInitializer : Initializer<Unit> {
//    override fun create(context: Context): Unit {
//        // This will lazily initialize ApplicationComponent before Application's onCreate
//        InitializerEntryPoint.resolve(context)
//        return Unit
//    }
//
//    override fun dependencies(): List<Class<out Initializer<*>>> {
//        return emptyList()
//    }
//}
//
//@InstallIn(SingletonComponent::class)
//@EntryPoint
//interface InitializerEntryPoint {
//    fun inject(initializer: WorkManagerInitializer)
//
//    companion object {
//        fun resolve(context: Context): InitializerEntryPoint {
//            val appContext = context.applicationContext ?: throw IllegalStateException()
//            return EntryPointAccessors.fromApplication(
//                appContext,
//                InitializerEntryPoint::class.java
//            )
//        }
//    }
//}