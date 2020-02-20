package org.bonitasoft.example

import com.bonitasoft.engine.api.APIClient
import org.bonitasoft.engine.identity.GroupCreator
import org.bonitasoft.engine.identity.UserCreator
import java.util.function.Consumer

class SetupOrganization : Consumer<APIClient> {

    companion object SetupOragnization {
        val users = listOf(
                "john.kelly",
                "jane.durand",
                "oliv.dupont",
                "james.kain",
                "jean.némar"
        )
    }
    override fun accept(apiClient: APIClient) {

        apiClient.safeExec {
            identityAPI.createGroup(GroupCreator("ACME"))
        }
        apiClient.safeExec {
            identityAPI.createRole("member")
        }
        val user = apiClient.safeExec {
            identityAPI.createUser(UserCreator("walter.bates", "bpm").apply {
                setFirstName("Walter")
                setLastName("Bates")
            })
        }
        users.map {
            apiClient.safeExec {
                identityAPI.createUser(UserCreator(it, "bpm").apply {
                    setFirstName(it.split(".").first())
                    setLastName(it.split(".").last())
                })
            }
        }.forEach {
            apiClient.safeExec {
                if (it != null) {
                    profileAPI.addUserToProfile(it, "User")
                    identityAPI.addUserMembership(it.id, identityAPI.getGroupByPath("/ACME").id, identityAPI.getRoleByName("member").id)
                }
            }
        }

        apiClient.safeExec {
            if (user != null) {
                profileAPI.addUserToProfile(user, "Administrator")
                profileAPI.addUserToProfile(user, "User")
                identityAPI.addUserMembership(user.id, identityAPI.getGroupByPath("/ACME").id, identityAPI.getRoleByName("member").id)
            }
        }
    }


}