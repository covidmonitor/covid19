package pl.covid19.ui.mainFragment

import android.app.Application
import android.location.Address
import android.location.Geocoder
import android.location.Location
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import pl.covid19.database.AreaDB
import pl.covid19.database.CovidDatabase
import pl.covid19.database.DatabaseDao
import pl.covid19.repository.CovidRepository
import pl.covid19.util.TodayToStringSql
import timber.log.Timber
import java.util.*


class MainFragmentViewModel(val databaseDao: DatabaseDao,application: Application) : AndroidViewModel(application) {


    val app= application

    private var viewModelJob = Job()

    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    //private val repository = CovidRepository(databaseDao,app.applicationContext)

    private val _curDate = MutableLiveData<String>()
    val curDate
        get() = _curDate

    var maxDate = databaseDao.getMaxDateDistinctLiveData()
    var areas = databaseDao.getAllAreas()
    var AGF = databaseDao.getAreaGovplxFazaWithMaxdate()

    private val _showNeedLocation = MutableLiveData<Boolean>()
    val showNeedLocation: LiveData<Boolean>
        get() = _showNeedLocation

    private val _navigateToViewFragment = MutableLiveData<Pair<Long,String>>()
    val navigateToViewFragment
        get() = _navigateToViewFragment


    private val _notNavigate= MutableLiveData<Boolean>()
    val notNavigate
        get() = _notNavigate

    fun IsCurMaxDate():Boolean {
        if (curDate.value == maxDate.value)
            return true
        else
            return false
    }

    fun onSetCurrToMaxDate()
    {
        _curDate.value=maxDate.value
    }

    fun onAreaClicked(id: Long) {
        _notNavigate.value = true
        onRefresh()
    }


    fun stopNavigate() {
        _notNavigate.value = false
    }


    fun onAGFClicked(tmpPair: Pair<Long, String>) {
        _navigateToViewFragment.value = tmpPair
    }

    fun onDelete(id: Long) {
        uiScope.launch {
            delete(id)
        }
    }

    fun onInsert(area: AreaDB?) {
        uiScope.launch {
            insert(area)
        }
    }

    fun  onGetArea(id: Long): AreaDB? {
        var tmpAreaCovid: AreaDB? = null
        uiScope.launch {
            tmpAreaCovid =get(id)}
        return tmpAreaCovid
    }
      fun onAreaRefresh(all:Boolean=false) {
          uiScope.launch { apiRefresh()  }
          areas = databaseDao.getAllAreas()
    }

    fun onRefresh() {
        uiScope.launch {
            apiRefresh()
        }
    }

    fun onRefreshList() {
        uiScope.launch {
            refreshList()
        }
    }

    fun onViewFragmentNavigated() {
        _navigateToViewFragment.value = null
    }


    init {
        _curDate.value= ""
        _notNavigate.value = false
/*        viewModelScope.launch {
            delay(5_000)
            _showNeedLocation.value = !repository.isFullyInitialized
        }*/
       }

    private suspend fun refreshList () {
        withContext(Dispatchers.IO) {
            AGF=databaseDao.getAreaGovplxFazaWithMaxdate()
        }
    }


    private  suspend fun apiRefresh() {
        val db = CovidDatabase.getInstance(app.applicationContext)
        val repository = CovidRepository(db, app.applicationContext)
        withContext(Dispatchers.IO) {
            repository.refresh()
        }
    }

    private suspend fun delete(key: Long) {
        withContext(Dispatchers.IO) {
            databaseDao.delete(key)
        }
    }

    private suspend fun clear() {
        withContext(Dispatchers.IO) {
            databaseDao.clear()
        }
    }

    private suspend fun update(area: AreaDB) {
        withContext(Dispatchers.IO) {
            databaseDao.update(area)
        }
    }

    private suspend fun insert(area: AreaDB?) {
        withContext(Dispatchers.IO) {
            if (area != null) {
                databaseDao.insert(area)
            }
        }
    }

    private suspend fun get(id: Long):AreaDB? {
        var tmpAreaCovid: AreaDB?
        withContext(Dispatchers.IO) {
            tmpAreaCovid = databaseDao.get(id)
        }
        return tmpAreaCovid
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    fun onLocationUpdated(location: Location) {
        viewModelScope.launch {
            //TODO Get Województwo and set GPS->Województwo
            Timber.i("Loc " +getAddress(location)?:"")

            // TODO update GPS  postalCode->powiat
            //repository.onLocationChanged(location)
            //  onQueryChanged() //TODO Requery ListView for nwe data
        }
    }

    fun getAddress(latLng: Location):String? {

        val geocoder = Geocoder(app.applicationContext, Locale.getDefault())
        val addresses: List<Address>?
        val address: Address?
        var fulladdress = ""
        addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)

        if (addresses.isNotEmpty()) {
            address = addresses[0]
            fulladdress =
                address.getAddressLine(0) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex
            var city = address.getLocality();
            var state = address.getAdminArea();

            var country = address.getCountryName();
            var postalCode = address.getPostalCode();
            var knownName = address.getFeatureName(); // Only if available else return NULL
            return state
        } else {
            fulladdress = "Location not found"
            return null
        }
    }


}
