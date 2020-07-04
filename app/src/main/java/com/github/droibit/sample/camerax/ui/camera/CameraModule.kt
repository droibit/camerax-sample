package com.github.droibit.sample.camerax.ui.camera

import android.content.Context
import android.os.Environment.DIRECTORY_PICTURES
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Named

@Module
@InstallIn(ActivityRetainedComponent::class)
object CameraXViewModelModule {

    @Named("pictureOutputDirectory")
    @Provides
    fun provideOutputDirectory(@ApplicationContext context: Context): File {
        return requireNotNull(context.getExternalFilesDir(DIRECTORY_PICTURES))
    }
}