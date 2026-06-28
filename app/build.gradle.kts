        plugins {
            alias(libs.plugins.android.application)
            alias(libs.plugins.kotlin.android)
            alias(libs.plugins.kotlin.compose)
            id("kotlin-parcelize")
            id("com.google.gms.google-services")
            alias(libs.plugins.ksp)
        }

        android {
            namespace = "com.example.nutrifit"
            compileSdk = 35

            defaultConfig {
                applicationId = "com.example.nutrifit"
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
                sourceCompatibility = JavaVersion.VERSION_17
                targetCompatibility = JavaVersion.VERSION_17
            }

            kotlinOptions {
                jvmTarget = "17"
            }

            buildFeatures {
                compose = true
            }
        }

        dependencies {
            // Core AndroidX
            implementation(libs.androidx.core.ktx)
            implementation(libs.androidx.lifecycle.runtime.ktx)
            implementation(libs.androidx.activity.compose)
            implementation(libs.androidx.browser)

            // Jetpack Compose
            implementation(platform(libs.androidx.compose.bom))
            implementation(libs.androidx.compose.ui)
            implementation(libs.androidx.compose.ui.graphics)
            implementation(libs.androidx.compose.ui.tooling.preview)
            implementation(libs.androidx.compose.material3)
            implementation(libs.androidx.compose.material.icons.extended)
            implementation(libs.androidx.navigation.compose)
            implementation(libs.androidx.lifecycle.viewmodel.compose)
            implementation(libs.androidx.compose.ui.text)
            debugImplementation(libs.androidx.compose.ui.tooling)
            debugImplementation(libs.androidx.compose.ui.test.manifest)
            androidTestImplementation(platform(libs.androidx.compose.bom))
            androidTestImplementation(libs.androidx.compose.ui.test.junit4)
            androidTestImplementation(libs.androidx.compose.ui.test)

            // Accompanist
            implementation(libs.accompanist.pager)
            implementation(libs.accompanist.pager.indicators)
            implementation("com.google.accompanist:accompanist-systemuicontroller:0.33.0-alpha")

            // Coil (image loading)
            implementation(libs.coil.compose)

            // ML Kit
            implementation("com.google.mlkit:image-labeling:17.0.9")
            implementation("com.google.mlkit:image-labeling-custom:17.0.3")

            // Retrofit + OkHttp + Gson
            implementation("com.squareup.retrofit2:retrofit:2.9.0")
            implementation("com.squareup.retrofit2:converter-gson:2.9.0")
            implementation("com.squareup.okhttp3:okhttp:4.11.0")
            implementation(libs.gson)
            implementation("org.json:json:20231013")

            // Firebase
            implementation(platform(libs.firebase.bom))
            implementation(libs.firebase.auth)
            implementation(libs.firebase.firestore)
            implementation(libs.firebase.storage)
            implementation(libs.firebase.messaging)

            // Google Sign-In
            implementation(libs.play.services.auth)

            // Time + Coroutines
            implementation(libs.threetenabp)
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")
            implementation(libs.kotlinx.coroutines.play.services)

            // Room
            implementation(libs.androidx.room.runtime)
            implementation(libs.androidx.room.ktx)
            ksp(libs.androidx.room.compiler)

            // WorkManager
            implementation(libs.androidx.work.runtime.ktx)

            // Testing
            testImplementation(libs.junit)
            androidTestImplementation(libs.androidx.junit)
            androidTestImplementation(libs.androidx.espresso.core)
        }
