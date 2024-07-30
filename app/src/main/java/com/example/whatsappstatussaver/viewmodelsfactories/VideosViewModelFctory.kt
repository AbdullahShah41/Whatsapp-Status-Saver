package com.example.whatsappstatussaver.viewmodelsfactories

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.whatsappstatussaver.viewmodels.VideosViewModel

class VideosViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(VideosViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return VideosViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
