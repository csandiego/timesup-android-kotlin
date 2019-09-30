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
interface MainActivityModule {

    @Binds
    @IntoMap
    @ViewModelKey(PresetEditorViewModel::class)
    fun providePresetEditorViewModel(viewModel: PresetEditorViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(PresetsViewModel::class)
    fun providePresetsViewModel(viewModel: PresetsViewModel): ViewModel

    @ActivityScope
    @Binds
    fun provideViewModelFactory(factory: DaggerViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @FragmentKey(NewPresetFragment::class)
    fun provideNewPresetFragment(fragment: NewPresetFragment): Fragment

    @Binds
    @IntoMap
    @FragmentKey(EditPresetFragment::class)
    fun provideEditPresetFragment(fragment: EditPresetFragment): Fragment

    @Binds
    @IntoMap
    @FragmentKey(PresetsFragment::class)
    fun providePresetsFragment(fragment: PresetsFragment): Fragment

    @ActivityScope
    @Binds
    fun provideFragmentFactory(factory: DaggerFragmentFactory): FragmentFactory
}