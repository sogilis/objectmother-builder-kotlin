package v2

import Rectangle

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

val SQUARED: Trait<Rectangle> = { it.apply { height = width } }

val CENTERED: Trait<Rectangle> = {
    it.copy (
        x = it.height / 2,
        y = it.width / 2
    )
}
val CENTERED_AND_SQUARED: Trait<Rectangle> = { SQUARED(CENTERED(it)) }

// Code below is generic

var i = 0

fun Rectangle.Companion.build(
    vararg traits: Trait<Rectangle> = emptyArray(),
    override: Rectangle.() -> Unit = {}
): Rectangle {
    return traits.fold(rectangle(i++)) { args, trait -> trait.invoke(args) }.apply(override)
}

typealias Trait<T> = (T) -> T
