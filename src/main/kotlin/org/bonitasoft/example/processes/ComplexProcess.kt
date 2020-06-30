package org.bonitasoft.example.processes

import com.bonitasoft.engine.api.APIClient
import com.bonitasoft.engine.bpm.process.impl.ProcessDefinitionBuilderExt
import org.bonitasoft.engine.bpm.bar.BarResource
import org.bonitasoft.engine.bpm.bar.BusinessArchiveBuilder
import org.bonitasoft.engine.bpm.bar.actorMapping.Actor
import org.bonitasoft.engine.bpm.bar.actorMapping.ActorMapping
import org.bonitasoft.engine.bpm.flownode.GatewayType
import org.bonitasoft.engine.bpm.process.impl.ProcessDefinitionBuilder
import org.bonitasoft.engine.expression.ExpressionBuilder
import org.bonitasoft.engine.operation.OperationBuilder
import org.bonitasoft.example.toExpression
import java.util.*

class ComplexProcess(private val number: Int) : BonitaProcess() {
    override fun process(): ProcessDefinitionBuilder =
            ProcessDefinitionBuilder().createNewInstance("ComplexProcess-$number", "1.0")
                    .apply {
                        addActor("theActor", true)
                        addLongTextData("myLongText",(0..500).map { "coucou" }.joinToString { "" }.toExpression())
                        addStartEvent("start")
                        addAutomaticTask("auto1")
                        addUserTask("user1", "theActor").apply {
                            addDisplayDescriptionAfterCompletion((0..500).map { "coucou" }.joinToString { "" }.toExpression())
                        }
                        addAutomaticTask("auto2").apply {
                            for (i in (1..10)) {
                                addOperation(OperationBuilder().createSetDataOperation("myLongTextData",
                                        ExpressionBuilder().createGroovyScriptExpression("theScript","""
                                //Lorem ipsum dolor sit amet, consectetur adipiscing elit. Curabitur lorem leo, pretium 
                                //in tortor malesuada, condimentum congue leo. Phasellus ullamcorper lorem et metus efficitur,
                                //sed molestie lacus porttitor. Donec placerat, dui vitae suscipit sodales, odio orci ultricies massa,
                                //eget tristique orci sem sed mauris. Quisque lacus purus, rutrum quis quam id, convallis volutpat nisi. Aliquam laoreet feugiat ipsum at luctus. Maecenas tincidunt feugiat nibh ac tincidunt. Praesent vel volutpat metus, quis mollis ligula. Aenean molestie elementum lorem consequat pharetra.
                                //
                                //Phasellus rhoncus ipsum ac risus gravida, a porttitor velit finibus. Sed in dictum arcu, eu accumsan augue. Nam at tristique ex. Phasellus eu leo diam. Nam nec facilisis arcu, vel lacinia tellus. Proin maximus iaculis orci. Mauris vitae semper urna, imperdiet ultricies odio. Vivamus sagittis metus elementum mauris rhoncus, at dictum nisi faucibus. Vivamus consectetur sit amet nisi vel vulputate. Proin scelerisque et quam vel vehicula. Nunc nec justo non elit tempor blandit. Suspendisse elementum justo quis venenatis elementum.
                                //
                                //Ut non commodo turpis. Morbi id suscipit felis. Aliquam erat volutpat. Phasellus malesuada, ligula ullamcorper imperdiet faucibus, neque nulla elementum metus, sed hendrerit orci enim in magna. Nullam et quam pulvinar, egestas nibh et, lobortis mi. Fusce non dignissim sem. Suspendisse et nibh eu enim eleifend pretium in sit amet nisl.
                                //
                                //Praesent fermentum placerat orci ac suscipit. Etiam ut vehicula neque. Etiam tincidunt condimentum purus, ut tristique est varius vestibulum. Suspendisse eget mauris ac quam consequat tristique in sed dolor. Praesent scelerisque dui turpis, nec pellentesque justo elementum in. Sed fringilla posuere nisi. Aliquam dictum semper risus feugiat ultrices. Integer in mauris quis lorem tristique ultrices. Cras aliquet nulla diam, quis accumsan enim ornare sed. Maecenas ornare euismod ipsum, a gravida dolor pellentesque id. Morbi elementum sit amet nulla eget sollicitudin. Vestibulum ut purus tristique, mattis sapien sollicitudin, faucibus nibh. Aenean tincidunt dapibus congue.
                                //
                                //Proin fermentum blandit pharetra. Etiam posuere nisi non mollis faucibus. Proin semper mi sed mi elementum consectetur id at sem. Suspendisse congue est tellus, nec finibus massa dictum id. Sed dolor odio, varius in lectus non, ultricies tempor odio. Curabitur porta tempor mattis. Proin suscipit elit tellus, vitae porta arcu congue sodales. Etiam sit amet euismod velit, at consectetur orci. Aliquam ac libero risus. Mauris vel massa ipsum. Interdum et malesuada fames ac ante ipsum primis in faucibus.
                                return "ok"
                            """.trimIndent(),"java.lang.String",
                                                ExpressionBuilder().createDataExpression("myLongText","java.lang.String"),
                                                ExpressionBuilder().createDataExpression("myLongText","java.lang.String"),
                                                ExpressionBuilder().createDataExpression("myLongText","java.lang.String"),
                                                ExpressionBuilder().createDataExpression("myLongText","java.lang.String"),
                                                ExpressionBuilder().createDataExpression("myLongText","java.lang.String"),
                                                ExpressionBuilder().createDataExpression("myLongText","java.lang.String"),
                                                ExpressionBuilder().createDataExpression("myLongText","java.lang.String"),
                                                ExpressionBuilder().createDataExpression("myLongText","java.lang.String"),
                                                ExpressionBuilder().createDataExpression("myLongText","java.lang.String"),
                                                ExpressionBuilder().createDataExpression("myLongText","java.lang.String"),
                                                ExpressionBuilder().createDataExpression("myLongText","java.lang.String"),
                                                ExpressionBuilder().createDataExpression("myLongText","java.lang.String"),
                                                ExpressionBuilder().createDataExpression("myLongText","java.lang.String"),
                                                ExpressionBuilder().createDataExpression("myLongText","java.lang.String"),
                                                ExpressionBuilder().createDataExpression("myLongText","java.lang.String"),
                                                ExpressionBuilder().createDataExpression("myLongText","java.lang.String"),
                                                ExpressionBuilder().createDataExpression("myLongText","java.lang.String"),
                                                ExpressionBuilder().createDataExpression("myLongText","java.lang.String"),
                                                ExpressionBuilder().createDataExpression("myLongText","java.lang.String"),
                                                ExpressionBuilder().createDataExpression("myLongText","java.lang.String"),
                                                ExpressionBuilder().createDataExpression("myLongText","java.lang.String"),
                                                ExpressionBuilder().createDataExpression("myLongText","java.lang.String"),
                                                ExpressionBuilder().createDataExpression("myLongText","java.lang.String"),
                                                ExpressionBuilder().createDataExpression("myLongText","java.lang.String"),
                                                ExpressionBuilder().createDataExpression("myLongText","java.lang.String"),
                                                ExpressionBuilder().createDataExpression("myLongText","java.lang.String"),
                                                ExpressionBuilder().createDataExpression("myLongText","java.lang.String"),
                                                ExpressionBuilder().createDataExpression("myLongText","java.lang.String"),
                                                ExpressionBuilder().createDataExpression("myLongText","java.lang.String"),
                                                ExpressionBuilder().createDataExpression("myLongText","java.lang.String"),
                                                ExpressionBuilder().createDataExpression("myLongText","java.lang.String"),
                                                ExpressionBuilder().createDataExpression("myLongText","java.lang.String"),
                                                ExpressionBuilder().createDataExpression("myLongText","java.lang.String"),
                                                ExpressionBuilder().createDataExpression("myLongText","java.lang.String"),
                                                ExpressionBuilder().createDataExpression("myLongText","java.lang.String"),
                                                ExpressionBuilder().createDataExpression("myLongText","java.lang.String"),
                                                ExpressionBuilder().createDataExpression("myLongText","java.lang.String"),
                                                ExpressionBuilder().createDataExpression("myLongText","java.lang.String"),
                                                ExpressionBuilder().createDataExpression("myLongText","java.lang.String"),
                                                ExpressionBuilder().createDataExpression("myLongText","java.lang.String"),
                                                ExpressionBuilder().createDataExpression("myLongText","java.lang.String"),
                                                ExpressionBuilder().createDataExpression("myLongText","java.lang.String"),
                                                ExpressionBuilder().createDataExpression("myLongText","java.lang.String"),
                                                ExpressionBuilder().createDataExpression("myLongText","java.lang.String"),
                                                ExpressionBuilder().createDataExpression("myLongText","java.lang.String"),
                                                ExpressionBuilder().createDataExpression("myLongText","java.lang.String"),
                                                ExpressionBuilder().createDataExpression("myLongText","java.lang.String"),
                                                ExpressionBuilder().createDataExpression("myLongText","java.lang.String"),
                                                ExpressionBuilder().createDataExpression("myLongText","java.lang.String"),
                                                ExpressionBuilder().createDataExpression("myLongText","java.lang.String"),
                                                ExpressionBuilder().createDataExpression("myLongText","java.lang.String"),
                                                ExpressionBuilder().createDataExpression("myLongText","java.lang.String"),
                                                ExpressionBuilder().createDataExpression("myLongText","java.lang.String"),
                                                ExpressionBuilder().createDataExpression("myLongText","java.lang.String"),
                                                ExpressionBuilder().createDataExpression("myLongText","java.lang.String"),
                                                ExpressionBuilder().createDataExpression("myLongText","java.lang.String"),
                                                ExpressionBuilder().createDataExpression("myLongText","java.lang.String"),
                                                ExpressionBuilder().createDataExpression("myLongText","java.lang.String"),
                                                ExpressionBuilder().createDataExpression("myLongText","java.lang.String"),
                                                ExpressionBuilder().createDataExpression("myLongText","java.lang.String"),
                                                ExpressionBuilder().createDataExpression("myLongText","java.lang.String"),
                                                ExpressionBuilder().createDataExpression("myLongText","java.lang.String"),
                                                ExpressionBuilder().createDataExpression("myLongText","java.lang.String"),
                                                ExpressionBuilder().createDataExpression("myLongText","java.lang.String"),
                                                ExpressionBuilder().createDataExpression("myLongText","java.lang.String"),
                                                ExpressionBuilder().createDataExpression("myLongText","java.lang.String"),
                                                ExpressionBuilder().createDataExpression("myLongText","java.lang.String"),
                                                ExpressionBuilder().createDataExpression("myLongText","java.lang.String"),
                                                ExpressionBuilder().createDataExpression("myLongText","java.lang.String"),
                                                ExpressionBuilder().createDataExpression("myLongText","java.lang.String"),
                                                ExpressionBuilder().createDataExpression("myLongText","java.lang.String"),
                                                ExpressionBuilder().createDataExpression("myLongText","java.lang.String"),
                                                ExpressionBuilder().createDataExpression("myLongText","java.lang.String"),
                                                ExpressionBuilder().createDataExpression("myLongText","java.lang.String"),
                                                ExpressionBuilder().createDataExpression("myLongText","java.lang.String"),
                                                ExpressionBuilder().createDataExpression("myLongText","java.lang.String"),
                                                ExpressionBuilder().createDataExpression("myLongText","java.lang.String"),
                                                ExpressionBuilder().createDataExpression("myLongText","java.lang.String"),
                                                ExpressionBuilder().createDataExpression("myLongText","java.lang.String"),
                                                ExpressionBuilder().createDataExpression("myLongText","java.lang.String"),
                                                ExpressionBuilder().createDataExpression("myLongText","java.lang.String"),
                                                ExpressionBuilder().createDataExpression("myLongText","java.lang.String"),
                                                ExpressionBuilder().createDataExpression("myLongText","java.lang.String"),
                                                ExpressionBuilder().createDataExpression("myLongText","java.lang.String"),
                                                ExpressionBuilder().createDataExpression("myLongText","java.lang.String"),
                                                ExpressionBuilder().createDataExpression("myLongText","java.lang.String"),
                                                ExpressionBuilder().createDataExpression("myLongText","java.lang.String"),
                                                ExpressionBuilder().createDataExpression("myLongText","java.lang.String"),
                                                ExpressionBuilder().createDataExpression("myLongText","java.lang.String"),
                                                ExpressionBuilder().createDataExpression("myLongText","java.lang.String"),
                                                ExpressionBuilder().createDataExpression("myLongText","java.lang.String"),
                                                ExpressionBuilder().createDataExpression("myLongText","java.lang.String"),
                                                ExpressionBuilder().createDataExpression("myLongText","java.lang.String"),
                                                ExpressionBuilder().createDataExpression("myLongText","java.lang.String"),
                                                ExpressionBuilder().createDataExpression("myLongText","java.lang.String"),
                                                ExpressionBuilder().createDataExpression("myLongText","java.lang.String"),
                                                ExpressionBuilder().createDataExpression("myLongText","java.lang.String"),
                                                ExpressionBuilder().createDataExpression("myLongText","java.lang.String")
                                        )
                                ))
                            }
                        }
                        addTransition("start", "auto1")
                        addTransition("start", "user1")
                        addTransition("user1", "auto2")
                    }

    override fun withResources(bar: BusinessArchiveBuilder) {
        bar.apply {
            actorMapping = ActorMapping().apply {
                addActor(Actor("theActor").apply {
                    addUser("jean.némar")
                })
            }
            addClasspathResource(BarResource("jar$number.jar", ByteArray(2 * 1000 * 1000).apply { Random().nextBytes(this) }))
        }
    }

    override fun accept(client: APIClient) {
        super.accept(client)
        val deployed = client.processAPI.getProcessDefinitionId(name, version)
        deployed.apply { client.processAPI.startProcess(this) }
    }
}