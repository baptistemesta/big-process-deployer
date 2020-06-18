package org.bonitasoft.example.processes

import com.bonitasoft.engine.bpm.process.impl.ProcessDefinitionBuilderExt
import org.bonitasoft.engine.bpm.flownode.GatewayType
import org.bonitasoft.engine.bpm.process.impl.ProcessDefinitionBuilder
import org.bonitasoft.example.toExpression

class InclusiveGatewayProcess() : BonitaProcess() {
    override fun process(): ProcessDefinitionBuilder =
            ProcessDefinitionBuilderExt().createNewInstance("Receive message process", "1,0")
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