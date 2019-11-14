package com.github.csandiego.timesup.dagger

import com.github.csandiego.timesup.TestTimesUpApplication
import com.github.csandiego.timesup.TimesUpApplication
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module(includes = [ApplicationModule::class])
interface TestApplicationModule {

    @Singleton
    @Binds
    fun provideTimesUpApplication(application: TestTimesUpApplication): TimesUpApplication
}