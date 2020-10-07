package com.charlag.tuta

import com.charlag.tuta.entities.tutanota.Mail
import com.charlag.tuta.entities.tutanota.MailTypeInfo
import com.charlag.tuta.network.mapping.InstanceMapper
import com.charlag.tuta.network.mapping.NestedMapper
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject

// TODO: better format
class MailDb(
    private val db: Db,
    private val instanceMapper: InstanceMapper,
    private val key: ByteArray
) {
    private val json = Json { }
    private val mapper = NestedMapper()

    fun writeSingle(uid: Int, mail: Mail) {
        val jsonInstance = runBlocking {
            instanceMapper.encryptAndMapToLiteral(
                mapper.map(Mail.serializer(), mail),
                MailTypeInfo.typemodel,
                key
            )
        }
        db.write(uid, json.encodeToString(JsonElement.serializer(), jsonInstance).toBytes())
    }

    fun readSingle(uid: Int): Mail? {
        return db.readSingle(uid)?.let { decodeMail(it) }
    }

    fun readMultiple(fromUid: Int, toUid: Int?): List<Mail> {
        println("reading from $fromUid upto $toUid ")
        return db.readMultiple(fromUid, toUid).map { decodeMail(it) }.also { println("Read ${it.size} mails") }
    }

    fun count(): Int {
        return db.count()
    }

    private fun decodeMail(bytes: ByteArray): Mail {
        val jsonObject = json.parseToJsonElement(bytesToString(bytes)).jsonObject
        val map = runBlocking {
            instanceMapper.decryptAndMapToMap(jsonObject, MailTypeInfo.typemodel, key)
        }
        return mapper.unmap(Mail.serializer(), map)
    }
}