# ObjectMother with customization in Kotlin

This POC is an experimentation in `Kotlin` to implement [Test Data Builders](http://wiki.c2.com/?TestDataBuilder) with
the minimum of verbosity. We try to see here if `Kotlin` allows to minimize one major drawback
of [Test Data Builders in `Java`](https://blog.sogilis.com/posts/2019-01-11-object-mother-builder-java/): verbosity.

The solution explored here is heavily inspired by [factory_bot](https://github.com/thoughtbot/factory_bot), a ruby
library which achieve to be very concise, readable and flexible.

## The problem

When writing tests, we often need to setup some data through instantiations, builders or factories (given in
given/when/then or arrange in arrange/act/assert).
This usually requires to provide much information in order to get valid data, but all this information are not
necessarily relevant depending on the test, which leads to noise.
More over, such information may hide the test intent. For example, does `Rectangle(center, 20, 20)` mean we need a
rectangle with 20 as width, a square or a rectangle precisely centered?

`ObjectMother` can resolve these issues, but we also need to be able to arbitrary customize created instances, which may
be tricky when classes are immutables.

Here is the functional solution we try to implement in this repository with Kotlin:

* the ability to create a valid (default) instance of a class where provided data are fixed or generated on the fly
* … which can be (manually) customized through constructor parameters
* … which can be (manually) customized instance once created (if class is mutable)
* … which can also be customized through many registered set of customizations (on constructor parameters or on created
  instances), called traits
* traits can be based on each others
* and all of these can be combined in following order:
    - default instance
    - traits
    - constructor parameters
    - mutable state

Additionally, the searched solution should ideally not imply any noise in tests.
For example, if we can to create a squared rectangle, `Rectangle.squared()` would be ideal, unlike `RenctangleFactory.build(SQUARED)`.

## POC v0 - data classes

### Prerequisites

* target class is a data class
* companion object declared in target class (no more necessary with Kotlin 2?)

### Usage

```
val rectangle = Rectangle.build(SQUARED, BIG)
    .copy(y = 10)
    .apply { emptyLabel() }
```

| feature                                     | is it implemented? |
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

```
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

```
typealias Trait<T> = T.() -> T
```

And follow code should be duplicated for each target class:

```
private var i = 0
fun Rectangle.Companion.build(
    vararg traits: Trait<Rectangle> = emptyArray()
) = traits.fold(build(i++)) { rectangle, trait -> trait.invoke(rectangle) }
```

## POC v1

### Prerequisites

* companion object declared in target class (no more necessary with Kotlin 2?)

### Usage

```
val rectangle = Rectangle.build(SQUARED, BIG, y = 10) {
    emptyLabel()
    label = "my rectangle"
}
```

| feature                                     | is it implemented? |
|---------------------------------------------|--------------------|
| default instance                            | ✅                  |
| customize with many traits                  | ✅                  |
| traits can customize constructor parameters | ❌                  |
| traits can customize mutable state          | ✅ with `apply()`   |
| traits based on each others                 | ✅                |
| manually customize constructor parameters   | ✅                  |
| manually customize mutable state            | ✅                  |
| combine all of this                         | ✅                  |

Note: implementation is a bit complex

### Implementation

```
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

```
typealias Trait<T> = (T) -> T
```

## POC v2

### Prerequisites

* target class is a data class
* companion object declared in target class (no more necessary with Kotlin 2?)

### Usage

```
val rectangle = Rectangle.build(SQUARED, BIG) {
    emptyLabel()
    label = "my rectangle"
}.copy(y = 10)
```

| feature                                     | is it implemented? |
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

```
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

```
typealias Trait<T> = (T) -> T
```

And follow code should be duplicated for each target class:

```
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
* companion object declared in target class (no more necessary with Kotlin 2?)

### Usage

```
val rectangle = Rectangle.build(SQUARED, BIG) {
    y = 10
}.apply {
    emptyLabel()
    label = "my rectangle"
}
```

| feature                                     | is it implemented? |
|---------------------------------------------|--------------------|
| default instance                            | ✅                  |
| customize with many traits                  | ✅                  |
| traits can customize constructor parameters | ✅                  |
| traits can customize mutable state          | ❌                  |
| traits based on each others                 | ???                |
| manually customize constructor parameters   | ✅                  |
| manually customize mutable state            | ✅ with `apply()`   |
| combine all of this                         | ✅                  |


### Implementation

```
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

```
typealias Trait<T> = T.() -> Unit
```

And follow code should be duplicated for each target class:

```
private var i = 0
fun Rectangle.Companion.build(
    vararg traits: Trait<RectangleParams> = emptyArray(),
    override: RectangleParams.() -> Unit = {}
) = RectangleParams(i++).apply {
    traits.forEach { apply(it) }
    apply(override)
}.build()
```

## Other approaches

* [Kotlin FactoryBot Library](https://github.com/gmkseta/k-factory-bot) : cannot handle immutable objets, trait usage is
  verified at compile time and object construction cannot be customized by traits
* [faktory-bot](https://github.com/raphiz/faktory-bot): code generation, cannot handle immutable objects and object
  construction cannot be customized by traits

## Further explorations

* Is it possible with Kotlin 2, to extend any companion ? If yes, it may reduce the amount of generic code to write with many solutions. 