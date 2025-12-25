plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
    id("kotlin-kapt")
}

android {
    namespace = "com.flatcode.littlemusic"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.flatcode.littlemusic"
        minSdk = 26
        targetSdk = 36
        versionCode = 7
        versionName = "1.35"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("release") {
            storeFile = file("D:\\MyProjects\\Kotlin\\Little Music\\Little Music\\LittleMusic.jks")
            storePassword = "00000000"
            keyAlias = "LittleMusic"
            keyPassword = "00000000"
        }
    }
    buildTypes {
        getByName("release") {
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlin {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        }
    }
    buildFeatures {
        dataBinding = true
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.17.0")
    implementation("androidx.appcompat:appcompat:1.7.1")
    implementation("androidx.preference:preference-ktx:1.2.1")           //Shared Preference
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.3.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.7.0")
    //Layout
    implementation("androidx.constraintlayout:constraintlayout:2.2.1")
    implementation("com.google.android.material:material:1.13.0")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    implementation("androidx.cardview:cardview:1.0.0")
    //Firebase
    implementation(platform("com.google.firebase:firebase-bom:34.7.0")) //Firebase BOM
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-database")
    implementation("com.google.firebase:firebase-storage")
    implementation("com.google.firebase:firebase-analytics")
    //implementation("com.google.firebase:firebase-crashlytics")
    //Image
    implementation("de.hdodenhof:circleimageview:3.1.0")                //Circle Image
    implementation("com.github.bumptech.glide:glide:5.0.5")            //Glide Image
    //noinspection KaptUsageInsteadOfKsp
    kapt("com.github.bumptech.glide:compiler:5.0.5")                   //Glide Compiler
    implementation("com.balysv:material-ripple:1.0.2")                  //Ripple Effect
    api("com.theartofdev.edmodo:android-image-cropper:2.8.0")           //Image Crop
    //noinspection GradleDependency
    implementation("com.github.smarteist:autoimageslider:1.3.2-appcompat")//Slider Show
    implementation("jp.wasabeef:glide-transformations:4.3.0")           //Image Blur
    //Bottom Navigation
    implementation ("com.github.Foysalofficial:NafisBottomNav:5.0")     //Nafis Bottom Navigation

    //Player
    implementation("com.github.jeancsanchez:JcPlayer:2.7.2")            //JcPlayer
    implementation("com.scwang.wave:MultiWaveHeader:1.0.0-andx")        //Wave
}