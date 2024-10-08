plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("com.google.protobuf")
    id("com.google.dagger.hilt.android")
    id("jacoco")
}

android {
    namespace = "jp.speakbuddy.edisonandroidexercise"
    compileSdk = 34

    defaultConfig {
        applicationId = "jp.speakbuddy.edisonandroidexercise"
        minSdk = 24
        targetSdk = 34
        versionCode = 2
        versionName = "2.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        debug {
            enableUnitTestCoverage = true
        }
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
    implementation(platform("androidx.compose:compose-bom:2023.08.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.08.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:1.9.0")


    // Hilt
    implementation("com.google.dagger:hilt-android:2.51.1")
    kapt("com.google.dagger:hilt-android-compiler:2.51.1")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // Room
    val roomVersion = "2.6.1"
    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    kapt("androidx.room:room-compiler:$roomVersion")
    // room paging
    implementation("androidx.room:room-paging:$roomVersion")

    // collectAsLazyPagingItems
    implementation("androidx.paging:paging-compose:3.3.2")

    // async image
    implementation("io.coil-kt:coil-compose:2.4.0")

    // JUnit 5
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.3")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.3")

    // MockK
    testImplementation("io.mockk:mockk:1.13.5")

    // Kotlin Coroutines Test
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.1")

    // Turbine for testing Flows
    testImplementation("app.cash.turbine:turbine:1.0.0")

    testImplementation("org.jacoco:org.jacoco.core:0.8.11")

}

tasks.withType<Test> {
    useJUnitPlatform()
    finalizedBy(tasks.named("jacocoTestReport"))
}

// Setup protobuf configuration, generating lite Java and Kotlin classes
protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:3.21.7"
    }
    generateProtoTasks {
        all().forEach { task ->
            task.builtins {
                create("java") {
                    option("lite")
                }
                create("kotlin") {
                    option("lite")
                }
            }
        }
    }
}

kapt {
    correctErrorTypes = true
}

jacoco {
    toolVersion = "0.8.7"
}

tasks.register<JacocoReport>("jacocoTestReport") {
    dependsOn(tasks.withType<Test>())
    reports {
        xml.required.set(true)
        html.required.set(true)
    }

    classDirectories.setFrom(
        fileTree("${buildDir}/tmp/kotlin-classes/debug") {
            exclude(
                "**/R.class",
                "**/R$*.class",
                "**/BuildConfig.*",
                "**/Manifest*.*",
                "**/*Test*.*",
                "android/**/*.*"
            )
        }
    )

    sourceDirectories.setFrom("${project.projectDir}/src/main/java")
    executionData.setFrom(file("${buildDir}/jacoco/testDebugUnitTest.exec"))
}

tasks.register("jacocoTestReportForFactViewModel") {
    dependsOn("jacocoTestReport")
    doLast {
        val reportFile = file("${buildDir}/reports/jacoco/jacocoTestReport/html/index.html")
        if (reportFile.exists()) {
            val content = reportFile.readText()
            val regex = """jp/speakbuddy/edisonandroidexercise/presentation/fact/FactViewModel.*?<td class="ctr2">(\d+)%""".toRegex(RegexOption.DOT_MATCHES_ALL)
            val matchResult = regex.find(content)
            val coverage = matchResult?.groupValues?.get(1)?.toIntOrNull()
            if (coverage != null) {
                println("Coverage for FactViewModel: $coverage%")
                if (coverage < 80) {
                    throw GradleException("Coverage for FactViewModel is below 80%. Actual: $coverage%")
                }
            } else {
                println("Could not find coverage for FactViewModel")
            }
        } else {
            println("JaCoCo report file not found")
        }
    }
}