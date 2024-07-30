package com.example.whatsappstatussaver.viewmodels

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.whatsappstatussaver.data.ModelImageUri
import com.example.whatsappstatussaver.data.ModelVideoUri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.io.OutputStream

class BusinessVideosViewModel(private val context: Context) : ViewModel() {

    private val _statusList = MutableLiveData<ArrayList<ModelImageUri>>()
    val statusList: LiveData<ArrayList<ModelImageUri>> get() = _statusList

    fun loadVideos(uriPath: String?) {
        viewModelScope.launch(Dispatchers.IO) {
            val list = ArrayList<ModelImageUri>()
            uriPath?.let {
                try {
                    val fileDoc = DocumentFile.fromTreeUri(context, Uri.parse(it))
                    fileDoc?.listFiles()?.forEach { file ->
                        if (file.name?.endsWith(".mp4") == true ||
                            file.name?.endsWith(".mkv") == true ||
                            file.name?.endsWith(".avi") == true
                        ) {
                            val modelImageUri = ModelImageUri(file.uri, file.lastModified())
                            list.add(modelImageUri)
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

    fun saveFile(status: ModelVideoUri, onSuccess: () -> Unit, onFailure: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val inputStream = context.contentResolver.openInputStream(status.videoUri)
            val fileName = "${System.currentTimeMillis()}.mp4"
            try {
                val values = ContentValues()
                values.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                values.put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4")
                values.put(MediaStore.MediaColumns.MIME_TYPE, "videos/mkv")
                values.put(MediaStore.MediaColumns.MIME_TYPE, "videos/avi")
                values.put(
                    MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS +
                            "/Whatsapp Statuses/"
                )
                val uri = context.contentResolver.insert(
                    MediaStore.Files.getContentUri("external"), values
                )
                val outputStream: OutputStream = uri?.let {
                    context.contentResolver.openOutputStream(it)
                }!!
                if (inputStream != null) {
                    outputStream.write(inputStream.readBytes())
                }
                outputStream.close()
                withContext(Dispatchers.Main) {
                    onSuccess()
                }
            } catch (e: IOException) {
                withContext(Dispatchers.Main) {
                    onFailure()
                }
            }
        }
    }
}