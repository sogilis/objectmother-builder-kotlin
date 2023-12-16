# ObjectMother with customization in Kotlin

This repository explores many solutions in `Kotlin` to **create test data dynamically** based on [ObjectMother](http://wiki.c2.com/?ObjectMother) pattern.
In `Java`, this can be addressed with [Test Data Builders](http://wiki.c2.com/?TestDataBuilder), but implies [a lot of verbosity](https://blog.sogilis.com/posts/2019-01-11-object-mother-builder-java/).

[factory_bot](https://github.com/thoughtbot/factory_bot), written un `ruby`, was a great source of inspiration, which achieves to be very concise, readable and flexible.

## The problem to resolve

When writing tests, we many objects have to be created in `given` (given/when/then) or `arrange` (arrange/act/assert) phase.
The creation of these objects usually requires to provide much information, but some of them are not relevant depending on the test, which leads to noise.
More over, this may hide the test intent. For example, does `Rectangle(center, 20, 20)` mean we need a rectangle with 20 as width, a square or a rectangle precisely centered?

`ObjectMother` can resolve these issues (ex: `Rectangle.buildSquare()`), but we usually also need to:
* **combine factories**. Ex: rectangle which is squared and centered on the origin
* arbitrary customize created instances, which may be tricky when classes are immutables. Ex: a square with a particular side length

Here is the functional solution we try to implement in this repository:

* the ability to create a valid (default) instance of a class where provided data are fixed or generated on the fly
* … which can be (manually) customized through constructor parameters
* … which can be (manually) customized instance once created (if class is mutable)
* … which can also be customized through many registered set of customizations (on constructor parameters or on created
  instances), called traits (like in [factory_bot](https://github.com/thoughtbot/factory_bot))
* traits can be based on each others
* and all of these can be combined in following order (each one takes precedence to the previous one):
    - default instance
    - traits
    - constructor parameters
    - mutable state

Additionally, the searched solutions should imply minimal noises in tests (with sufficient ).
For example, `Rectangle.squared()` would be preferable compared to `RenctangleFactory.build(SQUARED)`.

## POC v0 - data classes

### Prerequisites

* target class is a data class
* target class must have declared a companion object (no longer necessary after Kotlin 2.0 thanks to [static extension](https://github.com/Kotlin/KEEP/blob/statics/proposals/statics.md)

### Usage

```kotlin
val rectangle = Rectangle.build(SQUARED, BIG)
    .copy(y = 10)
    .apply { emptyLabel() }
```

| feature                                     | implemented?       |
|---------------------------------------------|--------------------|
| default instance                            | ✅                  |
| customize with many traits                  | ✅                  |
| traits can customize constructor parameters | ✅ with `copy()`    |
| traits can customize mutable state          | ❌ (*)              |
| traits based on each others                 | ???                |
| manually customize constructor parameters   | ✅ with `copy()`    |
| manually customize mutable state            | ✅ with `apply()`   |
| combine all of this                         | ✅                  |

(*) This is possible, but may lead to unexpected behavior when using multiple traits or mutable state is customized.

### Implementation

```kotlin
fun build(i: Int): Rectangle {
    val width = 100
    return Rectangle(i, arrayOf(1, 2, 3).random(), width, 120, "width = $width")
}

val SQUARED: Trait<Rectangle> = { copy(height = width) }
val CENTERED: Trait<Rectangle> = { copy(x = height / 2, y = width / 2) }
val BIG: Trait<Rectangle> = { copy(width = width + 1000, height = height + 1000) }
val CENTERED_AND_SQUARED: Trait<Rectangle> = { SQUARED(CENTERED(this)) }
```

### Generic code

```kotlin
typealias Trait<T> = T.() -> T
```

And follow code should be duplicated for each target class:

```kotlin
private var i = 0
fun Rectangle.Companion.build(
    vararg traits: Trait<Rectangle> = emptyArray()
) = traits.fold(build(i++)) { rectangle, trait -> trait.invoke(rectangle) }
```

## POC v1

### Prerequisites

* target class must have declared a companion object (no longer necessary after Kotlin 2.0 thanks to [static extension](https://github.com/Kotlin/KEEP/blob/statics/proposals/statics.md)

### Usage

```kotlin
val rectangle = Rectangle.build(SQUARED, BIG, y = 10) {
    emptyLabel()
    label = "my rectangle"
}
```

| feature                                     | implemented?       |
|---------------------------------------------|--------------------|
| default instance                            | ✅                  |
| customize with many traits                  | ✅                  |
| traits can customize constructor parameters | ❌                  |
| traits can customize mutable state          | ✅ with `apply()`   |
| traits based on each others                 | ✅                  |
| manually customize constructor parameters   | ✅                  |
| manually customize mutable state            | ✅                  |
| combine all of this                         | ✅                  |

Note: implementation is a bit complex

### Implementation

```kotlin
var i = 0

fun Rectangle.Companion.build(
    vararg traits: Trait<Rectangle> = emptyArray<Trait<Rectangle>>(),
    x: Int = i++,
    y: Int = arrayOf(1, 2, 3).random(),
    width: Int = 100,
    height: Int = 100,
    label: String = "width = $width",
    override: Rectangle.() -> Unit = {}
) = traits.fold(Rectangle(x, y, width, height, label)) { rectangle, trait -> trait.invoke(rectangle) }
    .apply(override)

val SQUARED: Trait<Rectangle> = { it.apply { height = width } }
val CENTERED: Trait<Rectangle> = {
    it.apply {
        x = height / 2
        y = width / 2
    }
}
val BIG: Trait<Rectangle> = {
    it.apply {
        width = width + 1000
        height = height + 1000
    }
}
val CENTERED_AND_SQUARED: Trait<Rectangle> = { SQUARED(CENTERED(it)) }
```

### Generic code

```kotlin
typealias Trait<T> = (T) -> T
```

## POC v2

### Prerequisites

* target class is a data class
* target class must have declared a companion object (no longer necessary after Kotlin 2.0 thanks to [static extension](https://github.com/Kotlin/KEEP/blob/statics/proposals/statics.md)

### Usage

```kotlin
val rectangle = Rectangle.build(SQUARED, BIG) {
    emptyLabel()
    label = "my rectangle"
}.copy(y = 10)
```

| feature                                     | implemented?       |
|---------------------------------------------|--------------------|
| default instance                            | ✅                  |
| customize with many traits                  | ✅                  |
| traits can customize constructor parameters | ❌                  |
| traits can customize mutable state          | ✅ with `copy()`    |
| traits based on each others                 | ✅                  |
| manually customize constructor parameters   | ❌                  |
| manually customize mutable state            | ✅                  |
| combine all of this                         | ✅                  |


### Implementation

```kotlin
private fun rectangle(i: Int): Rectangle {
    val width = 100
    return Rectangle(
        x = i, // Dynamic attribute based on a sequence
        y = arrayOf(1, 2, 3).random(), // Dynamic attribute
        width = width,
        height = 100,
        label = "width = $width", // Dynamic attribute based on other attribute
    )
}

val SQUARED: Trait<Rectangle> = { it.copy(height = it.width) }
val BIG: Trait<Rectangle> = { it.copy(height = it.height + 1000, width = it.width + 1000) }
val CENTERED: Trait<Rectangle> = {
    it.copy(
        x = it.height / 2,
        y = it.width / 2
    )
}
val CENTERED_AND_SQUARED: Trait<Rectangle> = { SQUARED(CENTERED(it)) }
```

### Generic code

```kotlin
typealias Trait<T> = (T) -> T
```

And follow code should be duplicated for each target class:

```kotlin
var i = 0
fun Rectangle.Companion.build(
    vararg traits: Trait<Rectangle> = emptyArray(),
    override: Rectangle.() -> Unit = {}
): Rectangle {
    return traits.fold(rectangle(i++)) { args, trait -> trait.invoke(args) }.apply(override)
}
```

## POC v3

### Prerequisites

* target class is a data class
* target class must have declared a companion object (no longer necessary after Kotlin 2.0 thanks to [static extension](https://github.com/Kotlin/KEEP/blob/statics/proposals/statics.md)

### Usage

```kotlin
val rectangle = Rectangle.build(SQUARED, BIG) {
    y = 10
}.apply {
    emptyLabel()
    label = "my rectangle"
}
```

| feature                                     | implemented?       |
|---------------------------------------------|--------------------|
| default instance                            | ✅                  |
| customize with many traits                  | ✅                  |
| traits can customize constructor parameters | ✅                  |
| traits can customize mutable state          | ❌                  |
| traits based on each others                 | ✅                  |
| manually customize constructor parameters   | ✅                  |
| manually customize mutable state            | ✅ with `apply()`   |
| combine all of this                         | ✅                  |


### Implementation

```kotlin
class RectangleParams(i: Int) {
    var x: Int = i
    var y: Int = arrayOf(1, 2, 3).random()
    var width: Int = 100
    var height: Int = width
    var label: String? = "width = $width"

    fun build() = Rectangle(x, y, width, height, label)
}

val SQUARED: Trait<RectangleParams> = { height = width }
val CENTERED: Trait<RectangleParams> = {
    x = height / 2
    y = width / 2
}
val BIG: Trait<RectangleParams> = {
    width += 1000
    height += 1000
}
val CENTERED_AND_SQUARED: Trait<Rectangle> = { ??? }
```

### Generic code

```kotlin
typealias Trait<T> = T.() -> Unit
```

And follow code should be duplicated for each target class:

```kotlin
private var i = 0
fun Rectangle.Companion.build(
    vararg traits: Trait<RectangleParams> = emptyArray(),
    override: RectangleParams.() -> Unit = {}
) = RectangleParams(i++).apply {
    traits.forEach { apply(it) }
    apply(override)
}.build()
```

## POC v4

### Prerequisites

* target class is a data class

### Usage

```kotlin
val rectangle = create(Rectangle, SQUARED, BIG)
    .copy(y = 10)
    .apply { emptyLabel() }
```

| feature                                     | implemented?       |
|---------------------------------------------|--------------------|
| default instance                            | ✅                  |
| customize with many traits                  | ✅                  |
| traits can customize constructor parameters | ✅ with `copy()`    |
| traits can customize mutable state          | ❌ (*)              |
| traits based on each others                 | ✅                  |
| manually customize constructor parameters   | ✅ with `copy()`    |
| manually customize mutable state            | ✅ with `apply()`   |
| combine all of this                         | ✅                  |

(*) This is possible, but may lead to unexpected behavior when using multiple traits or mutable state is customized.

Advantages:
* no generic code to replicate for each target class
* no need for target class to declare a companion object

### Implementation

```kotlin
object Rectangle : Factory<Rectangle> {
  override fun one(i: Int) = Rectangle(
    x = i,
    y = arrayOf(1, 2, 3).random(),
    width = 50,
    height = 100,
    label = "width = 50",
  )

  val SQUARED: Trait<Rectangle> = { copy(width = height) }
  val BIG: Trait<Rectangle> = {
    copy(
      width = width + 1000,
      height = height + 1000
    )
  }
}
```

### Generic code

```kotlin
interface Factory<T> {
    fun one(i: Int): T
}
typealias Trait<T> = T.() -> T

var i = 0;
fun <T> create(factory: Factory<T>, vararg traits: Trait<T>) =
    traits.fold(factory.one(i++)) { model, trait -> trait.invoke(model) }
```

## Alternative approaches

* [Kotlin FactoryBot Library](https://github.com/gmkseta/k-factory-bot) : cannot handle immutable objets, trait usage is
  verified at compile time and object construction cannot be customized by traits
* [faktory-bot](https://github.com/raphiz/faktory-bot): code generation, cannot handle immutable objects and object
  construction cannot be customized by traits

## Further explorations

* https://github.com/yujinyan/faktory with its usage of KProperty in `Factory.make()`