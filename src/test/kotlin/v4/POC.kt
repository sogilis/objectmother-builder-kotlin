package v4

import `dataclass-without-companion`.Rectangle
import v4.Rectangle.BIG
import v4.Rectangle.SQUARED

object Rectangle : Factory<Rectangle> {
    override fun one(i: Int) = Rectangle(
        x = i,
        y = arrayOf(1, 2, 3).random(),
        width = 50,
        height = 100,
        label = "width = 50",
    )

    val SQUARED: Trait<Rectangle> = { copy(width = height) }
    val BIG: Trait<Rectangle> = {
        copy(
            width = width + 1000,
            height = height + 1000
        )
    }
}

// Generic ------------------------------------------------
interface Factory<T> {
    fun one(i: Int): T
}
typealias Trait<T> = T.() -> T

var i = 0;
fun <T> create(factory: Factory<T>, vararg traits: Trait<T>) =
    traits.fold(factory.one(i++)) { model, trait -> trait.invoke(model) }

// Usage --------------------------------------------------
val rectangle = create(Rectangle, SQUARED, BIG).copy(y = 10).apply {
    emptyLabel()
}