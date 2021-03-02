package pl.covid19.ui.view


import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.annotation.ColorRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.android.material.tabs.TabLayout
import pl.covid19.CovidMonitorApplication
import pl.covid19.R
import pl.covid19.database.CovidDatabase
import pl.covid19.databinding.FragmentViewBinding
import pl.covid19.domain.Series
import pl.covid19.util.Constants.BASEHOST_URL
import pl.covid19.util.Constants.BASEAPI_URL
import pl.covid19.util.enableJava
import timber.log.Timber


class ViewFragment : Fragment() {

    //private var binding: FragmentViewBinding.binding(R.layout.fragment_view)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = DataBindingUtil.inflate<FragmentViewBinding>(inflater, R.layout.fragment_view,container,false)
        val args = ViewFragmentArgs.fromBundle(requireArguments())

        val app = requireNotNull(this.activity).application

        val dataSource = CovidDatabase.getInstance(app).covidDao

        val viewModelFactory = ViewFragmentViewModelFactory(
            dataSource, app, Pair(
                args.viewCovidKey,
                args.viewCovidDate
            )
        )

        val viewFragmentViewModel =ViewModelProvider(this, viewModelFactory).get(
            ViewFragmentViewModel::class.java
        )

        binding.vm = viewFragmentViewModel
        binding.lifecycleOwner = this

        binding.viewtabLayout.addTab(binding.viewtabLayout.newTab().setText(args.viewCovidDate))
        binding.viewtabLayout.addTab(binding.viewtabLayout.newTab().setText(getString(R.string.nameOgraniczenia)))

        binding.viewtabLayout.tabGravity = TabLayout.GRAVITY_FILL
        binding.viewtabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                when (tab.position) {
                    0 -> viewFragmentViewModel.setData()
                    1 -> viewFragmentViewModel.setDectription()
                    else -> viewFragmentViewModel.setData()
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

        viewFragmentViewModel.areaGovplx.observe(viewLifecycleOwner, Observer {
            (activity as AppCompatActivity?)!!.supportActionBar!!.title = it?.area?.name
            it?.let {
                //Animator.animateIncrementNumber(binding.Liczba,it.govpl.Liczba)
                viewFragmentViewModel.fazaUrl = it.fazy.idFazyKey.toString()
                viewFragmentViewModel.idGus = it.area.idGus.toString()

                //TODO 6 przenieść do  XML
                if (it.fazy.Color != null) {
                    binding.smiertelne.setBackgroundColor(Color.parseColor(it.fazy.Color))
                    binding.switchSmiertelne.setBackgroundColor(Color.parseColor(it.fazy.Color))
                    binding.Liczba.setBackgroundColor(Color.parseColor(it.fazy.Color))
                    binding.switchLiczba.setBackgroundColor(Color.parseColor(it.fazy.Color))
                    binding.Liczba10tys.setBackgroundColor(Color.parseColor(it.fazy.Color))
                    binding.switchLiczba10tys.setBackgroundColor(Color.parseColor(it.fazy.Color))
                    binding.Liczba10tys7.setBackgroundColor(Color.parseColor(it.fazy.Color))
                    binding.switchLiczba10tys7.setBackgroundColor(Color.parseColor(it.fazy.Color))
                    binding.Liczba10tysIndicator.setBackgroundColor(Color.parseColor(it.fazy.Color))
                }
            }
        })

        viewFragmentViewModel.swLiczba10tys7.observe(viewLifecycleOwner, Observer {
            if (it) {
                val tmpList = viewFragmentViewModel.listLiczba10tysAvg7.value
                if (tmpList != null) {
                    setLineChart(Pair("Średnia pozytywnych na 10tys za 7 dni", tmpList), binding)
                }
            }
        }
        )
        viewFragmentViewModel.listLiczba10tysAvg7.observe(viewLifecycleOwner, Observer {
            it?.let {
                setLineChart(Pair("Średnia pozytywnych na 10tys za 7 dni", it), binding)
            }
        }
        )
        viewFragmentViewModel.swLiczba10tys.observe(viewLifecycleOwner, Observer {
            if (it) {
                val tmpList = viewFragmentViewModel.listLiczba10tys.value
                if (tmpList != null) {
                    setLineChart(Pair("Pozytywnych na 10tys", tmpList), binding)
                }
            }
        }
        )
        viewFragmentViewModel.listLiczba10tys.observe(viewLifecycleOwner, Observer {
            it?.let {
                setLineChart(Pair("Pozytywnych na 10tys", it), binding)
            }
        }
        )
        viewFragmentViewModel.swSmiertelne.observe(viewLifecycleOwner, Observer {
            if (it) {
                val tmpList = viewFragmentViewModel.listSmiertelne.value
                if (tmpList != null) {
                    setLineChart(Pair("Liczba śmiertelnych", tmpList), binding)
                }
            }
        }
        )
        viewFragmentViewModel.listSmiertelne.observe(viewLifecycleOwner, Observer {
            it?.let {
                setLineChart(Pair("Liczba śmiertelnych", it), binding)
            }
        }
        )
        viewFragmentViewModel.swLiczba.observe(viewLifecycleOwner, Observer {
            if (it) {
                val tmpList = viewFragmentViewModel.listLiczba.value
                if (tmpList != null) {
                    setLineChart(Pair("Liczba pozytywnych", tmpList), binding)
                }
            }
        }
        )
        viewFragmentViewModel.listLiczba.observe(viewLifecycleOwner, Observer {
            it?.let {
                setLineChart(Pair("Liczba pozytywnych", it), binding)
            }
        })

        binding.reloadObostrzenia.setOnRefreshListener(OnRefreshListener {
            binding.obostrzenia.clearCache(true)
            binding.obostrzenia.reload()
            binding.reloadObostrzenia.setRefreshing(false)
        })

        viewFragmentViewModel.verObo.observe(viewLifecycleOwner, Observer {
            it?.let{
                viewFragmentViewModel.links = it.decription.split("|").toTypedArray()
            }
        })

        viewFragmentViewModel.oboSwNow.observe(viewLifecycleOwner, Observer {
            if (it and (viewFragmentViewModel.links.size>=1))
                setObostrzenia(BASEAPI_URL + viewFragmentViewModel.links[0]+viewFragmentViewModel.idGus.toString(), binding)
        })

        viewFragmentViewModel.oboSwNext.observe(viewLifecycleOwner, Observer {
            if (it and (viewFragmentViewModel.links.size>=viewFragmentViewModel.fazaUrl.toInt()+1))
                setObostrzenia(BASEAPI_URL + viewFragmentViewModel.links[viewFragmentViewModel.fazaUrl.toInt() + 1],binding)
        })

        return binding.root
    }

    private fun setObostrzenia(url: String, binding: FragmentViewBinding) {
        binding.obostrzenia.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String): Boolean {
                if (Uri.parse(url).host == BASEHOST_URL) {
                    return false
                }
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                startActivity(intent)
                return true
            }
        }
        enableJava(binding.obostrzenia.settings)
        if (CovidMonitorApplication.Variables.isNetworkConnected)
            binding.obostrzenia.loadUrl(url)
        else
            binding.obostrzenia.loadData(getString(R.string.notInternet), "text/html", "utf-8")
    }
    private fun setLineChart(dailys: Pair<String, List<Series>>, binding: FragmentViewBinding) {
        val DataSet =LineDataSet(
            dailys.second.mapIndexed { index, dailyItem ->Entry(dailyItem.x,dailyItem.y,dailyItem.Nazwa)}, dailys.first
        ).apply {
            setLineChartStyle(this, R.color.colorItemGrean)
        }

        val lineData = LineData(DataSet)

        with(binding.graph) {
            animateX(500)
            legend.textColor = R.color.colorAccent
            legend.textSize=10F


            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.textColor = R.color.colorAccent

            axisLeft.textColor = R.color.colorItemBlue

            legend.isEnabled = true
            axisRight.isEnabled = false
            description.isEnabled = false

            axisLeft.enableGridDashedLine(10f, 10f, 2f)
            xAxis.enableGridDashedLine(10f, 10f, 2f)

            var lastDate: String? = null
            val dates = dailys.second.map { val tmpData=it.Nazwa.takeLast(5);
                tmpData.takeLast(2)+"."+tmpData.take(2)} //FormatDate
            xAxis.valueFormatter = object : IndexAxisValueFormatter() {
                override fun getFormattedValue(value: Float): String {
/*                    if (lastDate == dates[value.toInt()]) return ""
                    lastDate = dates[value.toInt()]*/
                    return if(value.toInt()<dates.size) dates[value.toInt()] else ""
                }
            }

            axisLeft.valueFormatter = object : IndexAxisValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    val valueInt = value.toInt()
                    if (valueInt >= 1000) {
                        return "${valueInt / 1000}K"
                    }
                    return valueInt.toString()
                }
            }
            data = lineData
        }
    }

    private fun setLineChartStyle(lineDataSet: LineDataSet, @ColorRes colorResId: Int) {
        with(lineDataSet) {
            color = R.color.colorAccent
            lineWidth = 2f
            valueTextSize=10F
            circleRadius = 1f
            setDrawCircleHole(false)
            setCircleColor(colorResId)
            mode = LineDataSet.Mode.CUBIC_BEZIER
            valueTextColor = R.color.colorItemRed
            valueTextSize= 10F
            setDrawFilled(true)
            fillColor = R.color.colorAccent
           fillAlpha = 60
        }
    }


}
