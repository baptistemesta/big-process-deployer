package org.bonitasoft.example.processes

import com.bonitasoft.engine.bpm.process.impl.ProcessDefinitionBuilderExt
import org.bonitasoft.engine.bpm.flownode.GatewayType
import org.bonitasoft.engine.bpm.process.impl.ProcessDefinitionBuilder
import org.bonitasoft.example.toExpression

class ProcessWithALotOfGateways : BonitaProcess() {
    override fun process(): ProcessDefinitionBuilder =
            ProcessDefinitionBuilderExt().createNewInstance("ProcessWithALotOfGateways", "1,0")
                    .apply {
                        addStartEvent("start")
                        addGateway("para1", GatewayType.PARALLEL)
                        addTransition("start", "para1")

                        addAutomaticTask("auto1")
                        addAutomaticTask("auto2")
                        addAutomaticTask("auto3")
                        addAutomaticTask("otherAuto1")
                        addAutomaticTask("otherAuto2")
                        addAutomaticTask("otherAuto3")
                        addTransition("para1", "auto1")
                        addTransition("para1", "auto2")
                        addTransition("para1", "auto3")

                        addGateway("ex1", GatewayType.EXCLUSIVE)
                        addTransition("auto1", "ex1")

                        addAutomaticTask("auto4")
                        addAutomaticTask("auto5")
                        addDefaultTransition("ex1", "auto4")
                        addTransition("ex1", "auto5", true.toExpression())

                        addGateway("ex2", GatewayType.EXCLUSIVE)
                        addTransition("auto4", "ex2")
                        addTransition("auto5", "ex2")

                        addGateway("para2", GatewayType.PARALLEL)
                        addTransition("ex2", "para2")
                        addTransition("auto2", "para2")

                        addGateway("inclu1", GatewayType.INCLUSIVE)
                        addTransition("auto3", "inclu1")
                        addTransition("otherAuto1", "inclu1")
                        addTransition("otherAuto2", "inclu1")
                        addTransition("otherAuto3", "inclu1")

                        addEndEvent("end")
                        addTransition("inclu1", "end")

                    }
}