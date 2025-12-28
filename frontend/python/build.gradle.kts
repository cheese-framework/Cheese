plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    id("com.chaquo.python")
}
//val dependenciesFile = File("${project.projectDir}/dependencies.json")
//println(dependenciesFile.absolutePath)
//val dependenciesJson = groovy.json.JsonSlurper().parseText(dependenciesFile.readText()) as Map<String, List<String>>
chaquopy {
    defaultConfig {
//        version = "3.8"
        pip {
//            install("numpy")
//            (dependenciesJson["pip"] as List<String>).forEach { dep ->
//                install(dep)
//            }
        }
    }
}

android {
    namespace = "net.codeocean.cheese.frontend.python"
    compileSdk = 35

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")

        ndk {
            abiFilters += listOf(
                "arm64-v8a", "armeabi-v7a", "x86", "x86_64"
            )
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    api (project(":core"))
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}