package com.github.csandiego.timesup.dagger

import android.app.Application
import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.github.csandiego.timesup.TimesUpApplication
import com.github.csandiego.timesup.editor.EditPresetFragment
import com.github.csandiego.timesup.editor.NewPresetFragment
import com.github.csandiego.timesup.editor.PresetEditorViewModel
import com.github.csandiego.timesup.presets.PresetsFragment
import com.github.csandiego.timesup.presets.PresetsViewModel
import com.github.csandiego.timesup.repository.DefaultPresetRepository
import com.github.csandiego.timesup.repository.PresetRepository
import com.github.csandiego.timesup.room.TimesUpDatabase
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

@Module
class ApplicationModule(private val application: TimesUpApplication) {

    @Provides
    fun provideContext(): Context = application

    @Provides
    fun provideApplication(): Application = application

    @Provides
    fun provideDatabase(context: Context): TimesUpDatabase {
        return Room.databaseBuilder(context, TimesUpDatabase::class.java, "TimesUp")
            .build()
    }

    @Provides
    fun providePresetDao(database: TimesUpDatabase) = database.presetDao()

    @Provides
    fun provideCoroutineScope() = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    @Provides
    fun providePresetRepository(repository: DefaultPresetRepository): PresetRepository = repository

    @Provides
    @IntoMap
    @ViewModelKey(PresetEditorViewModel::class)
    fun providePresetEditorViewModel(viewModel: PresetEditorViewModel): ViewModel = viewModel

    @Provides
    @IntoMap
    @ViewModelKey(PresetsViewModel::class)
    fun providePresetsViewModel(viewModel: PresetsViewModel): ViewModel = viewModel

    @Provides
    fun provideViewModelFactory(factory: DaggerViewModelFactory): ViewModelProvider.Factory =
        factory

    @Provides
    @IntoMap
    @FragmentKey(NewPresetFragment::class)
    fun provideNewPresetFragment(fragment: NewPresetFragment): Fragment = fragment

    @Provides
    @IntoMap
    @FragmentKey(EditPresetFragment::class)
    fun provideEditPresetFragment(fragment: EditPresetFragment): Fragment = fragment

    @Provides
    @IntoMap
    @FragmentKey(PresetsFragment::class)
    fun providePresetsFragment(fragment: PresetsFragment): Fragment = fragment

    @Provides
    fun provideFragmentFactory(factory: DaggerFragmentFactory): FragmentFactory = factory
}