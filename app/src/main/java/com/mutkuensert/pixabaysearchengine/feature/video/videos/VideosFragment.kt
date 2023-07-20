package com.mutkuensert.pixabaysearchengine.feature.video.videos

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.mutkuensert.pixabaysearchengine.databinding.FragmentVideosBinding
import com.mutkuensert.pixabaysearchengine.domain.VideoRequestModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

private const val TAG = "VideosFragment"

@AndroidEntryPoint
class VideosFragment : Fragment() {
    private var _binding: FragmentVideosBinding? = null
    private val binding get() = _binding!!
    private val args: VideosFragmentArgs by navArgs()
    private val nextVideoSearchConfiguration = VideoRequestModel()
    private val viewModel: VideosFragmentViewModel by viewModels()
    private lateinit var recyclerAdapter: VideosRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        recyclerAdapter = VideosRecyclerAdapter(downloadUrl = viewModel::downloadUrl)
        initStartForResult()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVideosBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerView.layoutManager = LinearLayoutManager(this.context)
        binding.recyclerView.adapter = recyclerAdapter

        setOnClickListeners()
        setSpinners()
        setObservers()

        viewModel.requestVideos(args.videoRequestModel)

        binding.searchEditText.editText?.setText(args.videoRequestModel.search)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun initStartForResult() {
        var uri: Uri?
        viewModel.initDownloaderActivityResultLauncher {
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    uri = result.data?.data

                    if (uri != null) writeToFile(uri!!)
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
                        with(nextVideoSearchConfiguration) {
                            when (spinner) {
                                typeSpinner -> {
                                    videoType = selectedItem
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
                    with(VideoRequestModel()) {
                        when (spinner) {
                            typeSpinner -> {
                                nextVideoSearchConfiguration.videoType = videoType
                            }

                            orderSpinner -> {
                                nextVideoSearchConfiguration.order = order
                            }
                        }
                    }
                }
            }
        }
    }

    private fun setObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.data.collectLatest {
                recyclerAdapter.submitData(it)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            recyclerAdapter.loadStateFlow.collectLatest { loadState ->
                binding.progressBarLoadingMore.isVisible = loadState.append is LoadState.Loading
                binding.progressBarLoadingMore.isVisible = loadState.refresh is LoadState.Loading
            }
        }
    }

    private fun setOnClickListeners() {
        binding.searchEditText.setStartIconOnClickListener {
            nextVideoSearchConfiguration.search = binding.searchEditText.editText!!.text.toString()
            viewModel.requestVideos(nextVideoSearchConfiguration)
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

    private fun setSpinners() {
        setArrayAdapterOfTheSpinner(binding.typeSpinner, arrayOf("all", "film", "animation"))
        setArrayAdapterOfTheSpinner(binding.orderSpinner, arrayOf("popular", "latest"))
    }
}