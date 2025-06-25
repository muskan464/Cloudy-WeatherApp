import java.util.Properties
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}


// ✅ Manually load local.properties
val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localProperties.load(localPropertiesFile.inputStream())
}

val weatherApiKey = localProperties.getProperty("WEATHER_API_KEY") ?: ""
val weatherBaseUrl = localProperties.getProperty("WEATHER_BASE_URL") ?: ""

android {
    namespace = "com.example.cloudy_weatherapp"
    compileSdk = 35

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }

    defaultConfig {
        applicationId = "com.example.cloudy_weatherapp"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // ✅ Inject secrets into BuildConfig
        buildConfigField("String", "WEATHER_API_KEY", "\"$weatherApiKey\"")
        buildConfigField("String", "WEATHER_BASE_URL", "\"$weatherBaseUrl\"")
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
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    implementation(libs.retrofit)
    implementation(libs.converter.gson)

    implementation(libs.lottie)
    implementation(libs.play.services.location)

    implementation(libs.androidx.media3.exoplayer)
    implementation(libs.androidx.media3.ui)

    implementation(libs.intuit.sdp.android)
    implementation(libs.ssp.android)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
