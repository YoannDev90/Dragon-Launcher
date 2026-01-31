plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "org.elnix.dragonlauncher.base"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        minSdk = 26

        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        debug {}
        release {}

        create("unminifiedRelease") {
            initWith(getByName("release"))
        }

        create("debuggableRelease") {
            initWith(getByName("release"))
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlin {
        jvmToolchain(17)
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.material)
    implementation(libs.androidx.compose.runtime)

    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
}
