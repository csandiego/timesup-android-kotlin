package com.github.csandiego.timesup.editor

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import com.github.csandiego.timesup.R
import com.github.csandiego.timesup.databinding.FragmentPresetEditorBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

abstract class PresetEditorFragment(viewModelFactory: ViewModelProvider.Factory)
    : DialogFragment() {

    protected val viewModel by viewModels<PresetEditorViewModel> { viewModelFactory }
    protected abstract val titleResourceId: Int

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val context = requireContext()
        val binding = FragmentPresetEditorBinding.inflate(LayoutInflater.from(context)).apply {
            viewModel = this@PresetEditorFragment.viewModel
            with(numberPickerHours) {
                minValue = 0
                maxValue = 23
                setFormatter {
                    String.format("%02d", it)
                }
            }
            with(numberPickerMinutes) {
                minValue = 0
                maxValue = 59
                setFormatter {
                    String.format("%02d", it)
                }
            }
            with(numberPickerSeconds) {
                minValue = 0
                maxValue = 59
                setFormatter {
                    String.format("%02d", it)
                }
            }
            lifecycleOwner = this@PresetEditorFragment
        }
        return MaterialAlertDialogBuilder(context)
            .setTitle(titleResourceId)
            .setView(binding.root)
            .setPositiveButton(R.string.button_save) { _, _ -> viewModel.save() }
            .create()
            .apply {
                setOnShowListener { dialog ->
                    viewModel.showSaveButton.observe(this@PresetEditorFragment) {
                        (dialog as? AlertDialog)?.getButton(DialogInterface.BUTTON_POSITIVE)
                            ?.isEnabled = it
                    }
                }
            }
    }
}