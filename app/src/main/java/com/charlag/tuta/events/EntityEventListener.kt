package com.charlag.tuta.events

import android.content.Context
import android.util.Log
import com.charlag.tuta.network.API
import com.charlag.tuta.GroupType
import com.charlag.tuta.LoginFacade
import com.charlag.tuta.contacts.ContactsRepository
import com.charlag.tuta.data.AppDatabase
import com.charlag.tuta.data.MailFolderCounterEntity
import com.charlag.tuta.data.toEntity
import com.charlag.tuta.entities.*
import com.charlag.tuta.entities.sys.*
import com.charlag.tuta.entities.tutanota.Contact
import com.charlag.tuta.entities.tutanota.Mail
import com.charlag.tuta.entities.tutanota.MailFolder
import com.charlag.tuta.typemodelMap
import io.ktor.client.features.ClientRequestException
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.io.IOException
import java.lang.Exception
import java.util.concurrent.TimeUnit
import kotlin.reflect.KClass

class EntityEventListener(
    private val loginFacade: LoginFacade,
    private val api: API,
    private val db: AppDatabase,
    private val contactsRepository: ContactsRepository,
    appContext: Context
) {
    private val lastProcessedPrefs =
        appContext.getSharedPreferences(LAST_ENTITY_UPDATE_PREF_KEY, Context.MODE_PRIVATE)

    init {
        GlobalScope.launch {
            val user = loginFacade.waitForLogin()
            // Wait for contact list to load
            contactsRepository.ignite()
            reconnect(user)
        }
    }

    private fun reconnect(user: User) {
        // Launching each time so that we don't run into recursive executing problems.
        GlobalScope.launch {
            // Start consuming events right away so that we don't miss any
            val realtimeEvents =
                api.getEvents(userId = user._id.asString()).consumeAsFlow()
                    .onEach { update ->
                        if (update is API.WSEvent.CounterUpdate) {
                            processCounterUpdate(update)
                        }
                    }
                    .filterIsInstance<API.WSEvent.EntityUpdate>()
            try {
                loadMissedEvents(user)
                    .catch { e ->
                        Log.w(TAG, "Error during loading missed events", e)
                        throw e
                    }
                    .onCompletion {
                        emitAll(realtimeEvents.map { (entityData) ->
                            WebsocketEntityData(
                                entityData.eventBatchId,
                                entityData.eventBatchOwner,
                                entityData.eventBatch
                            )
                        })
                    }.collect { event ->
                        processEntityBatchUpdate(
                            event.eventBatchOwner.asString(),
                            event.eventBatchId.asString(),
                            event.eventBatch
                        )
                    }
            } catch (e: IOException) {
                Log.d(TAG, "Exception during event processing", e)
                delay(TimeUnit.SECONDS.toMillis(30))
            } catch (e: Exception) {
                Log.w(TAG, "Unexpected exeption $e")
                delay(TimeUnit.SECONDS.toMillis(90))
            }
            reconnect(user)
        }
    }

    private suspend fun processCounterUpdate(update: API.WSEvent.CounterUpdate) {
        for (counterValue in update.counterData.counterValues) {
            try {
                db.mailDao().insertFolderCounter(
                    MailFolderCounterEntity(
                        counterValue.mailListId.asString(),
                        counterValue.count
                    )
                )
            } catch (e: Exception) {
                Log.d(TAG, "Inserting counter failed $e")
            }
        }
    }

    private fun loadMissedEvents(user: User): Flow<WebsocketEntityData> {
        return flow {
            val eventGroups = eventGroups(user)
            for (groupId in eventGroups) {
                val lastPref = lastProcessedPrefs.getString(groupId.asString(), null)
                val eventBatches = api.loadRangeInBetween(
                    EntityEventBatch::class,
                    groupId,
                    lastPref?.let(::GeneratedId) ?: GENERATED_MIN_ID,
                    GENERATED_MAX_ID
                )

                for (batch in eventBatches) {
                    val batchId = batch._id.elementId.asString()
                    if (lastPref == null || lastPref < batchId) {
                        emit(WebsocketEntityData(GeneratedId(batchId), groupId, batch.events))
                    }
                }
            }
        }
    }

    private suspend fun processEntityBatchUpdate(
        groupId: String,
        batchId: String,
        entityUpdates: List<EntityUpdate>
    ) {
        withContext(Dispatchers.Default) {
            val lastProcessedForGroup = lastProcessedPrefs.getString(groupId, null)
            if (lastProcessedForGroup != null && batchId <= lastProcessedForGroup) {
                // Already processed
                return@withContext
            }
            for (entityUpdate in entityUpdates) {
                val typeInfo = typemodelMap[entityUpdate.type] ?: continue
                if (typeInfo.klass !in includedEntities) continue

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
    }

    private suspend fun <T : Entity> downloadAndInsert(
        typeInfo: TypeInfo<T>,
        entityUpdate: EntityUpdate
    ) {
        val klass = typeInfo.klass
        val downloaded = try {
            if (typeInfo.typemodel.type == MetamodelType.LIST_ELEMENT_TYPE) {
                @Suppress("UNCHECKED_CAST")
                api.loadListElementEntity(
                    klass as KClass<out ListElementEntity>,
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
        when (downloaded) {
            is Mail -> db.mailDao().insertMail(downloaded.toEntity())
            is MailFolder -> db.mailDao().insertFolder(downloaded.toEntity())
            is Contact -> db.contactDao().insertContact(downloaded.toEntity())
        }
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