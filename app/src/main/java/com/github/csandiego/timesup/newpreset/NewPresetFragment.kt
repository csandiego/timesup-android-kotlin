package com.github.csandiego.timesup.newpreset

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
import com.github.csandiego.timesup.databinding.FragmentNewPresetBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class NewPresetFragment(
    viewModelFactoryProducer: (() -> ViewModelProvider.Factory)?
) : DialogFragment(), DialogInterface.OnShowListener {

    constructor() : this(null)

    val viewModel by viewModels<NewPresetViewModel>(factoryProducer = viewModelFactoryProducer)

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val context = requireContext()
        val binding = FragmentNewPresetBinding.inflate(LayoutInflater.from(context)).apply {
            viewModel = this@NewPresetFragment.viewModel
            with (numberPickerHours) {
                minValue = 0
                maxValue = 23
                setFormatter {
                    String.format("%02d", it)
                }
            }
            with (numberPickerMinutes) {
                minValue = 0
                maxValue = 59
                setFormatter {
                    String.format("%02d", it)
                }
            }
            with (numberPickerSeconds) {
                minValue = 0
                maxValue = 59
                setFormatter {
                    String.format("%02d", it)
                }
            }
        }
        return MaterialAlertDialogBuilder(context)
            .setTitle(R.string.newpreset_title)
            .setView(binding.root)
            .setPositiveButton(R.string.button_create) { _, _ -> viewModel.create() }
            .create()
            .apply {
                setOnShowListener(this@NewPresetFragment)
            }
    }

    override fun onShow(dialog: DialogInterface?) {
        viewModel.showSaveButton.observe(this) {
            (dialog as? AlertDialog)?.getButton(DialogInterface.BUTTON_POSITIVE)?.isEnabled = it
        }
    }
}