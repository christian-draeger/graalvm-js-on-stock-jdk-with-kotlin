package it.skrape

import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo

internal class JavascriptEngineTest {

    @Test
    internal fun `can execute basic javascript operation with default context`() {
        val printExample = evalJs { "'Hello JavaScript!'" }.asString()
        expectThat(printExample).isEqualTo("Hello JavaScript!")
    }

    @Test
    internal fun `can get members of javascript object by using default context`() {
        @Language("JS")
        val source = """
            ({
                id: 42 + 13 ,
                text: 'foo',
                arr: [42, 4711]
            })
        """.trimIndent()

        with(source.evalJs) {
            expectThat(getMember("id").asInt()).isEqualTo(55)
            expectThat(getMember("text").asString()).isEqualTo("foo")
            expectThat(getMember("arr").getArrayElement(1).asInt()).isEqualTo(4711)
        }
    }

    data class MyExampleService(
        val name: String
    ) {
        fun toGreeting(name: String) = copy(name = "Hi $name!")
    }

    @Test
    internal fun `can mutate kotlin data class in JS and return it`() {
        val result = with(member(MyExampleService(""))) {
            evalJs { "$this.toGreeting('Christian');" }.`as`(MyExampleService::class.java)
        }
        expectThat(result.name).isEqualTo("Hi Christian!")
    }


    @Test
    internal fun `can mutate kotlin data class in JS and do further operations on the object`() {
        val result = with(member(MyExampleService(""))) {
            evalJs(
                """
                    const converted = $this.toGreeting('Christian');
                    converted.getName().toUpperCase() // call data class getter 'getName' and use JS 'toUpperCase' function
                """
            ).asString()
        }
        expectThat(result).isEqualTo("HI CHRISTIAN!")

    }


    @Test
    internal fun `can execute javascript function with passed parameter by using default context`() {
        val executedJsFunctionResult = evalJs("x => x + 1").execute(5).asInt()
        expectThat(executedJsFunctionResult).isEqualTo(6)

    }

}
