package v3

var i = 0

data class RectangleParams(
    var x: Int, var y: Int, var width: Int, var height: Int, var label: String?
)

fun Rectangle.Companion.build(
    vararg traits: Trait<RectangleParams> = emptyArray(),
    override: RectangleParams.() -> Unit = {}
): Rectangle {
    val width = 100
    val params = RectangleParams(
        x = i++, // Dynamic attribute based on a sequence
        y = arrayOf(1, 2, 3).random(), // Dynamic attribute
        width = width,
        height = 100,
        label = "width = $width", // Dynamic attribute based on other attribute
    )
    traits.forEach { params.apply(it) }
    params.apply(override)
    return Rectangle(params.x, params.y, params.width, params.height, params.label)
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

typealias Trait<T> = T.() -> Unit
