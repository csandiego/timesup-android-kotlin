package com.github.csandiego.timesup.dagger

import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineScope
import javax.inject.Singleton

@ExperimentalCoroutinesApi
@Module
class CoroutineScopeModule {

    @Singleton
    @Provides
    fun provideCoroutineScope(): CoroutineScope = TestCoroutineScope()
}