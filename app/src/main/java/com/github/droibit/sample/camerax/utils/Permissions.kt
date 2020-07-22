package com.github.droibit.sample.camerax.utils

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

fun Fragment.checkCameraPermissionGranted(): Boolean {
    return ContextCompat.checkSelfPermission(
        requireContext(),
        Manifest.permission.CAMERA
    ) == PackageManager.PERMISSION_GRANTED
}

fun Fragment.showCameraPermissionErrorToast() {
    Toast.makeText(requireContext(), "Grant the camera permission.", Toast.LENGTH_SHORT).show()
}