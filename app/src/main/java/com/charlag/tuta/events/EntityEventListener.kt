package com.charlag.tuta.events

import android.content.Context
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import com.charlag.tuta.GroupType
import com.charlag.tuta.UserController
import com.charlag.tuta.contacts.ContactsRepository
import com.charlag.tuta.data.AppDatabase
import com.charlag.tuta.data.MailFolderCounterEntity
import com.charlag.tuta.data.toEntity
import com.charlag.tuta.di.UserBound
import com.charlag.tuta.entities.*
import com.charlag.tuta.entities.sys.*
import com.charlag.tuta.entities.tutanota.Contact
import com.charlag.tuta.entities.tutanota.Mail
import com.charlag.tuta.entities.tutanota.MailFolder
import com.charlag.tuta.mail.MailRepository
import com.charlag.tuta.network.API
import com.charlag.tuta.typemodelMap
import com.charlag.tuta.util.timestmpToGeneratedId
import io.ktor.client.features.ClientRequestException
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.io.IOException
import java.util.Date
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.reflect.KClass

class EntityEventListener @Inject constructor(
    @UserBound private val api: API,
    private val db: AppDatabase,
    private val contactsRepository: ContactsRepository,
    private val mailRepository: MailRepository,
    private val userController: UserController,
    appContext: Context
) : LifecycleObserver {
    private val lastProcessedPrefs =
        appContext.getSharedPreferences(LAST_ENTITY_UPDATE_PREF_NAME, Context.MODE_PRIVATE)
    private var connectionJob: Job? = null
    // Can replace with userScope later
    private var stopped = false
    private var startTime: Date? = null

    fun start() {
        this.startTime = Date()
        this.stopped = false

        userController.loggedInScope.launch {
            val user = userController.waitForLogin()
            // Wait for contact list to load
            contactsRepository.ignite()
            reconnect(user)
            ProcessLifecycleOwner.get().lifecycle.addObserver(this@EntityEventListener)
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    @Suppress("unused")
    fun onResume() {
        Log.d(TAG, "onResume")
        userController.loggedInScope.launch {
            if (connectionJob == null) {
                reconnect(userController.waitForLogin())
            }
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    @Suppress("unused")
    fun onPause() {
        Log.d(TAG, "onPause, had connection: ${connectionJob != null}")
        connectionJob?.cancel("cancelling webSocket for background")
        connectionJob = null
    }

    fun stop() {
        Log.d(TAG, "stop")
        connectionJob?.cancel("Stop")
        connectionJob = null
        stopped = true
    }

    private fun reconnect(user: User) {
        Log.d(TAG, "reconnect")
        if (stopped) {
            Log.d(TAG, "Stopped, don't reconnect")
            return
        }
        // Launching each time so that we don't run into recursive executing problems.
        connectionJob = userController.loggedInScope.launch {
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
                Log.d(TAG, "Exception during event processing $e", e)
                delay(TimeUnit.SECONDS.toMillis(30))
            } catch (e: CancellationException) {
                Log.d(TAG, "Connection cancelled")
                throw e
            } catch (e: Throwable) {
                Log.w(TAG, "Unexpected exeption $e")
                delay(TimeUnit.SECONDS.toMillis(90))
            }
            reconnect(user)
        }
    }

    private suspend fun processCounterUpdate(update: API.WSEvent.CounterUpdate) {
        for (counterValue in update.counterData.counterValues) {
            try {
                mailRepository.onCounterUpdated(counterValue.mailListId, counterValue.count)
            } catch (e: Throwable) {
                Log.d(TAG, "Inserting counter failed $e")
            }
        }
    }

    private fun loadMissedEvents(user: User): Flow<WebsocketEntityData> {
        return flow {
            val eventGroups = eventGroups(user)
            for (groupId in eventGroups) {
                val lastPref = lastProcessedPrefs.getString(groupId.asString(), null)
                    ?.let(::GeneratedId) ?: run {
                    val startTime = checkNotNull(this@EntityEventListener.startTime)
                    val id = timestmpToGeneratedId(startTime.time - 60_000, 0)
                    // We save it so that on the next run we don't miss updates
                    lastProcessedPrefs.edit().putString(groupId.asString(), id.asString()).apply()
                    id
                }
                val eventBatches = api.loadRangeInBetween(
                    EntityEventBatch::class,
                    groupId,
                    lastPref
                )

                for (batch in eventBatches) {
                    val batchId = batch._id.elementId.asString()
                    if (lastPref.asString() < batchId) {
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
                            Mail::class -> mailRepository
                                .onMailDeleted(GeneratedId(entityUpdate.instanceId))
                            MailFolder::class -> mailRepository
                                .onFolderDeleted(GeneratedId(entityUpdate.instanceId))
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
            is Mail -> mailRepository.onMailUpdated(downloaded)
            is MailFolder -> mailRepository.onFolderUpdated(downloaded)
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
        private const val LAST_ENTITY_UPDATE_PREF_NAME = "lastEntityUpdate"
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