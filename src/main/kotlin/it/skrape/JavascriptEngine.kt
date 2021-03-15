package it.skrape

import com.oracle.truffle.js.scriptengine.GraalJSScriptEngine
import org.graalvm.polyglot.Context
import org.graalvm.polyglot.HostAccess
import org.graalvm.polyglot.Value
import org.intellij.lang.annotations.Language

object JavascriptEngine {

    val context by lazy(::createContext)

    val engine by lazy(::initializeEngine)

    private val contextConfig = Context.newBuilder("js")
        .allowHostAccess(HostAccess.ALL)
        .allowHostClassLookup { _ -> true }
        .option("js.ecmascript-version", "2021")

    private fun createContext() = contextConfig.build()

    private fun initializeEngine(): GraalJSScriptEngine = GraalJSScriptEngine.create(null, contextConfig)
}

fun jsEngine(source: () -> String): GraalJSScriptEngine = JavascriptEngine.engine.also { it.eval(source()) }

fun member(member: Any): String {
    val identifier = member.javaClass.simpleName
    JavascriptEngine.context.getBindings("js").putMember(identifier, member)
    return identifier
}

fun evalJs(@Language("JS") source: () -> String): Value = JavascriptEngine.context.eval("js", source())
fun evalJs(@Language("JS") source: String): Value = evalJs { source }


val String.evalJs get() = evalJs { this }
