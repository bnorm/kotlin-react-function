plugins {
  kotlin("jvm") version "1.5.0" apply false
  kotlin("js") version "1.5.0" apply false
  id("org.jetbrains.dokka") version "0.10.0" apply false
  id("com.gradle.plugin-publish") version "0.11.0" apply false
  id("com.github.gmazzo.buildconfig") version "2.0.2" apply false
}

allprojects {
  group = "com.bnorm.react"
  version = "0.5.0"
}

subprojects {
  repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/kotlin-js-wrappers")
  }
}
