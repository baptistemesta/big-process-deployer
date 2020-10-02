/*
 * Copyright 2020 Bonitasoft S.A.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.bonitasoft.example

import org.bonitasoft.engine.expression.Expression
import org.bonitasoft.engine.expression.ExpressionBuilder
import org.bonitasoft.engine.expression.ExpressionConstants
import java.io.BufferedOutputStream
import java.io.ByteArrayOutputStream
import java.util.jar.JarEntry
import java.util.jar.JarOutputStream



fun String.toExpression(): Expression = ExpressionBuilder().createConstantStringExpression(this)
fun Boolean.toExpression(): Expression = ExpressionBuilder().createConstantBooleanExpression(this)
fun String.toParameter(): Expression = ExpressionBuilder().createParameterExpression(this, this, String::class.java.name)
fun String.toIntegerParameter(): Expression = ExpressionBuilder().createParameterExpression(this, this, Integer::class.java.name)
fun Int.toExpression(): Expression = ExpressionBuilder().createConstantIntegerExpression(this)
fun ExpressionConstants.toExpression(): Expression = ExpressionBuilder().createEngineConstant(this)
fun String.toScript(vararg dependencies: Expression): Expression = ExpressionBuilder().createGroovyScriptExpression("aScript", this, String::class.java.name, dependencies.toList())
fun String.toIntegerScript(vararg dependencies: Expression): Expression = ExpressionBuilder().createGroovyScriptExpression("aScript", this, Integer::class.java.name, dependencies.toList())

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