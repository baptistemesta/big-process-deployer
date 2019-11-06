package org.bonitasoft.example.connector

import org.bonitasoft.engine.connector.Connector

public class Sleep1sConnector : Connector {
    override fun validateInputParameters() {
    }

    override fun disconnect() {
    }

    override fun connect() {
    }

    override fun setInputParameters(parameters: MutableMap<String, Any>?) {
    }

    override fun execute(): MutableMap<String, Any> {
        Thread.sleep(1000)
        return mutableMapOf()
    }
}