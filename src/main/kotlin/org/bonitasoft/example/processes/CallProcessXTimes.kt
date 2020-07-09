package org.bonitasoft.example.processes

import org.bonitasoft.engine.bpm.bar.BusinessArchiveBuilder
import org.bonitasoft.engine.bpm.bar.actorMapping.Actor
import org.bonitasoft.engine.bpm.bar.actorMapping.ActorMapping
import org.bonitasoft.engine.bpm.process.impl.ProcessDefinitionBuilder
import org.bonitasoft.example.toExpression
import org.bonitasoft.example.toParameter

class CallProcessXTimes(private val targetProcessName: String, private val targetProcessVersion: String, private val times: Int) : BonitaProcess() {
    override fun process(): ProcessDefinitionBuilder =
            ProcessDefinitionBuilder().createNewInstance("CallProcessXTimes", "1.0")
                    .apply {
                        addActor("theActor", true)
                        addParameter("targetProcessName", "java.lang.String")
                        addParameter("targetProcessVersion", "java.lang.String")
                        addCallActivity("callProc", "targetProcessName".toParameter(), "targetProcessVersion".toParameter()).addMultiInstance(false, times.toExpression())
                    }


    override fun withResources(bar: BusinessArchiveBuilder) {
        bar.apply {
            actorMapping = ActorMapping().apply {
                addActor(Actor("theActor").apply {
                    addUser("walter.bates")
                })
            }

            setParameters(mapOf(
                    "targetProcessName" to targetProcessName,
                    "targetProcessVersion" to targetProcessVersion
            ))
        }
    }
}