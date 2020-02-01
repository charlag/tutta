package com.charlag.tuta

import com.charlag.tuta.entities.tutanota.File
import com.charlag.tuta.entities.tutanota.FileDataDataGet
import io.ktor.http.HttpMethod

data class DataFile(
    val name: String,
    val data: ByteArray
)

class FileFacade(
    private val api: API
) {
    suspend fun downloadFile(file: File): DataFile {
        val sessionKey = api.resolveSessionKey(
            typemodelMap.getValue(File::class.noReflectionName).typemodel,
            file._ownerEncSessionKey,
            file._ownerGroup?.asString(),
            file._permissions.asString(),
            api
        )
        val data = api.serviceRequestBinary(
            "tutanota", "filedataservice", HttpMethod.Get,
            FileDataDataGet(base64 = false, file = file._id), sessionKey
        )
        return DataFile(file.name, data)
    }
}