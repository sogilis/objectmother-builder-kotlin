package v1

import Rectangle

var i = 0

fun Rectangle.Companion.build(
    vararg traits: Trait<Rectangle> = emptyArray<Trait<Rectangle>>(),
    x: Int = i++, // Dynamic attribute based on a sequence
    y: Int = arrayOf(1, 2, 3).random(), // Dynamic attribute
    width: Int = 100,
    height: Int = 100,
    label: String = "width = $width", // Dynamic attribute based on other attribute
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
// TODO What about traits which affect constructor?

typealias Trait<T> = (T) -> T
