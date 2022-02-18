package com.example.funlauncher.ui.shared_view_models

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.funlauncher.room.AppsDao
import com.example.funlauncher.room.model.AppDbModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppsDbViewModel @Inject constructor(private val appsDao: AppsDao) : ViewModel() {

    var customAvailableApps: LiveData<List<AppDbModel>> = appsDao.getAllAppDbModel()

    fun addAppDbModel(appDbModel: AppDbModel) {
        viewModelScope.launch {
            appsDao.insertAppDbModel(appDbModel)
        }
    }
    fun deleteAppDbModel(appDbModel: AppDbModel) {
        viewModelScope.launch {
            appsDao.delete(appDbModel)
        }
    }


}