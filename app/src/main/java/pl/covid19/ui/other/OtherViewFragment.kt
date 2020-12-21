package pl.covid19.ui.other


import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.*
import pl.covid19.R
import pl.covid19.database.CovidDatabase
import pl.covid19.databinding.FragmentOtherviewBinding
import pl.covid19.util.Constants
import pl.covid19.util.Constants.PRIVACY_NAME
import pl.covid19.util.Constants.PRIVACY_URL
import pl.covid19.util.enableJava


class OtherViewFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {


        val binding = DataBindingUtil.inflate<FragmentOtherviewBinding>(inflater,R.layout.fragment_otherview,container,false)

        val application = requireNotNull(this.activity).application

        val dataSource = CovidDatabase.getInstance(application).covidDao

        val viewModelFactory = OtherViewFragmentViewModelFactory(dataSource, application)

        val viewFragmentViewModel =ViewModelProvider(this, viewModelFactory).get(OtherViewFragmentViewModel::class.java)

        val args = OtherViewFragmentArgs.fromBundle(requireArguments())

        binding.OtherTextMulti.linksClickable= true
        binding.OtherTextMulti.movementMethod = LinkMovementMethod.getInstance()

        binding.webview.visibility = View.INVISIBLE
        binding.OtherTextMulti.visibility = View.VISIBLE

        @StringRes val resId = resources.getIdentifier("about_" + args.OtherViewKey.toString(),"string",context?.getPackageName())
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            binding.OtherTextMulti.text =
                Html.fromHtml(getString(resId), Html.FROM_HTML_MODE_LEGACY)
        else
            binding.OtherTextMulti.text = Html.fromHtml(getString(resId))

        (activity as AppCompatActivity?)!!.supportActionBar!!.title = args.OtherViewName
        binding.viewModel = viewFragmentViewModel
        binding.lifecycleOwner = this

        viewFragmentViewModel.networkStatus.observe(viewLifecycleOwner, Observer {
        //TODO NOT Network
        if (it && (args.OtherViewName == PRIVACY_NAME)) {
            binding.webview.visibility = View.VISIBLE
            binding.OtherTextMulti.visibility = View.INVISIBLE
            binding.webview.webViewClient = WebViewClient()
            enableJava(binding.webview.settings)
            binding.webview.loadUrl(PRIVACY_URL)
            }
        })

        return binding.root
    }


}
