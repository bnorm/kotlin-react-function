rootProject.name = "kotlin-react-function-sample"

// To test this sample project against changes to the compiler plugin, uncomment the following line
includeBuild("..") {
  dependencySubstitution {
    substitute(module("com.bnorm.react:kotlin-react-function")).with(project(":kotlin-react-function"))
    substitute(module("com.bnorm.react:kotlin-react-function-gradle")).with(project(":kotlin-react-function-gradle"))
    substitute(module("com.bnorm.react:kotlin-react-function-plugin")).with(project(":kotlin-react-function-plugin"))
  }
}
