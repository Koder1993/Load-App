package com.udacity.util

import com.udacity.R

enum class RadioOption(val url: String, val fileNamePath: Int) {
    GLIDE(
        "https://github.com/bumptech/glide/archive/refs/heads/master.zip",
        R.string.glide_radio_button_text
    ),
    LOAD_APP(
        "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip",
        R.string.load_app_radio_button_text
    ),
    RETROFIT(
        "https://github.com/square/retrofit/archive/refs/heads/master.zip",
        R.string.retrofit_radio_button_text
    ),
    NOT_SELECTED(
        "", -1
    )
}