package com.github.csandiego.timesup.dagger

import android.app.Application
import com.github.csandiego.timesup.TimesUpApplication
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import javax.inject.Singleton

@Singleton
@Component(modules = [
    AndroidInjectionModule::class,
    ApplicationModule::class,
    RoomModule::class,
    CoroutineScopeModule::class,
    MainActivityInjectorModule::class
])
interface ApplicationComponent {

    fun inject(application: TimesUpApplication)

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun application(application: Application): Builder

        fun build(): ApplicationComponent
    }
}