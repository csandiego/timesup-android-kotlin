package com.github.csandiego.timesup

import com.github.csandiego.timesup.editor.PresetEditorViewModelTest
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
    PresetEditorViewModelTest::class
)
class LocalTestSuite