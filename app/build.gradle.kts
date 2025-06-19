plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.howdoesthehumaneyeseethings"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.howdoesthehumaneyeseethings"
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

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(17))
        }
    }

    buildFeatures {
        viewBinding = true
    }

    // Support loading .gltf and .bin files from assets
    packaging {
        resources {
            excludes += setOf("/META-INF/{AL2.0,LGPL2.1}")
        }
        jniLibs {
            useLegacyPackaging = true
        }
    }

    androidResources {
        noCompress += listOf("gltf","glb", "bin", "jpeg", "png")
    }

    // Optional: include extra asset folders
    sourceSets {
        getByName("main") {
            assets.srcDirs("src/main/assets/human_eye")
        }
    }
}

// Apply to all projects
allprojects {
    tasks.withType<JavaCompile>().configureEach {
        options.compilerArgs.add("-Xlint:deprecation")
    }
}

dependencies {
    implementation(project(":openCVLibrary"))
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.activity)

    // ARCore + SceneView
    implementation(libs.arcore)
    //implementation("com.gorisse.thomas.sceneform:core:1.23.0")
    implementation("com.gorisse.thomas.sceneform:ux:1.23.0")

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}
