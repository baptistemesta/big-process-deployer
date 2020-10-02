package org.bonitasoft.example

import org.bonitasoft.engine.api.APIClient
import org.bonitasoft.engine.bpm.bar.BusinessArchiveFactory
import org.bonitasoft.engine.bpm.bar.BusinessArchive
import org.bonitasoft.engine.bpm.bar.BusinessArchiveBuilder
import org.bonitasoft.engine.bpm.bar.actorMapping.Actor
import org.bonitasoft.engine.bpm.bar.actorMapping.ActorMapping
import org.bonitasoft.engine.bpm.bar.form.model.FormMappingDefinition
import org.bonitasoft.engine.bpm.bar.form.model.FormMappingModel
import org.bonitasoft.engine.bpm.contract.Type
import org.bonitasoft.engine.bpm.flownode.ActivityInstanceCriterion
import org.bonitasoft.engine.bpm.flownode.GatewayType
import org.bonitasoft.engine.bpm.process.impl.ProcessDefinitionBuilder
import org.bonitasoft.engine.expression.ExpressionBuilder
import org.bonitasoft.engine.expression.ExpressionConstants
import org.bonitasoft.engine.form.FormMappingTarget
import org.bonitasoft.engine.form.FormMappingType
import org.bonitasoft.engine.operation.OperationBuilder
import org.bonitasoft.engine.search.SearchOptionsBuilder
import java.io.File
import java.util.function.Consumer

class ProcessForDataExtraction : Consumer<APIClient> {

    val processes = mutableListOf<BusinessArchive>()

    override fun accept(apiClient: APIClient) {
        createBusinessArchives()
        disableExistingProcesses(apiClient)
        deployProcesses(apiClient)
    }

    private fun deployProcesses(apiClient: APIClient) {
        processes.forEach {
            apiClient.safeExec {
                val processDefinition = processAPI.deploy(it)
                processAPI.enableProcess(processDefinition.id)
            }
        }
    }

    private fun disableExistingProcesses(apiClient: APIClient) {
        processes.forEach {
            apiClient.safeExec {
                processAPI.disableProcess(
                        processAPI.getProcessDefinitionId(it.processDefinition.name, it.processDefinition.version))
            }
            apiClient.safeExec {
                val processDefinitionId = processAPI.getProcessDefinitionId(it.processDefinition.name, it.processDefinition.version)
                processAPI.deleteProcessInstances(processDefinitionId, 0, 1000)
                processAPI.deleteProcessDefinition(processDefinitionId)
            }
        }
    }

    internal fun createBusinessArchives() {
        processes.add(createLoanRequest())
        processes.add(createBotProcess())
    }

    private fun createBotProcess(): BusinessArchive {
        return BusinessArchiveBuilder().createNewBusinessArchive()
                .setActorMapping(ActorMapping().apply {
                    Actor("starter").apply {
                        addUser("walter.bates")
                    }
                })
                .setProcessDefinition(
                        ProcessDefinitionBuilder().createNewInstance("BotProcessForLoanRequest", "1.0").apply {
                            setActorInitiator("starter")
                            addContract().apply {
                                addInput("numberOfInstances", Type.INTEGER, "number of process to start")
                            }
                            addData("nbInstances", "java.lang.Integer", ExpressionBuilder().createContractInputExpression("numberOfInstances", "java.lang.Integer"))
                            addData("instanceIds", "java.util.List", null)


                            addAutomaticTask("startXInstances").apply {
                                addData("instanceId", "java.lang.Long", null)
                                addOperation(OperationBuilder().createSetDataOperation("instanceId", ExpressionBuilder()
                                        .createGroovyScriptExpression("startXInst",
                                                """
                                                def pId = apiAccessor.processAPI.getProcessDefinitionId("LoanRequest", "1.0")
                                                def amount = (Math.random() * 10000).round()
                                                def type = ["house", "car", "phone"][(Math.random() * 2).round().toInteger()]
                                                def instance = apiAccessor.processAPI.startProcessWithInputs(pId, [amount: amount, type: type])
                                                return instance.id
                                            """.trimIndent(), "java.lang.Long",
                                                ExpressionBuilder().createDataExpression("nbInstances", "java.lang.Integer"),
                                                ExpressionBuilder().createEngineConstant(ExpressionConstants.API_ACCESSOR))))
                                        .addMultiInstance(false, ExpressionBuilder().createDataExpression("nbInstances", "java.lang.Integer"))
                                        .addDataOutputItemRef("instanceId")
                                        .addLoopDataOutputRef("instanceIds")
                            }

                            addAutomaticTask("executeUserTasks").apply {
                                addData("instanceId", "java.lang.Long", null)
                                addMultiInstance(false, "instanceIds")
                                        .addDataInputItemRef("instanceId")
                                addDisplayName(ExpressionBuilder().createGroovyScriptExpression("executeTask",
                                        """
                                                def start = System.currentTimeMillis()
                                                while (System.currentTimeMillis() - start < 10000) {
                                                    def tasks = apiAccessor.processAPI.searchActivities(new org.bonitasoft.engine.search.SearchOptionsBuilder(0,1)
                                                            .filter("parentProcessInstanceId",instanceId).done()).result
                                                    if(!tasks.empty){
                                                        def userId = apiAccessor.identityAPI.getUserByUserName("walter.bates").id
                                                        apiAccessor.processAPI.assignUserTask(tasks[0].id, userId)
                                                        apiAccessor.processAPI.executeUserTask(tasks[0].id, [validated:Math.random()>0.5])
                                                    }
                                                    break
                                                    Thread.sleep(500)
                                                }
                                                return OK
                                            """.trimIndent(),
                                        "java.lang.String",
                                        ExpressionBuilder().createEngineConstant(ExpressionConstants.API_ACCESSOR),
                                        ExpressionBuilder().createDataExpression("instanceId", "java.lang.Long")
                                ))
                            }
                            addTransition("startXInstances", "executeUserTasks")

                        }.done()
                ).done()
    }

    private fun createLoanRequest(): BusinessArchive {
        return BusinessArchiveBuilder().createNewBusinessArchive()
                .setFormMappings(FormMappingModel().apply {
                    addFormMapping(FormMappingDefinition("custompage_processAutogeneratedForm", FormMappingType.PROCESS_START, FormMappingTarget.INTERNAL))
                    addFormMapping(FormMappingDefinition("custompage_taskAutogeneratedForm", FormMappingType.TASK, FormMappingTarget.INTERNAL, "verifyManually"))
                })
                .setActorMapping(ActorMapping().apply {
                    addActor(Actor("requester").apply {
                        addGroup("/ACME")
                    })
                    addActor(Actor("validator").apply {
                        addGroup("/ACME")
                    })
                })
                .setProcessDefinition(
                        ProcessDefinitionBuilder().createNewInstance("LoanRequest", "1.0")
                                .apply {
                                    addActor("requester", true)
                                    addActor("validator")
                                    addContract().apply {
                                        addInput("amount", Type.LONG, "amount of the loan")
                                        addInput("type", Type.TEXT, "type of the loan")
                                    }
                                    addData("amountData", "java.lang.Long", ExpressionBuilder().createContractInputExpression("amount", "java.lang.Long"))
                                    addData("typeData", "java.lang.String", ExpressionBuilder().createContractInputExpression("type", "java.lang.String"))
                                    addData("manuallyValidated", "java.lang.Boolean", null)
                                    addStartEvent("start")
                                    addGateway("gate1", GatewayType.EXCLUSIVE)
                                    addUserTask("verifyManually", "validator").apply {
                                        addContract().apply {
                                            addInput("validate", Type.BOOLEAN, "validate the loan")
                                        }
                                        addOperation(OperationBuilder().createSetDataOperation("manuallyValidated", ExpressionBuilder().createContractInputExpression("validate", "java.lang.Boolean")))
                                    }
                                    addAutomaticTask("verifyAuto")
                                    addEndEvent("validated")
                                    addEndEvent("rejected")
                                    addTransition("start", "gate1")
                                    addDefaultTransition("gate1", "verifyManually")
                                    addTransition("gate1", "verifyAuto", ExpressionBuilder().createGroovyScriptExpression(
                                            "bigLoan",
                                            "amountData < 3000",
                                            "java.lang.Boolean",
                                            ExpressionBuilder().createDataExpression("amountData", "java.lang.Long")
                                    ))
                                    addTransition("verifyAuto", "validated")
                                    addTransition("verifyManually", "validated", ExpressionBuilder().createDataExpression(
                                            "manuallyValidated", "java.lang.Boolean"
                                    ))
                                    addDefaultTransition("verifyManually", "rejected")
                                }.done())
                .done()
    }

}


fun main(args: Array<String>) {
    ProcessForDataExtraction().apply {
        createBusinessArchives()
        processes.forEach {
            val file = File("${it.processDefinition.name}-${it.processDefinition.version}.bar")
            BusinessArchiveFactory.writeBusinessArchiveToFile(it, file)
            println("exported process: ${file.absolutePath}")
        }
    }
}