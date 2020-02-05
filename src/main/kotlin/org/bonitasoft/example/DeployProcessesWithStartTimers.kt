package org.bonitasoft.example

import com.bonitasoft.engine.bpm.process.impl.ProcessDefinitionBuilderExt
import com.bonitasoft.engine.api.APIClient
import org.bonitasoft.engine.bpm.bar.BarResource
import org.bonitasoft.engine.bpm.bar.BusinessArchiveBuilder
import org.bonitasoft.engine.bpm.bar.actorMapping.Actor
import org.bonitasoft.engine.bpm.bar.actorMapping.ActorMapping
import org.bonitasoft.engine.bpm.connector.ConnectorEvent
import org.bonitasoft.engine.bpm.flownode.TimerType
import org.bonitasoft.engine.bpm.process.DesignProcessDefinition
import org.bonitasoft.engine.bpm.process.impl.ProcessDefinitionBuilder
import org.bonitasoft.engine.expression.Expression
import org.bonitasoft.engine.expression.ExpressionBuilder
import org.bonitasoft.engine.expression.ExpressionConstants
import org.bonitasoft.engine.operation.OperationBuilder
import org.bonitasoft.example.connector.Sleep1sConnector
import java.util.function.Consumer

class DeployProcessesWithStartTimers : Consumer<APIClient> {
    override fun accept(apiClient: APIClient) {
//        apiClient.safeExec {
//
//            processAPI.disableProcess(processAPI.getProcessDefinitionId("startProcessEvery10Seconds", "1,0"))
////            processAPI.deployAndEnableProcess(processWithStartTimer("startProcessEvery10Seconds", 10))
//        }
//        apiClient.safeExec {
//            processAPI.disableProcess(processAPI.getProcessDefinitionId("startProcessEvery10Seconds", "1,0"))
////            processAPI.deployAndEnableProcess(processWithStartTimer("startProcessEvery7Seconds", 7))
//        }
//        apiClient.safeExec {
//            processAPI.disableProcess(processAPI.getProcessDefinitionId("startProcessEvery6Seconds", "1,0"))
////            processAPI.deployAndEnableProcess(processWithStartTimer("startProcessEvery6Seconds", 7))
//        }
//        apiClient.safeExec {
//            processAPI.disableProcess(processAPI.getProcessDefinitionId("startProcessEvery5Seconds", "1,0"))
////            processAPI.deployAndEnableProcess(processWithStartTimer("startProcessEvery5Seconds", 5))
//        }
//        apiClient.safeExec {
//            processAPI.disableProcess(processAPI.getProcessDefinitionId("startProcessEvery2Seconds", "1,0"))
////            processAPI.deployAndEnableProcess(processWithStartTimer("startProcessEvery2Seconds", 2))
//        }
//        apiClient.safeExec {
//            processAPI.disableProcess(processAPI.getProcessDefinitionId("parallel", "1,0"))
//            processAPI.deployAndEnableProcess(ProcessDefinitionBuilder().createNewInstance("parallel", "1,0")
//                    .apply {
//                        addStartEvent("startTimer")
//                                .addTimerEventTriggerDefinition(TimerType.CYCLE, ExpressionBuilder().createConstantStringExpression("*/${4} * * * * ?"))
//                        addAutomaticTask("task1").addMultiInstance(false, ExpressionBuilder().createConstantIntegerExpression(100))
//                        addAutomaticTask("task2").addMultiInstance(false, ExpressionBuilder().createConstantIntegerExpression(100))
//                        addTransition("task1", "task2")
//                    }.done())
//        }
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
            processAPI.deployAndEnableProcess(BusinessArchiveBuilder().createNewBusinessArchive()
                    .setActorMapping(ActorMapping().apply {
                        addActor(Actor("theActor").apply {
                            addUser("walter.bates")
                        })
                    })
                    .setProcessDefinition(
                            ProcessDefinitionBuilderExt().createNewInstance("Receive message process", "1,0")
                    .apply {
                        addActor("theActor")
                        addStartEvent("start").addMessageEventTrigger("myMessage")
                        setStringIndex(1, "string index 1", "toto".toExpression())
                        setStringIndex(2, "string index 2", "titi".toExpression())
                        setStringIndex(3, "string index 3", "tata".toExpression())
                        addUserTask("userTask1", "theActor")
                        addTransition("start", "userTask1")
                    }.done())
                    .done())
        }
        apiClient.safeExec {
            val bar = BusinessArchiveBuilder().createNewBusinessArchive().apply {
                setProcessDefinition(ProcessDefinitionBuilder().createNewInstance("Process with 50 tasks", "1,0")
                        .apply {
                            addAutomaticTask("task1").apply {
                                addMultiInstance(false, 50.toExpression())
                                addConnector("sleep1sConnector", "sleep1sConnector", "1.0", ConnectorEvent.ON_ENTER)
                            }
                            addSendTask("sendMessage", "myMessage", "Receive message process".toExpression()).addMultiInstance(false, 50.toExpression())
                        }.done()
                )
                addConnectorImplementation(BarResource("sleep1sConnector.impl",
                        buildConnectorImplementationFile("sleep1sConnector", "1.0", "sleep1sConnector", "1.0", Sleep1sConnector::class.java.name, listOf("sleep1sConnector.jar"))))
                addClasspathResource(BarResource("sleep1sConnector.jar",
                        getJar(Sleep1sConnector::class.java)
                ))
            }.done()
            processAPI.deployAndEnableProcess(bar)
        }
        apiClient.safeExec {
            val deploy = processAPI.deploy(ProcessDefinitionBuilder().createNewInstance("Start 50 processes every 5s", "1,0")
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
                    }.done())
            processAPI.updateParameterInstanceValue(deploy.id, "instancesBySeconds", "100")
            processAPI.enableProcess(deploy.id)

        }
    }

    private fun processWithStartTimer(name: String, everyXSeconds: Int): DesignProcessDefinition {
        return ProcessDefinitionBuilder().createNewInstance(name, "1,0")
                .apply {
                    addStartEvent("startTimer")
                            .addTimerEventTriggerDefinition(TimerType.CYCLE, "*/$everyXSeconds * * * * ?".toExpression())
                    addAutomaticTask("task").addLoop(true, ExpressionBuilder().createConstantBooleanExpression(true), ExpressionBuilder().createConstantIntegerExpression(100))
                    addTransition("startTimer", "task1")
                }.done()
    }
}