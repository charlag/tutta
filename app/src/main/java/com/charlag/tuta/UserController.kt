package com.charlag.tuta

import com.charlag.tuta.di.UserBound
import com.charlag.tuta.entities.Id
import com.charlag.tuta.entities.sys.GroupInfo
import com.charlag.tuta.entities.sys.User
import com.charlag.tuta.entities.tutanota.TutanotaProperties
import com.charlag.tuta.network.API
import com.charlag.tuta.network.SessionData
import com.charlag.tuta.util.AsyncProvider
import com.charlag.tuta.util.lazyAsync
import kotlinx.coroutines.*

interface UserController {
    val userId: Id
    suspend fun waitForLogin(): User
    suspend fun getUserGroupInfo(): GroupInfo
    suspend fun getProps(): TutanotaProperties

    val loggedInScope: CoroutineScope
}

class RealUserController(
    override val userId: Id,
    @UserBound
    private val api: API,
    private val deferred: Deferred<SessionData>
) : UserController {
    override suspend fun waitForLogin(): User = deferred.await().user

    override suspend fun getUserGroupInfo(): GroupInfo = userGroupInfo()

    override suspend fun getProps(): TutanotaProperties = props()

    private val userGroupInfo = lazyAsync {
        waitForLogin()
        api.loadListElementEntity<GroupInfo>(waitForLogin().userGroup.groupInfo)
    }

    private val props: AsyncProvider<TutanotaProperties> = lazyAsync {
        waitForLogin()
        api.loadRoot(
            TutanotaProperties::class,
            waitForLogin().userGroup.group
        )
    }

    override val loggedInScope: CoroutineScope = CoroutineScope(SupervisorJob())
}