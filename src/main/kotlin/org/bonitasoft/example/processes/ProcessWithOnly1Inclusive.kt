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

class ProcessWithOnly1Inclusive : BonitaProcess() {
    override fun process(): ProcessDefinitionBuilder =
            ProcessDefinitionBuilder().createNewInstance("ProcessWithOnly1Inclusive", "1,0")
                    .apply {

                        addAutomaticTask("auto1")
                        addAutomaticTask("auto2")
                        addGateway("inclu1", GatewayType.INCLUSIVE)
                        addEndEvent("end")

                        addTransition("auto1", "inclu1")
                        addTransition("auto2", "inclu1")
                        addTransition("inclu1", "end")

                    }
}