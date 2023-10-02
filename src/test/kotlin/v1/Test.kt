package v1

import Rectangle
import ch.tutteli.atrium.api.infix.en_GB.toEqual
import ch.tutteli.atrium.api.verbs.expect
import kotlin.test.Test


class Test {

    @Test
    fun simple() = expect(Rectangle.build().width) toEqual 100

    @Test
    fun `override property`() = expect(Rectangle.build(y = 10).y) toEqual 10

    @Test
    fun trait() {
        val rectangle = Rectangle.build(SQUARED)
        expect(rectangle.width) toEqual rectangle.height
    }

    @Test
    fun `multiple traits`() {
        val rectangle = Rectangle.build(SQUARED, CENTERED)
        expect(rectangle.width) toEqual rectangle.height
        expect(rectangle.x) toEqual (rectangle.height / 2)
    }

    //FIXME: This test fail: y=10 is applied BEFORE traits
    @Test
    fun `override property with multiple traits`() {
        val rectangle = Rectangle.build(SQUARED, CENTERED, y = 10)
        expect(rectangle.width) toEqual rectangle.height
        expect(rectangle.x) toEqual (rectangle.height / 2)
        expect(rectangle.y) toEqual 10
    }

    @Test
    fun `arbitrary customization`() {
        val rectangle = Rectangle.build { fill() }
        expect(rectangle.width) toEqual rectangle.height
        expect(rectangle.filled) toEqual true
    }

    @Test
    fun `override property with traits and arbitrary customization`() {
        val rectangle = Rectangle.build(CENTERED_AND_SQUARED, y = 10) {
            emptyLabel()
            label = "my rectangle"
        }
        expect(rectangle.width) toEqual rectangle.height
        expect(rectangle.x) toEqual (rectangle.height / 2)
        expect(rectangle.y) toEqual 10
        expect(rectangle.label) toEqual "my rectangle"
    }
}
