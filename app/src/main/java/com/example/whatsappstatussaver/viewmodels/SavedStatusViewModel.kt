package com.example.whatsappstatussaver.viewmodels

import android.app.Application
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.whatsappstatussaver.data.ModelImageUri
import com.example.whatsappstatussaver.data.ModelVideoUri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class SavedStatusViewModel(application: Application) : AndroidViewModel(application) {

    private val _savedImages = MutableLiveData<List<ModelImageUri>>()
    private val _savedVideos = MutableLiveData<List<ModelVideoUri>>()
    val savedImage: LiveData<List<ModelImageUri>> get() = _savedImages
    val savedVideos: LiveData<List<ModelVideoUri>> get() = _savedVideos

    fun loadSavedStatus() {
        loadSavedImages()
        loadSavedVideos()
    }
    private fun loadSavedImages() {
        Log.d("SavedStatusViewModel", "loadSavedStatus: Method Called")
        viewModelScope.launch(Dispatchers.IO) {
            val savedImages = mutableListOf<ModelImageUri>()
            val projection = arrayOf(
                MediaStore.MediaColumns._ID,
                MediaStore.MediaColumns.DISPLAY_NAME,
                MediaStore.MediaColumns.DATA
            )
            val selection = "${MediaStore.MediaColumns.DATA} LIKE ?"
            val selectionArgs = arrayOf("%Pictures/WhatsappStatuses/%")
            val cursor = getApplication<Application>().contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                null
            )
            if (cursor == null){
                Log.d("SavedStatusViewModel", "Cursor is null")
            }
            else {
                Log.d(
                    "SavedStatusViewModel",
                    "loadSavedStatus: Cursor is not null, count = ${cursor.count}"
                )
                cursor?.use {
                    val idColumn = it.getColumnIndexOrThrow(MediaStore.MediaColumns._ID)
                    val nameColumn = it.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME)
                    val dataColumn = it.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)

                    while (it.moveToNext()) {
                        val id = it.getLong(idColumn)
                        val name = it.getString(nameColumn)
                        val data = it.getString(dataColumn)
                        val uri = Uri.withAppendedPath(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            id.toString()
                        )
                        val file = File(data)
                        if (file.exists()) {
                            savedImages.add(ModelImageUri(uri, System.currentTimeMillis()))
                            Log.d("StatusSavedViewModel", "file exists: $data")
                            Log.d("StatusSavedViewModel", "URI: $uri")
                        } else {
                            Log.d("StatusSavedViewModel", "File does not exist: $data")
                        }
                    }
                }
            }
            withContext(Dispatchers.Main) {
                _savedImages.value = savedImages
            }
        }
    }
    private fun loadSavedVideos() {
        Log.d("SavedStatusViewModel", "loadSavedStatus: Method Called")
        viewModelScope.launch(Dispatchers.IO) {
            val savedVideos = mutableListOf<ModelVideoUri>()
            val projection = arrayOf(
                MediaStore.MediaColumns._ID,
                MediaStore.MediaColumns.DISPLAY_NAME,
                MediaStore.MediaColumns.DATA
            )
            val selection = "${MediaStore.MediaColumns.DATA} LIKE ?"
            val selectionArgs = arrayOf("%Movies/WhatsappStatuses/%")
            val cursor = getApplication<Application>().contentResolver.query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                null
            )
            if (cursor == null){
                Log.d("SavedStatusViewModel", "Cursor is null")
            }
            else {
                Log.d(
                    "SavedStatusViewModel",
                    "loadSavedStatus: Cursor is not null, count = ${cursor.count}"
                )
                cursor.use {
                    val idColumn = it.getColumnIndexOrThrow(MediaStore.MediaColumns._ID)
                    val nameColumn = it.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME)
                    val dataColumn = it.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)

                    while (it.moveToNext()) {
                        val id = it.getLong(idColumn)
                        val name = it.getString(nameColumn)
                        val data = it.getString(dataColumn)
                        val uri = Uri.withAppendedPath(
                            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                            id.toString()
                        )
                        val file = File(data)
                        if (file.exists()) {
                            savedVideos.add(ModelVideoUri(uri, System.currentTimeMillis()))
                            Log.d("StatusSavedViewModel", "file exists: $data")
                            Log.d("StatusSavedViewModel", "URI: $uri")
                        } else {
                            Log.d("StatusSavedViewModel", "File does not exist: $data")
                        }
                    }
                }
            }
            withContext(Dispatchers.Main) {
                _savedVideos.value = savedVideos
            }
        }
    }
}