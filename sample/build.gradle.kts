import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

buildscript {
  dependencies {
    classpath("gradle.plugin.com.bnorm.react:kotlin-react-function-gradle:0.1.0")
  }
}

plugins {
  kotlin("js") version "1.4.0"
}
apply(plugin = "com.bnorm.react.kotlin-react-function")

repositories {
  mavenCentral()
  jcenter()
  maven { setUrl("https://kotlin.bintray.com/kotlinx") }
  maven { setUrl("https://dl.bintray.com/kotlin/kotlin-js-wrappers") }
}

kotlin {
  js(IR) {
    browser {
      runTask {
        devServer = KotlinWebpackConfig.DevServer(
          port = 8081,
          proxy = mapOf("/api/v1/**" to "http://localhost:8080"),
          contentBase = listOf("$projectDir/src/main/resources")
        )
        outputFileName = "web.js"
      }
    }
    binaries.executable()
    compilations.all {
      kotlinOptions {
        kotlinOptions.freeCompilerArgs += listOf("-verbose")
        this.moduleKind
      }
    }
  }
}

dependencies {
  implementation(kotlin("stdlib-js"))

  implementation("org.jetbrains:kotlin-extensions:1.0.1-pre.112-kotlin-1.4.0")
  implementation("org.jetbrains:kotlin-react:16.13.1-pre.112-kotlin-1.4.0")
  implementation("org.jetbrains:kotlin-react-dom:16.13.1-pre.112-kotlin-1.4.0")

  implementation(npm("core-js", "3.2.0"))
  implementation(npm("react", "16.13.1"))
  implementation(npm("react-dom", "16.13.1"))
}

tasks.named<org.jetbrains.kotlin.gradle.dsl.KotlinJsCompile>("compileKotlinJs").configure {
  outputs.upToDateWhen { false }
  kotlinOptions {
    freeCompilerArgs = listOf("-verbose") + freeCompilerArgs
  }
}
