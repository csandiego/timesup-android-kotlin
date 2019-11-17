package com.github.csandiego.timesup.presets

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ShapeDrawable
import android.os.Bundle
import android.view.*
import android.view.animation.DecelerateInterpolator
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.*
import com.github.csandiego.timesup.R
import com.github.csandiego.timesup.data.Preset
import com.github.csandiego.timesup.databinding.ListItemPresetsBinding
import com.google.android.material.card.MaterialCardView
import kotlinx.android.synthetic.main.fragment_presets.*
import javax.inject.Inject
import kotlin.math.absoluteValue

class PresetsFragment @Inject constructor(viewModelFactory: ViewModelProvider.Factory) :
    Fragment(R.layout.fragment_presets) {

    private val viewModel by viewModels<PresetsViewModel> { viewModelFactory }
    private var actionMode: ActionMode? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        try {
            val navController = findNavController()
            toolbar.setupWithNavController(navController, AppBarConfiguration(navController.graph))
        } catch (e: Exception) {
        }
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
                recyclerView.visibility = if (it.isEmpty()) View.GONE else View.VISIBLE
                emptyView.visibility = if (it.isEmpty()) View.VISIBLE else View.GONE
            }
            selection.observe(viewLifecycleOwner) {
                if (it.isEmpty()) {
                    actionMode?.finish()
                } else {
                    val mode =
                        actionMode ?: requireActivity().startActionMode(createActionModeCallback())!!
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
        findNavController().navigate(
            PresetsFragmentDirections.actionPresetsFragmentToTimerFragment(preset.id)
        )
    }

    private fun createActionModeCallback() = object : ActionMode.Callback {

        override fun onActionItemClicked(mode: ActionMode, item: MenuItem) = when (item.itemId) {
            R.id.menuEdit -> {
                findNavController().navigate(
                    PresetsFragmentDirections
                        .actionPresetsFragmentToEditPresetFragment(
                            viewModel.selection.value!!.first()
                        )
                )
                true
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

            val icon = requireContext().getDrawable(R.drawable.ic_delete)!!.mutate()
            val margin = resources.getDimensionPixelOffset(R.dimen.list_item_swipe_icon_margin)
            val radius = resources.getDimension(R.dimen.list_Item_swipe_radius)
            val interpolator = DecelerateInterpolator()

            with(ShapeDrawable()) {
                setTint(Color.RED)
                setBounds(
                    viewHolder.itemView.left,
                    viewHolder.itemView.top,
                    viewHolder.itemView.right,
                    viewHolder.itemView.bottom
                )
                draw(c)
            }

            with(icon) {
                setTint(Color.WHITE)

                val top =
                    viewHolder.itemView.top + viewHolder.itemView.height / 2 - intrinsicHeight / 2
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

            with(viewHolder.itemView as MaterialCardView) {
                this.radius = radius * interpolator.getInterpolation(dX.absoluteValue / width)
            }
        }

        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ) = false

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            viewModel.delete((viewHolder as ViewHolder).binding.preset!!)
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
            binding.preset = preset
        }
    }
}