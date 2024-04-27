plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.diego.tupro"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.diego.tupro"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation(platform("androidx.compose:compose-bom:2024.04.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")

    //implementation("androidx.compose.material:material:1.4.2")
    implementation("androidx.navigation:navigation-compose:2.7.7")
    implementation("androidx.compose.material3:material3-android:1.2.1")
    // view model
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    // iconos
    implementation("androidx.compose.material:material-icons-extended-android:1.6.6")
    // rebote, no funciona
    implementation("com.google.accompanist:accompanist-insets:0.23.0")
    // cambiar barra de tareas
    implementation("com.google.accompanist:accompanist-systemuicontroller:0.23.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")
    // ocultar top bar
    // implementation("com.google.accompanist:accompanist-coil:0.16.0")
    // animaciones nav host
    // implementation("com.google.accompanist:accompanist-navigation-animation:0.23.0")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.04.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    implementation(platform("com.google.firebase:firebase-bom:32.8.1"))
    implementation("com.google.firebase:firebase-analytics")
    // implementation("com.google.firebase:firebase-crashlytics")
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.android.gms:play-services-auth:21.1.0")
    implementation("com.google.firebase:firebase-firestore:24.11.1")
    implementation("com.google.firebase:firebase-core:21.1.1")

    implementation("androidx.compose.runtime:runtime-livedata:1.6.6")
    implementation("androidx.datastore:datastore-preferences:1.1.0")
}
