
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
    id("dagger.hilt.android.plugin")
    id("kotlin-kapt")
    id("kotlin-parcelize")
    id("com.google.devtools.ksp")
    id("kotlinx-serialization")
}

kotlin {
    sourceSets {
        debug {
            kotlin.srcDir("build/generated/ksp/debug/kotlin")
        }
        release {
            kotlin.srcDir("build/generated/ksp/release/kotlin")
        }
    }
}
android {
    compileSdk = 34

    defaultConfig {
        applicationId = "com.nullpointer.blogcompose"
        minSdk = 21
        targetSdk = 34
        versionCode = 3
        versionName = "3.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables.useSupportLibrary = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs = freeCompilerArgs + listOf(
                "-opt-in=androidx.compose.animation.ExperimentalAnimationApi",
                "-opt-in=androidx.compose.material.ExperimentalMaterialApi",
            )
        }
    }

    applicationVariants.all {
        addJavaSourceFoldersToModel(
            File(buildDir, "generated/ksp/$name/kotlin")
        )
    }


    namespace = "com.nullpointer.blogcompose"


}


dependencies {

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.compose.ui:ui:1.6.0")
    implementation("androidx.compose.material:material:1.6.0")
    implementation("androidx.compose.ui:ui-tooling-preview:1.6.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation("androidx.test.ext:junit-ktx:1.1.5")
    implementation("androidx.test:runner:1.5.2")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.6.0")
    debugImplementation("androidx.compose.ui:ui-tooling:1.6.0")
    debugImplementation("androidx.compose.ui:ui-test-manifest:1.6.0")

    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.5.1")

    // * navigation compose
    implementation("androidx.navigation:navigation-compose:2.5.2")
    implementation("io.github.raamcosta.compose-destinations:animations-core:1.5.12-beta")
    ksp("io.github.raamcosta.compose-destinations:ksp:1.5.12-beta")

    implementation("com.facebook.android:facebook-login:13.0.0")

    // * hilt
    val daggerHiltVersion = "2.50"
    implementation("com.google.dagger:hilt-android:${daggerHiltVersion}")
    kapt("com.google.dagger:hilt-compiler:${daggerHiltVersion}")
    implementation("androidx.hilt:hilt-navigation-compose:1.1.0")
    // ? hilt test
    testImplementation("com.google.dagger:hilt-android-testing:${daggerHiltVersion}")
    androidTestImplementation("com.google.dagger:hilt-android-testing:${daggerHiltVersion}")
    kaptAndroidTest("com.google.dagger:hilt-android-compiler:${daggerHiltVersion}")

    // * room
    val roomVersion = "2.6.1"
    implementation("androidx.room:room-runtime:${roomVersion}")
    ksp("androidx.room:room-compiler:${roomVersion}")
    implementation("androidx.room:room-ktx:${roomVersion}")
    testImplementation("androidx.room:room-testing:${roomVersion}")


    // * Timber
    implementation("com.jakewharton.timber:timber:4.7.1")
    implementation("com.orhanobut:logger:2.2.0")

    // * coil
    implementation("io.coil-kt:coil-compose:2.2.2")

    // * swipe refresh
    implementation("com.google.accompanist:accompanist-swiperefresh:0.25.1")

    // * Firebase
    implementation(platform("com.google.firebase:firebase-bom:29.0.0"))
    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation("com.google.firebase:firebase-storage-ktx")
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-messaging-ktx")
    implementation("com.google.android.gms:play-services-auth:20.3.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.6.4")
    implementation("com.google.firebase:firebase-functions-ktx")

    // * paddings
    implementation("com.google.accompanist:accompanist-insets:0.24.1-alpha")
    implementation("com.google.accompanist:accompanist-insets-ui:0.24.1-alpha")

    // * save state view model
    implementation("androidx.savedstate:savedstate-ktx:1.2.0")

    // * img compress
    implementation("com.github.Shouheng88:compressor:1.6.0")

    // * splash
    implementation("androidx.core:core-splashscreen:1.0.0")

    // * data store
    implementation("androidx.datastore:datastore:1.0.0")
    implementation("androidx.datastore:datastore-preferences:1.0.0")

    // * shimmer effect
    implementation("com.valentinilk.shimmer:compose-shimmer:1.0.3")

    // *lottie compose
    implementation("com.airbnb.android:lottie-compose:5.1.1")

    implementation("com.google.code.gson:gson:2.8.9")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.0")

    implementation("com.github.CanHub:Android-Image-Cropper:4.3.2")

    implementation("io.coil-kt:coil:2.2.2")
    implementation("com.jsibbold:zoomage:1.3.1")

    implementation("androidx.lifecycle:lifecycle-service:2.2.0")


}

