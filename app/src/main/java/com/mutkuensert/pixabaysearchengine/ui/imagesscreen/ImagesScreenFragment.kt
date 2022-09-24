package com.mutkuensert.pixabaysearchengine.ui.imagesscreen

import android.Manifest
import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.getSystemService
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.mutkuensert.pixabaysearchengine.R
import com.mutkuensert.pixabaysearchengine.data.ImageHitsModel
import com.mutkuensert.pixabaysearchengine.data.ImageRequestModel
import com.mutkuensert.pixabaysearchengine.databinding.FragmentImagesScreenBinding
import com.mutkuensert.pixabaysearchengine.util.Status
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.cancel
import javax.inject.Inject

private const val TAG = "ImagesScreenFragment"
private const val CHANNEL_ID = "notification_channel_1"

@AndroidEntryPoint
class ImagesScreenFragment : Fragment() {
    private var _binding: FragmentImagesScreenBinding? = null
    private val binding get() = _binding!!
    private val args: ImagesScreenFragmentArgs by navArgs()
    private val viewModel: ImagesScreenViewModel by viewModels()
    @Inject lateinit var recyclerAdapter: ImagesRecyclerAdapter
    private var nextImageSearchConfiguration = ImageRequestModel()
    private var oldHitsList = mutableListOf<ImageHitsModel>()
    private val linearLayoutManager = LinearLayoutManager(this.context)
    private lateinit var loadMoreImageRequest: ImageRequestModel
    private lateinit var startForResult: ActivityResultLauncher<Intent>
    @Inject lateinit var imagesRecyclerAdapterClickListener: ImagesRecyclerAdapterClickListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createNotificationChannel()
        initStartForResult()
    }

    private fun initStartForResult(){
        var uri: Uri? = null
        startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
            if(result.resultCode == Activity.RESULT_OK){
                uri = result.data?.data
                imagesRecyclerAdapterClickListener.writeToFile(requireContext(), uri, CHANNEL_ID)
            }
        }
        imagesRecyclerAdapterClickListener.startForResult = startForResult
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentImagesScreenBinding.inflate(inflater, container, false)
        return binding.root
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setOnClickListeners()
        setObservers()
        requestDownloadProgressIndicatorNotificationPermission()

        binding.recyclerView.layoutManager = linearLayoutManager
        binding.recyclerView.adapter = recyclerAdapter

        viewModel.requestImages(args.imageRequestModel)

        setSpinners()

        loadMoreImageRequest = args.imageRequestModel
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_LOW // https://stackoverflow.com/a/45920861
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun requestDownloadProgressIndicatorNotificationPermission(){
        val requestPermissionLauncher =
            registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                if (!isGranted) {
                    Toast.makeText(
                        requireContext(),
                        "The notification permission is required to show download progress in notifications.",
                        Toast.LENGTH_LONG).show()
                }
            }
        when {
            /*ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED -> {
                // You can use the API that requires the permission.
            }
            */
            shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
                Toast.makeText(
                    requireContext(),
                    "The notification permission is required to show download progress in notifications.",
                    Toast.LENGTH_LONG).show()
        }
            else -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        }
    }

    private fun setArrayAdapterOfTheSpinner(spinner: Spinner, array: Array<String>){
        ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, array).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = it
        }

        spinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                (p0!!.getItemAtPosition(p2) as String).also{ selectedItem ->
                    with(binding){
                        with(nextImageSearchConfiguration){
                            when(spinner){
                                typeSpinner ->  { imageType = selectedItem }
                                orientationSpinner -> {orientation = selectedItem}
                                colorSpinner -> {colors = selectedItem}
                                orderSpinner -> {order = selectedItem}
                            }
                        }
                    }
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

                with(binding){
                        with(ImageRequestModel()){
                            when(spinner){
                                typeSpinner ->  { nextImageSearchConfiguration.imageType = imageType }
                                orientationSpinner -> {nextImageSearchConfiguration.orientation = orientation}
                                colorSpinner -> {nextImageSearchConfiguration.colors = colors}
                                orderSpinner -> {nextImageSearchConfiguration.order = order}
                            }

                        }
                }
            }

        }

    }

    private fun setOnClickListeners(){

        binding.searchEditText.setEndIconOnClickListener {
            with(binding.spinnersLayout){
                if(visibility == View.VISIBLE){
                    visibility = View.GONE
                }else{
                    visibility = View.VISIBLE
                }
            }
        }

        binding.loadMoreButton.setOnClickListener {
            it.visibility = View.GONE
            binding.progressBarLoadingMore.visibility = View.VISIBLE
            loadMoreImageRequest.page += 1
            viewModel.requestImages(loadMoreImageRequest)
        }
    }

    private fun setObservers(){

        viewModel.data.observe(viewLifecycleOwner){ resource ->
            when(resource.status){
                Status.STANDBY -> {}
                Status.LOADING -> {
                    binding.progressBarLoadingMore.visibility = View.VISIBLE
                }
                Status.SUCCESS -> {
                    binding.progressBarLoadingMore.visibility = View.GONE
                    binding.recyclerView.visibility = View.VISIBLE
                    resource.data?.let { imagesModel ->
                        Log.i(TAG, "$imagesModel")
                        imagesModel.hits?.let {
                            if(it.isEmpty()){
                                Toast.makeText(requireContext(), "The end of the search results.", Toast.LENGTH_LONG).show()
                            }else{
                                recyclerAdapter.submitList(oldHitsList + it)
                                oldHitsList += it
                                binding.loadMoreButton.visibility = View.VISIBLE
                            }

                        }
                    }
                }
                Status.ERROR -> {
                    Toast.makeText(requireContext(), "Error.", Toast.LENGTH_LONG).show()
                    ImagesScreenFragmentDirections.actionImagesScreenFragmentToSearchScreenFragment().also {
                        findNavController().navigate(it)
                    }
                }
            }
        }


    }

    private fun setSpinners(){
        setArrayAdapterOfTheSpinner(binding.orderSpinner, arrayOf("popular", "latest"))
        setArrayAdapterOfTheSpinner(binding.typeSpinner, arrayOf("all", "photo", "illustration", "vector" ))
        setArrayAdapterOfTheSpinner(binding.orientationSpinner, arrayOf("all", "horizontal", "vertical" ))
        setArrayAdapterOfTheSpinner(binding.colorSpinner, arrayOf("grayscale", "transparent", "red", "orange", "yellow", "green", "turquoise", "blue", "lilac", "pink", "white", "gray", "black", "brown" ))
    }

    override fun onDestroy() {
        super.onDestroy()
        imagesRecyclerAdapterClickListener.scope?.cancel()
        _binding = null
    }
}