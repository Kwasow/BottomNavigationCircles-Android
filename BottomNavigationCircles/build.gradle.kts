plugins {
    id("com.android.library")
    id("maven-publish")
    kotlin("android")
}

val sharedVersionName = "1.1-beta3"

fun versionCode(): Int {
    val secondsSinceEpoch = System.currentTimeMillis() / 1000
    // This will fail eventually, but well… It's the best we have
    return secondsSinceEpoch.toInt()
}

android {
    compileSdkVersion(30)
    buildToolsVersion = "30.0.3"

    defaultConfig {
        minSdkVersion(21)
        targetSdkVersion(30)
        versionCode = versionCode()
        versionName = sharedVersionName

        testInstrumentationRunner("androidx.test.runner.AndroidJUnitRunner")
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
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
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.4.32")
    implementation("androidx.core:core-ktx:1.3.2")
    implementation("androidx.appcompat:appcompat:1.2.0")
    api("com.google.android.material:material:1.4.0-alpha02")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.4.1")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.2")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.3.0")
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                from(components["release"])

                groupId = "com.github.kwasow"
                artifactId = "bottomnavigationcircles"
                version = sharedVersionName
            }
        }
    }
}