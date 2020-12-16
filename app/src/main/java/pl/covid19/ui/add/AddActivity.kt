package pl.covid19.ui.add

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar
import pl.covid19.R
import pl.covid19.database.CovidDatabase
import pl.covid19.databinding.ActivityAddBinding


class AddActivity : AppCompatActivity() {
    private val viewModel: AddActivityViewModel by lazy {
        val activity = requireNotNull(this)
        val dataSource = CovidDatabase.getInstance(application).covidDao
        ViewModelProvider(this, AddActivityViewModelFactory(dataSource, activity.application)).get(
            AddActivityViewModel::class.java
        )
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding: ActivityAddBinding = DataBindingUtil.setContentView(this,R.layout.activity_add)
        binding.setLifecycleOwner(this)
        binding.viewModel = viewModel

        viewModel.showSnackBarEvent.observe(this, Observer {
            if (it == true) { // Observed state is true.
                Snackbar.make(
                    binding.root,
                    "Do zapisania dane ",
                    Snackbar.LENGTH_SHORT
                ).show()
                viewModel.doneShowingSnackbar()
            }
        })

        binding.SpinerPoviat.setTitle("szukaj powiatu")
        binding.SpinerPoviat.setPositiveButton("OK")
        //TODO 7. show keyboard
        /* val imm: InputMethodManager =
            getSystemService( Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
*/

        viewModel.region.observe(this, object : Observer<Array<String>> {
            override fun onChanged(data: Array<String>?) {
                data ?: return
                val chipGroup = binding.regionList
                val inflator = LayoutInflater.from(chipGroup.context)

                val children = data.map { regionName ->
                    val chip = inflator.inflate(R.layout.region, chipGroup, false) as Chip
                    chip.text = regionName
                    chip.tag = regionName
                    chip.setOnCheckedChangeListener { button, isChecked ->
                        viewModel.onFilterChanged(button.tag as String, isChecked)
                        binding.SpinerPoviat.setSelection(0)
                        binding.SpinerVoviev.setSelection(0)
                    }
                    chip
                }

                chipGroup.removeAllViews()

                for (chip in children) {
                    chipGroup.addView(chip)
                }
            }
        })


        viewModel.navigateBack.observe(this, Observer { it ->
            if (it) {
                onNavigateUp()
                viewModel.doneNavigating()
            }
        })
    }

    override fun onBackPressed() {
        super.onBackPressed()
        onNavigateUp()
    }
}