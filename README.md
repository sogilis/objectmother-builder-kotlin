# ObjectMother with customization in Kotlin

This POC is an experimentation in `Kotlin` to implement [Test Data Builders](http://wiki.c2.com/?TestDataBuilder) with the minimum of verbosity. We try to see here if `Kotlin` allows to minimize one major drawback of [Test Data Builders in `Java`](https://blog.sogilis.com/posts/2019-01-11-object-mother-builder-java/): verbosity.

The solution explored here is heavily inspired by [factory_bot](https://github.com/thoughtbot/factory_bot), a ruby library which achieve to be very concise, readable and flexible.

## POC v1

```
val rectangle = Rectangle.build(CENTERED_AND_SQUARED, y = 10) {
    emptyLabel()
    label = "my rectangle"
}
```

Drawbacks:
* this requires mutable objects
* traits cannot be applied to constructor parameters
* traits can override custom constructor parameters

See more usages in [tests](src/test/kotlin/v1/Test.kt), and implementation [here](src/test/kotlin/v1/POC.kt).

## POC v2

```
val rectangle = Rectangle.build(CENTERED_AND_SQUARED) {
    emptyLabel()
    label = "my rectangle"
}.copy(y = 10)
```

Drawbacks:
* requires data classes
* boilerplate with `copy()` to override constructor parameters

See more usages in [tests](src/test/kotlin/v2/Test.kt), and implementation [here](src/test/kotlin/v2/POC.kt).

## Other approaches

* [Kotlin FactoryBot Library](https://github.com/gmkseta/k-factory-bot) : cannot handle immutable objets, trait usage is verified at compile time and object construction cannot be customized by traits
* [faktory-bot](https://github.com/raphiz/faktory-bot): code generation, cannot handle immutable objects and object construction cannot be customized by traits
