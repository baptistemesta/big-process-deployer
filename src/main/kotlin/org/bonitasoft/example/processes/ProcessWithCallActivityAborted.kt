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