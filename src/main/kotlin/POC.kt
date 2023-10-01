import FormeTraits.CENTERED
import FormeTraits.CENTERED_AND_SQUARED
import FormeTraits.SQUARED

// The class to initialize
class Rectangle(var x: Int, var y: Int, var width: Int, var height: Int, var label: String?) {
    fun fill() {}
    fun empty() {}

    companion object
}

// Test data builder
var i = 0

private fun Rectangle.Companion.build(
    vararg traits: Trait<Rectangle> = emptyArray<Trait<Rectangle>>(),
    x: Int = i++, // Dynamic attribute based on a sequence
    y: Int = arrayOf(1, 2, 3).random(), // Dynamic attribute
    width: Int = 100,
    height: Int = 100,
    label: String = "width = $width", // Dynamic attribute based on other attribute
    override: Rectangle.() -> Unit = {}
) = traits.collect(Rectangle(x, y, width, height, label)).apply(override)
object FormeTraits {
    val SQUARED: Trait<Rectangle> = { it.apply { height = width } }
    val CENTERED: Trait<Rectangle> = {
        it.apply {
            x = height / 2
            y = width / 2
        }
    }
    val CENTERED_AND_SQUARED: Trait<Rectangle> = { SQUARED(CENTERED(it)) }
    // TODO What about traits which affect constructor?

}

private fun <T, U> Array<T>.collect(forme: U): U {
    TODO("Get it work with merging T and U")
}

typealias Trait<T> = (T) -> T

// Demonstrate the usage of test data builder
fun test() {
    Rectangle.build()
    Rectangle.build(y = 10)
    Rectangle.build(SQUARED)
    Rectangle.build(SQUARED, CENTERED)
    Rectangle.build(SQUARED, CENTERED, y = 10)
    Rectangle.build { fill() }
    Rectangle.build(CENTERED_AND_SQUARED, y = 10) {
        empty()
        label = "my forme"
    }
}
