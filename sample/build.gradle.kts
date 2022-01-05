plugins {
  kotlin("js") version "1.6.10"
  kotlin("plugin.serialization") version "1.6.10"
  id("com.bnorm.react.kotlin-react-function") version "0.6.0"
}

repositories {
  mavenCentral()
}

kotlin {
  js(IR) {
    browser()
    binaries.executable()
  }
}

dependencies {
  implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.0")
  implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.2.1")

  implementation(enforcedPlatform("org.jetbrains.kotlin-wrappers:kotlin-wrappers-bom:0.0.1-pre.286-kotlin-1.6.10"))
  implementation("org.jetbrains.kotlin-wrappers:kotlin-react-dom")
  implementation("org.jetbrains.kotlin-wrappers:kotlin-styled")

  implementation(npm("@reach/accordion", "^0.13.0"))
  implementation(npm("prop-types", "^15.6.2")) // Not sure why this is needed...
}
