import FormeTraits.CENTERED
import FormeTraits.SQUARED

// The class used in test
class Forme(var x: Int, var y: Int, var width: Int, var height: Int) {
    fun fill() {}
    fun empty() {}

    companion object
}

// One test
fun test() {
    Forme.build()
    Forme.build(y = 10)
    Forme.build(SQUARED)
    Forme.build(SQUARED, CENTERED)
    Forme.build(SQUARED, CENTERED, y = 10)
    Forme.build().apply { fill() }
    Forme.build(SQUARED, CENTERED, y = 10).apply { fill() }
}

// Factory_bot like builder
var i = 0
private fun Forme.Companion.build(
    vararg traits: Trait<Forme> = emptyArray<Trait<Forme>>(),
    x: Int = i++, // Dynamic attribute based on a sequence
    y: Int = arrayOf(1, 2, 3).random(), // Dynamic attribute
    width: Int = 100,
    height: Int = 100,
    label: String = "width = $width", // Dynamic attribute based on other attribute
) = traits.collect(Forme(x, y, width, height))

object FormeTraits {
    val SQUARED: Trait<Forme> = { it.apply { height = width } }
    val CENTERED: Trait<Forme> = {
        it.apply {
            x = height / 2
            y = width / 2
        }
    }
    val CENTERED_AND_SQUARED: Trait<Forme> = { SQUARED(CENTERED(it)) }

    // TODO What about traits which affect constructor?
}

private fun <T, U> Array<T>.collect(forme: U): U {
    TODO("Get it work with merging T and U")
}
typealias Trait<T> = (T) -> T
