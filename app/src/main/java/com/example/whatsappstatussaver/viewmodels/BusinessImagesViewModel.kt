package com.example.whatsappstatussaver.viewmodels

import android.app.Application
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.documentfile.provider.DocumentFile
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.whatsappstatussaver.data.ModelImageUri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.io.OutputStream

class BusinessImagesViewModel(application: Application) : AndroidViewModel(application) {
    private val _statusList = MutableLiveData<ArrayList<ModelImageUri>>()
    val statusList: LiveData<ArrayList<ModelImageUri>> get() = _statusList

    fun loadImages(uriPath: String?) {
        viewModelScope.launch(Dispatchers.IO) {
            val list = ArrayList<ModelImageUri>()
            uriPath?.let {
                try {
                    val fileDoc = DocumentFile.fromTreeUri(getApplication(), Uri.parse(it))
                    fileDoc?.listFiles()?.forEach { file ->
                        if (file.name?.endsWith(".jpg") == true ||
                            file.name?.endsWith(".png") == true ||
                            file.name?.endsWith(".jpeg") == true
                        ) {
                            val modelImageUri = ModelImageUri(file.uri, file.lastModified())
                            list.add(modelImageUri)
                        }
                    }
                } catch (e: Exception) {
                    Log.d("ImagesViewModel", "loadImages: ${e.message}")

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

    fun getRealPathFromURI(activity: FragmentActivity,contentURI: Uri): String? {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = activity.contentResolver.query(contentURI, projection, null, null, null)
        cursor?.use {
            if (it.moveToNext()){
                val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                return it.getString(columnIndex)
            }
        }
        return null
    }

    fun saveFile(status: ModelImageUri, onSuccess: () -> Unit, onFailure: () -> Unit,activity: FragmentActivity) {
        viewModelScope.launch(Dispatchers.IO) {
            val inputStream =
                getApplication<Application>().contentResolver.openInputStream(status.imageUri)
            val fileName = "${System.currentTimeMillis()}.jpg"
            val path = getRealPathFromURI(activity,status.imageUri)
            try {
                val values = ContentValues()
                values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
                values.put(MediaStore.Images.Media.MIME_TYPE, "images/jpg")
                values.put(MediaStore.Images.Media.MIME_TYPE, "images/png")
                values.put(MediaStore.Images.Media.MIME_TYPE, "images/jpeg")
                values.put(
                    MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES +
                            "/WhatsappStatuses/"
                )
                val uri = activity.contentResolver.insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values
                )
                val outputStream: OutputStream = uri?.let {
                    getApplication<Application>().contentResolver.openOutputStream(it)
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
