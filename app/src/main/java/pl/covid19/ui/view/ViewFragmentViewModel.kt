package pl.covid19.ui.view

import android.app.Application
import androidx.lifecycle.*
import com.github.mikephil.charting.data.Entry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import pl.covid19.database.DatabaseDao
import pl.covid19.domain.Series
import pl.covid19.util.TodayToStringSql
import kotlin.reflect.jvm.internal.impl.protobuf.LazyStringArrayList


class ViewFragmentViewModel(val databaseDao: DatabaseDao, app: Application, val areadate: Pair<Long, String>) : AndroidViewModel(
    app) {

    private var viewModelJob = Job()

    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    val areaGovplx = databaseDao.getAreaGovplxFazaWithIdDate(areadate.first, areadate.second)
    val areaGovplxs = databaseDao.getAllAreaGovplxFazaWithId(areadate.first)
    var verObo = databaseDao.getVersionOgraniczeniaWithData(TodayToStringSql())

    var fazaUrl = "-99"
    var idGus = ""
    var links  = emptyArray<String>()

    private var _swLiczba = MutableLiveData<Boolean>()
    val swLiczba: LiveData<Boolean>
        get() = _swLiczba

    private var _swLiczba10tys = MutableLiveData<Boolean>()
    val swLiczba10tys: LiveData<Boolean>
        get() = _swLiczba10tys

    private var _swLiczba10tys7 = MutableLiveData<Boolean>()
    val swLiczba10tys7: LiveData<Boolean>
        get() = _swLiczba10tys7

    private var _swSmiertelne = MutableLiveData<Boolean>()
    val swSmiertelne: LiveData<Boolean>
        get() = _swSmiertelne

    private var _showGroup = MutableLiveData<Boolean?>()
    val showGroup: LiveData<Boolean?>
        get() = _showGroup

    private var _oboSwNow = MutableLiveData<Boolean>()
    val oboSwNow: LiveData<Boolean>
        get() = _oboSwNow

    private var _oboSwNext = MutableLiveData<Boolean>()
    val oboSwNext: LiveData<Boolean>
        get() = _oboSwNext



    private var _showDesc = MutableLiveData<Boolean?>()
    val showDesc: LiveData<Boolean?>
        get() = _showDesc

    //TODO  20 Change  idApi to idGus

    val listLiczba10tysAvg7 = Transformations.map(areaGovplxs) { it ->
        val tmpList = it.map { dailyItem ->
            if (dailyItem.govpl.Liczba10tysAvg7 != null) {
                Pair(dailyItem.govpl.Liczba10tysAvg7?.toFloat() ?: 0F, dailyItem.govpl.Date)
            } else
                null
        }.filterNotNull()
        tmpList.mapIndexed { index, dailyItem ->
            Series(index.toFloat(), dailyItem.first, dailyItem.second)
        }
    }

    val listLiczba10tys = Transformations.map(areaGovplxs) { it  ->
        it.mapIndexed { index, dailyItem ->
            Series(index.toFloat(),dailyItem.govpl.Liczba10tys.toFloat(),dailyItem.govpl.Date)
        }
    }
    val listLiczba = Transformations.map(areaGovplxs) { it  ->
        it.mapIndexed { index, dailyItem ->
            Series(index.toFloat(),dailyItem.govpl.Liczba.toFloat() ,dailyItem.govpl.Date)
        }
    }

    val listSmiertelne = Transformations.map(areaGovplxs) { it  ->
        it.mapIndexed { index, dailyItem ->
            Series(index.toFloat(),dailyItem.govpl.WszystkieSmiertelne.toFloat() ,dailyItem.govpl.Date)
        }
    }
    init {
        setData()
        switchLiczba()
        oboSwitchNow()
      //  links = verObo.value?.decription?.split("|")?.toTypedArray() ?: emptyArray<String>() //TODO 1. set links
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }


    fun switchLiczba() {
        _swLiczba.value=true
        _swLiczba10tys.value=false
        _swLiczba10tys7.value=false
        _swSmiertelne.value=false
    }
    fun switchLiczba10tys() {
        _swLiczba.value=false
        _swLiczba10tys.value=true
        _swLiczba10tys7.value=false
        _swSmiertelne.value=false
    }

    fun oboSwitchNext()
    {
        _oboSwNow.value=false
        _oboSwNext.value=true
      }
    fun oboSwitchNow()
    {
        _oboSwNow.value=true
        _oboSwNext.value=false
    }

    fun switchLiczba10tys7() {
        _swLiczba.value=false
        _swLiczba10tys.value=false
        _swLiczba10tys7.value=true
        _swSmiertelne.value=false
    }

    fun switchSmiertelne() {
        _swLiczba.value=false
        _swLiczba10tys.value=false
        _swLiczba10tys7.value=false
        _swSmiertelne.value=true
    }

    fun setData() {
        _showGroup.value=true
        _showDesc.value=false
    }

    fun setDectription() {
        oboSwitchNow()
        _showGroup.value=false
        _showDesc.value=true
    }

}
