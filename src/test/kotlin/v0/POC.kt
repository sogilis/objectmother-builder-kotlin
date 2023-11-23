package v0

import dataclass.Rectangle

// Factory
fun build(i: Int): Rectangle {
    val width = 100
    return Rectangle(i, arrayOf(1, 2, 3).random(), width, 120, "width = $width")
}

val SQUARED: Trait<Rectangle> = { copy(height = width) }
val CENTERED: Trait<Rectangle> = { copy(x = height / 2, y = width / 2) }
val BIG: Trait<Rectangle> = { copy(width = width + 1000, height = height + 1000) }
val CENTERED_AND_SQUARED: Trait<Rectangle> = { SQUARED(CENTERED(this)) }

// Code below is generic
typealias Trait<T> = T.() -> T

private var i = 0
fun Rectangle.Companion.build(
    vararg traits: Trait<Rectangle> = emptyArray()
) = traits.fold(build(i++)) { rectangle, trait -> trait.invoke(rectangle) }

// Usage example
val rectangle = Rectangle.build(SQUARED, BIG)
    .copy(y = 10)
    .apply { emptyLabel() }