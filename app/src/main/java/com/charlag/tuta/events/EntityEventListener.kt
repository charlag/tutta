package com.charlag.tuta.events

import android.util.Log
import com.charlag.tuta.API
import com.charlag.tuta.LoginFacade
import com.charlag.tuta.data.AppDatabase
import com.charlag.tuta.entities.Entity
import com.charlag.tuta.entities.GeneratedId
import com.charlag.tuta.entities.MetamodelType
import com.charlag.tuta.entities.TypeInfo
import com.charlag.tuta.entities.sys.EntityUpdate
import com.charlag.tuta.entities.sys.IdTuple
import com.charlag.tuta.entities.sys.User
import com.charlag.tuta.entities.sys.WebsocketEntityData
import com.charlag.tuta.entities.tutanota.Mail
import com.charlag.tuta.entities.tutanota.MailFolder
import com.charlag.tuta.mail.toEntity
import com.charlag.tuta.typemodelMap
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlin.reflect.KClass

class EntityEventListener(
    private val loginFacade: LoginFacade,
    private val api: API,
    private val db: () -> AppDatabase
) {
    init {
        GlobalScope.launch {
            val user = loginFacade.waitForLogin()
            api.getEvents(userId = user._id.asString()).collect { event ->
                when (event) {
                    is API.WSEvent.EntityUpdate -> {
                        Log.d(TAG, "Entity update ${event.entityData}")
                        processEntityUpdate(event.entityData)
                        Log.d(TAG, "Entity update processed")
                    }
                    is API.WSEvent.CounterUpdate -> Log.d(TAG, "Counter update $event")
                    is API.WSEvent.Unknown -> Log.d(TAG, "Unknown update")
                }
            }
        }
    }

    val includedEntities = setOf(
        Mail::class,
        MailFolder::class,
        User::class
    )

    private suspend fun processEntityUpdate(entityData: WebsocketEntityData) {
        for (entityUpdate in entityData.eventBatch) {
            val typeInfo = typemodelMap[entityUpdate.type] ?: continue
            if (typeInfo.klass !in includedEntities) return

            when (entityUpdate.operation) {
                OPERATION_CREATE, OPERATION_UPDATE -> {
                    downloadAndInsert(typeInfo, entityUpdate)
                }
                OPERATION_DELETE -> {
                    when (typeInfo.klass) {
                        Mail::class -> db().mailDao().deleteMail(entityUpdate.instanceId)
                        MailFolder::class -> db().mailDao().deleteMail(entityUpdate.instanceId)
                    }
                }
            }
        }
    }

    private suspend fun downloadAndInsert(
        typeInfo: TypeInfo<*>,
        entityUpdate: EntityUpdate
    ) {
        @Suppress("UNCHECKED_CAST")
        val klass = typeInfo.klass as KClass<out Entity>
        val downloaded = if (typeInfo.typemodel.type == MetamodelType.LIST_ELEMENT_TYPE) {
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
        Log.d(TAG, "Loaded $downloaded")
        when (downloaded) {
            is Mail -> db().mailDao().insertMail(downloaded.toEntity())
            is MailFolder -> db().mailDao().insertFolder(downloaded.toEntity())
        }
        Log.d(TAG, "Inserted $downloaded")
    }

    companion object {
        private const val TAG = "Event"
    }
}

const val OPERATION_CREATE = 0L
const val OPERATION_UPDATE = 1L
const val OPERATION_DELETE = 2L