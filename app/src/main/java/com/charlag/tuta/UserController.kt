package com.charlag.tuta

import com.charlag.tuta.entities.sys.Group
import com.charlag.tuta.entities.sys.GroupInfo
import com.charlag.tuta.entities.tutanota.TutanotaProperties
import com.charlag.tuta.network.API

class UserController(
    private val api: API,
    private val loginFacade: LoginFacade,
    private val mailFacade: MailFacade
) {
    private var props: TutanotaProperties? = null
    suspend fun getProps(): TutanotaProperties {
        return props
            ?: api.loadRoot(
                TutanotaProperties::class,
                loginFacade.waitForLogin().userGroup.group
            ).also {
                this.props = it
            }
    }

    private var enabledMailAddresses: List<String>? = null
    suspend fun getEnabledMailAddresses(): List<String> {
        val user = loginFacade.waitForLogin()
        val userGroupInfo = api.loadListElementEntity<GroupInfo>(user.userGroup.groupInfo)
        val mailGroupMembership =
            user.memberships.first { it.groupType == GroupType.Mail.value }
        val mailGroup = api.loadElementEntity<Group>(mailGroupMembership.group)
        return mailFacade.getEnabledMailAddresses(
            user,
            userGroupInfo,
            mailGroup
        ).also {
            this.enabledMailAddresses = it
        }
    }
}