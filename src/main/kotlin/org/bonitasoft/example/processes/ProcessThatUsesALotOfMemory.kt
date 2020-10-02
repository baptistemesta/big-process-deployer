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
package org.bonitasoft.example.processes

import org.bonitasoft.engine.api.APIClient
import org.bonitasoft.engine.bpm.bar.BarResource
import org.bonitasoft.engine.bpm.bar.BusinessArchiveBuilder
import org.bonitasoft.engine.bpm.bar.actorMapping.Actor
import org.bonitasoft.engine.bpm.bar.actorMapping.ActorMapping
import org.bonitasoft.engine.bpm.process.impl.ProcessDefinitionBuilder
import org.bonitasoft.engine.expression.ExpressionBuilder
import org.bonitasoft.example.toIntegerParameter
import org.bonitasoft.example.toParameter
import org.bonitasoft.example.toScript
import java.util.*

class ProcessThatUsesALotOfMemory(private val number: Int) : BonitaProcess() {
    override fun process(): ProcessDefinitionBuilder =
            ProcessDefinitionBuilder().createNewInstance("ProcessThatUsesALotOfMemory-$number", "1.0")
                    .apply {
                        addActor("theActor", true)
                        addStartEvent("start")
                        addParameter("nbAlloc", "java.lang.Integer")
                        addParameter("sizeAllocMb", "java.lang.Integer")
                        addAutomaticTask("auto1").addDisplayName("""
                            def value = new java.lang.StringBuilder("toto")
                            for(int i = 0; i < nbAlloc; i++) {
                                byte[] bytes = new byte[sizeAllocMb*1000*1000];
                            }
                            return value.toString()
                        """.trimIndent().toScript("nbAlloc".toIntegerParameter(), "sizeAllocMb".toIntegerParameter()))
                        addTransition("start", "auto1")
                    }

    override fun withResources(bar: BusinessArchiveBuilder) {
        bar.apply {
            actorMapping = ActorMapping().apply {
                addActor(Actor("theActor").apply {
                    addUser("jean.némar")
                    addUser("walter.bates")
                })
            }
            setParameters(mapOf(
                    "nbAlloc" to "1000",
                    "sizeAllocMb" to "5"
            ))
        }
    }

    override fun accept(client: APIClient) {
        super.accept(client)

        println("Start process $name $version")
        val deployed = client.processAPI.getProcessDefinitionId(name, version)
        deployed.apply { client.processAPI.startProcess(this) }

    }
}