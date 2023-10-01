# ObjectMother with customization in Kotlin

This POC is an experimentation in `Kotlin` to implement [Test Data Builders](http://wiki.c2.com/?TestDataBuilder) with the minimum of verbosity. We try to see here if `Kotlin` allows to minimize one major drawback of [Test Data Builders in `Java`](https://blog.sogilis.com/posts/2019-01-11-object-mother-builder-java/): verbosity.

The solution explored here is heavily inspired by [factory_bot](https://github.com/thoughtbot/factory_bot), a ruby library which achieve to be very concise, readable and flexible.

## POC

[Rectangle class](src/main/kotlin/Rectangle.kt) is extended in [POC.kt](src/test/kotlin/v1/POC.kt). See how it can be used in [tests](src/test/kotlin/Test.kt).

## Other approaches

* [Kotlin FactoryBot Library](https://github.com/gmkseta/k-factory-bot) : cannot handle immutable objets, trait usage is verified at compile time and object construction cannot be customized by traits
* [faktory-bot](https://github.com/raphiz/faktory-bot): code generation, cannot handle immutable objects and object construction cannot be customized by traits

## Backlog and ideas

- Make it work with immutable objects
- Take advantage of data classes?
- Traits should be applied to constructor AND after object is created?
- Custom properties should be applied to constructor AND after object is created?
