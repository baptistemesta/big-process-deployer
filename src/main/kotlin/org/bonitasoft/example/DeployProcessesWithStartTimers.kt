package org.bonitasoft.example

import org.bonitasoft.engine.api.APIClient
import org.bonitasoft.engine.api.ProcessAPI
import org.bonitasoft.engine.bpm.bar.BarResource
import org.bonitasoft.engine.bpm.bar.BusinessArchiveBuilder
import org.bonitasoft.engine.bpm.connector.ConnectorEvent
import org.bonitasoft.engine.bpm.flownode.ActivityInstanceCriterion
import org.bonitasoft.engine.bpm.flownode.TimerType
import org.bonitasoft.engine.bpm.process.DesignProcessDefinition
import org.bonitasoft.engine.bpm.process.ProcessDeploymentInfoSearchDescriptor
import org.bonitasoft.engine.bpm.process.impl.ProcessDefinitionBuilder
import org.bonitasoft.engine.expression.ExpressionBuilder
import org.bonitasoft.engine.expression.ExpressionConstants
import org.bonitasoft.engine.operation.OperationBuilder
import org.bonitasoft.engine.search.SearchOptionsBuilder
import org.bonitasoft.example.connector.Sleep1sConnector
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
            processAPI.deployAndEnableProcess(ProcessDefinitionBuilder().createNewInstance("Receive message process", "1,0")
                    .apply {
                        addStartEvent("start").addMessageEventTrigger("myMessage")
                    }.done())
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
            val processDefinition = processAPI.deployAndEnableProcess(ProcessDefinitionBuilder().createNewInstance("Start 50 processes every 5s", "1,0")
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

            processDefinition.getOpenTasks(processAPI).forEach {
                println("Task ${it.name} is assigned to user with id ${it.assigneeId}")
            }

            processAPI.searchProcessDeploymentInfos(100.elements() withProcessName "Start 50 processes every 5s")

            val start = 100
            val end = 170
            processAPI.getHumanTasks(USER_ID, start..end)
        }
    }

    private fun Int.elements(): SearchOptionsBuilder = SearchOptionsBuilder(0, this)

    private infix fun SearchOptionsBuilder.withProcessName(name: String) = filter(ProcessDeploymentInfoSearchDescriptor.NAME, name).done()!!

    private fun ProcessAPI.getHumanTasks(userId: Long, range: IntRange) =
            getAssignedHumanTaskInstances(userId, range.first, range.step, ActivityInstanceCriterion.DEFAULT)


    private fun processWithStartTimer(name: String, everyXSeconds: Int): DesignProcessDefinition {
        return ProcessDefinitionBuilder().createNewInstance(name, "1,0")
                .apply {
                    addStartEvent("startTimer")
                            .addTimerEventTriggerDefinition(TimerType.CYCLE, "*/$everyXSeconds * * * * ?".toExpression())
                    addAutomaticTask("task").addLoop(true, ExpressionBuilder().createConstantBooleanExpression(true), ExpressionBuilder().createConstantIntegerExpression(100))
                    addTransition("startTimer", "task1")
                }.done()
    }

    companion object {
        private const val USER_ID = 117L
    }
}