plugins {
  kotlin("js") version "1.4.0"
  kotlin("plugin.serialization") version "1.4.0"
  id("com.bnorm.react.kotlin-react-function") version "0.2.0"
}

repositories {
  mavenCentral()
  jcenter()
  maven { setUrl("https://kotlin.bintray.com/kotlinx") }
  maven { setUrl("https://dl.bintray.com/kotlin/kotlin-js-wrappers") }
}

kotlin {
  js(IR) {
    browser { }
    binaries.executable()
  }
}

dependencies {
  implementation(kotlin("stdlib-js"))
  implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.0.0-RC")
  implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.1.0")

  implementation("com.bnorm.react:kotlin-react-function:0.2.0")
  implementation("org.jetbrains:kotlin-react-dom:16.13.1-pre.112-kotlin-1.4.0")
  implementation("org.jetbrains:kotlin-styled:1.0.0-pre.112-kotlin-1.4.0")

  implementation(npm("@reach/accordion", "^0.8.0"))
}
