plugins {
  kotlin("js") version "1.5.0"
  kotlin("plugin.serialization") version "1.5.0"
  id("com.bnorm.react.kotlin-react-function") version "0.4.0"
}

repositories {
  mavenCentral()
  maven("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/kotlin-js-wrappers")
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

  implementation("com.bnorm.react:kotlin-react-function:0.3.0")
  implementation("org.jetbrains:kotlin-react-dom:17.0.2-pre.154-kotlin-1.5.0")
  implementation("org.jetbrains:kotlin-styled:5.2.3-pre.154-kotlin-1.5.0")

  implementation(npm("@reach/accordion", "^0.13.0"))
  implementation(npm("prop-types", "^15.6.2")) // Not sure why this is needed...
}
