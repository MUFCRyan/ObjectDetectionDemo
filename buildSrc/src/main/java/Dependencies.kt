object BuildVersion {
  const val compileSdk = 30
  const val buildTools = "30.0.2"
  const val minSdk = 21
  const val targetSdk = 30
}

object Versions {
  const val kotlin = "1.4.30"
  const val core_ktx = "1.3.2"
  const val appcompat = "1.2.0"
  const val constraintlayout = "2.1.1"
  const val material = "1.3.0"
  const val retrofit = "2.9.0"
  const val multidex = "2.0.1"
  const val recyclerview = "1.2.1"
  const val lifecycle_runtime_ktx = "2.3.1"
  const val lifecycle_extensions = "2.2.0"
  const val kotlin_stdlib_jdk7 = "1.5.21"
  const val rxbinding = "2.1.0"
  const val rxjava = "2.2.9"
  const val rxandroid = "2.1.1"
  const val okhttp = "4.9.0"
  const val adapter_rxjava2 = "2.6.2"
  const val converter_gson = "2.6.2"
  const val gson = "2.8.6"
  const val glide = "4.11.0"
  const val glide_compiler = "4.8.0"
  const val glide_transformations = "4.0.1"
  const val stetho = "1.5.0"
  const val permissionsdispatcher = "4.8.0"
  const val permissionsdispatcher_processor = "4.8.0"
  const val smoothprogressbar_library = "1.1.0"
  const val smoothprogressbar_library_circular = "1.3.0"
  const val photo_view = "2.3.0"
  const val pytorch_android = "1.7.0"
  const val pytorch_android_torchvision = "1.7.0"

  const val junit = "4.13.2"
  const val ext_junit = "1.1.3"
  const val espresso_core = "3.4.0"
}

object Libs {
  const val kotlin_stdlib = "org.jetbrains.kotlin:kotlin-stdlib:${Versions.kotlin}"
  const val core_ktx = "androidx.core:core-ktx:1.3.2:${Versions.core_ktx}"
  const val appcompat = "androidx.appcompat:appcompat:${Versions.appcompat}"
  const val constraintlayout = "androidx.constraintlayout:constraintlayout:${Versions.constraintlayout}"
  const val material = "com.google.android.material:material:${Versions.material}"
  const val retrofit = "com.squareup.retrofit2:retrofit:${Versions.retrofit}"
  const val multidex = "androidx.multidex:multidex:${Versions.multidex}"
  const val recyclerview = "androidx.recyclerview:recyclerview:${Versions.recyclerview}"
  const val lifecycle_runtime_ktx = "androidx.lifecycle:lifecycle-runtime-ktx:${Versions.lifecycle_runtime_ktx}"
  const val lifecycle_extensions = "androidx.lifecycle:lifecycle-extensions:${Versions.lifecycle_extensions}"
  const val kotlin_stdlib_jdk7 = "org.jetbrains.kotlin:kotlin-stdlib-jdk7:${Versions.kotlin_stdlib_jdk7}"

  // opensource third-jar
  const val rxbinding = "com.jakewharton.rxbinding2:rxbinding:${Versions.rxbinding}"
  const val rxjava = "io.reactivex.rxjava2:rxjava:${Versions.rxjava}}"
  const val rxandroid = "io.reactivex.rxjava2:rxandroid:${Versions.rxandroid}"
  const val okhttp = "com.squareup.okhttp3:okhttp:${Versions.okhttp}"
  const val adapter_rxjava2 = "com.squareup.retrofit2:adapter-rxjava2:${Versions.adapter_rxjava2}"
  const val converter_gson = "com.squareup.retrofit2:converter-gson:${Versions.converter_gson}"
  const val gson = "com.google.code.gson:gson:${Versions.gson}"

  const val glide = "com.github.bumptech.glide:glide:${Versions.glide}"
  const val glide_compiler = "com.github.bumptech.glide:compiler:${Versions.glide_compiler}"
  const val glide_transformations = "jp.wasabeef:glide-transformations:${Versions.glide_transformations}"
  // Stetho
  const val stetho = "com.facebook.stetho:stetho:${Versions.stetho}"

  const val permissionsdispatcher = "org.permissionsdispatcher:permissionsdispatcher:${Versions.permissionsdispatcher}"
  const val permissionsdispatcher_processor = "org.permissionsdispatcher:permissionsdispatcher-processor:${Versions.permissionsdispatcher_processor}"

  const val smoothprogressbar_library = "com.github.castorflex.smoothprogressbar:library:${Versions.smoothprogressbar_library}"
  const val smoothprogressbar_library_circular = "com.github.castorflex.smoothprogressbar:library-circular:${Versions.smoothprogressbar_library_circular}"
  const val photo_view = "com.github.chrisbanes:PhotoView:${Versions.photo_view}"
  // pytorch
  const val pytorch_android = "org.pytorch:pytorch_android:${Versions.pytorch_android}"
  const val pytorch_android_torchvision = "org.pytorch:pytorch_android_torchvision:${Versions.pytorch_android_torchvision}"
}

object TestLibs {
  const val junit = "junit:junit:${Versions.junit}"
  const val ext_junit = "androidx.test.ext:junit:${Versions.ext_junit}"
  const val espresso_core = "androidx.test.espresso:espresso-core:${Versions.espresso_core}"
}
