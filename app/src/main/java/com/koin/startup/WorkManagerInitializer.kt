package com.koin.startup

import android.content.Context
import androidx.hilt.work.HiltWorkerFactory
import androidx.startup.Initializer
import androidx.work.Configuration
import androidx.work.WorkManager
import javax.inject.Inject
import kotlin.jvm.java

// Create this file: app/src/main/java/com/koin/startup/WorkManagerInitializer.kt

//class WorkManagerInitializer : Initializer<WorkManager>, Configuration.Provider {
//
//    @Inject
//    lateinit var workerFactory: HiltWorkerFactory
//
//    override fun create(context: Context): WorkManager {
//        InitializerEntryPoint.resolve(context).inject(this)
//        WorkManager.initialize(context, workManagerConfiguration)
//        return WorkManager.getInstance(context)
//    }
//
//    override fun dependencies(): List<Class<out Initializer<*>>> {
//        return listOf(DependencyGraphInitializer::class.java)
//    }
//
//    override val workManagerConfiguration: Configuration
//        get() = Configuration.Builder()
//            .setWorkerFactory(workerFactory)
//            .build()
//}