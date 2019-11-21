package com.github.csandiego.timesup.presets

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.github.csandiego.timesup.R
import com.github.csandiego.timesup.data.Preset
import com.github.csandiego.timesup.repository.TestPresetRepository
import org.junit.Before
import org.junit.Test

class PresetsFragmentGivenNoDataUITest {

    private lateinit var repository: TestPresetRepository
    private val preset = Preset(name = "1 second", seconds = 1)

    @Before
    fun setUp() {
        repository = TestPresetRepository()
        val viewModelFactory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return PresetsViewModel(repository) as T
            }
        }
        launchFragmentInContainer(themeResId = R.style.Theme_TimesUp) {
            PresetsFragment(viewModelFactory)
        }
    }

    @Test
    fun whenLoadedThenShowEmptyView() {
        onView(withId(R.id.emptyView))
            .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
    }

    @Test
    fun whenLoadedThenHideRecyclerView() {
        onView(withId(R.id.recyclerView))
            .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)))
    }

    @Test
    fun whenPresetAddedThenHideEmptyView() {
        repository.saveBlocking(preset)
        onView(withId(R.id.emptyView))
            .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)))
    }

    @Test
    fun whenPresetAddedThenShowRecyclerView() {
        repository.saveBlocking(preset)
        onView(withId(R.id.recyclerView))
            .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
    }
}