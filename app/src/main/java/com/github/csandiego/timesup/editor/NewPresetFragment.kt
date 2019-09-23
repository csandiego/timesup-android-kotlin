package com.github.csandiego.timesup.editor

import androidx.lifecycle.ViewModelProvider
import com.github.csandiego.timesup.R
import javax.inject.Inject

class NewPresetFragment @Inject constructor(viewModelFactory: ViewModelProvider.Factory)
    : PresetEditorFragment(viewModelFactory) {

    override val titleResourceId = R.string.editor_new_title
}