package com.example.whatsappstatussaver.viewmodelsfactories

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.whatsappstatussaver.viewmodels.ImagesViewModel

class ImagesViewModelFactory(private val application: Application) : ViewModelProvider.AndroidViewModelFactory(application) {
     @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ImagesViewModel::class.java)) {
            return ImagesViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}




