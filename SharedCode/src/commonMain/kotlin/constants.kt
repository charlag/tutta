package com.charlag.tuta

enum class PermissionType(val value: Long) {
    Public(0),
    Symmetric(1),
    PublicSymemtric(2),
    UnEncrypted(3),
    External(5),
    OwnerList(8)
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