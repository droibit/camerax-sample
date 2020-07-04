package com.github.droibit.sample.camerax.ui.camera

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.annotation.UiThread
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.github.droibit.sample.camerax.utils.Event
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import timber.log.Timber
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Named
import kotlin.LazyThreadSafetyMode.NONE
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

sealed class TakePictureResult {
    data class Success(val uri: Uri) : TakePictureResult()
    object Failure : TakePictureResult()
}

private const val FILENAME = "yyyy-MM-dd-HH-mm-ss-SSS"

class CameraXViewModel @ViewModelInject constructor(
    @ApplicationContext context: Context,
    @Named("pictureOutputDirectory") private val outputDirectory: File
) : AndroidViewModel(context as Application) {

    private val takePictureResultLiveData = MutableLiveData<Event<TakePictureResult>>()
    val takePictureResult: LiveData<Event<TakePictureResult>> = takePictureResultLiveData

    private var imageCapture: ImageCapture? = null

    private var captureJob: Job? = null

    val processCameraProvider: LiveData<ProcessCameraProvider> by lazy(NONE) {
        val cameraProviderLiveData = MutableLiveData<ProcessCameraProvider>()

        viewModelScope.launch {
            cameraProviderLiveData.value = ProcessCameraProvider(getApplication())
        }
        cameraProviderLiveData
    }

    @UiThread
    fun setImageCapture(imageCapture: ImageCapture) {
        this.imageCapture = imageCapture
    }

    @UiThread
    fun takePicture() {
        if (captureJob?.isActive == true) {
            return
        }

        captureJob = viewModelScope.launch {
            imageCapture?.let { imageCapture ->
                // Create output file to hold the image
                val photoFile = createFile()
                // Setup image capture metadata
                val metadata = ImageCapture.Metadata()

                // Create output options object which contains file + metadata
                val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile)
                    .setMetadata(metadata)
                    .build()

                val result = try {
                    val output = imageCapture.takePicture(outputOptions)
                    val savedUri = output.savedUri ?: Uri.fromFile(photoFile)
                    TakePictureResult.Success(savedUri)
                } catch (e: ImageCaptureException) {
                    Timber.e(e, "Photo capture failed: ${e.message}")
                    TakePictureResult.Failure
                }
                takePictureResultLiveData.value = Event(result)
            }
        }
    }

    private suspend fun ImageCapture.takePicture(outputFileOptions: ImageCapture.OutputFileOptions): ImageCapture.OutputFileResults {
        return suspendCoroutine { cont ->
            takePicture(
                outputFileOptions,
                ContextCompat.getMainExecutor(getApplication()),
                object : ImageCapture.OnImageSavedCallback {
                    override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                        cont.resume(outputFileResults)
                    }

                    override fun onError(exception: ImageCaptureException) {
                        cont.resumeWithException(exception)
                    }
                }
            )
        }
    }

    private fun createFile(): File {
        return File(
            outputDirectory, SimpleDateFormat(FILENAME, Locale.US)
                .format(System.currentTimeMillis()) + "jpg"
        )
    }
}

@Suppress("FunctionName")
private suspend fun ProcessCameraProvider(context: Context): ProcessCameraProvider {
    return suspendCancellableCoroutine { cont ->
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener(Runnable {
            try {
                if (cameraProviderFuture.isCancelled) {
                    cont.cancel()
                } else {
                    cont.resume(cameraProviderFuture.get())
                }
            } catch (e: Exception) {
                cont.resumeWithException(e)
            }
        }, ContextCompat.getMainExecutor(context))

        cont.invokeOnCancellation {
            cameraProviderFuture.cancel(false)
        }
    }
}