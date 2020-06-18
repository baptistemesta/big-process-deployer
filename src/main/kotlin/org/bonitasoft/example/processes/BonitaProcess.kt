package org.bonitasoft.example.processes

import com.bonitasoft.engine.api.APIClient
import org.bonitasoft.engine.bpm.bar.BusinessArchive
import org.bonitasoft.engine.bpm.bar.BusinessArchiveBuilder
import org.bonitasoft.engine.bpm.process.impl.ProcessDefinitionBuilder
import org.bonitasoft.example.safeExec
import java.util.function.Consumer

abstract class BonitaProcess : Consumer<APIClient> {
    val businessArchive: BusinessArchive by lazy {
        BusinessArchiveBuilder().createNewBusinessArchive().apply {
            setProcessDefinition(process().done())
            withResources(this)
        }.done()
    }

    val name: String by lazy { businessArchive.processDefinition.name }
    val version: String by lazy { businessArchive.processDefinition.version }

    var processDefinitionId: Long? = null

    override fun accept(client: APIClient) {
        client.safeExec {
            processDefinitionId = processAPI.getProcessDefinitionId(name, version)
            processDefinitionId?.apply { processAPI.disableProcess(this) }
        }
        client.safeExec {
            processDefinitionId?.apply {
                processAPI.deleteArchivedProcessInstances(this, 0, 1000000)
                processAPI.deleteProcessInstances(this, 0, 1000000)
                processAPI.deleteProcessDefinition(this)
            }
        }
        client.safeExec {
            processAPI.deploy(businessArchive)
        }
        client.safeExec {
            processAPI.getProcessDefinitionId(name, version).apply { processAPI.enableProcess(this) }
        }
    }

    open fun withResources(bar: BusinessArchiveBuilder) {
    }

    abstract fun process(): ProcessDefinitionBuilder

}