package com.charlag.tuta.files

import android.app.DownloadManager
import android.content.Context
import android.os.Environment
import androidx.core.content.getSystemService
import com.charlag.tuta.FileFacade
import com.charlag.tuta.entities.tutanota.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FileHandler(
    private val fileFacade: FileFacade,
    private val context: Context
) {
    suspend fun downloadFile(file: File) {
        val dataFile = fileFacade.downloadFile(file)
        val systemFile =
            java.io.File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), file.name)
        withContext(Dispatchers.IO) {
            systemFile.writeBytes(dataFile.data)
        }

        context.getSystemService<DownloadManager>()!!.addCompletedDownload(
            file.name,
            "Tuta download",
            true,
            file.mimeType,
            systemFile.path,
            systemFile.length(),
            true
        )
    }
}