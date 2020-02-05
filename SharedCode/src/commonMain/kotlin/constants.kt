package com.charlag.tuta

enum class PermissionType(val value: Long) {
    Public(0),
    Symmetric(1),
    PublicSymemtric(2),
    UnEncrypted(3),
    External(5),
    OwnerList(8)
}

enum class BucketPermissionType(val value: Long) {
    Public(2),
    External(3)
}

enum class GroupType(val value: Long) {
    User(0),
    Admin(1),
    MailingList(2),
    Customer(3),
    External(4),
    Mail(5),
    Contact(6),
    File(7),
    LocalAdmin(8),
    Calendar(9);

    fun getByValue(value: Long): GroupType {
        return when (value) {
            0L -> User
            1L -> Admin
            2L -> MailingList
            3L -> Customer
            4L -> External
            5L -> Mail
            6L -> Contact
            7L -> File
            8L -> LocalAdmin
            9L -> Calendar
            else -> throw IllegalArgumentException("No GroupType for $value")
        }
    }
}

enum class ConversationType(val value: Long) {
    NEW(0),
    REPLY(1),
    FORWARD(2);

    companion object {
        fun fromRaw(raw: Long): ConversationType {
            return when (raw) {
                NEW.value -> NEW
                REPLY.value -> REPLY
                FORWARD.value -> FORWARD
                else -> throw IllegalArgumentException("Unknown conversation type $raw")
            }
        }
    }
}

enum class ReplyType(val raw: Long) {
    NONE(0),
    REPLY(1),
    FORWARD(2),
    REPLY_FORWARD(3);

    companion object {
        fun fromRaw(raw: Long): ReplyType {
            return when (raw) {
                NONE.raw -> NONE
                REPLY.raw -> REPLY
                FORWARD.raw -> FORWARD
                REPLY_FORWARD.raw -> REPLY_FORWARD
                else -> throw IllegalArgumentException("Unknown conversation type $raw")
            }
        }
    }
}

enum class MailFolderType(val value: Long) {
    CUSTOM(0),
    INBOX(1),
    SENT(2),
    TRASH(3),
    ARCHIVE(4),
    SPAM(5),
    DRAFT(6)
}