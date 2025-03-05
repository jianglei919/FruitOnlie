package com.conestoga.group12.fruitonline.utils

import android.widget.Button
import com.conestoga.group12.fruitonline.R

object ButtonUtils {
    fun disabledButton(button: Button) {
        button.isEnabled = false
        button.setBackgroundResource(R.drawable.button_disabled)
    }

    fun displayButton(button: Button) {
        button.isEnabled = true
        button.setBackgroundResource(R.drawable.rounded_button)
    }
}
