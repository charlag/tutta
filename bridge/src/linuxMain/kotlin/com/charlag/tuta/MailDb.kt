package com.charlag.tuta

import com.charlag.tuta.entities.Entity
import com.charlag.tuta.entities.IdTuple
import com.charlag.tuta.entities.TypeInfo
import com.charlag.tuta.entities.tutanota.Mail
import com.charlag.tuta.entities.tutanota.MailFolder
import com.charlag.tuta.entities.tutanota.MailFolderTypeInfo
import com.charlag.tuta.entities.tutanota.MailTypeInfo
import com.charlag.tuta.imap.getId
import com.charlag.tuta.network.mapping.InstanceMapper
import com.charlag.tuta.network.mapping.NestedMapper
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject

// TODO: better format
class MailDb(
    private val sqliteDb: SqliteDb,
    private val instanceMapper: InstanceMapper,
    private val key: ByteArray
) {
    private val json = Json { }
    private val mapper = NestedMapper()

    init {
        sqliteDb.exec(
            """CREATE TABLE IF NOT EXISTS Mail(
|uid INTEGER PRIMARY KEY, 
|elementId TEXT NOT NULL, 
|listId TEXT NOT NULL, 
|data BLOB NOT NULL
|);""".trimMargin()
        )
        // Actually primary key should probably be idTuple
        sqliteDb.exec(
            """CREATE TABLE IF NOT EXISTS Folder(
|elementId TEXT PRIMARY KEY, 
|listId TEXT, 
|mailListId TEXT NOT NULL,
|data BYTES NOT NULL
|);""".trimMargin()
        )
    }

    fun writeFolders(folders: List<MailFolder>) {
        for (folder in folders) {
            writeFolder(folder)
        }
    }

    fun writeFolder(folder: MailFolder) {
        sqliteDb.insert(
            "INSERT OR REPLACE INTO Folder(elementId, listId, mailListId, data) VALUES (?, ?, ?, ?)",
            folder._id!!.elementId.asString(),
            folder._id!!.listId.asString(),
            folder.mails.asString(),
            folder.serialize(MailFolderTypeInfo)
        )
    }

    fun readFolders(): List<MailFolder> {
        return sqliteDb.queryMultiple("SELECT data FROM Folder") {
            deserialize(MailFolderTypeInfo, readBlob(0))
        }
    }

    fun writeSingle(uid: Int, mail: Mail) {
        return sqliteDb.insert(
            "INSERT OR REPLACE INTO Mail(uid, elementId, listId, data) VALUES (?, ?, ?, ?)",
            uid,
            mail.getId().elementId.asString(),
            mail.getId().listId.asString(),
            mail.serialize(MailTypeInfo.copy())
        )
    }

    fun readSingle(mailListId: String, uid: Int): Mail? {
        return sqliteDb.querySingle(
            "SELECT data FROM Mail WHERE uid = ? AND listId = ? LIMIT 1",
            uid,
            mailListId
        ) {
            deserialize(MailTypeInfo, readBlob(0))
        }
    }

    fun readMultiple(mailListId: String, fromUid: Int, toUid: Int?): List<Mail> {
        println("reading from $fromUid upto $toUid in $mailListId ")
        return if (toUid == null) {
            sqliteDb.queryMultiple(
                "SELECT data FROM Mail WHERE UID >= ? AND listId = ?",
                fromUid,
                mailListId
            ) {
                deserialize(MailTypeInfo, readBlob(0))
            }
        } else {
            sqliteDb.queryMultiple(
                "SELECT data FROM Mail WHERE uid >= ? AND uid <= ? AND listId = ?",
                fromUid,
                toUid,
                mailListId
            ) {
                deserialize(MailTypeInfo, readBlob(0))
            }
        }
    }

    fun count(mailListId: String): Int {
        return sqliteDb.querySingle(
            "SELECT COUNT (uid) FROM Mail WHERE listId = ?",
            mailListId
        ) {
            readInt(0)
        }!!
    }

    fun deleteMail(id: IdTuple) {
        sqliteDb.exec("DELETE FROM Mail WHERE listId = '${id.listId.asString()}' AND elementId = '${id.elementId.asString()}'")
    }

    fun deleteFolder(id: IdTuple) {
        sqliteDb.exec("DELETE FROM Folder WHERE listId = '${id.listId.asString()}' AND elementId = '${id.elementId.asString()}'")
    }

    private inline fun <reified T : Entity> T.serialize(typeInfo: TypeInfo<T>): ByteArray {
        val jsonInstance = runBlocking {
            instanceMapper.encryptAndMapToLiteral(
                mapper.map(typeInfo.serializer, this@serialize),
                typeInfo.typemodel,
                key
            )
        }
        return json.encodeToString(JsonElement.serializer(), jsonInstance).toBytes()
    }

    private inline fun <reified T : Entity> deserialize(
        typeInfo: TypeInfo<T>,
        bytes: ByteArray
    ): T {
        val jsonObject = json.parseToJsonElement(bytesToString(bytes)).jsonObject
        val map = runBlocking {
            instanceMapper.decryptAndMapToMap(jsonObject, typeInfo.typemodel, key)
        }
        return mapper.unmap(typeInfo.serializer, map)
    }
}