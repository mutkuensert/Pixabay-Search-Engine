package com.mutkuensert.pixabaysearchengine.feature.image

import android.Manifest
import android.app.Activity
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.mutkuensert.pixabaysearchengine.databinding.FragmentImagesBinding
import com.mutkuensert.pixabaysearchengine.domain.ImageRequestModel
import com.mutkuensert.pixabaysearchengine.util.CHANNEL_ID
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

private const val TAG = "ImagesScreenFragment"

@AndroidEntryPoint
class ImagesFragment : Fragment() {
    private var _binding: FragmentImagesBinding? = null
    private val binding get() = _binding!!
    private val args: ImagesFragmentArgs by navArgs()
    private val viewModel: ImagesFragmentViewModel by viewModels()
    private lateinit var recyclerAdapter: ImagesRecyclerAdapter
    private var nextImageSearchConfiguration = ImageRequestModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        recyclerAdapter = ImagesRecyclerAdapter(downloadUrl = viewModel::downloadUrl)
        initActivityResultLauncher()
    }

    private fun initActivityResultLauncher() {
        var uri: Uri?

        viewModel.initDownloaderActivityResultLauncher {
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    uri = result.data?.data

                    writeToFile(requireContext(), uri, CHANNEL_ID)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentImagesBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setOnClickListeners()
        requestDownloadProgressIndicatorNotificationPermission()

        binding.recyclerView.layoutManager = LinearLayoutManager(this.context)
        binding.recyclerView.adapter = recyclerAdapter
        setObservers()

        viewModel.requestImages(args.imageRequestModel)

        setSpinners()

        binding.searchEditText.editText!!.setText(args.imageRequestModel.search)
    }

    private fun requestDownloadProgressIndicatorNotificationPermission() {
        val requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
                if (!isGranted) {
                    Toast.makeText(
                        requireContext(),
                        "The notification permission is required to show download progress in notifications.",
                        Toast.LENGTH_LONG
                    ).show()
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
                    Toast.LENGTH_LONG
                ).show()
            }

            else -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        }
    }

    private fun setArrayAdapterOfTheSpinner(spinner: Spinner, array: Array<String>) {
        ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, array).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = it
        }

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                (p0!!.getItemAtPosition(p2) as String).also { selectedItem ->
                    with(binding) {
                        with(nextImageSearchConfiguration) {
                            when (spinner) {
                                typeSpinner -> {
                                    imageType = selectedItem
                                }

                                orientationSpinner -> {
                                    orientation = selectedItem
                                }

                                colorSpinner -> {
                                    colors = if (selectedItem == "None") null else selectedItem
                                }

                                orderSpinner -> {
                                    order = selectedItem
                                }
                            }
                        }
                    }
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                with(binding) {
                    with(ImageRequestModel()) {
                        when (spinner) {
                            typeSpinner -> {
                                nextImageSearchConfiguration.imageType = imageType
                            }

                            orientationSpinner -> {
                                nextImageSearchConfiguration.orientation = orientation
                            }

                            colorSpinner -> {
                                nextImageSearchConfiguration.colors = colors
                            }

                            orderSpinner -> {
                                nextImageSearchConfiguration.order = order
                            }
                        }
                    }
                }
            }

        }

    }

    private fun setOnClickListeners() {
        binding.searchEditText.setStartIconOnClickListener {
            nextImageSearchConfiguration.search = binding.searchEditText.editText!!.text.toString()
            viewModel.requestImages(nextImageSearchConfiguration)
        }

        binding.searchEditText.setEndIconOnClickListener {
            with(binding.spinnersLayout) {
                visibility = if (visibility == View.VISIBLE) {
                    View.GONE
                } else {
                    View.VISIBLE
                }
            }
        }
    }

    private fun setObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.data.collectLatest { pagingData ->
                recyclerAdapter.submitData(pagingData)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            recyclerAdapter.loadStateFlow.collectLatest { loadState ->
                binding.progressBarLoadingMore.isVisible = loadState.append is LoadState.Loading
                binding.progressBarLoadingMore.isVisible = loadState.refresh is LoadState.Loading
            }
        }
    }

    private fun setSpinners() {
        setArrayAdapterOfTheSpinner(binding.orderSpinner, arrayOf("popular", "latest"))
        setArrayAdapterOfTheSpinner(
            binding.typeSpinner,
            arrayOf("all", "photo", "illustration", "vector")
        )
        setArrayAdapterOfTheSpinner(
            binding.orientationSpinner,
            arrayOf("all", "horizontal", "vertical")
        )
        setArrayAdapterOfTheSpinner(
            binding.colorSpinner,
            arrayOf(
                "None",
                "grayscale",
                "transparent",
                "red",
                "orange",
                "yellow",
                "green",
                "turquoise",
                "blue",
                "lilac",
                "pink",
                "white",
                "gray",
                "black",
                "brown"
            )
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}