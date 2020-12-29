package pl.covid19.ui.other

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.google.android.material.snackbar.Snackbar
import pl.covid19.R
import pl.covid19.database.CovidDatabase
import pl.covid19.database.DatabaseDao
import pl.covid19.databinding.FragmentOtherBinding
import pl.covid19.util.Constants.ABOUT_NAME
import pl.covid19.util.Constants.ABOUT_URL
import pl.covid19.util.Constants.PLANED_NAME
import pl.covid19.util.Constants.PLANED_URL
import pl.covid19.util.Constants.PRIVACY_NAME
import pl.covid19.util.Constants.PRIVACY_URL
import pl.covid19.util.Constants.SHARE_NAME


class OtherFragment : Fragment() {
    lateinit var otherViewModel : OtherViewFragmentViewModel
    lateinit var dataSource : DatabaseDao
    val sampleList = arrayOf(ABOUT_NAME, "Dane źródłowe", "O autorach","Licencje", PLANED_NAME, SHARE_NAME, PRIVACY_NAME)

    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,savedInstanceState: Bundle?): View {
        val binding = DataBindingUtil.inflate<FragmentOtherBinding>(inflater,R.layout.fragment_other,container,false)

        val arrayAdapter =context?.let { ArrayAdapter(it,android.R.layout.simple_list_item_1,sampleList) }
        binding.sampleList.adapter = arrayAdapter
        binding.sampleList.setOnItemClickListener { parent, view, position, id ->
            when(sampleList[position]) {
                SHARE_NAME -> shareMaessage(getString(R.string.shareInfo))
                PLANED_NAME -> startUrl(PLANED_URL)
                PRIVACY_NAME->startUrl(PRIVACY_URL)
                ABOUT_NAME->startUrl(ABOUT_URL)
                else -> {view.findNavController().navigate(OtherFragmentDirections.actionOtherFragmentToOtherViewFragment(position,sampleList[position]))
                }
            }
        }

        val application = requireNotNull(this.activity).application

        dataSource = CovidDatabase.getInstance(application).covidDao

        val viewModelFactory = OtherViewFragmentViewModelFactory(dataSource, application)

        otherViewModel = ViewModelProvider(this, viewModelFactory).get(OtherViewFragmentViewModel::class.java)

        binding.otherViewModel = otherViewModel

        binding.topToolbar.setOnClickListener {otherViewModel.onAllRefresh()
            Snackbar.make(it, "Rozpoczęto synchronizację danych", Snackbar.LENGTH_LONG).show()}
        return binding.root
    }
    fun shareMaessage(text: String)
    {
        val intent2 = Intent()
        intent2.setAction(Intent.ACTION_SEND)
        intent2.type = "text/plain"
        intent2.putExtra(Intent.EXTRA_TEXT, text)
        startActivity(Intent.createChooser(intent2, getString(R.string.shareInfo)))
    }

    fun startUrl(url: String) {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(browserIntent)
    }

}
