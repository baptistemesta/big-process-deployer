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
import org.bonitasoft.engine.bpm.bar.BarResource
import org.bonitasoft.engine.bpm.bar.BusinessArchiveBuilder
import org.bonitasoft.engine.bpm.process.impl.ProcessDefinitionBuilder
import java.util.*
import java.util.function.Consumer

class DeployBigProcesses : Consumer<APIClient> {
    override fun accept(apiClient: APIClient) {
        apiClient.safeExec {
            (1..60).forEach { processNumber ->
                processAPI.deployAndEnableProcess(
                        BusinessArchiveBuilder().createNewBusinessArchive().apply {
                            (1..50).forEach { jarNumber ->
                                addClasspathResource(BarResource("jar$jarNumber.jar", ByteArray(1 * 1000 * 1000).apply { Random().nextBytes(this) }))
                            }
                            setProcessDefinition(ProcessDefinitionBuilder().createNewInstance("MyProcess$processNumber", "1,0").apply {
                                addAutomaticTask("doSomething")
                            }.done())
                        }.done()
                )
            }
        }
    }
}