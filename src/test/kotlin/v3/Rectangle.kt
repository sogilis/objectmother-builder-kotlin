package v3

data class Rectangle(val x: Int, val y: Int, val width: Int, val height: Int, var label: String?) {
    var filled= false
    fun fill() { filled = true}
    fun emptyLabel() { label = null}

    // This is necessary in order to complete it in test data builder
    companion object
}