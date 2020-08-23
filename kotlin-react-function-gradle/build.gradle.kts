import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  id("java-gradle-plugin")
  kotlin("jvm")

  id("com.gradle.plugin-publish")
}

dependencies {
  implementation(kotlin("stdlib"))
  implementation(kotlin("gradle-plugin-api"))
}

pluginBundle {
  website = "https://github.com/bnorm/kotlin-react-function"
  vcsUrl = "https://github.com/bnorm/kotlin-react-function.git"
  tags = listOf("kotlin", "react")
}

gradlePlugin {
  plugins {
    create("kotlinReactFunction") {
      id = "com.bnorm.react.kotlin-react-function"
      displayName = "Kotlin React Function Plugin"
      description = "Kotlin Compiler plugin for React boilerplate"
      implementationClass = "com.bnorm.react.ReactFunctionGradlePlugin"
    }
  }
}

tasks.withType<KotlinCompile> {
  kotlinOptions.jvmTarget = "1.8"
}
