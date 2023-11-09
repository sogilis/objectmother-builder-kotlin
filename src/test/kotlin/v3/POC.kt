package v3

class RectangleParams(i: Int) {
    var x: Int = i
    var y: Int = arrayOf(1, 2, 3).random()
    var width: Int = 100
    var height: Int = width
    var label: String? = "width = $width"

    fun build() = Rectangle(x, y, width, height, label)
}

enum class TRAIT(private val override: RectangleParams.() -> Unit) {
    SQUARED({ height = width }),
    CENTERED({
        x = height / 2
        y = width / 2
    }),
    BIG({
        width += 1000
        height += 1000
    });
    fun applyTo(params: RectangleParams) = params.apply(override)
}

// Code below is generic

private var i = 0

fun Rectangle.Companion.build(
    vararg traits: TRAIT = emptyArray(),
    override: RectangleParams.() -> Unit = {}
) = RectangleParams(i++).apply {
    traits.forEach { it.applyTo(this) }
    apply(override)
}.build()
