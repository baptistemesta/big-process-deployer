package org.bonitasoft.example.processes

import com.bonitasoft.engine.api.APIClient
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