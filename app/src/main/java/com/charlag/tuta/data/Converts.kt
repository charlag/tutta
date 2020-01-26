package com.charlag.tuta.data

import androidx.room.TypeConverter
import com.charlag.tuta.base64ToBytes
import com.charlag.tuta.entities.GeneratedId
import com.charlag.tuta.entities.Id
import com.charlag.tuta.entities.sys.IdTuple
import com.charlag.tuta.toBase64
import kotlinx.serialization.internal.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlinx.serialization.serializer
import java.util.*
import kotlin.collections.HashMap

val json = Json(JsonConfiguration.Stable)

class TutanotaConverters {

    @TypeConverter
    fun mailAddressToString(mailAddress: MailAddressEntity): String {
        return json.stringify(MailAddressEntity.serializer(), mailAddress)
    }

    @TypeConverter
    fun stringToMailAddress(string: String): MailAddressEntity {
        return json.parse(MailAddressEntity.serializer(), string)
    }

    val mailAddressListSerializer = ArrayListSerializer(MailAddressEntity.serializer())

    @TypeConverter
    fun mailAddressListToString(mailAddresses: List<MailAddressEntity>): String {
        return json.stringify(mailAddressListSerializer, mailAddresses)
    }

    @TypeConverter
    fun stringToMailAddressess(string: String): List<MailAddressEntity> {
        return json.parse(mailAddressListSerializer, string)
    }

    @TypeConverter
    fun idToString(id: Id): String = id.asString()

    @TypeConverter
    fun stringToId(string: String): Id = GeneratedId(string)

    // Doing IdTuple manually as automatic converrsion fails somehow
    @TypeConverter
    fun idTupleToString(idTuple: IdTuple): String {
        return "[\"${idTuple.listId.asString()}\", \"${idTuple.elementId.asString()}\"]"
    }

    @TypeConverter
    fun stringToIdTuple(string: String): IdTuple {
        val array = json.parseJson(string).jsonArray
        return IdTuple(
            GeneratedId(array[0].primitive.content),
            GeneratedId(array[1].primitive.content)
        )
    }

    @TypeConverter
    fun idTupleListToString(idTuples: List<IdTuple>): String {
        return idTuples.joinToString(
            prefix = "[",
            separator = ",",
            postfix = "]",
            transform = this::idTupleToString
        )
    }

    @TypeConverter
    fun stringToIdTupleList(string: String): List<IdTuple> {
        return json.parseJson(string).jsonArray
            .map { jsontuple ->
                IdTuple(
                    GeneratedId(jsontuple.jsonArray[0].primitive.content),
                    GeneratedId(jsontuple.jsonArray[1].primitive.content)
                )
            }
    }

    private val ivsSerializer = HashMapSerializer(String.serializer(), String.serializer().nullable)

    @TypeConverter
    fun finalIvsToString(ivs: Map<String, ByteArray?>?): String? {
        ivs ?: return null
        return ivs.mapValuesTo(HashMap()) { (k, v) -> v?.toBase64() }
            .let { json.stringify(ivsSerializer, it) }
    }

    @TypeConverter
    fun stringToFinalIvs(string: String?): Map<String, ByteArray?>? {
        string ?: return null
        return json.parse(ivsSerializer, string).mapValues { (k, v) -> v?.let(::base64ToBytes) }
    }
}

class DateConverter {
    @TypeConverter
    fun dateToLong(date: Date?): Long = date?.time ?: -1

    @TypeConverter
    fun longToDate(long: Long): Date? = if (long == -1L) null else Date(long)
}