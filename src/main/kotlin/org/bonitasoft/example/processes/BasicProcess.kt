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
import org.bonitasoft.engine.bpm.flownode.GatewayType
import org.bonitasoft.engine.bpm.process.impl.ProcessDefinitionBuilder
import org.bonitasoft.example.toExpression
import java.util.*

class BasicProcess(private val number: Int) : BonitaProcess() {
    override fun process(): ProcessDefinitionBuilder =
            ProcessDefinitionBuilder().createNewInstance("BasicProcess-$number", "1.0")
                    .apply {
                        addActor("theActor", true)
                        addStartEvent("start")
                        addAutomaticTask("auto1")
                        addUserTask("user1", "theActor").apply {

                            addDisplayDescriptionAfterCompletion((0..500).map { "coucou" }.joinToString { "" }.toExpression())
                        }
                        addTransition("start", "auto1")
                        addTransition("start", "user1")
                    }

    override fun withResources(bar: BusinessArchiveBuilder) {
        bar.apply {
            actorMapping = ActorMapping().apply {
                addActor(Actor("theActor").apply {
                    addUser("jean.némar")
                    addUser("walter.bates")
                })
            }
//            addClasspathResource(BarResource("jar$number.jar", ByteArray(2 * 1000 * 1000).apply { Random().nextBytes(this) }))
        }
    }

    override fun accept(client: APIClient) {
        super.accept(client)
        val deployed = client.processAPI.getProcessDefinitionId(name, version)
        deployed.apply { client.processAPI.startProcess(this) }
    }
}