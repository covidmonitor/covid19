package pl.covid19.ui.mainFragment


import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator
import kotlinx.android.synthetic.main.fragment_main.*
import pl.covid19.CovidMonitorApplication
import pl.covid19.MainActivity
import pl.covid19.R
import pl.covid19.database.AreaDB
import pl.covid19.database.CovidDatabase
import pl.covid19.database.DatabaseDao
import pl.covid19.databinding.FragmentMainBinding
import timber.log.Timber
import java.lang.Long.getLong
import java.text.SimpleDateFormat
import java.util.*

/*private const val LOCATION_PERMISSION_REQUEST = 1

private const val LOCATION_PERMISSION = "android.permission.ACCESS_FINE_LOCATION"*/

class MainFragment : Fragment() {
    lateinit var binding:FragmentMainBinding
    lateinit var dataSource :DatabaseDao
    lateinit var mainFragmentViewModel :MainFragmentViewModel
    lateinit var adapter :AreasCovidAdapter
    lateinit var adapterGF :AreasGFAdapter
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate<FragmentMainBinding>(inflater,R.layout.fragment_main,container,false)

        val application = requireNotNull(this.activity).application

        dataSource = CovidDatabase.getInstance(application).covidDao

        val viewModelFactory = MainFragmentViewModelFactory(dataSource, application)

        mainFragmentViewModel =ViewModelProvider(this, viewModelFactory).get(MainFragmentViewModel::class.java)

        binding.mainFragmentViewModel = mainFragmentViewModel
        binding.lifecycleOwner = this

        mainFragmentViewModel.navigateToViewFragment.observe(viewLifecycleOwner, Observer { it ->
            it?.let {
                this.findNavController()
                    .navigate(MainFragmentDirections.actionMainFragmentToViewFragment(
                        it.first,
                        it.second))
                mainFragmentViewModel.onViewFragmentNavigated()
            }
        })
        binding.swipeRefreshLayout.setOnRefreshListener {
            mainFragmentViewModel.onAreaRefresh()
            swipeRefreshLayout.setRefreshing(false)
        }

        val dividerItemDecoration = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)

        adapter = AreasCovidAdapter(AreasCovidListener { mainFragmentViewModel.onAreaClicked(it) })
        adapterGF = AreasGFAdapter(AreasGFListener { mainFragmentViewModel.onAGFClicked(it) })
        binding.areasList.adapter = adapter

        binding.areasList.addItemDecoration(dividerItemDecoration)

        val itemTouchHelper = ItemTouchHelper(simpleCallback)
        itemTouchHelper.attachToRecyclerView(binding.areasList)

        mainFragmentViewModel.areas.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.SubmitList(it)
            }
        })

        mainFragmentViewModel.AGF.observe(viewLifecycleOwner, Observer {
            it?.let {
                if (it.size > 0) {
                    AreasGFAdapter(AreasGFListener { mainFragmentViewModel.onAGFClicked(it) })
                    binding.areasList.adapter = adapterGF
                    adapterGF.SubmitList(it)
                }
            }
        })

        mainFragmentViewModel.verApk.observe(viewLifecycleOwner, Observer {
            if (it!=null) {
                val tmpUri = Uri.parse(it.decription)
                if (it.version != CovidMonitorApplication.Variables.version)
                    Snackbar.make(binding.root, getString(R.string.new_version), Snackbar.LENGTH_LONG)
                            .setAction(getString(R.string.OpenLink), View.OnClickListener() {
                                    val intents = Intent(Intent.ACTION_VIEW, tmpUri)
                                    startActivity(intents)})
                            .show()
            }
        })

        mainFragmentViewModel.maxDate.observe(viewLifecycleOwner, Observer {
            if (mainFragmentViewModel.maxDate.value == null) {
                (activity as AppCompatActivity?)!!.supportActionBar!!.title =
                    getString(R.string.mywatch) + " ?"
                Snackbar.make(binding.areasList,"Rozpoczęto pobieranie danych",Snackbar.LENGTH_LONG).show()
                    mainFragmentViewModel.onRefresh()

            } else {
                (activity as AppCompatActivity?)!!.supportActionBar!!.title =
                    getString(R.string.mywatch) + " " + mainFragmentViewModel.maxDate.value!!
                if (!mainFragmentViewModel.IsCurMaxDate()) {
                    mainFragmentViewModel.onRefreshList()
                    mainFragmentViewModel.onSetCurrToMaxDate()       }
            }
        })

        mainFragmentViewModel.notNavigate.observe(viewLifecycleOwner, Observer {
            if (mainFragmentViewModel.notNavigate.value == true) {
                Snackbar.make(binding.areasList,
                    "Trwa pobieranie danych sprawdź czy jest internet",
                    Snackbar.LENGTH_LONG).show()
                mainFragmentViewModel.stopNavigate()
            }
        })

      /*  mainFragmentViewModel.showNeedLocation.observe(viewLifecycleOwner, object: Observer<Boolean> {
            override fun onChanged(show: Boolean?) {
                if (show == true) {
                    Snackbar.make(binding.root,
                        "Brak lokalizacji. Włącz w ustawieniach lokalizację i sprawdź uprawnienia",Snackbar.LENGTH_LONG).show()
                }
            }
        })*/

    /*  Simple adapter
        val arrayAdapter =
            context?.let { ArrayAdapter(it, android.R.layout.simple_list_item_1, sampleList) }
        binding.sampleList.adapter = arrayAdapter
        binding.sampleList.setOnItemClickListener { parent, view, position, id ->
            view.findNavController()
                .navigate(
                    MainFragmentDirections.actionMainFragmentToViewFragment(
                        position.toLong(),
                        sampleList[position]
                    )
                )
        }*/

        binding.fabButton.setOnClickListener { view ->
            view.findNavController().navigate(MainFragmentDirections.actionMainFragmentToAddActivity2())
            //crashMe()
        }
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //requestLastLocationOrStartLocationUpdates()
    }

    var deletedArea: AreaDB? = null
    var simpleCallback: ItemTouchHelper.SimpleCallback =
        object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder,
            ): Boolean {
                return false
            }
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val pos =adapter.getNoteAt(viewHolder.adapterPosition)
                /*if (pos==null)
                    val pos =adapterGF.getNoteAt(viewHolder.adapterPosition)*/
                when (direction) {
                    ItemTouchHelper.LEFT -> {
                        //TODO 5 check why isnull
                        deletedArea = pos.let { mainFragmentViewModel.onGetArea(it) }
                        pos.let { mainFragmentViewModel.onDelete(it) }
                        Snackbar.make(binding.areasList, "Usunięto", Snackbar.LENGTH_LONG)
                            // .setAction(getString(R.string.undo), {mainFragmentViewModel.onInsert(deletedArea)})
                            .show()
                    }
                }
            }
            override fun onChildDraw(
                c: Canvas,recyclerView: RecyclerView,viewHolder: RecyclerView.ViewHolder,dX: Float,dY: Float,actionState: Int,isCurrentlyActive: Boolean,
            ) {
                RecyclerViewSwipeDecorator.Builder(context,c,recyclerView,viewHolder,dX,dY,actionState,isCurrentlyActive)
                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(context!!,R.color.colorAccent))
                    .addSwipeLeftActionIcon(R.drawable.ic_delete_black_24dp)
                    .addSwipeRightBackgroundColor(ContextCompat.getColor(context!!,R.color.colorPrimaryDark))
                    .addSwipeRightActionIcon(R.drawable.ic_archive_black_24dp)
                    .setActionIconTint(ContextCompat.getColor(recyclerView.context,android.R.color.white))
                    .create()
                    .decorate()
                super.onChildDraw(c,recyclerView,viewHolder,dX,dY,actionState,isCurrentlyActive)
            }
        }

    override fun onResume() {
        super.onResume()
        //requestLocationPermission()
    }

    /*override fun onDestroyView() {
        super.onDestroyView()
        this.binding = null
    }*/


   /* *//**
     * Show the user a dialog asking for permission to use location.
     *//*
    private fun requestLocationPermission() {
        requestPermissions(arrayOf(LOCATION_PERMISSION), LOCATION_PERMISSION_REQUEST)
    }

    *//**
     * Request the last location of this device, if known, otherwise start location updates.
     *
     * The last location is cached from the last application to request location.
     *//*
    private fun requestLastLocationOrStartLocationUpdates() {
        // if we don't have permission ask for it and wait until the user grants it
        if (ContextCompat.checkSelfPermission(requireContext(), LOCATION_PERMISSION) != PackageManager.PERMISSION_GRANTED) {
            requestLocationPermission()
            return
        }

        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location == null) {
                startLocationUpdates(fusedLocationClient)
            } else {
                mainFragmentViewModel.onLocationUpdated(location)
            }
        }
    }

    *//**
     * Start location updates, this will ask the operating system to figure out the devices location.
     *//*
    private fun startLocationUpdates(fusedLocationClient: FusedLocationProviderClient) {
        // if we don't have permission ask for it and wait until the user grants it
        if (ContextCompat.checkSelfPermission(requireContext(), LOCATION_PERMISSION) != PackageManager.PERMISSION_GRANTED) {
            requestLocationPermission()
            return
        }
        val request = LocationRequest().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        val callback = object: LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                val location = locationResult?.lastLocation ?: return
                mainFragmentViewModel.onLocationUpdated(location)
            }
        }
        fusedLocationClient.requestLocationUpdates(request, callback, null)
    }
    *//**
     * This will be called by Android when the user responds to the permission request.
     * If granted, continue with the operation that the user gave us permission to do.
     *//*
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode) {
            LOCATION_PERMISSION_REQUEST -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    requestLastLocationOrStartLocationUpdates()
                }
            }
        }
    }*/
    @Throws(NullPointerException ::class)
    fun crashMe(){
        throw  NullPointerException("Manually threw NullPointerException");
    }

}
