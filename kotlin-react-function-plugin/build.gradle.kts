import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlin.gradle.targets.js.KotlinJsCompilerAttribute

plugins {
  kotlin("jvm")
  kotlin("kapt")
  id("org.jetbrains.dokka")

  signing
  `maven-publish`
}

val jsCompileTest by configurations.creating {
  attributes {
    attribute(KotlinJsCompilerAttribute.jsCompilerAttribute, KotlinJsCompilerAttribute.ir)
    attribute(Usage.USAGE_ATTRIBUTE, project.objects.named(Usage::class.java, "kotlin-runtime"))
  }
}

dependencies {
  compileOnly("org.jetbrains.kotlin:kotlin-compiler-embeddable")

  kapt("com.google.auto.service:auto-service:1.0")
  compileOnly("com.google.auto.service:auto-service-annotations:1.0")

  testImplementation(kotlin("test-junit"))
  testImplementation("org.jetbrains.kotlin:kotlin-compiler-embeddable")
  testImplementation("com.github.tschuchortdev:kotlin-compile-testing:1.4.5")

  jsCompileTest(kotlin("stdlib-js"))
  jsCompileTest(project(":kotlin-react-function"))
  jsCompileTest(enforcedPlatform("org.jetbrains.kotlin-wrappers:kotlin-wrappers-bom:0.0.1-pre.286-kotlin-1.6.10"))
  jsCompileTest("org.jetbrains.kotlin-wrappers:kotlin-react-dom-legacy")
}

// Download and relocate the Kotlin/JS dependencies for use by unit tests
val jsCompileTestDownload by tasks.registering(Sync::class) {
  from(jsCompileTest)
  into("$buildDir/jsJars")
}
tasks.test.configure {
  dependsOn(jsCompileTestDownload)
  testLogging {
    exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
  }
}

tasks.withType<KotlinCompile> {
  kotlinOptions.jvmTarget = "1.8"
}

tasks.dokka {
  outputFormat = "html"
  outputDirectory = "$buildDir/javadoc"
}

tasks.register("sourcesJar", Jar::class) {
  group = "build"
  description = "Assembles Kotlin sources"

  archiveClassifier.set("sources")
  from(sourceSets.main.get().allSource)
  dependsOn(tasks.classes)
}

tasks.register("dokkaJar", Jar::class) {
  group = "documentation"
  description = "Assembles Kotlin docs with Dokka"

  archiveClassifier.set("javadoc")
  from(tasks.dokka)
  dependsOn(tasks.dokka)
}

signing {
  setRequired(provider { gradle.taskGraph.hasTask("publish") })
  sign(publishing.publications)
}

publishing {
  publications {
    create<MavenPublication>("default") {
      from(components["java"])
      artifact(tasks["sourcesJar"])
      artifact(tasks["dokkaJar"])

      pom {
        name.set(project.name)
        description.set("Kotlin Compiler plugin for React boilerplate")
        url.set("https://github.com/bnorm/kotlin-react-function")

        licenses {
          license {
            name.set("Apache License 2.0")
            url.set("https://github.com/bnorm/kotlin-react-function/blob/main/LICENSE.txt")
          }
        }
        scm {
          url.set("https://github.com/bnorm/kotlin-react-function")
          connection.set("scm:git:git://github.com/bnorm/kotlin-react-function.git")
        }
        developers {
          developer {
            name.set("Brian Norman")
            url.set("https://github.com/bnorm")
          }
        }
      }
    }
  }

  repositories {
    if (
      hasProperty("sonatypeUsername") &&
      hasProperty("sonatypePassword") &&
      hasProperty("sonatypeSnapshotUrl") &&
      hasProperty("sonatypeReleaseUrl")
    ) {
      maven {
        val url = when {
          "SNAPSHOT" in version.toString() -> property("sonatypeSnapshotUrl")
          else -> property("sonatypeReleaseUrl")
        } as String
        setUrl(url)
        credentials {
          username = property("sonatypeUsername") as String
          password = property("sonatypePassword") as String
        }
      }
    }
    maven {
      name = "test"
      setUrl("file://${rootProject.buildDir}/localMaven")
    }
  }
}
