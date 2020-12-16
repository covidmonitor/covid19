package pl.covid19.ui.view

import android.app.Application
import androidx.lifecycle.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import pl.covid19.R
import pl.covid19.database.DatabaseDao
import pl.covid19.util.TodayToStringSql
import timber.log.Timber
import java.util.*



class ViewFragmentViewModel(val databaseDao: DatabaseDao, app: Application, val areadate: Pair<Long, String>) : AndroidViewModel(
    app) {

    private var viewModelJob = Job()

    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    val areaGovplx =databaseDao.getAreaGovplxFazaWithIdDate(areadate.first,areadate.second)

    private var _showGroup = MutableLiveData<Boolean?>()
    val showGroup: LiveData<Boolean?>
        get() = _showGroup

    private var _showDesc = MutableLiveData<Boolean?>()
    val showDesc: LiveData<Boolean?>
        get() = _showDesc

    //TODO  20 Change  idApi to idGus

    init {
        setData()
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    fun setData() {
        _showGroup.value=true
        _showDesc.value=false
    }

    fun setDectription() {
        _showGroup.value=false
        _showDesc.value=true
    }

}
