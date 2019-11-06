package org.bonitasoft.example

import org.bonitasoft.engine.expression.Expression
import org.bonitasoft.engine.expression.ExpressionBuilder
import org.bonitasoft.engine.expression.ExpressionConstants
import java.io.BufferedOutputStream
import java.io.ByteArrayOutputStream
import java.util.jar.JarEntry
import java.util.jar.JarOutputStream



fun String.toExpression(): Expression = ExpressionBuilder().createConstantStringExpression(this)
fun Int.toExpression(): Expression = ExpressionBuilder().createConstantIntegerExpression(this)
fun ExpressionConstants.toExpression(): Expression = ExpressionBuilder().createEngineConstant(this)
fun String.toScript(vararg dependencies: Expression): Expression = ExpressionBuilder().createGroovyScriptExpression("aScript", this, String::class.java.name, dependencies.toList())

fun getJar(vararg classes: Class<out Any>): ByteArray {
    val map = classes.associate { c ->
        val className = c.name.replace(".", "/") + ".class"
        className to c.classLoader.getResourceAsStream(className).readBytes()
    }
    var baos = ByteArrayOutputStream()
    var jarOutStream = JarOutputStream(BufferedOutputStream(baos))
    map.forEach { name, content ->
        jarOutStream.putNextEntry(JarEntry(name))
        jarOutStream.write(content)
        jarOutStream.closeEntry()
    }
    jarOutStream.flush()
    baos.flush()
    jarOutStream.close()
    return baos.toByteArray()
}

fun buildConnectorImplementationFile(definitionId: String, definitionVersion: String, implementationId: String,
                                     implementationVersion: String, implementationClassname: String, dependencies: List<String>): ByteArray {
    val jarDependencies = dependencies.map { "\n        <jarDependency>$it</jarDependency>" }.joinToString("")
    val content = """<?xml version="1.0" encoding="UTF-8"?>
<implementation:connectorImplementation xmlns:implementation="http://www.bonitasoft.org/ns/connector/implementation/6.0">
    <definitionId>$definitionId</definitionId>
    <definitionVersion>$definitionVersion</definitionVersion>
    <implementationClassname>$implementationClassname</implementationClassname>
    <implementationId>$implementationId</implementationId>
    <implementationVersion>$implementationVersion</implementationVersion>
    <jarDependencies>${jarDependencies}
    </jarDependencies>
</implementation:connectorImplementation>"""
    return content.toByteArray()
}