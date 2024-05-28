plugins {
    id("com.android.application")
}


android {
    namespace = "com.example.recordapplication"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.recordapplication"
        minSdk = 24
        targetSdk = 33
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation("mysql:mysql-connector-java:5.1.49")
    implementation("pl.droidsonroids.gif:android-gif-drawable:1.1.17")
    implementation("com.karumi:dexter:6.2.3")
    implementation("com.arthenica:mobile-ffmpeg-full:4.4")
    implementation("com.google.android.gms:play-services-location:21.0.1")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")





    implementation("org.florescu.android.rangeseekbar:rangeseekbar-library:0.3.0")

    implementation ("com.mpatric:mp3agic:0.9.1")

    implementation ("com.arthenica:mobile-ffmpeg-full:4.4")




}

