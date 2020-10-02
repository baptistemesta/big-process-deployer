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