# kotlin-react-function

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.bnorm.react/kotlin-react-function/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.bnorm.react/kotlin-react-function)

Writing functional components with Kotlin/JS for React is great but requires a
bit of boilerplate which makes each new component require some setup. Most of
this boilerplate is extremely simple, perfect for automation.

Consider the following example React functional component written with
Kotlin/JS:

```kotlin
private external interface HelloProps : RProps {
  var name: String
}

private val HELLO_COMPONENT = rFunction<HelloProps> { props ->
  +"Hello, ${props.name}"
}

fun RBuilder.Hello(name: String) {
  HELLO_COMPONENT.invoke {
    attrs.name = name
  }
}
```

Now consider the following, free of boilerplate:

```kotlin
fun RBuilder.Hello(name: String) {
  +"Hello, $name"
}
```

This is really nice and clean! Because it is not (currently) a React component,
it cannot take advantage of React hooks. With the Kotlin compiler plugin
provided by this library, making this function a React component is as simple as
adding an annotation! (and Gradle dependency stuff)


```kotlin
@RFunction
fun RBuilder.Hello(name: String) {
  +"Hello, $name"
}
```

The compiler plugin automatically generates the RProps interface and functional
component RClass. It then rewrites the original function to invoke the
component, automatically assigning all the component attributes. Magic!

## Work In Progress

This library is a work in progress and requires using Kotlin/JS IR backend for
compilation. This compiler feature is currently in preview and is not ready for
production. Use at your own risk!

See the `sample` directory for a working example project using this compiler
plugin!

## Gradle Plugin

__*COMING SOON!*__

Builds of the Gradle plugin are available through the
[Gradle Plugin Portal][kotlin-react-function-gradle].

```kotlin
plugins {
  kotlin("jvm") version "1.4.0"
  id("com.bnorm.power.kotlin-react-function") version "0.1.0"
}
```

Annotation is available via Maven Central:

```kotlin
implementation("com.bnorm.react:kotlin-react-function:0.1.0")
```

Make sure Kotlin/JS is configured to compile using IR!

```kotlin
kotlin {
  js(IR) {
    // ...
  }
}
```

[kotlin-react-function-gradle]: https://plugins.gradle.org/plugin/com.bnorm.react.kotlin-react-function
