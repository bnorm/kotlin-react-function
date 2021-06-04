# kotlin-react-function

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.bnorm.react/kotlin-react-function/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.bnorm.react/kotlin-react-function)

Writing functional components with Kotlin/JS for React is great but requires a
bit of boilerplate which makes each new component require some setup. Most of
this boilerplate is extremely simple, perfect for automation.

## Introduction

Consider the following Kotlin/JS DOM builder function:

```kotlin
fun RBuilder.Hello(name: String) {
  +"Hello, $name"
}
```

This function adds text to the DOM but does not support any React hooks because
it is not currently a React functional component. To do so requires a fair
amount of boilerplate:

```kotlin
private external interface HelloProps : RProps {
  var name: String
}

private val HELLO_COMPONENT = functionalComponent<HelloProps>("Hello") { props ->
  +"Hello, ${props.name}"
}

fun RBuilder.Hello(name: String) {
  child(HELLO_COMPONENT) {
    attrs.name = name
  }
}
```

With the Kotlin compiler plugin provided by this library, making the original
function a React component is as simple as adding an annotation! (and Gradle
dependency stuff)

```kotlin
@RFunction
fun RBuilder.Hello(name: String) {
  +"Hello, $name"
}
```

The compiler plugin automatically generates the RProps interface and the
functional component property. It then rewrites the original function to add the
component as a child and automatically assign all the component property
attributes. Basically doing all the boilerplate for you automatically! Magic!

## Work In Progress

This library is a work in progress and requires using Kotlin/JS IR backend for
compilation and often requires a specific version of Kotlin as well. The
Kotlin/JS IR backend is currently in preview and is not recommended for
production. Use at your own risk!

| Kotlin Version | Plugin Version |
| -------------- | -------------- |
| 1.4.10         | 0.2.1          |
| 1.4.20         | 0.3.0          |
| 1.4.30         | 0.4.0          |
| 1.5.0          | 0.5.0          |
| 1.5.10         | 0.5.1          |

See the [sample][sample] directory for a working project using this compiler
plugin which is also
[available live](https://bnorm.github.io/kotlin-react-function/).

## Gradle Plugin

Builds of the Gradle plugin are available through the
[Gradle Plugin Portal][kotlin-react-function-gradle].

```kotlin
plugins {
  kotlin("jvm") version "1.5.10"
  id("com.bnorm.react.kotlin-react-function") version "0.5.1"
}
```

Annotations are available via Maven Central:

```kotlin
implementation("com.bnorm.react:kotlin-react-function:0.5.1")
```

Make sure Kotlin/JS is configured to compile using IR!

```kotlin
kotlin {
  js(IR) {
    // ...
  }
}
```

## Advanced Topics

### Component Key

To set the key of a React functional component, use the `@RKey` annotation on a
single parameter to a `@RFunction` annotated function.

```kotlin
@RFunction
fun RBuilder.ListItem(@RKey item: Item) {
  ...
}
```

This uses the `toString()` value of the annotated parameter as the key for the
component. If the value of the key needs to be controlled more explicitly, mark 
an unused parameter as the key.

```kotlin
@RFunction
fun RBuilder.Component(... other parameters ..., @RKey key: String) {
  // `key` doesn't need to be used to be set as the key of the component
}
```

If the `@RKey` annotated parameter is `null`, then the string `"null"` will be
set as the component key. It is also possible to use default parameters to
derive the key from another parameter.

[sample]: https://github.com/bnorm/kotlin-react-function/blob/main/sample
[kotlin-react-function-gradle]: https://plugins.gradle.org/plugin/com.bnorm.react.kotlin-react-function
