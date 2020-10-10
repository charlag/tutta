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

enum class MailMethod(val value: Long) {
    NONE(0),
    ICAL_PUBLISH(1),
    ICAL_REQUEST(2),
    ICAL_REPLY(3),
    ICAL_ADD(4),
    ICAL_CANCEL(5),
    ICAL_REFRESH(6),
    ICAL_COUNTER(7),
    ICAL_DECLINECOUNTER(8),
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
    DRAFT(6);

    companion object {
        fun fromRaw(raw: Long): MailFolderType {
            return when (raw) {
                0L -> CUSTOM
                1L -> INBOX
                2L -> SENT
                3L -> TRASH
                4L -> ARCHIVE
                5L -> SPAM
                6L -> DRAFT
                else -> throw IllegalArgumentException("Unknown MailFolderType: $raw")
            }
        }
    }
}

enum class InboxRuleType(val raw: String) {
    FROM_EQUALS("0"),
    RECIPIENT_TO_EQUALS("1"),
    RECIPIENT_CC_EQUALS("2"),
    RECIPIENT_BCC_EQUALS("3"),
    SUBJECT_CONTAINS("4"),
    MAIL_HEADER_CONTAINS("5")
}

enum class ContactAddressType(val raw: Long) {
    PRIVATE(0),
    WORK(1),
    OTHER(2),
    CUSTOM(3);
}

enum class SecondFactorType(val raw: Long) {
    U2F(0),
    TOTP(1)
}