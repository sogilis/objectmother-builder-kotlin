package v3

import dataclass.Rectangle

class RectangleParams(i: Int) {
    var x: Int = i
    var y: Int = arrayOf(1, 2, 3).random()
    var width: Int = 100
    var height: Int = width
    var label: String? = "width = $width"

    fun build() = Rectangle(x, y, width, height, label)
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
// FIXME
val CENTERED_AND_SQUARED: Trait<RectangleParams> = {
    apply(SQUARED)
    apply(SQUARED)
}

// Code below is generic

typealias Trait<T> = T.() -> Unit

private var i = 0

fun Rectangle.Companion.build(
    vararg traits: Trait<RectangleParams> = emptyArray(),
    override: RectangleParams.() -> Unit = {}
) = RectangleParams(i++).apply {
    traits.forEach { apply(it) }
    apply(override)
}.build()
