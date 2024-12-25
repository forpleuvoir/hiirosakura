package moe.forpleuivoir.hiirosakura.test

import org.junit.Test
import javax.script.ScriptEngineManager
import kotlin.time.measureTime

class ScriptTest {

    @Test
    fun test1() {
        val script = """
           print("hello world")
        """.trimIndent()
        val engine = ScriptEngineManager().getEngineByName("nashorn")
        repeat(10) {
            measureTime {
                engine.eval(script)
            }.let { println(it) }
        }
    }


    @Test
    fun test2() {
        val script = """
            load("nashorn:mozilla_compat.js");
            importPackage("moe.forpleuvoir.nebula.common.color");
            var color = Colors.RED
        """.trimIndent()
        val engine = ScriptEngineManager().getEngineByName("nashorn")
        engine.eval(script)
        repeat(10) {
            measureTime {
                engine.eval(
                    """
                        print(color)
                """.trimIndent()
                )
            }.let { println(it) }
        }
    }

}
