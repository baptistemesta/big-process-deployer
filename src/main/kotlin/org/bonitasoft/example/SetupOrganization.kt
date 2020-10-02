/*
 * Copyright 2020 Bonitasoft S.A.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.bonitasoft.example

import org.bonitasoft.engine.api.APIClient
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