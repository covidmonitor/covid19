package pl.covid19.ui.add


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import pl.covid19.R
import pl.covid19.databinding.FragmentAddBinding

/**
 * A simple [Fragment] subclass.
 */
class AddFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DataBindingUtil.inflate<FragmentAddBinding>(
            inflater,
            R.layout.fragment_add,
            container,
            false
        )

        binding.textView.text="Tu możliwość definiowania miejsca do obserwacji"

        return binding.root
    }

}
