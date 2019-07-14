/*
 * This Kotlin source file was generated by the Gradle 'init' task.
 */
package org.bonitasoft.example

import org.bonitasoft.engine.api.APIClient
import org.bonitasoft.engine.api.ApiAccessType
import org.bonitasoft.engine.api.ProfileAPI
import org.bonitasoft.engine.bpm.bar.BarResource
import org.bonitasoft.engine.bpm.bar.BusinessArchiveBuilder
import org.bonitasoft.engine.bpm.process.impl.ProcessDefinitionBuilder
import org.bonitasoft.engine.identity.User
import org.bonitasoft.engine.identity.UserCreator
import org.bonitasoft.engine.profile.ProfileMemberCreator
import org.bonitasoft.engine.search.SearchOptionsBuilder
import org.bonitasoft.engine.util.APITypeManager
import java.io.File
import java.lang.Exception
import java.util.*

class App {

    fun run(url: String) {
        APITypeManager.setAPITypeAndParams(ApiAccessType.HTTP, mapOf(
                "server.url" to url,
                "application.name" to "bonita"
        ))
        val apiClient = APIClient().apply { login("install", "install") }

        val user = apiClient.safeExec {
            identityAPI.createUser(UserCreator("walter.bates", "bpm").apply {
                setFirstName("Walter")
                setLastName("Bates")
            })
        }

        apiClient.safeExec {
            if (user != null) {
                profileAPI.addUserToProfile(user, "Administrator")
                profileAPI.addUserToProfile(user, "User")
            }
        }

        apiClient.safeExec {
            (1..150).forEach { processNumber ->
                processAPI.deployAndEnableProcess(
                        BusinessArchiveBuilder().createNewBusinessArchive().apply {
                            addClasspathResource("org/apache/commons/commons-lang3/3.5/commons-lang3-3.5.jar".toBarResource())
                            addClasspathResource("org/hibernate/hibernate-search-engine/5.10.4.Final/hibernate-search-engine-5.10.4.Final.jar".toBarResource())
                            addClasspathResource("org/eclipse/jdt/core/compiler/ecj/4.6.1/ecj-4.6.1.jar".toBarResource())
                            addClasspathResource("org/apache/lucene/lucene-analyzers-common/5.5.5/lucene-analyzers-common-5.5.5.jar".toBarResource())
                            addClasspathResource("org/apache/httpcomponents/httpclient/4.5.2/httpclient-4.5.2.jar".toBarResource())
                            addClasspathResource("org/apache/activemq/activemq-client/5.8.0/activemq-client-5.8.0.jar".toBarResource())
                            addClasspathResource("org/apache/ant/ant/1.8.2/ant-1.8.2.jar".toBarResource())
                            addClasspathResource("org/apache/commons/commons-collections4/4.0/commons-collections4-4.0.jar".toBarResource())
                            addClasspathResource("org/apache/commons/commons-compress/1.18/commons-compress-1.18.jar".toBarResource())
                            setProcessDefinition(ProcessDefinitionBuilder().createNewInstance("MyProcess$processNumber", "1,0").apply {
                                addAutomaticTask("doSomething")
                            }.done())
                        }.done()
                )
            }
        }

    }

    infix fun <T> APIClient.safeExec(executable: APIClient.() -> T): T? {
        try {
            return this.executable()
        } catch (e: Exception) {
            println("Error: ${e.javaClass.name} ${e.message}")
            return null
        }
    }

    fun ProfileAPI.getProfileByName(name: String): Long =
            searchProfiles(SearchOptionsBuilder(0, 1).filter("name", name).done()).result.first().id

    fun ProfileAPI.addUserToProfile(user: User, profileName: String) {
        createProfileMember(ProfileMemberCreator(getProfileByName(profileName)).setUserId(user.id))
    }

}

private fun String.toBarResource(): BarResource {
    val jarFile = File("/Users/baptiste/.m2/repository/$this")
    return BarResource(jarFile.name, jarFile.readBytes())
}

fun main(args: Array<String>) {
    App().run(args.getOrElse(0) { "http://localhost:8080" })

}
