plugins {
  kotlin("js") version "1.5.10"
  kotlin("plugin.serialization") version "1.5.10"
  id("com.bnorm.react.kotlin-react-function") version "0.5.1"
}

repositories {
  mavenCentral()
}

kotlin {
  js(IR) {
    browser {}
    binaries.executable()
  }
}

dependencies {
  implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.2.0")
  implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.2.0")

  implementation("com.bnorm.react:kotlin-react-function:0.5.1")
  implementation("org.jetbrains.kotlin-wrappers:kotlin-react-dom:17.0.2-pre.206-kotlin-1.5.10")
  implementation("org.jetbrains.kotlin-wrappers:kotlin-styled:5.3.0-pre.206-kotlin-1.5.10")

  implementation(npm("@reach/accordion", "^0.13.0"))
  implementation(npm("prop-types", "^15.6.2")) // Not sure why this is needed...
}
