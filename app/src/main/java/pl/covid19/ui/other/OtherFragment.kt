package pl.covid19.ui.other

import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_other.*
import pl.covid19.R
import pl.covid19.database.CovidDatabase
import pl.covid19.database.DatabaseDao
import pl.covid19.databinding.FragmentOtherBinding
import pl.covid19.ui.mainFragment.MainFragmentViewModel
import pl.covid19.ui.mainFragment.MainFragmentViewModelFactory
import pl.covid19.util.Constants.PRIVACY_NAME


class OtherFragment : Fragment() {
    lateinit var otherViewModel : OtherViewFragmentViewModel
    lateinit var dataSource : DatabaseDao
    val sampleList = arrayOf(
        "O tej aplikacji", "Dane źródłowe", "O autorach",
        "Licencje", "Planowane zmiany", "Zaproś znajonych",PRIVACY_NAME
    )



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View {
        val binding = DataBindingUtil.inflate<FragmentOtherBinding>(inflater,R.layout.fragment_other,container,false)

    val arrayAdapter =
        context?.let { ArrayAdapter(it, android.R.layout.simple_list_item_1, sampleList) }
    binding.sampleList.adapter = arrayAdapter
    binding.sampleList.setOnItemClickListener { parent, view, position, id ->
        view.findNavController()
            .navigate(
                OtherFragmentDirections.actionOtherFragmentToOtherViewFragment(
                    position,
                    sampleList[position]
                )
            )
    }

        val application = requireNotNull(this.activity).application

        dataSource = CovidDatabase.getInstance(application).covidDao

        val viewModelFactory = OtherViewFragmentViewModelFactory(dataSource, application)

        otherViewModel = ViewModelProvider(this, viewModelFactory).get(OtherViewFragmentViewModel::class.java)

        binding.otherViewModel = otherViewModel

        binding.topToolbar.setOnClickListener {
            otherViewModel.onAllRefresh()
            Snackbar.make(it,"Rozpoczęto synchronizację danych",Snackbar.LENGTH_LONG).show()
        }

    return binding.root
    }
}
