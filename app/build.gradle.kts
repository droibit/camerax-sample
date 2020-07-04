import org.jetbrains.kotlin.ir.backend.js.compile

plugins {
  id("com.android.application")
  kotlin("android")
  kotlin("kapt")
  id("kotlin-android-extensions")
  id("dagger.hilt.android.plugin")
}

android {
  compileSdkVersion(29)

  defaultConfig {
    applicationId = "com.github.droibit.sample.camerax"
    minSdkVersion(23)
    targetSdkVersion(29)
    versionCode = 1
    versionName = "1.0"

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }

  buildTypes {
    getByName("release") {
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
    }
  }

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
  }

  kotlinOptions {
    jvmTarget = "1.8"
  }
}

dependencies {
  val kotlinVersion: String by project
  implementation(kotlin("stdlib-jdk8", version = kotlinVersion))

  val coroutinesVersion = "1.3.7"
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutinesVersion")

  implementation("androidx.core:core-ktx:1.3.0")
  implementation("androidx.fragment:fragment-ktx:1.3.0-alpha06")
  implementation("androidx.appcompat:appcompat:1.1.0")
  implementation("androidx.constraintlayout:constraintlayout:1.1.3")

  val cameraxVersion = "1.0.0-beta06"
  implementation("androidx.camera:camera-core:$cameraxVersion")
  implementation("androidx.camera:camera-camera2:$cameraxVersion")
  implementation("androidx.camera:camera-lifecycle:$cameraxVersion")
  implementation("androidx.camera:camera-view:1.0.0-alpha13")

  val lifecycleVersion = "2.3.0-alpha05"
  implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycleVersion")
  implementation("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycleVersion")

  val navigationVersion = "2.3.0"
  implementation("androidx.navigation:navigation-fragment-ktx:$navigationVersion")

  val daggerVersion: String by project
  implementation("com.google.dagger:hilt-android:$daggerVersion")
  kapt("com.google.dagger:hilt-android-compiler:$daggerVersion")

  val hiltVersion = "1.0.0-alpha01"
  implementation("androidx.hilt:hilt-lifecycle-viewmodel:$hiltVersion")
  kapt("androidx.hilt:hilt-compiler:$hiltVersion")

  implementation("com.jakewharton.timber:timber:4.7.1")

  testImplementation("junit:junit:4.13")
  androidTestImplementation("androidx.test.ext:junit:1.1.1")
  androidTestImplementation("androidx.test.espresso:espresso-core:3.2.0")
}

kapt {
  correctErrorTypes = true
}