import FormeTraits.CENTERED
import FormeTraits.CENTERED_AND_SQUARED
import FormeTraits.SQUARED

// The class used in test
class Forme(var x: Int, var y: Int, var width: Int, var height: Int, var label: String?) {
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
    Forme.build { fill() }
    Forme.build(CENTERED_AND_SQUARED, y = 10) {
        empty()
        label = "my forme"
    }
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
    override: Forme.() -> Unit = {}
) = traits.collect(Forme(x, y, width, height, label)).apply(override)

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
