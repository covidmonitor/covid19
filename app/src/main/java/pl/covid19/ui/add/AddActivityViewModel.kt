package pl.covid19.ui.add

import android.app.Application
import android.view.View
import android.widget.AdapterView
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pl.covid19.R
import pl.covid19.database.AreaDB
import pl.covid19.database.CovidDatabase
import pl.covid19.database.DatabaseDao
import pl.covid19.repository.CovidRepository
import timber.log.Timber


class AddActivityViewModel(val database: DatabaseDao, val app: Application) : AndroidViewModel(app) {
    private val list:Array<String> = app.getResources().getStringArray(R.array.chipsName)

    val aplication =app
    private val _region = MutableLiveData<Array<String>>()
    val region: LiveData<Array<String>>
        get() = _region

    private var filter = FilterHolder()

    private var _idValue =MutableLiveData<String>() // 2 Polland, other from tag GPS -1
    val idValue: LiveData<String>
        get() = _idValue

    private var _showVoiev = MutableLiveData<Boolean?>()
    val showVoiev: LiveData<Boolean?>
        get() = _showVoiev

    private var _showPoviat = MutableLiveData<Boolean?>()
    val showPoviat: LiveData<Boolean?>
        get() = _showPoviat

    private val _navigateBack = MutableLiveData<Boolean>()
    val navigateBack: LiveData<Boolean>
        get() = _navigateBack
    fun doneNavigating() {
        _navigateBack.value=false
    }

    val clicksVoievListener = object : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(parent: AdapterView<*>?) {
        }
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            val spinner_pos: Int = parent!!.getSelectedItemPosition()
            val size_values: Array<String> = app.getResources().getStringArray(R.array.array_voiev_gus)
           // val tmpSelected =Integer.valueOf(size_values[spinner_pos])
            if (spinner_pos>0)
                _idValue.value= (size_values[spinner_pos])

        }
    }

    val clicksPoviatsListener = object : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(parent: AdapterView<*>?) {
        }
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            val spinner_pos: Int = parent!!.getSelectedItemPosition()
            val size_values: Array<String> = app.getResources().getStringArray(R.array.array_poviats_gus)
            //val tmpSelected =Integer.valueOf(size_values[spinner_pos])
            if (spinner_pos>0)
                _idValue.value= (size_values[spinner_pos])
        }
    }

    private var _showSnackbarEvent = MutableLiveData<Boolean?>()
    val showSnackBarEvent: LiveData<Boolean?>
        get() = _showSnackbarEvent
    fun doneShowingSnackbar() {
        _showSnackbarEvent.value = null
    }

    fun onSubmitApplication() {
        if (_idValue.value !=null) {
            viewModelScope.launch {
                val newArea = AreaDB()
                //newArea.idApi =_idValue.value
                newArea.type = filter.currentValue
                when (filter.currentValue) {
                    list[0] -> {
                        newArea.name = list[0]
                    //    newArea.idApi = "2"
                        newArea.idGus = "t0000"
                        }//Polska
                    list[1] -> {
                        //val tagValues: Array<String> =app.getResources().getStringArray(R.array.array_voiev_gus)
                        val nameValues: Array<String> =app.getResources().getStringArray(R.array.array_voiev)
                        val gusValues: Array<String> =app.getResources().getStringArray(R.array.array_voiev_gus)
                        //gusValues.find {it==_idValue.value  }
                        newArea.name =nameValues[gusValues.indexOf(_idValue.value)]
                        newArea.idGus = _idValue.value
                    }//Woj
                    list[2] -> {
                        //val tagValues: Array<String> =app.getResources().getStringArray(R.array.array_poviats_gus)
                        val nameValues: Array<String> =app.getResources().getStringArray(R.array.array_poviats)
                        val gusValues: Array<String> =app.getResources().getStringArray(R.array.array_poviats_gus)
                        newArea.name =nameValues[gusValues.indexOf(_idValue.value)]
                        newArea.idGus = _idValue.value
                        /*newArea.name = nameValues[tagValues.indexOf(_idValue.value)]
                        newArea.idGus = gusValues[tagValues.indexOf(_idValue.value)]*/
                    }//Powiat
                    list[3] -> {newArea.name = list[0]
                      //  newArea.idApi = "-1"
                        newArea.idGus = "-1"
                    }//GPS

                }
                insert(newArea)
                _navigateBack.value=true
            }
        }else
             _showSnackbarEvent.value = true
    }

    private suspend fun insert(area: AreaDB) {
        withContext(Dispatchers.IO) {
            database.insert(area)
        }
    }
    init {
        _region.value = list
        _showVoiev.value=false
        _showPoviat.value=false
        _navigateBack.value=false
    }


    fun onFilterChanged(filter: String, isChecked: Boolean) {
        if (this.filter.update(filter, isChecked)) {
                when (this.filter.currentValue) {
                    list[0] -> {
                        _showVoiev.value = false
                        _showPoviat.value = false
                        _idValue.value = "t0000"
                    }//Polska
                    list[1] -> {
                        _showVoiev.value = true
                        _showPoviat.value = false
                        _idValue.value = null
                    }//Woj
                    list[2] -> {
                        _showVoiev.value = false
                        _showPoviat.value = true
                        _idValue.value = null
                    }//Powiat
                  /*  list[3] -> {
                        _showVoiev.value = false
                        _showPoviat.value = false
                        _idValue.value = "-1"
                    }*///GPS
                    else->{
                        _showVoiev.value = false
                        _showPoviat.value = false
                        _idValue.value = null
                    }
                }
            }
        }

    private class FilterHolder {
        var currentValue: String? = null
            private set

        fun update(changedFilter: String, isChecked: Boolean): Boolean {
            if (isChecked) {
                currentValue = changedFilter
                return true
            } else if (currentValue == changedFilter) {
                currentValue = null
                return true
            }
            return false
        }
    }
}