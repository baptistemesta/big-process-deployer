package org.bonitasoft.example.processes

import org.bonitasoft.engine.api.APIClient
import org.bonitasoft.engine.bpm.bar.BarResource
import org.bonitasoft.engine.bpm.bar.BusinessArchiveBuilder
import org.bonitasoft.engine.bpm.bar.actorMapping.Actor
import org.bonitasoft.engine.bpm.bar.actorMapping.ActorMapping
import org.bonitasoft.engine.bpm.process.impl.ProcessDefinitionBuilder
import org.bonitasoft.example.toScript
import java.util.*

class ProcessWithBigData(private val number: Int) : BonitaProcess() {
    override fun process(): ProcessDefinitionBuilder =
            ProcessDefinitionBuilder().createNewInstance("BigData-$number", "1.0")
                    .apply {
                        addActor("theActor", true)
                        addStartEvent("start")
                        addAutomaticTask("auto1")
                        addUserTask("user1", "theActor")
                        addLongTextData("longText1", """
                            def value = new java.lang.StringBuilder()
                            for(int i = 0; i < 1000; i++) {
                                value.append("Lorem Elsass ipsum vielmols, libero. amet, turpis Salu bissame sit Pellentesque Racing. rucksack salu tellus bissame réchime leo Huguette in,")
                                value.append("Wurschtsalad senectus ftomi! mamsell semper Spätzle schpeck s'guelt elementum varius Gal. leo ante et Morbi kuglopf ac Pfourtz ! DNA, id Chulia")
                                value.append("Roberstau commodo jetz gehts los hopla ullamcorper picon bière Verdammi id, leverwurscht consectetur ac rossbolla lotto-owe ornare porta sed suspendisse")
                                value.append("pellentesque Coopé de Truchtersheim non condimentum gewurztraminer")
                            }
                            return value.toString()
                        """.trimIndent().toScript())
                        addTransition("start", "auto1")
                        addTransition("start", "user1")
                    }

    override fun withResources(bar: BusinessArchiveBuilder) {
        bar.apply {
            actorMapping = ActorMapping().apply {
                addActor(Actor("theActor").apply {
                    addUser("jean.némar")
                    addUser("walter.bates")
                })
            }
//            addClasspathResource(BarResource("jar$number.jar", ByteArray(2 * 1000 * 1000).apply { Random().nextBytes(this) }))
        }
    }

    override fun accept(client: APIClient) {
        super.accept(client)

        println("Start process $name $version")
        val deployed = client.processAPI.getProcessDefinitionId(name, version)
        deployed.apply { client.processAPI.startProcess(this) }

    }
}