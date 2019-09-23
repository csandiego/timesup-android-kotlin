package com.github.csandiego.timesup.dagger

import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

@Module
class CoroutineScopeModule {

    @Singleton
    @Provides
    fun provideCoroutineScope() = CoroutineScope(SupervisorJob() + Dispatchers.IO)
}