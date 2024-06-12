plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("org.jetbrains.kotlin.kapt")
}

android {
    namespace = "com.example.whatsappstatussaver"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.whatsappstatussaver"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    buildFeatures{
        viewBinding = true
        dataBinding = true
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
    implementation("androidx.viewpager2:viewpager2:1.1.0")
    implementation("com.google.android.material:material:1.3.0")
    implementation("com.github.bumptech.glide:glide:4.11.0")
    implementation(libs.firebase.crashlytics.buildtools)
    implementation(libs.androidx.ui.desktop)
    annotationProcessor("com.github.bumptech.glide:compiler:4.11.0")
    implementation(libs.androidx.core.ktx)
    implementation("com.karumi:dexter:6.2.2")
    implementation("commons-io:commons-io:2.16.1")
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    kapt("androidx.room:room-compiler:2.5.0")
    implementation("de.hdodenhof:circleimageview:3.1.0")
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}