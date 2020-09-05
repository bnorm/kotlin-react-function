plugins {
  kotlin("jvm") version "1.4.0" apply false
  kotlin("js") version "1.4.0" apply false
  id("org.jetbrains.dokka") version "0.10.0" apply false
  id("com.gradle.plugin-publish") version "0.11.0" apply false
}

allprojects {
  group = "com.bnorm.react"
  version = "0.3.0-SNAPSHOT"
}

subprojects {
  repositories {
    mavenCentral()
    jcenter()
  }
}
