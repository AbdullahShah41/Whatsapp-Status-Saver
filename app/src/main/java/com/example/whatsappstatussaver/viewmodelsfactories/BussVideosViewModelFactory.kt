package com.example.whatsappstatussaver.viewmodelsfactories

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.whatsappstatussaver.viewmodels.BusinessVideosViewModel

class BussVideosViewModelFactory(private val application: Application) : ViewModelProvider.AndroidViewModelFactory(application) {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BusinessVideosViewModel::class.java)) {
            return BusinessVideosViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}