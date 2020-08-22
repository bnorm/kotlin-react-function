rootProject.name = "kotlin-react-function-sample"

includeBuild("..") {
  dependencySubstitution {
    substitute(module("com.bnorm.react:kotlin-react-function-gradle")).with(project(":kotlin-react-function-gradle"))
    substitute(module("com.bnorm.react:kotlin-react-function-plugin")).with(project(":kotlin-react-function-plugin"))
    substitute(module("gradle.plugin.com.bnorm.react:kotlin-react-function-gradle")).with(project(":kotlin-react-function-gradle"))
  }
}
