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

import org.bonitasoft.engine.api.APIClient
import org.bonitasoft.engine.bpm.bar.BusinessArchiveFactory
import org.bonitasoft.engine.bpm.bar.BarResource
import org.bonitasoft.engine.bpm.bar.BusinessArchive
import org.bonitasoft.engine.bpm.bar.BusinessArchiveBuilder
import org.bonitasoft.engine.bpm.bar.actorMapping.Actor
import org.bonitasoft.engine.bpm.bar.actorMapping.ActorMapping
import org.bonitasoft.engine.bpm.flownode.TimerType
import org.bonitasoft.engine.bpm.process.DesignProcessDefinition
import org.bonitasoft.engine.bpm.process.impl.ProcessDefinitionBuilder
import org.bonitasoft.engine.expression.ExpressionBuilder
import org.bonitasoft.engine.expression.ExpressionConstants
import org.bonitasoft.engine.operation.OperationBuilder
import org.bonitasoft.example.connector.Sleep1sConnector
import java.io.File
import java.util.function.Consumer

class CallActivities : Consumer<APIClient> {
    override fun accept(apiClient: APIClient) {
        apiClient.safeExec {
            processAPI.disableProcess(processAPI.getProcessDefinitionId("CalledProcess", "1.0"))
        }
        apiClient.safeExec {
            processAPI.disableProcess(processAPI.getProcessDefinitionId("CallingProcess", "1.0"))
        }
        apiClient.safeExec {
            val processDefinitionId = processAPI.getProcessDefinitionId("CalledProcess", "1.0")
            processAPI.deleteProcessInstances(processDefinitionId, 0, 1000)
            processAPI.deleteArchivedProcessInstances(processDefinitionId, 0, 1000)
            processAPI.deleteProcessDefinition(processDefinitionId)
        }
        apiClient.safeExec {
            val processDefinitionId = processAPI.getProcessDefinitionId("CallingProcess", "1.0")
            processAPI.deleteProcessInstances(processDefinitionId, 0, 1000)
            processAPI.deleteArchivedProcessInstances(processDefinitionId, 0, 1000)
            processAPI.deleteProcessDefinition(processDefinitionId)
        }
        apiClient.safeExec {
            processAPI.deployAndEnableProcess(createCalledProcess())
        }
        apiClient.safeExec {
            processAPI.deployAndEnableProcess(createCallingProcess())
        }
    }

    internal fun createCalledProcess(): BusinessArchive? {
        return BusinessArchiveBuilder().createNewBusinessArchive()
                .setActorMapping(ActorMapping().apply {
                    addActor(Actor("theActor").apply {
                        addUser("walter.bates")
                    })
                })
                .setProcessDefinition(
                        ProcessDefinitionBuilder().createNewInstance("CalledProcess", "1.0")
                                .apply {
                                    addActor("theActor", true)
                                    addAutomaticTask("sub1")
                                    addAutomaticTask("sub2")
                                }.done())
                .done()
    }
    internal fun createCallingProcess(): BusinessArchive? {
        return BusinessArchiveBuilder().createNewBusinessArchive()
                .setActorMapping(ActorMapping().apply {
                    addActor(Actor("theActor").apply {
                        addUser("walter.bates")
                    })
                })
                .setProcessDefinition(
                        ProcessDefinitionBuilder().createNewInstance("CallingProcess", "1.0")
                                .apply {
                                    addActor("theActor", true)
                                    addStartEvent("start")
                                    addCallActivity("call", "CalledProcess".toExpression(), "1.0".toExpression()).addLoop(false, true.toExpression())
                                    addAutomaticTask("auto1")
                                    addAutomaticTask("auto2")
                                    addAutomaticTask("auto3")
                                    addEndEvent("end").addTerminateEventTrigger()
                                    addTransition("start", "call")
                                    addTransition("start", "auto1")
                                    addTransition("auto1", "auto2")
                                    addTransition("auto2", "end")
                                }.done())
                .done()
    }




}