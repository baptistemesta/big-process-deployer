package org.bonitasoft.example.processes

import org.bonitasoft.engine.bpm.bar.BusinessArchiveBuilder
import org.bonitasoft.engine.bpm.bar.actorMapping.Actor
import org.bonitasoft.engine.bpm.bar.actorMapping.ActorMapping
import org.bonitasoft.engine.bpm.process.impl.ProcessDefinitionBuilder
import org.bonitasoft.example.toExpression

class ProcessWithCallActivityAborted(private val processName: String, private val processVersion: String) : BonitaProcess() {

    override fun process(): ProcessDefinitionBuilder {
        return ProcessDefinitionBuilder().createNewInstance("ProcessWithCallActivityAborted", processVersion)
                .apply {
                    addActor("theActor", true)
                    addStartEvent("start")
                    addCallActivity("call", processName.toExpression(), processVersion.toExpression()).addLoop(false, true.toExpression())
                    addAutomaticTask("auto1")
                    addAutomaticTask("auto2")
                    addAutomaticTask("auto3")
                    addEndEvent("end").addTerminateEventTrigger()
                    addTransition("start", "call")
                    addTransition("start", "auto1")
                    addTransition("auto1", "auto2")
                    addTransition("auto2", "end")
                }
    }


    override fun withResources(bar: BusinessArchiveBuilder) {
        bar.apply {
            actorMapping = ActorMapping().apply {
                addActor(Actor("theActor").apply {
                    addUser("walter.bates")
                })
            }
        }
    }
}