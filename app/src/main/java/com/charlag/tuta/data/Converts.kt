package com.charlag.tuta.data

import androidx.room.TypeConverter
import com.charlag.tuta.entities.GeneratedId
import com.charlag.tuta.entities.Id
import com.charlag.tuta.entities.sys.IdTuple
import kotlinx.serialization.internal.ArrayListSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import java.util.*

class TutanotaConverters {

    val json  = Json(JsonConfiguration.Stable)

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
    fun stringToIdTuple(string: String): IdTuple  {
        val array = json.parseJson(string).jsonArray
        return IdTuple(GeneratedId(array[0].primitive.content),
            GeneratedId(array[1].primitive.content))
    }
}

class DateConverter {
    @TypeConverter
    fun dateToLong(date: Date): Long = date.time

    @TypeConverter
    fun longToDate(long: Long): Date = Date(long)
}