package com.charlag.tuta.notifications

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.util.Log
import com.charlag.tuta.*
import com.charlag.tuta.di.SSEPath
import com.charlag.tuta.di.UserBound
import com.charlag.tuta.entities.Date
import com.charlag.tuta.entities.Id
import com.charlag.tuta.entities.sys.PushIdentifier
import com.charlag.tuta.entities.sys.User
import com.charlag.tuta.network.API
import com.charlag.tuta.network.SessionDataProvider
import com.charlag.tuta.notifications.push.PushNotificationService
import com.charlag.tuta.notifications.push.SseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class PushNotificationsManager @Inject constructor(
    private val sseStorage: SseStorage,
    private val context: Context,
    @UserBound private val api: API,
    private val cryptor: Cryptor,
    private val userController: UserController,
    @UserBound private val sessionDataProvider: SessionDataProvider,
    @SSEPath private val ssePath: String
) {
    fun register() {
        userController.loggedInScope.launch(Dispatchers.Default) {
            try {
                val user = userController.waitForLogin()
                val listId = user.pushIdentifierList!!.list
                val deviceIdentifier = sseStorage.pushIdentifier

                if (deviceIdentifier == null) {
                    val newDeviceIdentifier = cryptor.generateRandomData(32)
                        .toBase64()
                    val (sessionKey, id) = createPushIdentifier(
                        user,
                        newDeviceIdentifier,
                        listId
                    )
                    storeInfoLocally(user, id, sessionKey, newDeviceIdentifier)
                } else {
                    val pushIdentifier = loadRemotePushIdentifier(listId, deviceIdentifier)
                    if (pushIdentifier == null) {
                        val (sessionKey, id) = createPushIdentifier(
                            user,
                            deviceIdentifier,
                            listId
                        )
                        storeInfoLocally(user, id, sessionKey, deviceIdentifier)
                    }
                }

                scheduleJob()
            } catch (e: Throwable) {
                Log.e("PushManager", "Error during register", e)
            }
        }
    }

    private suspend fun loadRemotePushIdentifier(
        listId: Id,
        deviceIdentifier: String
    ): PushIdentifier? {
        return api.loadAll(PushIdentifier::class, listId)
            .find { it.identifier == deviceIdentifier }
    }

    suspend fun deletePushIdentifier() {
        val user = userController.waitForLogin()
        val listId = user.pushIdentifierList!!.list
        val deviceIdentifier = withContext(Dispatchers.IO) {
            sseStorage.pushIdentifier
        } ?: return
        val pushIdentifierId = loadRemotePushIdentifier(listId, deviceIdentifier)?._id ?: return
        api.deleteListElementEntity(PushIdentifier::class, pushIdentifierId)
    }

    private fun storeInfoLocally(
        user: User,
        id: Id,
        sessionKey: ByteArray,
        deviceIdentifier: String
    ) {
        sseStorage.storePushIdentifierSessionKey(
            user._id!!.asString(),
            id.asString(),
            sessionKey.toBase64()
        )
        sseStorage.storePushIdentifier(deviceIdentifier, ssePath)
    }

    private suspend fun createPushIdentifier(
        user: User,
        newDeviceIdentifier: String,
        listId: Id
    ): Pair<ByteArray, Id> {
        val sessionKey = cryptor.aes128RandomKey()
        val userGroupKey =
            sessionDataProvider.getGroupKey(user.userGroup.group.asString())
        val pushIdentifierTemplate = PushIdentifier(
            _owner = userController.getUserGroupInfo().group,
            _ownerGroup = userController.getUserGroupInfo().group,
            _ownerEncSessionKey = cryptor.encryptKey(
                sessionKey,
                userGroupKey!!
            ),
            pushServiceType = PushServiceType.SSE.raw,
            displayName = "Multiplatform Android app",
            identifier = newDeviceIdentifier,
            language = "en", // not used for SSE anymore
            disabled = false,
            lastNotificationDate = null,
            lastUsageTime = Date(System.currentTimeMillis())
        )
        val id = api.createListElementEntity(
            listId,
            pushIdentifierTemplate
        )!!
        return sessionKey to id
    }

    private fun scheduleJob() {
        val jobScheduler =
            context.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
        val jobInfo =
            JobInfo.Builder(1, ComponentName(context, PushNotificationService::class.java))
                .setPeriodic(TimeUnit.MINUTES.toMillis(15))
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setPersisted(true)
                .build()

        jobScheduler.schedule(jobInfo)
    }
}