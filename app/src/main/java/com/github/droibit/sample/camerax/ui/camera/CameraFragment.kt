package com.github.droibit.sample.camerax.ui.camera

import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import android.widget.Toast
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.view.doOnAttach
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import com.github.droibit.sample.camerax.R
import com.github.droibit.sample.camerax.ui.camera.CameraFragmentDirections.Companion.toGalleryFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_camera.*
import timber.log.Timber
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

private const val RATIO_4_3_VALUE = 4.0 / 3.0
private const val RATIO_16_9_VALUE = 16.0 / 9.0

@AndroidEntryPoint
class CameraFragment : Fragment(R.layout.fragment_camera) {

    private val cameraViewModel: CameraXViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cameraViewModel.processCameraProvider.observe(viewLifecycleOwner) { cameraProvider ->
            view_finder.doOnAttach {
                bindCameraUseCases(cameraProvider)
            }
        }

        cameraViewModel.takePictureResult.observe(viewLifecycleOwner) { event ->
            event.consume()?.let { result ->
                when(result) {
                    is TakePictureResult.Success -> {
                        showShortToast("The photo was taken.")
                    }
                    is TakePictureResult.Failure -> {
                        showShortToast("Failed to take a photo.")
                    }
                }
            }
        }

        camera_capture_button.setOnClickListener {
            cameraViewModel.takePhoto()
        }

        cameraViewModel.navigateToGallery.observe(viewLifecycleOwner) {
            it.consume()?.let { photoUris ->
                if (photoUris.isEmpty()) {
                    showShortToast("There are no photos.")
                } else {
                    findNavController().navigate(toGalleryFragment(photoUris.toTypedArray()))
                }
            }
        }

        gallery_button.setOnClickListener {
            cameraViewModel.onGalleryButtonClick()
        }
    }

    private fun showShortToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun bindCameraUseCases(cameraProvider: ProcessCameraProvider) {
        val cameraSelector = CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
            .build()

        // Get screen metrics used to setup camera for full screen resolution
        val metrics = DisplayMetrics().also { view_finder.display.getRealMetrics(it) }
        Timber.d("Screen metrics: ${metrics.widthPixels} x ${metrics.heightPixels}")

        val screenAspectRatio = aspectRatio(metrics.widthPixels, metrics.heightPixels)
        Timber.d("Preview aspect ratio: $screenAspectRatio")

        val rotation = view_finder.display.rotation
        val preview = Preview.Builder()
            // We request aspect ratio but no resolution
                .setTargetAspectRatio(screenAspectRatio)
            // Set initial target rotation
                .setTargetRotation(rotation)
            .build()

        val imageCapture = ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            // We request aspect ratio but no resolution to match preview config, but letting
            // CameraX optimize for whatever specific resolution best fits our use cases
                .setTargetAspectRatio(screenAspectRatio)
            // Set initial target rotation, we will have to call this again if rotation changes
            // during the lifecycle of this use case
                .setTargetRotation(rotation)
            .build().also {
                cameraViewModel.setImageCapture(imageCapture = it)
            }

        // ImageAnalysis
        val imageAnalyzer = ImageAnalysis.Builder()
            // We request aspect ratio but no resolution
                .setTargetAspectRatio(screenAspectRatio)
            // Set initial target rotation, we will have to call this again if rotation changes
            // during the lifecycle of this use case
                .setTargetRotation(rotation)
            .build()

        cameraProvider.unbindAll()

        cameraProvider.bindToLifecycle(this,
            cameraSelector, preview, imageCapture, imageAnalyzer
        )
        preview.setSurfaceProvider(view_finder.createSurfaceProvider())
    }
}

/**
 *  [androidx.camera.core.ImageAnalysis] requires enum value of
 *  [androidx.camera.core.AspectRatio]. Currently it has values of 4:3 & 16:9.
 *
 *  Detecting the most suitable ratio for dimensions provided in @params by counting absolute
 *  of preview ratio to one of the provided values.
 *
 *  @param width - preview width
 *  @param height - preview height
 *  @return suitable aspect ratio
 */
private fun aspectRatio(width: Int, height: Int): Int {
    val previewRatio = max(width, height).toDouble() / min(width, height)
    if (abs(previewRatio - RATIO_4_3_VALUE) <= abs(previewRatio - RATIO_16_9_VALUE)) {
        return AspectRatio.RATIO_4_3
    }
    return AspectRatio.RATIO_16_9
}
