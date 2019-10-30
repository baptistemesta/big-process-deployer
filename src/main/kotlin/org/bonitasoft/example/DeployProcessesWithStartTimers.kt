package org.bonitasoft.example

import org.bonitasoft.engine.api.APIClient
import org.bonitasoft.engine.bpm.connector.ConnectorEvent
import org.bonitasoft.engine.bpm.flownode.TimerType
import org.bonitasoft.engine.bpm.process.DesignProcessDefinition
import org.bonitasoft.engine.bpm.process.impl.ProcessDefinitionBuilder
import org.bonitasoft.engine.expression.Expression
import org.bonitasoft.engine.expression.ExpressionBuilder
import org.bonitasoft.engine.expression.ExpressionConstants
import org.bonitasoft.engine.operation.OperationBuilder
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
            processAPI.deployAndEnableProcess(ProcessDefinitionBuilder().createNewInstance("Receive message process", "1,0")
                    .apply {
                        addStartEvent("start").addMessageEventTrigger("myMessage")
                    }.done())
        }
        apiClient.safeExec {

            processAPI.deployAndEnableProcess(ProcessDefinitionBuilder().createNewInstance("Process with 50 tasks", "1,0")
                    .apply {
                        addAutomaticTask("task1").apply {
                            addMultiInstance(false, 50.toExpression())
                            //TODO add connector
//                            addConnector("sleep1sConnector", "sleep1sConnector", "1.0", ConnectorEvent.ON_ENTER)
                        }
                        addSendTask("sendMessage", "myMessage", "Receive message process".toExpression()).addMultiInstance(false, 50.toExpression())
                    }.done())
        }
        apiClient.safeExec {
            processAPI.deployAndEnableProcess(ProcessDefinitionBuilder().createNewInstance("Start 50 processes every 5s", "1,0")
                    .apply {
                        addStartEvent("startTimer")
                                .addTimerEventTriggerDefinition(TimerType.CYCLE, "*/${5} * * * * ?".toExpression())
                        addShortTextData("someText", null)
                        addAutomaticTask("task1").apply {
                            addMultiInstance(false, 50.toExpression())
                            addOperation(OperationBuilder().createSetDataOperation("someText", """
                                def pId = apiAccessor.processAPI.getProcessDefinitionId("Process with 50 tasks", "1,0")
                                apiAccessor.processAPI.startProcess(pId)
                                return "ok"
                            """.trimIndent().toScript(ExpressionConstants.API_ACCESSOR.toExpression())))

                        }
                        addTransition("startTimer", "task1")
                    }.done())

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

    private fun String.toExpression() = ExpressionBuilder().createConstantStringExpression(this)
    private fun Int.toExpression() = ExpressionBuilder().createConstantIntegerExpression(this)
    private fun ExpressionConstants.toExpression() = ExpressionBuilder().createEngineConstant(this)
    private fun String.toScript(vararg dependencies: Expression) = ExpressionBuilder().createGroovyScriptExpression("aScript", this, String::class.java.name, dependencies.toList())
}