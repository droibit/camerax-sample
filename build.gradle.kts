// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
  val kotlinVersion by extra("1.4.10")
  val daggerVersion by extra("2.29.1-alpha")
  val navigationVersion by extra("2.3.1")

  repositories {
    google()
    jcenter()
  }
  dependencies {
    classpath("com.android.tools.build:gradle:4.1.1")
    classpath(kotlin("gradle-plugin", version = kotlinVersion))
    classpath("com.google.dagger:hilt-android-gradle-plugin:$daggerVersion")
    classpath("androidx.navigation:navigation-safe-args-gradle-plugin:$navigationVersion")
  }
}

allprojects {
  repositories {
    google()
    jcenter()
  }
}

task<Delete>("clean") {
  delete(rootProject.buildDir)
}
