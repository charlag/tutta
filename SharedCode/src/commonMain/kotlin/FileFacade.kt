package com.charlag.tuta

import com.charlag.tuta.entities.Id
import com.charlag.tuta.entities.tutanota.File
import com.charlag.tuta.entities.tutanota.FileDataDataGet
import com.charlag.tuta.entities.tutanota.FileDataDataPost
import com.charlag.tuta.entities.tutanota.FileDataReturnPost
import com.charlag.tuta.network.API
import com.charlag.tuta.network.SessionKeyResolver
import io.ktor.http.HttpMethod

data class DataFile(
    val name: String,
    val mimeType: String,
    val data: ByteArray
)

class FileFacade(
    private val api: API,
    private val cryptor: Cryptor,
    private val keyResolver: SessionKeyResolver
) {
    suspend fun downloadFile(file: File): DataFile {
        requireNotNull(file._id) { "File has no id" }
        val sessionKey = keyResolver.resolveSessionKey(
            typemodelMap.getValue(File::class.noReflectionName).typemodel,
            file._ownerEncSessionKey,
            file._ownerGroup?.asString(),
            file._permissions?.asString(),
            api
        )
        val data = api.serviceRequestBinaryGet(
            "tutanota", "filedataservice", HttpMethod.Get,
            FileDataDataGet(base64 = false, file = file._id, _format = null), sessionKey
        )
        return DataFile(file.name, file.mimeType ?: DEFAULT_MIME, data)
    }

    suspend fun uploadFile(file: DataFile, group: Id): UploadedFile {
        println("FileFacade upload ${file.name}")
        val sessionKey = cryptor.aes128RandomKey()
        val data = FileDataDataPost(_format = 0, group = group, size = file.data.size.toLong())
        val fileDataReturn = api.serviceRequest(
            "tutanota",
            "filedataservice",
            HttpMethod.Post,
            data,
            FileDataReturnPost::class,
            null,
            sessionKey
        )
        println("FileFacade fileData created ${file.name}")

        api.serviceRequestBinaryPost(
            "tutanota",
            "filedataservice",
            HttpMethod.Put,
            file.data,
            mapOf("fileDataId" to fileDataReturn.fileData.asString()),
            sessionKey
        )

        println("FileFacade fileData uploaded ${file.name}")
        return UploadedFile(
            fileDataReturn.fileData,
            file.name,
            file.mimeType,
            data.size,
            sessionKey
        )
    }

    companion object {
        const val DEFAULT_MIME = "application/binary"
    }
}

class UploadedFile(
    val fileDataId: Id,
    val name: String,
    val mimeType: String,
    val size: Long,
    val fileSessionKey: ByteArray
)