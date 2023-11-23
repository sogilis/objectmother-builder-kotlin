package dataclass

import org.jetbrains.annotations.NotNull
@Repeatable
annotation class A(val value: Int)

@A(0)
@A(1)
@A(42)
data class Rectangle(val x: Int, val y: Int, val width: Int, val height: Int, var label: String?) {
    var filled= false
    fun fill() { filled = true}
    fun emptyLabel() { label = null}

    // This is necessary in order to complete it in test data builder
    companion object
}


