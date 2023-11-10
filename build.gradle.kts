import java.io.FileInputStream
import java.util.Properties

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.2.0-beta06" apply false
    id("org.jetbrains.kotlin.android") version "1.9.10" apply false
    id("io.github.gradle-nexus.publish-plugin") version "1.3.0"
}

ext["ossrhUsername"] = ""
ext["ossrhPassword"] = ""
ext["sonatypeStagingProfileId"] = ""
ext["signing.key"] = ""
ext["signing.password"] = ""

val secretPropsFile: File? = project.rootProject.file("local.properties")
if (secretPropsFile?.exists() == true) {
    // Read local.properties file first if it exists
    val p = Properties()

    FileInputStream(secretPropsFile).use { p.load(it) }
    p.forEach { name, value ->
        println("property $name: $value")
        ext[name.toString()] = value
    }
} else {
    ext["signing.keyId"] = System.getenv("SIGNING_KEY_ID")
    ext["signing.key"] = System.getenv("SIGNING_KEY")
    ext["signing.password"] = System.getenv("SIGNING_PASSWORD")
    ext["ossrhUsername"] = System.getenv("OSSRH_USERNAME")
    ext["ossrhPassword"] = System.getenv("OSSRH_PASSWORD")
    ext["sonatypeStagingProfileId"] = System.getenv("SONATYPE_STAGING_PROFILE_ID")
}

nexusPublishing {
    repositories {
        sonatype {
            nexusUrl.set(uri("https://s01.oss.sonatype.org/service/local/"))
            snapshotRepositoryUrl.set(uri("https://s01.oss.sonatype.org/content/repositories/snapshots/"))

            stagingProfileId.set(rootProject.ext["sonatypeStagingProfileId"].toString())

            username.set(rootProject.ext["ossrhUsername"].toString())
            password.set(rootProject.ext["ossrhPassword"].toString())
        }
    }
}
