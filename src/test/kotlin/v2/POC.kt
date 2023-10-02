package v2

import Rectangle

var i = 0

fun Rectangle.Companion.build(
    vararg traits: Trait<Rectangle> = emptyArray(),
    override: Rectangle.() -> Unit = {}
): Rectangle {
    val width = 100
    val rectangle = Rectangle(
        x = i++, // Dynamic attribute based on a sequence
        y = arrayOf(1, 2, 3).random(), // Dynamic attribute
        width = width,
        height = 100,
        label = "width = $width", // Dynamic attribute based on other attribute
    )
    return traits.fold(rectangle) { args, trait -> trait.invoke(args) }.apply(override)
}

val SQUARED: Trait<Rectangle> = { it.apply { height = width } }
val CENTERED: Trait<Rectangle> = {
    it.apply {
        x = height / 2
        y = width / 2
    }
}
val CENTERED_AND_SQUARED: Trait<Rectangle> = { SQUARED(CENTERED(it)) }

typealias Trait<T> = (T) -> T
