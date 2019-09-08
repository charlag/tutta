package com.charlag.tuta

enum class PermissionType(val value: Long) {
    Public(0),
    Symmetric(1),
    PublicSymemtric(2),
    UnEncrypted(3),
    External(5),
    OwnerList(8)
}