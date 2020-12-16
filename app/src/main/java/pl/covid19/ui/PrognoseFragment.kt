package pl.covid19.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.google.android.material.tabs.TabLayout
import pl.covid19.R
import pl.covid19.databinding.FragmentPrognoseBinding
import pl.covid19.util.enableJava


class PrognoseFragment : Fragment() {
    //var mWebView: WebView? = null
    //private val mRefreshLayout: SwipeRefreshLayout? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val binding = DataBindingUtil.inflate<FragmentPrognoseBinding>(
            inflater,
            R.layout.fragment_prognose,
            container,
            false
        )

        binding.srICM.setOnRefreshListener(OnRefreshListener {
            binding.webview.clearCache(true)
            binding.webview.reload()
            binding.srICM.setRefreshing(false)
        })


        binding.webview.webViewClient = WebViewClient()
        enableJava(binding.webview.settings)
        binding.webview.setWebChromeClient(object : WebChromeClient() {
            override fun onProgressChanged(view: WebView, progress: Int) {
                if (progress == 100) {
                    binding.loadingSpinner.visibility = View.GONE
                }
                else
                    binding.loadingSpinner.visibility = View.VISIBLE
            }
        })

        binding.webview.loadUrl("https://covid19.mimuw.edu.pl/index.html")
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                when (tab.position) {
                    //TODO 4. getPrognoseUrls.php
                    0 -> binding.webview.loadUrl("https://covid19.mimuw.edu.pl")
                    1 -> binding.webview.loadUrl("https://covid-19.icm.edu.pl/biezace-prognozy")
                    2 -> binding.webview.loadUrl("https://mocos.pl/pl/prognosis")
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
        return binding.root
    }
}
