package com.example.whatsappstatussaver.viewmodels

import android.app.Activity
import android.app.Application
import android.content.ContentValues
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.documentfile.provider.DocumentFile
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.whatsappstatussaver.data.ModelVideoUri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.io.OutputStream

class VideosViewModel(application: Application) : AndroidViewModel(application) {

    private val _statusList = MutableLiveData<ArrayList<ModelVideoUri>>()
    val statusList: LiveData<ArrayList<ModelVideoUri>> get() = _statusList

    fun loadVideos(uriPath: String?) {
        viewModelScope.launch(Dispatchers.IO) {
            val list = ArrayList<ModelVideoUri>()
            uriPath?.let {
                try {
                    val fileDoc = DocumentFile.fromTreeUri(getApplication(), Uri.parse(it))
                    fileDoc?.listFiles()?.forEach { file ->
                        if (file.name?.endsWith(".mp4") == true ||
                            file.name?.endsWith(".mkv") == true ||
                            file.name?.endsWith(".avi") == true
                        ) {
                            val modelVideoUri = ModelVideoUri(file.uri, file.lastModified())
                            list.add(modelVideoUri)
                        }
                    }
                } catch (e: Exception) {
                    Log.d("VideosViewModel", "loadVideos: ${e.message}")

                    // Handle exception
                }
            }
            withContext(Dispatchers.Main) {
                _statusList.value = list
            }
        }
    }

    fun sortByAscendingTime() {
        _statusList.value?.let { list ->
            list.sortBy { it.timeStamp }
            _statusList.value = ArrayList(list)
        }
    }

    fun sortByDescendingTime() {
        _statusList.value?.let { list ->
            list.sortByDescending { it.timeStamp }
            _statusList.value = ArrayList(list)
        }
    }

    fun getRealPathFromURI(contentURI: Uri): String? {
        val projection = arrayOf(MediaStore.Video.Media.DATA)
        val cursor = getApplication<Application>().contentResolver.query(contentURI, projection, null, null, null)
        cursor?.use {
            if (it.moveToNext()){
                val columnIndex = it.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)
                return it.getString(columnIndex)
            }
        }
        return null
    }

    fun saveFile(status: ModelVideoUri, onSuccess: () -> Unit, onFailure: () -> Unit,activity: FragmentActivity) {
        viewModelScope.launch(Dispatchers.IO) {
            val inputStream = getApplication<Application>().contentResolver.openInputStream(status.videoUri)
            val fileName = "${System.currentTimeMillis()}.mp4"
            val path = getRealPathFromURI(status.videoUri)

            try {
                val values = ContentValues().apply {
                    put(MediaStore.Video.Media.DATA, path)
                    put(MediaStore.Video.Media.DISPLAY_NAME, fileName)
                    put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")
                    put(MediaStore.Video.Media.MIME_TYPE, "videos/mkv")
                    put(MediaStore.Video.Media.MIME_TYPE, "videos/avi")
                }
                values.put(
                    MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_MOVIES +
                            "/WhatsappStatuses/"
                )
                val uri = activity.contentResolver.insert(
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                    values
                )
                if (uri != null) {
                    activity.contentResolver.openOutputStream(uri)?.use { outputStream ->
                        inputStream?.copyTo(outputStream)
                    }
                    withContext(Dispatchers.Main) {
                        onSuccess()
                    }

                } else {
                    withContext(Dispatchers.Main) {
                        onFailure()
                    }
                }
            }
            catch (e: IOException) {
                withContext(Dispatchers.Main) {
                    onFailure()
                }
            }
//                val uri = getApplication<Application>().contentResolver.insert(
//                    MediaStore.Files.getContentUri("external"), values
//                )
//                val outputStream: OutputStream = uri?.let {
//                    getApplication<Application>().contentResolver.openOutputStream(it)
//                }!!
//                if (inputStream != null) {
//                    outputStream.write(inputStream.readBytes())
//                }
//                outputStream.close()
//                withContext(Dispatchers.Main) {
//                    onSuccess()
//                }

        }
    }
}



