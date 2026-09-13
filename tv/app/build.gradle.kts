import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.compose.screenshot)
}

android {
    namespace = "com.zorenkonte.tibeepost"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.zorenkonte.tibeepost"
        minSdk = 26
        targetSdk = 35
        versionCode = (findProperty("tibeeVersionCode") as String?)?.toIntOrNull() ?: 1
        versionName = (findProperty("tibeeVersionName") as String?) ?: "0.1.0"
    }

    signingConfigs {
        getByName("debug") {
            storeFile = rootProject.file("debug.keystore")
            storePassword = "android"
            keyAlias = "androiddebugkey"
            keyPassword = "android"
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    testOptions {
        unitTests.isReturnDefaultValues = true
    }

    experimentalProperties["android.experimental.enableScreenshotTest"] = true
}

val webClientDir = rootProject.layout.projectDirectory.dir("../client")
val webClientOutput = layout.buildDirectory.dir("webclient")
val npmAvailable = System.getenv("PATH").orEmpty().split(File.pathSeparator).any { File(it, "npm").canExecute() }

val installWebClient by tasks.registering(Exec::class) {
    onlyIf { npmAvailable && !webClientDir.dir("node_modules").asFile.exists() }
    workingDir(webClientDir)
    commandLine("npm", "ci", "--no-fund", "--no-audit")
}

val bundleWebClient by tasks.registering(Exec::class) {
    dependsOn(installWebClient)
    onlyIf { npmAvailable }
    inputs.dir(webClientDir.dir("src"))
    inputs.files(
        webClientDir.file("index.html"),
        webClientDir.file("package.json"),
        webClientDir.file("package-lock.json"),
        webClientDir.file("vite.config.ts"),
        webClientDir.file("tsconfig.app.json"),
    )
    outputs.dir(webClientOutput)
    workingDir(webClientDir)
    environment("VITE_BASE", "/")
    commandLine(
        "npm", "run", "build", "--",
        "--outDir", webClientOutput.get().dir("web").asFile.absolutePath,
        "--emptyOutDir",
    )
}

android.sourceSets.getByName("main").assets.srcDir(webClientOutput)
tasks.named("preBuild") { dependsOn(bundleWebClient) }

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
        freeCompilerArgs.addAll(
            "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api",
            "-opt-in=androidx.compose.material3.ExperimentalMaterial3ExpressiveApi",
        )
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material3)
    implementation(libs.compose.material.icons)
    implementation(libs.compose.ui.tooling.preview)
    screenshotTestImplementation(libs.compose.ui.tooling)
    screenshotTestImplementation(libs.screenshot.validation.api)
    implementation(libs.coroutines.android)
    implementation(libs.nanohttpd)
    implementation(libs.zxing.core)

    testImplementation(libs.junit)
    testImplementation(libs.org.json)
}
