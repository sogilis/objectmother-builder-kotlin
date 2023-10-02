data class  Rectangle(var x: Int, var y: Int, var width: Int, var height: Int, var label: String?) {
    var filled= false
    fun fill() { filled = true}
    fun emptyLabel() { label = null}

    // This is necessary in order to complete it in test data builder
    companion object
}
