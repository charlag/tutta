package com.charlag.tuta.events

import android.content.Context
import android.util.Log
import com.charlag.tuta.API
import com.charlag.tuta.GroupType
import com.charlag.tuta.LoginFacade
import com.charlag.tuta.data.AppDatabase
import com.charlag.tuta.data.toEntity
import com.charlag.tuta.entities.*
import com.charlag.tuta.entities.sys.EntityEventBatch
import com.charlag.tuta.entities.sys.EntityUpdate
import com.charlag.tuta.entities.sys.IdTuple
import com.charlag.tuta.entities.sys.User
import com.charlag.tuta.entities.tutanota.Mail
import com.charlag.tuta.entities.tutanota.MailFolder
import com.charlag.tuta.typemodelMap
import io.ktor.client.features.ClientRequestException
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlin.reflect.KClass

class EntityEventListener(
    private val loginFacade: LoginFacade,
    private val api: API,
    private val db: AppDatabase,
    appContext: Context
) {
    private val lastProcessedPrefs =
        appContext.getSharedPreferences(LAST_ENTITY_UPDATE_PREF_KEY, Context.MODE_PRIVATE)

    init {
        GlobalScope.launch {
            val user = loginFacade.waitForLogin()
            processMissedEvents(user)

            // TODO: reconnect on network error and download missed events
            // TODO: between processing missed events and connecting to ws we can miss some things
            // Should connect right away and queue them in some way.
            api.getEvents(userId = user._id.asString()).collect { event ->
                when (event) {
                    is API.WSEvent.EntityUpdate -> {
                        Log.d(TAG, "Entity update ${event.entityData}")
                        processEntityUpdate(
                            event.entityData.eventBatchOwner.asString(),
                            event.entityData.eventBatchId.asString(),
                            event.entityData.eventBatch
                        )
                        Log.d(TAG, "Entity update processed")
                    }
                    is API.WSEvent.CounterUpdate -> Log.d(TAG, "Counter update $event")
                    is API.WSEvent.Unknown -> Log.d(TAG, "Unknown update")
                }
            }
        }
    }

    private suspend fun processMissedEvents(user: User) {
        val eventGroups = eventGroups(user)
        for (groupId in eventGroups) {
            val lastPref = lastProcessedPrefs.getString(groupId.asString(), null)
            val eventBatches = api.loadAll(EntityEventBatch::class, groupId)

            for (batch in eventBatches) {
                val batchId = batch._id.elementId.asString()
                if (lastPref == null || lastPref < batchId) {
                    processEntityUpdate(groupId.asString(), batchId, batch.events)
                }
            }
        }
    }

    private suspend fun processEntityUpdate(
        groupId: String,
        batchId: String,
        entityUpdates: List<EntityUpdate>
    ) {
        for (entityUpdate in entityUpdates) {
            val typeInfo = typemodelMap[entityUpdate.type] ?: continue
            if (typeInfo.klass !in includedEntities) return

            when (entityUpdate.operation) {
                OPERATION_CREATE, OPERATION_UPDATE -> {
                    downloadAndInsert(typeInfo, entityUpdate)
                }
                OPERATION_DELETE -> {
                    when (typeInfo.klass) {
                        Mail::class -> db.mailDao().deleteMail(entityUpdate.instanceId)
                        MailFolder::class -> db.mailDao().deleteMail(entityUpdate.instanceId)
                    }
                }
            }
        }
        lastProcessedPrefs.edit().putString(groupId, batchId).apply()
    }

    private suspend fun downloadAndInsert(
        typeInfo: TypeInfo<*>,
        entityUpdate: EntityUpdate
    ) {
        @Suppress("UNCHECKED_CAST")
        val klass = typeInfo.klass as KClass<out Entity>
        val downloaded = try {
            if (typeInfo.typemodel.type == MetamodelType.LIST_ELEMENT_TYPE) {
                api.loadListElementEntity(
                    klass,
                    IdTuple(
                        GeneratedId(entityUpdate.instanceListId),
                        GeneratedId(entityUpdate.instanceId)
                    )
                )
            } else {
                api.loadElementEntity(klass, GeneratedId(entityUpdate.instanceId))
            }
        } catch (e: ClientRequestException) {
            if (e.response.status == HttpStatusCode.NotFound) {
                // pass
                return
            } else {
                throw e
            }
        }
        Log.d(TAG, "Loaded $downloaded")
        when (downloaded) {
            is Mail -> db.mailDao().insertMail(downloaded.toEntity())
            is MailFolder -> db.mailDao().insertFolder(downloaded.toEntity())
        }
        Log.d(TAG, "Inserted $downloaded")
    }

    private fun eventGroups(user: User): List<Id> {
        return user.memberships
            .filter { mship -> mship.groupType != GroupType.MailingList.value }
            .map { it.group } + user.userGroup.group
    }

    companion object {
        private const val TAG = "Event"
        private const val LAST_ENTITY_UPDATE_PREF_KEY = "lastEntityUpdate"
        private val includedEntities = setOf(
            Mail::class,
            MailFolder::class,
            User::class
        )
    }
}

const val OPERATION_CREATE = 0L
const val OPERATION_UPDATE = 1L
const val OPERATION_DELETE = 2L