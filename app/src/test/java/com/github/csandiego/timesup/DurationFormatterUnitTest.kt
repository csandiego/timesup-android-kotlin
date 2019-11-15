package com.github.csandiego.timesup

import com.github.csandiego.timesup.timer.DurationFormatter
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class DurationFormatterUnitTest {

    @Test
    fun givenDurationWhenFormatThenProduceFormattedString() {
        val hours = 12
        val minutes = 34
        val seconds = 56
        val duration = hours * 60L * 60L + minutes * 60L + seconds
        val string = String.format("%02d:%02d:%02d", hours, minutes, seconds)
        assertThat(DurationFormatter.format(duration)).isEqualTo(string)
    }
}