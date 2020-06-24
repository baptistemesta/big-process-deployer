package org.bonitasoft.example.processes

import com.bonitasoft.engine.bpm.process.impl.ProcessDefinitionBuilderExt
import org.bonitasoft.engine.bpm.flownode.GatewayType
import org.bonitasoft.engine.bpm.process.impl.ProcessDefinitionBuilder

class ProcessWithOnly1Inclusive : BonitaProcess() {
    override fun process(): ProcessDefinitionBuilder =
            ProcessDefinitionBuilderExt().createNewInstance("ProcessWithOnly1Inclusive", "1,0")
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