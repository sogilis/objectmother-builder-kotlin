package v3

var i = 0

data class RectangleParams(
    var x: Int = i,
    var y: Int = arrayOf(1, 2, 3).random(),
    var width: Int = 100,
    var height: Int = width,
    var label: String? = "width = $width"
)

fun Rectangle.Companion.build(
    vararg traits: Trait<RectangleParams> = emptyArray(),
    override: RectangleParams.() -> Unit = {}
): Rectangle {
    i++
    val params = RectangleParams()
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
