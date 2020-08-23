plugins {
  kotlin("js")
  id("org.jetbrains.dokka")

  signing
  `maven-publish`
}

kotlin {
  js(IR) {
    moduleName = project.name
    browser()
  }
}

tasks.dokka {
  outputFormat = "html"
  outputDirectory = "$buildDir/javadoc"
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
      from(components["kotlin"])
      artifact(tasks["kotlinSourcesJar"])
      artifact(tasks["dokkaJar"])

      pom {
        name.set(project.name)
        description.set("Kotlin Compiler plugin for React boilerplate")
        url.set("https://github.com/bnorm/kotlin-react-function")

        licenses {
          license {
            name.set("Apache License 2.0")
            url.set("https://github.com/bnorm/kotlin-react-function/blob/master/LICENSE.txt")
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
