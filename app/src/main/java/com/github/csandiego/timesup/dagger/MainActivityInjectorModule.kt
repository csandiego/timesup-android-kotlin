package com.github.csandiego.timesup.dagger

import com.github.csandiego.timesup.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class MainActivityInjectorModule {

    @ActivityScope
    @ContributesAndroidInjector(modules = [MainActivityModule::class])
    abstract fun contributeAndroidInjector(): MainActivity
}