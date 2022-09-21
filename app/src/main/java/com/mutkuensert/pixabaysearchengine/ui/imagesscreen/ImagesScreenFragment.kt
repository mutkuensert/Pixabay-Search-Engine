package com.mutkuensert.pixabaysearchengine.ui.imagesscreen

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.mutkuensert.pixabaysearchengine.data.ImageHitsModel
import com.mutkuensert.pixabaysearchengine.data.ImageRequestModel
import com.mutkuensert.pixabaysearchengine.databinding.FragmentImagesScreenBinding
import com.mutkuensert.pixabaysearchengine.util.Status
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.cancel
import javax.inject.Inject

private const val TAG = "ImagesScreenFragment"

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
        initStartForResult()
    }

    private fun initStartForResult(){
        var uri: Uri? = null
        startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
            if(result.resultCode == Activity.RESULT_OK){
                uri = result.data?.data
                imagesRecyclerAdapterClickListener.scope?.cancel()
                imagesRecyclerAdapterClickListener.writeToFile(requireContext(), uri)
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

        binding.recyclerView.layoutManager = linearLayoutManager
        binding.recyclerView.adapter = recyclerAdapter

        viewModel.requestImages(args.imageRequestModel)

        setSpinners()

        loadMoreImageRequest = args.imageRequestModel
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
        _binding = null
    }
}