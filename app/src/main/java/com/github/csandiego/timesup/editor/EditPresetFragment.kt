package com.github.csandiego.timesup.editor

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.github.csandiego.timesup.R

class EditPresetFragment(
    viewModelFactoryProducer: (() -> ViewModelProvider.Factory)?
) : PresetEditorFragment(viewModelFactoryProducer) {

    constructor() : this(null)

    override val titleResourceId = R.string.editor_edit_title
    private val params by navArgs<EditPresetFragmentArgs>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.load(params.presetId)
    }
}