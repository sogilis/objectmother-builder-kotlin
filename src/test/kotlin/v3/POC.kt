package v3

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

val SQUARED: Trait<Rectangle> = { it.copy(height = it.width) }
val CENTERED: Trait<Rectangle> = { it.copy(x = it.height / 2, y = it.width / 2) }
val BIG: Trait<Rectangle> = { it.copy(width = 1000 + it.width, height = 1000 + it.height) }

typealias Trait<T> = (T) -> T
