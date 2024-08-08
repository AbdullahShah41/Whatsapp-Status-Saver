package com.example.whatsappstatussaver.viewmodels

import android.app.Application
import android.content.ContentValues
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.documentfile.provider.DocumentFile
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.whatsappstatussaver.data.ModelImageUri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

class ImagesViewModel() : ViewModel() {

    private val _statusList = MutableLiveData<ArrayList<ModelImageUri>>()
    val statusList: LiveData<ArrayList<ModelImageUri>> get() = _statusList

    fun loadImages(activity: FragmentActivity, uriPath: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val list = ArrayList<ModelImageUri>()
            uriPath?.let {
                try {
                    val fileDoc = DocumentFile.fromTreeUri(activity, Uri.parse(it))
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

    fun saveFile(
        status: ModelImageUri,
        onSuccess: () -> Unit,
        onFailure: () -> Unit,
        activity: FragmentActivity
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val inputStream =
                activity.contentResolver.openInputStream(status.imageUri)

            val fileName = "${System.currentTimeMillis()}.jpg"

            val path = getRealPathFromURI(activity,status.imageUri)

            try {
                val values = ContentValues().apply {
                    put(MediaStore.Images.Media.DATA, path)
                    put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
                    put(MediaStore.Images.Media.MIME_TYPE, "images/jpg")
                    put(MediaStore.Images.Media.MIME_TYPE, "images/png")
                    put(MediaStore.Images.Media.MIME_TYPE, "images/jpeg")
                }
                values.put(
                    MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES +
                            "/WhatsappStatuses/"
                )

                val uri = activity.contentResolver.insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
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
}




