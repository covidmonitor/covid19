package pl.covid19.ui.view


import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.tabs.TabLayout
import pl.covid19.R
import pl.covid19.database.CovidDatabase
import pl.covid19.databinding.FragmentViewBinding
import pl.covid19.util.Constants.BASE_URL
import pl.covid19.util.enableJava

class ViewFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DataBindingUtil.inflate<FragmentViewBinding>(
            inflater,
            R.layout.fragment_view,
            container,
            false
        )
        val args = ViewFragmentArgs.fromBundle(requireArguments())

        val app = requireNotNull(this.activity).application

        val dataSource = CovidDatabase.getInstance(app).covidDao

        val viewModelFactory = ViewFragmentViewModelFactory(dataSource, app,Pair(args.viewCovidKey,args.viewCovidDate))

        val viewFragmentViewModel =ViewModelProvider(this, viewModelFactory).get(ViewFragmentViewModel::class.java)

        binding.vm = viewFragmentViewModel
        binding.lifecycleOwner = this

        binding.viewtabLayout.addTab(binding.viewtabLayout.newTab().setText(args.viewCovidDate))
        binding.viewtabLayout.addTab(binding.viewtabLayout.newTab().setText("Opis"))

        binding.viewtabLayout.tabGravity = TabLayout.GRAVITY_FILL
        binding.viewtabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                if (tab.position == 0) {
                    viewFragmentViewModel.setData()
                } else {
                    viewFragmentViewModel.setDectription()
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

        viewFragmentViewModel.areaGovplx.observe(viewLifecycleOwner, Observer {
            (activity as AppCompatActivity?)!!.supportActionBar!!.title = it?.area?.name
            it?.let {
                binding.obostrzenia.webViewClient = WebViewClient()
                enableJava(binding.obostrzenia.settings)
                binding.obostrzenia.loadUrl(BASE_URL+it.fazy.idFazyKey.toString()+".html")
                //TODO 6 przenieść do  XML
                if (it.fazy.Color != null) {
                    binding.smiertelne.setBackgroundColor(Color.parseColor(it.fazy.Color))
                    binding.Liczba.setBackgroundColor(Color.parseColor(it.fazy.Color))
                    binding.Liczba10tys.setBackgroundColor(Color.parseColor(it.fazy.Color))
                    binding.Liczba10tys7.setBackgroundColor(Color.parseColor(it.fazy.Color))
                    binding.Liczba10tysIndicator.setBackgroundColor(Color.parseColor(it.fazy.Color))
                }
            }
        })


        return binding.root
    }


}
