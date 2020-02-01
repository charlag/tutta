package com.charlag.tuta.files

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import androidx.core.content.FileProvider
import androidx.core.content.getSystemService
import androidx.core.net.toUri
import com.charlag.tuta.FileFacade
import com.charlag.tuta.entities.tutanota.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

typealias SystemFile = java.io.File

class FileHandler(
    private val fileFacade: FileFacade,
    private val context: Context
) {
    suspend fun downloadFile(file: File): Uri {
        val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
            ?: error("External storage not available")
        val systemFile = download(file, storageDir)

        context.getSystemService<DownloadManager>()!!.addCompletedDownload(
            file.name,
            "Tuta download",
            true,
            file.mimeType,
            systemFile.path,
            systemFile.length(),
            true
        )
        return systemFile.toUri()
    }

    suspend fun openFile(file: File) {
        val shareDir = SystemFile(context.getExternalFilesDir(null), "shared_files")
        shareDir.mkdirs()
        val systemFile = download(file, shareDir)
        val contentUri = FileProvider.getUriForFile(context, "com.charlag.fileprovider", systemFile)
        context.grantUriPermission(
            context.packageName,
            contentUri,
            Intent.FLAG_GRANT_READ_URI_PERMISSION
        )
        val intent = Intent(Intent.ACTION_VIEW)
            .setDataAndType(contentUri, file.mimeType)
            .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            // We must set FLAG_ACTIVITY_NEW_TASK because we are using app context but it doesn't
            // hurt either way.
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    private suspend fun download(file: File, dir: SystemFile): SystemFile {
        val dataFile = fileFacade.downloadFile(file)
        val systemFile = java.io.File(dir, file.name)
        withContext(Dispatchers.IO) {
            systemFile.writeBytes(dataFile.data)
        }
        return systemFile
    }
}