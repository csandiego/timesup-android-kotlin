package com.github.csandiego.timesup.editor

import androidx.lifecycle.ViewModelProvider
import com.github.csandiego.timesup.R

class NewPresetFragment(
    viewModelFactoryProducer: (() -> ViewModelProvider.Factory)?
) : PresetEditorFragment(viewModelFactoryProducer) {

    override val titleResourceId = R.string.editor_new_title
}