package com.mutkuensert.pixabaysearchengine.ui.videosscreen

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.mutkuensert.pixabaysearchengine.data.video.VideoHitsModel
import com.mutkuensert.pixabaysearchengine.data.video.VideoRequestModel
import com.mutkuensert.pixabaysearchengine.databinding.FragmentVideosBinding
import com.mutkuensert.pixabaysearchengine.util.CHANNEL_ID
import com.mutkuensert.pixabaysearchengine.util.Status
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.cancel
import javax.inject.Inject

private const val TAG = "VideosFragment"

@AndroidEntryPoint
class VideosFragment : Fragment() {
    private var _binding: FragmentVideosBinding? = null
    private val binding get() = _binding!!
    private val args: VideosFragmentArgs by navArgs()
    private val nextVideoSearchConfiguration = VideoRequestModel()
    private val viewModel: VideosFragmentViewModel by viewModels()
    private lateinit var loadMoreVideoRequest: VideoRequestModel
    private var oldHitsList = mutableListOf<VideoHitsModel>()
    @Inject lateinit var recyclerAdapter: VideosRecyclerAdapter
    private lateinit var startForResult: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initStartForResult()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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

        loadMoreVideoRequest = args.videoRequestModel
        binding.searchEditText.editText?.setText(args.videoRequestModel.search)
    }

    override fun onDestroy() {
        super.onDestroy()
        recyclerAdapter.downloader.scope?.cancel()
        _binding = null
    }

    private fun initStartForResult(){
        var uri: Uri? = null
        startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
            if(result.resultCode == Activity.RESULT_OK){
                uri = result.data?.data
                recyclerAdapter.downloader.writeToFile(requireContext(), uri, CHANNEL_ID)
            }
        }
        recyclerAdapter.downloader.startForResult = startForResult
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
                        with(nextVideoSearchConfiguration){
                            when(spinner){
                                typeSpinner ->  { videoType = selectedItem }
                                orderSpinner -> {order = selectedItem}
                            }
                        }
                    }
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

                with(binding){
                    with(VideoRequestModel()){
                        when(spinner){
                            typeSpinner ->  { nextVideoSearchConfiguration.videoType = videoType }
                            orderSpinner -> {nextVideoSearchConfiguration.order = order}
                        }

                    }
                }
            }
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
                    resource.data?.let { videosModel ->
                        Log.i(TAG, videosModel.toString())
                        videosModel.hits?.let {
                            if(it.isEmpty()){
                                Toast.makeText(requireContext(), "The end of the search results.", Toast.LENGTH_LONG).show()
                            }else{
                                Log.d(TAG, it.toString())
                                recyclerAdapter.submitList(oldHitsList + it)
                                oldHitsList += it
                                binding.loadMoreButton.visibility = View.VISIBLE
                            }

                        }
                    }
                }
                Status.ERROR -> {
                    Toast.makeText(requireContext(), "Error.", Toast.LENGTH_LONG).show()
                    VideosFragmentDirections.actionVideosFragmentToSearchScreenFragment().also {
                        findNavController().navigate(it)
                    }
                }
            }
        }


    }

    private fun setOnClickListeners(){

        binding.searchEditText.setStartIconOnClickListener {
            nextVideoSearchConfiguration.search = binding.searchEditText.editText!!.text.toString()
            oldHitsList.clear()
            loadMoreVideoRequest = nextVideoSearchConfiguration.copy()
            viewModel.requestVideos(nextVideoSearchConfiguration)
        }

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
            loadMoreVideoRequest.page += 1
            viewModel.requestVideos(loadMoreVideoRequest)
        }
    }

    private fun setSpinners(){
        setArrayAdapterOfTheSpinner(binding.typeSpinner,arrayOf("all", "film", "animation"))
        setArrayAdapterOfTheSpinner(binding.orderSpinner, arrayOf("popular", "latest" ))
    }
}