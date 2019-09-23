package com.github.csandiego.timesup.editor

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.github.csandiego.timesup.R
import javax.inject.Inject

class EditPresetFragment @Inject constructor(viewModelFactory: ViewModelProvider.Factory)
    : PresetEditorFragment(viewModelFactory) {

    override val titleResourceId = R.string.editor_edit_title
    private val params by navArgs<EditPresetFragmentArgs>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.load(params.presetId)
    }
}