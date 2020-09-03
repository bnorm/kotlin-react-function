import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
  kotlin("js") version "1.4.0"
  id("com.bnorm.react.kotlin-react-function") version "0.1.0"
}

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
  }
}

dependencies {
  implementation(kotlin("stdlib-js"))
  implementation("com.bnorm.react:kotlin-react-function:0.1.0")
  implementation("org.jetbrains:kotlin-react-dom:16.13.1-pre.112-kotlin-1.4.0")
}

tasks.named<org.jetbrains.kotlin.gradle.dsl.KotlinJsCompile>("compileKotlinJs").configure {
  // Always compile Kotlin/JS again because of compiler plugin
  outputs.upToDateWhen { false }
}
