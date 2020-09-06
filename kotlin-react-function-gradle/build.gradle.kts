import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  id("java-gradle-plugin")
  kotlin("jvm")
  id("com.gradle.plugin-publish")
  id("com.github.gmazzo.buildconfig")
}

dependencies {
  implementation(kotlin("stdlib"))
  implementation(kotlin("gradle-plugin-api"))
}

buildConfig {
  val project = project(":kotlin-react-function-plugin")
  packageName(project.group.toString())
  buildConfigField("String", "PROJECT_GROUP_ID", "\"${project.group}\"")
  buildConfigField("String", "PROJECT_ARTIFACT_ID", "\"${project.name}\"")
  buildConfigField("String", "PROJECT_VERSION", "\"${project.version}\"")
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
