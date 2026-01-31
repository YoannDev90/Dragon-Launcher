plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "org.elnix.dragonlauncher.enumsui"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        minSdk = 26

        consumerProguardFiles("consumer-rules.pro")
    }

    flavorDimensions += "channel"

    productFlavors {
        create("stable") { dimension = "channel" }
        create("beta")   { dimension = "channel" }
        create("fdroid") { dimension = "channel" }
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

    kotlin {
        jvmToolchain(17)
    }

    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)

    /* Composable, icons and stringResource in enums */
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.material.icons.extended)


    implementation(project(":core:common"))
}
