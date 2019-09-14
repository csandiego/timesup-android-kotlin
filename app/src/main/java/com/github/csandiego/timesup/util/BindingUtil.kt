package com.github.csandiego.timesup.util

import androidx.databinding.InverseMethod

object BindingUtil {

    @JvmStatic
    @InverseMethod("stringToInt")
    fun intToString(newValue: Int) = newValue.toString()

    @JvmStatic
    fun stringToInt(newValue: String) = newValue?.toInt()
}