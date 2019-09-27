package com.github.csandiego.timesup.presets

import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ShapeDrawable
import android.os.Bundle
import android.provider.AlarmClock
import android.view.*
import androidx.databinding.BindingMethod
import androidx.databinding.BindingMethods
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.*
import com.github.csandiego.timesup.R
import com.github.csandiego.timesup.data.Preset
import com.github.csandiego.timesup.databinding.ListItemPresetsBinding
import kotlinx.android.synthetic.main.fragment_presets.*
import javax.inject.Inject

@BindingMethods(value = [
    BindingMethod(
        type = View::class,
        attribute = "app:activated",
        method = "setActivated"
    )
])
class PresetsFragment @Inject constructor(viewModelFactory: ViewModelProvider.Factory)
    : Fragment(R.layout.fragment_presets) {

    private val viewModel by viewModels<PresetsViewModel> { viewModelFactory }
    private var actionMode: ActionMode? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val adapter = createRecyclerViewAdapter()
        with(recyclerView) {
            this.adapter = adapter
            layoutManager = LinearLayoutManager(context)
            ItemTouchHelper(createItemTouchHelperCallback()).attachToRecyclerView(this)
        }
        buttonNew.setOnClickListener {
            findNavController().navigate(
                PresetsFragmentDirections.actionPresetsFragmentToNewPresetFragment()
            )
        }
        with(viewModel) {
            presets.observe(viewLifecycleOwner) {
                adapter.submitList(it)
            }
            selection.observe(viewLifecycleOwner) {
                if (it.isEmpty()) {
                    actionMode?.finish()
                } else {
                    val mode = actionMode ?: requireActivity().startActionMode(createActionModeCallback())
                    actionMode = mode.apply {
                        title = it.size.toString()
                        menu?.findItem(R.id.menuEdit)?.isVisible = it.size == 1
                    }
                }
            }
            startTimerForPreset.observe(viewLifecycleOwner) {
                it?.let {
                    viewModel.startTimerForPresetHandled()
                    startTimer(it)
                }
            }
        }
    }

    private fun startTimer(preset: Preset) {
        val s = preset.run {
            hours * 60 * 60 + minutes * 60 + seconds
        }
        val intent = Intent(AlarmClock.ACTION_SET_TIMER).apply {
            putExtra(AlarmClock.EXTRA_MESSAGE, preset.name)
            putExtra(AlarmClock.EXTRA_LENGTH, s)
            putExtra(AlarmClock.EXTRA_SKIP_UI, false)
        }
        if (intent.resolveActivity(requireActivity().packageManager) != null) {
            startActivity(intent)
        }
    }

    private fun createActionModeCallback() = object : ActionMode.Callback {

        override fun onActionItemClicked(mode: ActionMode, item: MenuItem) = when (item.itemId) {
            R.id.menuEdit -> {
                viewModel.selection.value?.firstOrNull()?.let {
                    findNavController().navigate(
                        PresetsFragmentDirections
                            .actionPresetsFragmentToEditPresetFragment(it.id)
                    )
                    true
                } ?: throw IllegalStateException("No selected preset for editing")
            }
            R.id.menuDelete -> {
                viewModel.deleteSelected()
                true
            }
            else -> false
        }

        override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
            mode.menuInflater.inflate(R.menu.preset_list_action_menu, menu)
            return true
        }

        override fun onPrepareActionMode(mode: ActionMode, menu: Menu) = false

        override fun onDestroyActionMode(mode: ActionMode) {
            actionMode = null
            viewModel.clearSelection()
        }
    }

    private fun createItemTouchHelperCallback() = object : ItemTouchHelper.SimpleCallback(
        0,
        ItemTouchHelper.START or ItemTouchHelper.END
    ) {

        override fun onChildDraw(
            c: Canvas,
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            dX: Float,
            dY: Float,
            actionState: Int,
            isCurrentlyActive: Boolean
        ) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)

            val icon = requireContext().getDrawable(R.drawable.ic_delete) ?: throw IllegalStateException("Delete icon not found")
            val margin = resources.getDimensionPixelOffset(R.dimen.list_item_swipe_icon_margin)

            with(ShapeDrawable()) {
                setTint(Color.RED)
                setBounds(viewHolder.itemView.left, viewHolder.itemView.top, viewHolder.itemView.right, viewHolder.itemView.bottom)
                draw(c)
            }

            with(icon) {
                setTint(Color.WHITE)

                val top = viewHolder.itemView.top + viewHolder.itemView.height / 2 - intrinsicHeight / 2
                val bottom = top + intrinsicHeight
                var left = viewHolder.itemView.left + margin
                var right = left + intrinsicWidth
                setBounds(left, top, right, bottom)
                draw(c)

                right = viewHolder.itemView.right - margin
                left = right - intrinsicWidth
                setBounds(left, top, right, bottom)
                draw(c)
            }
        }

        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ) = false

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            (viewHolder as? ViewHolder)?.binding?.preset?.let {
                viewModel.delete(it)
            } ?: throw IllegalStateException("No corresponding preset for deletion")
        }
    }

    private fun createDiffUtilItemCallback() = object : DiffUtil.ItemCallback<Preset>() {

        override fun areItemsTheSame(oldItem: Preset, newItem: Preset) = oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Preset, newItem: Preset) = oldItem == newItem
    }

    private fun createRecyclerViewAdapter() =
        object : ListAdapter<Preset, ViewHolder>(createDiffUtilItemCallback()) {

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
                ListItemPresetsBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )

            override fun onBindViewHolder(holder: ViewHolder, position: Int) {
                holder.bind(getItem(position))
            }
        }

    private inner class ViewHolder(val binding: ListItemPresetsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            with(binding) {
                viewModel = this@PresetsFragment.viewModel
                lifecycleOwner = viewLifecycleOwner
            }
        }

        fun bind(preset: Preset) {
            with(binding) {
                this.preset = preset
                root.tag = preset.hashCode()
            }
        }
    }
}