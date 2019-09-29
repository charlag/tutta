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
    Calendar(9)
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