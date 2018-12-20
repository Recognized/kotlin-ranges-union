# kotlin-ranges-union
Kotlin ranges union library

## Overview

This library provides IntRangeUnion and LongRangeUnion classes, which represent a mutable set of closed ranges of corresponding type.

Example:
```kotlin
val set = IntRangeUnion()
set.union(0..10)    \\ {[0, 10]}
set.union(20..30)   \\ {[0, 30]}
set.exclude(15..17) \\ {[0, 14], [18, 30]}
```

## Install

Using gradle:
```groovy

repositories {
    maven { url "https://jitpack.io" }
}

dependencies {
    implementation "com.github.Recognized:kotlin-ranges-union:v1.01"
}

```
