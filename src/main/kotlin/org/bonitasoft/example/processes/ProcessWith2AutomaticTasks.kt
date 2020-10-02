package org.bonitasoft.example.processes

import org.bonitasoft.engine.bpm.bar.BusinessArchiveBuilder
import org.bonitasoft.engine.bpm.bar.actorMapping.Actor
import org.bonitasoft.engine.bpm.bar.actorMapping.ActorMapping
import org.bonitasoft.engine.bpm.process.impl.ProcessDefinitionBuilder

class ProcessWith2AutomaticTasks : BonitaProcess() {
    override fun process(): ProcessDefinitionBuilder {
        return ProcessDefinitionBuilder().createNewInstance("ProcessWith2AutomaticTasks", "1.0")
                .apply {
                    addActor("theActor", true)
                    addAutomaticTask("sub1")
                    addAutomaticTask("sub2")
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