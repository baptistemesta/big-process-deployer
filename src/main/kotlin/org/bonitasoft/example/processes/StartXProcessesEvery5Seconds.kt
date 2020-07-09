package org.bonitasoft.example.processes

import org.bonitasoft.engine.bpm.bar.BusinessArchiveBuilder
import org.bonitasoft.engine.bpm.flownode.TimerType
import org.bonitasoft.engine.bpm.process.impl.ProcessDefinitionBuilder
import org.bonitasoft.engine.expression.ExpressionConstants
import org.bonitasoft.engine.operation.OperationBuilder
import org.bonitasoft.example.toExpression
import org.bonitasoft.example.toIntegerScript
import org.bonitasoft.example.toParameter
import org.bonitasoft.example.toScript

class StartXProcessesEvery5Seconds(private val targetProcessName: String, private val targetProcessVersion: String, private val instances: Int) : BonitaProcess() {
    override fun process(): ProcessDefinitionBuilder =
            ProcessDefinitionBuilder().createNewInstance("StartXProcessesEvery5Seconds", "1.1")
                    .apply {
                        addParameter("instances", String::class.java.name)
                        addStartEvent("startTimer")
                                .addTimerEventTriggerDefinition(TimerType.CYCLE, "*/${5} * * * * ?".toExpression())
                        addShortTextData("someText", null)
                        addAutomaticTask("task1").apply {
                            addMultiInstance(false, "Integer.valueOf(instances)".toIntegerScript("instances".toParameter()))
                            addOperation(OperationBuilder().createSetDataOperation("someText", """
                                    def pId = apiAccessor.processAPI.getProcessDefinitionId("$targetProcessName", "$targetProcessVersion")
                                    apiAccessor.processAPI.startProcess(pId)
                                    return "ok"
                                """.trimIndent().toScript(ExpressionConstants.API_ACCESSOR.toExpression())))

                        }
                        addTransition("startTimer", "task1")
                    }

    override fun withResources(bar: BusinessArchiveBuilder) {
        bar.setParameters(mapOf("instances" to instances.toString()))
    }
}