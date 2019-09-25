package com.github.csandiego.timesup

import com.github.csandiego.timesup.editor.EditPresetFragmentTest
import com.github.csandiego.timesup.editor.NewPresetFragmentTest
import com.github.csandiego.timesup.editor.PresetEditorViewModelTest
import com.github.csandiego.timesup.presets.PresetsFragmentTest
import com.github.csandiego.timesup.presets.PresetsViewModelTest
import com.github.csandiego.timesup.repository.DefaultPresetRepositoryTest
import com.github.csandiego.timesup.room.PresetDaoTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.runner.RunWith
import org.junit.runners.Suite

@ExperimentalCoroutinesApi
@RunWith(Suite::class)
@Suite.SuiteClasses(
    PresetDaoTest::class,
    DefaultPresetRepositoryTest::class,
    PresetsViewModelTest::class,
    PresetEditorViewModelTest::class,
    EditPresetFragmentTest::class,
    NewPresetFragmentTest::class,
    PresetsFragmentTest::class,
    AddPresetTest::class
)
class InstrumentedTestSuite