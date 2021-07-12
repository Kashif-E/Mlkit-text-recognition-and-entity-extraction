package io.intelligible.intelligentocr.constants

import android.Manifest

class Constants {
    companion object{
        // This is an array of all the permission specified in the manifest.
        val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        const val RATIO_4_3_VALUE = 4.0 / 3.0
        const val RATIO_16_9_VALUE = 16.0 / 9.0
    }
}