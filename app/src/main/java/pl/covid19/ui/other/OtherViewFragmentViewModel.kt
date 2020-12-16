package pl.covid19.ui.other

import android.app.Application
import androidx.lifecycle.*
import kotlinx.coroutines.*
import pl.covid19.CovidMonitorApplication
import pl.covid19.database.CovidDatabase
import pl.covid19.database.DatabaseDao
import pl.covid19.repository.CovidRepository
import pl.covid19.util.Constants


class OtherViewFragmentViewModel(val database: DatabaseDao, application: Application) : AndroidViewModel(application) {
    val app =application

    private var viewModelJob = Job()

    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)


    private val _networkStatus = MutableLiveData<Boolean>()
    val networkStatus: LiveData<Boolean>
        get() = _networkStatus


    private val _navigateBack = MutableLiveData<Boolean>()
    val navigateBack: LiveData<Boolean>
        get() = _navigateBack
    fun doneNavigating() {
        _navigateBack.value=false
    }
    fun onAllRefresh() {
        uiScope.launch { apiRefresh(true)  }
    }

    init {
        _navigateBack.value=false
        _networkStatus.value = CovidMonitorApplication.Variables.isNetworkConnected
    }


    private  suspend fun apiRefresh(all:Boolean=false) {
        val db = CovidDatabase.getInstance(app.applicationContext)
        val repository = CovidRepository(db, app.applicationContext)
        withContext(Dispatchers.IO) {
            repository.refresh(all)
        }
    }


}