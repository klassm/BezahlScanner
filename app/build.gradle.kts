import org.jetbrains.kotlin.gradle.dsl.Coroutines

val bezahlScannerReleaseAliasPassword: String by project
val bezahlScannerReleaseStorePassword: String by project

buildscript {
    repositories {
        google()
        jcenter()
    }

    dependencies {
        classpath("com.google.gms:google-services:4.1.0")
    }
}

val kotlinVersion = "1.2.71"

plugins {
    val kotlinPluginVersion = embeddedKotlinVersion

    id("com.android.application") version "3.2.0"
    id("net.researchgate.release").version("2.6.0")
    id("org.jetbrains.kotlin.android").version(embeddedKotlinVersion)
    id("org.jetbrains.kotlin.android.extensions").version(embeddedKotlinVersion)
    id("org.jetbrains.kotlin.kapt").version(embeddedKotlinVersion)
}

fun getAppVersionCode() = version.toString().replace("\\.".toRegex(), "").replace("-SNAPSHOT", "").toInt()

android {
    compileSdkVersion(28)
    buildToolsVersion("28.0.3")


    defaultConfig {
        multiDexEnabled = true
        applicationId = "li.klass.bezahl.scanner"
        minSdkVersion(17)
        targetSdkVersion(26)
        versionCode = getAppVersionCode()
        versionName = version.toString()
    }

    signingConfigs {
        create("release") {
            storeFile = file ("release.jks")
            keyAlias = "BezahlScanner"
        }
    }

    buildTypes {
        getByName("debug") {
            isMinifyEnabled = false
        }

        getByName("release") {
            isMinifyEnabled  = false
            proguardFiles(getDefaultProguardFile ("proguard-android.txt"), "proguard-production.txt")
            signingConfig = signingConfigs.getByName("release")
        }
    }

    packagingOptions {
        exclude("META-INF/NOTICE.txt")
        exclude("META-INF/LICENSE.txt")
        exclude("APK LICENSE.txt")
        exclude("LICENSE.txt")
    }

    if (project.hasProperty("bezahlScannerReleaseStorePassword")) {
        signingConfigs.getByName("release").storePassword = bezahlScannerReleaseStorePassword
    }
    if (project.hasProperty("bezahlScannerReleaseAliasPassword")) {
        signingConfigs.getByName("release").keyPassword = bezahlScannerReleaseAliasPassword
    }

}

val supportLibVersion = "28.0.0"
val ankoVersion = "0.10.1"

dependencies {
    testCompile("junit:junit:4.12")
    testCompile("com.tngtech.java:junit-dataprovider:1.12.0")
    testCompile("org.assertj:assertj-core:3.10.0")
    testCompile("org.mockito:mockito-core:2.21.0")
    testCompile("com.nhaarman:mockito-kotlin:1.5.0")

    compile("com.android.support:appcompat-v7:$supportLibVersion")
    compile("com.android.support:design:$supportLibVersion")
    compile("com.google.zxing:android-integration:3.2.1")
    compile("com.google.android.gms:play-services-auth:16.0.1")
    compile("com.google.android.gms:play-services-drive:16.0.0")
    compile("org.apache.commons:commons-csv:1.2")
    compile("joda-time:joda-time:2.9.9")
    compile("org.apache.commons:commons-lang3:3.7")
    compile("org.jetbrains.kotlin:kotlin-stdlib-jdk7:$kotlinVersion")
    compile("org.jetbrains.anko:anko-sdk25:$ankoVersion")
    compile("org.jetbrains.anko:anko-appcompat-v7:$ankoVersion")
    compile("org.jetbrains.anko:anko-sdk25-coroutines:$ankoVersion")
    compile("org.jetbrains.anko:anko-appcompat-v7-coroutines:$ankoVersion")
    compile("org.jetbrains.anko:anko-coroutines:$ankoVersion")
    compile("com.android.support:multidex:1.0.3")
}

release {
    buildTasks = listOf("build")
}

kotlin {
    experimental.coroutines = Coroutines.ENABLE
}

apply(plugin = "com.google.gms.google-services")
