# ObjectMother with customization in Kotlin

This POC is an experimentation in `Kotlin` to implement something which hash same advantages then [Test Data Builders](http://wiki.c2.com/?TestDataBuilder). We try to see here if `Kotlin` allows to minimize one major drawback of [Test Data Builders in `Java`](https://blog.sogilis.com/posts/2019-01-11-object-mother-builder-java/): verbosity.

The solution explored here is heavily inspired by [factory_bot](https://github.com/thoughtbot/factory_bot), a ruby library which achieve to be very concise, readable and flexible.

[see test() method in sources](src/main/kotlin/POC.kt)