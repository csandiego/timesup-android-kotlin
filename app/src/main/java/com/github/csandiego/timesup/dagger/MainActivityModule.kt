package com.github.csandiego.timesup.dagger

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.github.csandiego.timesup.editor.EditPresetFragment
import com.github.csandiego.timesup.editor.NewPresetFragment
import com.github.csandiego.timesup.editor.PresetEditorViewModel
import com.github.csandiego.timesup.presets.PresetsFragment
import com.github.csandiego.timesup.presets.PresetsViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class MainActivityModule {

    @Binds
    @IntoMap
    @ViewModelKey(PresetEditorViewModel::class)
    abstract fun providePresetEditorViewModel(viewModel: PresetEditorViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(PresetsViewModel::class)
    abstract fun providePresetsViewModel(viewModel: PresetsViewModel): ViewModel

    @ActivityScope
    @Binds
    abstract fun provideViewModelFactory(factory: DaggerViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @FragmentKey(NewPresetFragment::class)
    abstract fun provideNewPresetFragment(fragment: NewPresetFragment): Fragment

    @Binds
    @IntoMap
    @FragmentKey(EditPresetFragment::class)
    abstract fun provideEditPresetFragment(fragment: EditPresetFragment): Fragment

    @Binds
    @IntoMap
    @FragmentKey(PresetsFragment::class)
    abstract fun providePresetsFragment(fragment: PresetsFragment): Fragment

    @ActivityScope
    @Binds
    abstract fun provideFragmentFactory(factory: DaggerFragmentFactory): FragmentFactory
}