package com.charlag.tuta

import com.charlag.tuta.entities.sys.GroupInfo
import com.charlag.tuta.entities.sys.User
import com.charlag.tuta.entities.tutanota.TutanotaProperties
import com.charlag.tuta.network.API
import com.charlag.tuta.util.AsyncProvider
import com.charlag.tuta.util.lazyAsync

class UserController(
    private val api: API,
    private val loginFacade: LoginFacade
) {
    suspend fun waitForLogin(): User {
        return loginFacade.waitForLogin()
    }

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
}