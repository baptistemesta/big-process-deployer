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

import org.bonitasoft.engine.bpm.flownode.GatewayType
import org.bonitasoft.engine.bpm.process.impl.ProcessDefinitionBuilder
import org.bonitasoft.example.toExpression

class InclusiveGatewayProcess() : BonitaProcess() {
    override fun process(): ProcessDefinitionBuilder =
            ProcessDefinitionBuilder().createNewInstance("InclusiveGatewayProcess", "1,0")
                    .apply {
                        addActor("theActor")
                        addStartEvent("start")
                        addAutomaticTask("auto1")
                        addUserTask("user1", "theActor")
                        addUserTask("user2", "theActor")
                        addUserTask("user3", "theActor")
                        addUserTask("user4", "theActor")
                        addUserTask("user5", "theActor")
                        addUserTask("user6", "theActor")
                        addUserTask("user7", "theActor")
                        addUserTask("user8", "theActor")
                        addUserTask("user9", "theActor")
                        addUserTask("user10", "theActor")
                        addGateway("inclusive1", GatewayType.INCLUSIVE)
                        addTransition("start", "auto1")
                        addTransition("start", "user1")
                        addTransition("auto1", "inclusive1")
                        addTransition("user1", "user2")
                        addTransition("user2", "user3")
                        addTransition("user3", "user4")
                        addTransition("user4", "user5")
                        addTransition("user5", "user6")
                        addTransition("user6", "user7")
                        addTransition("user7", "user8")
                        addTransition("user8", "user9")
                        addTransition("user9", "user10")
                        addTransition("user10", "inclusive1")

                    }}