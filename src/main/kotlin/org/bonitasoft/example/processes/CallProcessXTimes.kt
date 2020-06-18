package org.bonitasoft.example.processes

import org.bonitasoft.engine.bpm.process.impl.ProcessDefinitionBuilder
import org.bonitasoft.example.toExpression

class CallProcessXTimes(private val targetProcessName: String, private val targetProcessVersion: String, private val times: Int) : BonitaProcess() {
    override fun process(): ProcessDefinitionBuilder =
            ProcessDefinitionBuilder().createNewInstance("CallProcessXTimes", "1,0")
                    .apply {
                        addCallActivity("callProc", targetProcessName.toExpression(), targetProcessVersion.toExpression()).addMultiInstance(false, times.toExpression())
                    }
}