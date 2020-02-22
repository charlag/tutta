package com.charlag.tuta.data

import androidx.room.*
import com.charlag.tuta.entities.Id
import com.charlag.tuta.entities.IdTuple
import com.charlag.tuta.entities.tutanota.*
import kotlinx.serialization.internal.ArrayListSerializer
import java.util.*

@Entity
@TypeConverters(ContactEntity.ContactConverters::class)
data class ContactEntity(
    @PrimaryKey
    val id: String,
    val listId: String,
    val _owner: Id,
    val _ownerEncSessionKey: ByteArray?,
    val _ownerGroup: Id?,
    val _permissions: Id,
    val autoTransmitPassword: String,
    val comment: String,
    val company: String,
    val firstName: String,
    val lastName: String,
    val nickname: String?,
    val oldBirthday: Date?,
    val presharedPassword: String?,
    val role: String,
    val title: String?,
    val addresses: List<ContactAddress>,
    @Embedded(prefix = "birthday")
    val birthday: Birthday?,
    val mailAddresses: List<ContactMailAddress>,
    val phoneNumbers: List<ContactPhoneNumber>,
    val socialIds: List<ContactSocialId>,
    @Embedded(prefix = "photo")
    val photo: IdTuple?
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ContactEntity

        if (id != other.id) return false
        if (listId != other.listId) return false
        if (_owner != other._owner) return false
        if (_ownerGroup != other._ownerGroup) return false
        if (_permissions != other._permissions) return false
        if (autoTransmitPassword != other.autoTransmitPassword) return false
        if (comment != other.comment) return false
        if (company != other.company) return false
        if (firstName != other.firstName) return false
        if (lastName != other.lastName) return false
        if (nickname != other.nickname) return false
        if (oldBirthday != other.oldBirthday) return false
        if (presharedPassword != other.presharedPassword) return false
        if (role != other.role) return false
        if (title != other.title) return false
        if (addresses != other.addresses) return false
        if (birthday != other.birthday) return false
        if (mailAddresses != other.mailAddresses) return false
        if (phoneNumbers != other.phoneNumbers) return false
        if (socialIds != other.socialIds) return false
        if (photo != other.photo) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + listId.hashCode()
        result = 31 * result + _owner.hashCode()
        result = 31 * result + (_ownerGroup?.hashCode() ?: 0)
        result = 31 * result + _permissions.hashCode()
        result = 31 * result + autoTransmitPassword.hashCode()
        result = 31 * result + comment.hashCode()
        result = 31 * result + company.hashCode()
        result = 31 * result + firstName.hashCode()
        result = 31 * result + lastName.hashCode()
        result = 31 * result + (nickname?.hashCode() ?: 0)
        result = 31 * result + (oldBirthday?.hashCode() ?: 0)
        result = 31 * result + (presharedPassword?.hashCode() ?: 0)
        result = 31 * result + role.hashCode()
        result = 31 * result + (title?.hashCode() ?: 0)
        result = 31 * result + addresses.hashCode()
        result = 31 * result + (birthday?.hashCode() ?: 0)
        result = 31 * result + mailAddresses.hashCode()
        result = 31 * result + phoneNumbers.hashCode()
        result = 31 * result + socialIds.hashCode()
        result = 31 * result + (photo?.hashCode() ?: 0)
        return result
    }

    class ContactConverters {
        private val addressesSerializer = ArrayListSerializer(ContactAddress.serializer())
        @TypeConverter
        fun contactAddressesToJson(addresses: List<ContactAddress>): String =
            json.stringify(addressesSerializer, addresses)

        @TypeConverter
        fun jsonToContactAddresses(jsonString: String): List<ContactAddress> =
            json.parse(addressesSerializer, jsonString)

        private val mailAddressesSerializer = ArrayListSerializer(ContactMailAddress.serializer())
        @TypeConverter
        fun contactMailAddressesToJson(addresses: List<ContactMailAddress>): String =
            json.stringify(mailAddressesSerializer, addresses)

        @TypeConverter
        fun jsonToContactMailAddresses(jsonString: String): List<ContactMailAddress> =
            json.parse(mailAddressesSerializer, jsonString)

        private val phoneNumberSerializer = ArrayListSerializer(ContactPhoneNumber.serializer())
        @TypeConverter
        fun phoneNumbersToJson(addresses: List<ContactPhoneNumber>): String =
            json.stringify(phoneNumberSerializer, addresses)

        @TypeConverter
        fun jsonToPhoneNumbers(jsonString: String): List<ContactPhoneNumber> =
            json.parse(phoneNumberSerializer, jsonString)

        private val socialIdSerializer = ArrayListSerializer(ContactSocialId.serializer())
        @TypeConverter
        fun socialIdsToJson(addresses: List<ContactSocialId>): String =
            json.stringify(socialIdSerializer, addresses)

        @TypeConverter
        fun jsonToSocialIds(jsonString: String): List<ContactSocialId> =
            json.parse(socialIdSerializer, jsonString)
    }
}