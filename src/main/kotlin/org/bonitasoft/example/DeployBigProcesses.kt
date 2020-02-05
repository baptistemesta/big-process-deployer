package org.bonitasoft.example

import com.bonitasoft.engine.api.APIClient
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