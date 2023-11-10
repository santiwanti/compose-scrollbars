plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.dokka") version "1.9.10"
    id("maven-publish")
    id("signing")
}

android {
    namespace = "com.odea.compose_scrollbars"
    compileSdk = 34

    defaultConfig {
        minSdk = 21
        targetSdk = 34

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
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

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.3"
    }

    publishing {
        singleVariant("release") {
            withSourcesJar()
            withJavadocJar()
        }
    }

//    testFixtures {
//        enable = true
//    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    // Compose
    val composeBom = platform("androidx.compose:compose-bom:2023.09.02")
    implementation(composeBom)
    androidTestImplementation(composeBom)
    implementation("androidx.compose.ui:ui")
    // Choose one of the following:
    // We might omit material3 and just use foundation given that the UI is very custom
    implementation("androidx.compose.foundation:foundation")
    // Material Design 3
    implementation("androidx.compose.material3:material3")
    // or only import the main APIs for the underlying toolkit systems,
    // such as input and measurement/layout

    // Android Studio Preview support
    implementation("androidx.compose.ui:ui-tooling-preview")
    debugImplementation("androidx.compose.ui:ui-tooling")
}

//tasks.dokkaHtmlPartial.configure {
//    pluginsMapConfiguration.set(
//        mapOf("org.jetbrains.dokka.base.DokkaBase" to """{ "separateInheritedMembers": true}}""")
//    )
//}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "com.sw.compose-scrollbars"
            artifactId = "scrollbars"
            version = "0.1"

            pom {
                name = "Scrollbars"
                description = "Scrollbars for Compose LazyLists and Pagers"
                url = "https://github.com/santiwanti/compose-scrollbars"
                licenses {
                    license {
                        name = "MIT License"
                        url = "https://github.com/santiwanti/compose-scrollbars/blob/master/LICENSE"
                    }
                }
                developers {
                    developer {
                        id = "santiwanti"
                        name = "Santi De Tord"
                        email = "santi.detord@posteo.net"
                    }
                }
                scm {
                    connection = "scm:git:github.com/santiwanti/compose-scrollbars.git"
                    developerConnection =
                        "scm:git:ssh://github.com/santiwanti/compose-scrollbars.git"
                    url = "https://github.com/santiwanti/compose-scrollbars.git"
                }
            }

            afterEvaluate {
                from(components["release"])
            }
        }
    }
}

signing {
    useInMemoryPgpKeys(
        rootProject.ext["signing.keyId"].toString(),
        rootProject.ext["signing.key"].toString(),
        rootProject.ext["signing.password"].toString(),
    )

    sign(publishing.publications)
}
