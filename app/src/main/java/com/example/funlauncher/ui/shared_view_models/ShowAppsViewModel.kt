package com.example.funlauncher.ui.shared_view_models

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.ResolveInfo
import android.util.Log
import androidx.lifecycle.*
import com.example.funlauncher.model.AppInfoModel
import com.example.funlauncher.model.FinalAppModel
import com.example.funlauncher.room.AppsDao
import com.example.funlauncher.room.model.AppDbModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Default
import java.util.*
import javax.inject.Inject


const val TAG = "ShowAppsViewModel"

@HiltViewModel
class ShowAppsViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val appsDao: AppsDao
) : ViewModel() {

    private var _defaultAvailableApps: MutableLiveData<MutableList<AppInfoModel>> =
        MutableLiveData<MutableList<AppInfoModel>>()
    var defaultAvailableApps: LiveData<MutableList<AppInfoModel>> = _defaultAvailableApps

    var customAvailableApps: LiveData<List<AppDbModel>> = appsDao.getAllAppDbModel()

    val currentTime = liveData(Dispatchers.Default) {
        while (true) {
            emit(Calendar.getInstance().time)
            delay(1000)
        }
    }

    lateinit var defaultAndCustomAvailableApps: LiveData<Pair<MutableList<AppInfoModel>?, List<AppDbModel>?>>

    var finalAppList: MutableLiveData<MutableList<FinalAppModel>> =
        MutableLiveData<MutableList<FinalAppModel>>()
    private var getFinalAppModelJob: Job? = null

    init {
        getInstalledAppData()
        initializeMediator()
    }

    private fun initializeMediator() {
        defaultAndCustomAvailableApps =
            MediatorLiveData<Pair<MutableList<AppInfoModel>?, List<AppDbModel>?>>().apply {
                addSource(defaultAvailableApps) { value = Pair(it, customAvailableApps.value) }
                addSource(customAvailableApps) { value = Pair(defaultAvailableApps.value, it) }
            }
    }

    fun getInstalledAppData() {
        Log.d(TAG, "getInstalledAppData: called")
        viewModelScope.launch {
            val tempInstalledAppList = mutableListOf<AppInfoModel>()
            val resolvedAppList: List<ResolveInfo> = context.packageManager
                .queryIntentActivities(
                    Intent(Intent.ACTION_MAIN, null)
                        .addCategory(Intent.CATEGORY_LAUNCHER), 0
                )
            for (resolvedApp in resolvedAppList) {
                if (resolvedApp.activityInfo.packageName != context.packageName) {

                    val app = AppInfoModel(
                        appPackageName = resolvedApp.activityInfo.packageName,
                        appName = resolvedApp.loadLabel(context.packageManager).toString(),
                        appIcon = resolvedApp.activityInfo.loadIcon(context.packageManager)
                    )

                    tempInstalledAppList.add(app)
                    Log.d(TAG, "Apps - $app")
                }
            }
            _defaultAvailableApps.value = mutableListOf()
            _defaultAvailableApps.value = tempInstalledAppList
        }
    }

    fun getFinalAppModel(
        appInfoModels: MutableList<AppInfoModel>,
        appDbModels: MutableList<AppDbModel>
    ) {
        getFinalAppModelJob?.cancel()

        getFinalAppModelJob = viewModelScope.launch(Default) {
            finalAppList.postValue(mutableListOf())

            val tempFinalAppList = mutableListOf<FinalAppModel>()

            outer@ for (appInfo in appInfoModels) {
                Log.d(TAG, "outer ${appInfo.appName}")
                inner@ for (appDb in appDbModels) {
                    Log.d(TAG, "inner ${appInfo.appName}")

                    if (appInfo.appPackageName == appDb.appPackageName) {
                        tempFinalAppList.add(
                            FinalAppModel(
                                appName = appInfo.appName,
                                appIcon = appInfo.appIcon,
                                appPackageName = appInfo.appPackageName,
                                customAppName = appDb.customAppName,
                                customAppIcon = appDb.customAppIcon,
                                isModified = true
                            )
                        )
                        continue@outer
                    }
                }
                Log.d(TAG, "outer after inner${appInfo.appName}")

                tempFinalAppList.add(
                    FinalAppModel(
                        appName = appInfo.appName,
                        appIcon = appInfo.appIcon,
                        appPackageName = appInfo.appPackageName,
                        customAppName = null,
                        customAppIcon = null,
                        isModified = false
                    )
                )
            }
            finalAppList.postValue(tempFinalAppList)
        }
    }


    fun getAppInfoModelFromPackageName(packageName: String): AppInfoModel? {
        val appsList: List<AppInfoModel>? = defaultAvailableApps.value
        return appsList?.let {
            it.find { appInfo -> appInfo.appPackageName == packageName }
        }
    }

    fun getFinalAppModelFromPackageName(packageName: String): FinalAppModel? {
        val appsList: List<FinalAppModel>? = finalAppList.value
        return appsList?.let {
            it.find { finalAppModel -> finalAppModel.appPackageName == packageName }
        }
    }
}