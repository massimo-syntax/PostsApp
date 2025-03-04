plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.gms.google.services)
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.example.postsapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.postsapp"
        minSdk = 24
        targetSdk = 35
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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures{
        viewBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    
    implementation(libs.firebase.auth)
    //    coroutines support for firebase operations
    implementation(libs.kotlinx.coroutines.play.services)
    //    Lifecycle-aware coroutine scopes
    implementation(libs.androidx.lifecycle.runtime.ktx)


    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)



    val room_version = "2.6.1"

    implementation("androidx.room:room-runtime:$room_version")

    // If this project uses any Kotlin source, use Kotlin Symbol Processing (KSP)
    // See Add the KSP plugin to your project
    ksp("androidx.room:room-compiler:2.5.0")

    
    // optional - Kotlin Extensions and Coroutines support for Room
    implementation("androidx.room:room-ktx:$room_version")


    /*
    *
    *
    *
    *
    * This operation requires the libraries androidx.navigation:navigation-fragment-ktx:+,
    *  androidx.navigation:navigation-ui-ktx:+.
    *   Problem: Inconsistencies in the existing project dependencies found.
    *  Version incompatibility between:
    * - androidx.appcompat:appcompat:1.7.0 and: - androidx.lifecycle:lifecycle-runtime-android:2.8.7
    * With the dependency:
    * - androidx.lifecycle:lifecycle-common:2.3.1 versus: - androidx.lifecycle:lifecycle-common:[2.8.7]
    * The project may not compile after adding these libraries. Would you like to add them anyway?
    *
    *
    *
    *
    *
    *
    *
    *
    *
    *
    *
    *
    *  */



}