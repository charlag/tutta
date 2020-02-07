package com.charlag.tuta

import com.charlag.tuta.entities.sys.Group
import com.charlag.tuta.entities.sys.GroupInfo
import com.charlag.tuta.entities.tutanota.TutanotaProperties
import com.charlag.tuta.network.API
import com.charlag.tuta.util.AsyncProvider
import com.charlag.tuta.util.lazyAsync

class UserController(
    private val api: API,
    private val loginFacade: LoginFacade,
    private val mailFacade: MailFacade
) {
    val getUserGroupInfo = lazyAsync {
        val user = loginFacade.waitForLogin()
        api.loadListElementEntity<GroupInfo>(user.userGroup.groupInfo)
    }

    val getProps: AsyncProvider<TutanotaProperties> = lazyAsync {
        api.loadRoot(
            TutanotaProperties::class,
            loginFacade.waitForLogin().userGroup.group
        )
    }

    val getEnabledMailAddresses: AsyncProvider<List<String>> = lazyAsync {
        val user = loginFacade.waitForLogin()
        val mailGroupMembership =
            user.memberships.first { it.groupType == GroupType.Mail.value }
        val mailGroup = api.loadElementEntity<Group>(mailGroupMembership.group)
        mailFacade.getEnabledMailAddresses(
            user,
            getUserGroupInfo(),
            mailGroup
        )
    }
}