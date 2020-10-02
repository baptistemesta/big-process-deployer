package org.bonitasoft.example.processes

import org.bonitasoft.engine.api.APIClient
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
            println("Disable process $name $version")
            processDefinitionId = processAPI.getProcessDefinitionId(name, version)
            processDefinitionId?.apply { processAPI.disableProcess(this) }
        }
        client.safeExec {
            println("Delete process $name $version")
            processDefinitionId?.apply {
                processAPI.deleteArchivedProcessInstances(this, 0, 1000000)
                processAPI.deleteProcessInstances(this, 0, 1000000)
                processAPI.deleteProcessDefinition(this)
            }
        }
        client.safeExec {
            println("Deploy process $name $version")
            processDefinitionId = processAPI.deploy(businessArchive).id
        }
        client.safeExec {
            println("Enable process $name $version")
            processAPI.enableProcess(processDefinitionId!!)
        }
    }

    open fun withResources(bar: BusinessArchiveBuilder) {
    }

    abstract fun process(): ProcessDefinitionBuilder

}