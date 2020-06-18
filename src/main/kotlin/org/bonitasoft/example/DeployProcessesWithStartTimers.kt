package org.bonitasoft.example

import com.bonitasoft.engine.bpm.process.impl.ProcessDefinitionBuilderExt
import com.bonitasoft.engine.api.APIClient
import com.bonitasoft.engine.bpm.bar.BusinessArchiveFactory
import org.bonitasoft.engine.bpm.bar.BarResource
import org.bonitasoft.engine.bpm.bar.BusinessArchive
import org.bonitasoft.engine.bpm.bar.BusinessArchiveBuilder
import org.bonitasoft.engine.bpm.bar.actorMapping.Actor
import org.bonitasoft.engine.bpm.bar.actorMapping.ActorMapping
import org.bonitasoft.engine.bpm.flownode.GatewayType
import org.bonitasoft.engine.bpm.flownode.TimerType
import org.bonitasoft.engine.bpm.process.DesignProcessDefinition
import org.bonitasoft.engine.bpm.process.impl.ProcessDefinitionBuilder
import org.bonitasoft.engine.expression.ExpressionBuilder
import org.bonitasoft.engine.expression.ExpressionConstants
import org.bonitasoft.engine.operation.OperationBuilder
import org.bonitasoft.example.connector.Sleep1sConnector
import java.io.File
import java.util.function.Consumer

class DeployProcessesWithStartTimers : Consumer<APIClient> {
    override fun accept(apiClient: APIClient) {
        apiClient.safeExec {
            processAPI.disableProcess(processAPI.getProcessDefinitionId("Process with 50 tasks", "1,0"))
        }
        apiClient.safeExec {
            processAPI.disableProcess(processAPI.getProcessDefinitionId("Start 50 processes every 5s", "1,0"))
        }
        apiClient.safeExec {
            processAPI.disableProcess(processAPI.getProcessDefinitionId("Receive message process", "1,0"))
        }
        apiClient.safeExec {
            processAPI.deployAndEnableProcess(createReceiveProcess())
        }
        apiClient.safeExec {
            val bar = createProcessWith50()
            processAPI.deployAndEnableProcess(bar)
        }
        apiClient.safeExec {
            val deploy = processAPI.deploy(createstart50())
            processAPI.updateParameterInstanceValue(deploy.id, "instancesBySeconds", "100")
            processAPI.enableProcess(deploy.id)

        }
    }

    internal fun createReceiveProcess(): BusinessArchive? {
        return BusinessArchiveBuilder().createNewBusinessArchive()
                .setActorMapping(ActorMapping().apply {
                    addActor(Actor("theActor").apply {
                        addUser("walter.bates")
                    })
                })
                .setProcessDefinition(
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

                                }.done())
                .done()
    }

    internal fun createProcessWith50(): BusinessArchive? {
        return BusinessArchiveBuilder().createNewBusinessArchive().apply {
            setProcessDefinition(ProcessDefinitionBuilder().createNewInstance("Process with 50 tasks", "1,0")
                    .apply {
                        addAutomaticTask("task1").apply {
                            addMultiInstance(false, 20.toExpression())
                        }
                        addCallActivity("callProc", "Receive message process".toExpression(), "1,0".toExpression()).addMultiInstance(false, 5.toExpression())
//                        addSendTask("sendMessage", "myMessage", "Receive message process".toExpression()).addMultiInstance(false, 5.toExpression())
                    }.done()
            )
            addConnectorImplementation(BarResource("sleep1sConnector.impl",
                    buildConnectorImplementationFile("sleep1sConnector", "1.0", "sleep1sConnector", "1.0", Sleep1sConnector::class.java.name, listOf("sleep1sConnector.jar"))))
            addClasspathResource(BarResource("sleep1sConnector.jar",
                    getJar(Sleep1sConnector::class.java)
            ))
        }.done()
    }

    internal fun createstart50(): DesignProcessDefinition {
        return ProcessDefinitionBuilder().createNewInstance("Start 50 processes every 5s", "1,0")
                .apply {
                    addParameter("instancesBySeconds", String::class.java.name)
                    addStartEvent("startTimer")
                            .addTimerEventTriggerDefinition(TimerType.CYCLE, "*/${5} * * * * ?".toExpression())
                    addShortTextData("someText", null)
                    addAutomaticTask("task1").apply {
                        addMultiInstance(false, "Integer.valueOf(instancesBySeconds)".toIntegerScript("instancesBySeconds".toParameter()))
                        addOperation(OperationBuilder().createSetDataOperation("someText", """
                                    def pId = apiAccessor.processAPI.getProcessDefinitionId("Process with 50 tasks", "1,0")
                                    apiAccessor.processAPI.startProcess(pId)
                                    return "ok"
                                """.trimIndent().toScript(ExpressionConstants.API_ACCESSOR.toExpression())))

                    }
                    addTransition("startTimer", "task1")
                }.done()
    }
}


fun main(args: Array<String>) {
    DeployProcessesWithStartTimers().apply {

        BusinessArchiveFactory.writeBusinessArchiveToFile(BusinessArchiveBuilder().createNewBusinessArchive().setProcessDefinition(createstart50()).done(), File("/Users/baptiste/git/big-process-deployer/start50.bar"))
        BusinessArchiveFactory.writeBusinessArchiveToFile(createProcessWith50(), File("/Users/baptiste/git/big-process-deployer/processWith50Tasks.bar"))
        BusinessArchiveFactory.writeBusinessArchiveToFile(createReceiveProcess(), File("/Users/baptiste/git/big-process-deployer/receiveProcess.bar"))
    }

}