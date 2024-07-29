import com.android.build.gradle.internal.api.ApkVariantOutputImpl

plugins {
  alias(libs.plugins.androidApplication)
  alias(libs.plugins.jetbrainsKotlinAndroid)
}

android {
  namespace = "com.aviatormysticalhero"
  compileSdk = 34

  defaultConfig {
    applicationId = "com.aviatormysticalhero"
    minSdk = 24
    targetSdk = 34
    versionCode = 1
    versionName = "1.0"
    viewBinding.isEnabled = true

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }

  buildTypes {
    getByName("debug") {
      isMinifyEnabled = false
    }
    getByName("release") {
      isMinifyEnabled = true
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

  applicationVariants.all {
    outputs.all {
      val output = this as ApkVariantOutputImpl
      output.outputFileName = "AviatorMysticalHero.apk"
    }
  }
}

dependencies {
  implementation(libs.progressBar)
  implementation(libs.navigationFragment)
  implementation(libs.androidx.core.ktx)
  implementation(libs.androidx.appcompat)
  implementation(libs.material)
  implementation(libs.androidx.activity)
  implementation(libs.androidx.constraintlayout)
  testImplementation(libs.junit)
  androidTestImplementation(libs.androidx.junit)
  androidTestImplementation(libs.androidx.espresso.core)
}