package pl.covid19.ui.view

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import pl.covid19.database.DatabaseDao

class ViewFragmentViewModelFactory(
    private val dataSource: DatabaseDao,
    private val application: Application,
    private val areadate: Pair<Long,String>,
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ViewFragmentViewModel::class.java)) {
            return ViewFragmentViewModel(dataSource, application, areadate) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

