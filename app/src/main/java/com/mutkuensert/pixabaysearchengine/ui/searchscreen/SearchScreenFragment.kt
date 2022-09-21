package com.mutkuensert.pixabaysearchengine.ui.searchscreen

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.view.*
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.mutkuensert.pixabaysearchengine.data.ImageRequestModel
import com.mutkuensert.pixabaysearchengine.databinding.FragmentSearchScreenBinding
import com.mutkuensert.pixabaysearchengine.util.Status
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*

private const val TAG = "SearchScreenFragment"

@AndroidEntryPoint
class SearchScreenFragment : Fragment() {
    private var _binding: FragmentSearchScreenBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SearchScreenFragmentViewModel by viewModels()
    private var imageOrVideoSelection: String = "image"
    private var imageRequestModel = ImageRequestModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setImageOrVidepSpinner()
        setObserversAndClickListeners()
        //viewModel.requestSearchScreenBackgroundImage()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun makeOwnerIdTextCardViewVisibleAgainWithAnimation(){
        binding.ownerIdTextCardView.animate()
            .alpha(1f)
            .setDuration(1000L)
            .setListener(null)
    }

    private fun setObserversAndClickListeners(){

        viewModel.backgroundImageData.observe(viewLifecycleOwner){ resource->

            Log.i(TAG, "observeBackgroundImageStringUrl(): viewModel.backgroundImageUrl = ${resource.data}")

            with(binding.backgroundImage){
                when(resource.status){
                    Status.STANDBY -> println("")//setImageDrawable(null)

                    Status.LOADING -> CircularProgressDrawable(this.context).apply {
                        strokeWidth = 15f
                        centerRadius = 550f
                        setColorSchemeColors(Color.GRAY)
                        start()
                    }.also { setImageDrawable(it) }

                    Status.SUCCESS -> {
                        Glide.with(this).load(resource.data?.largeImageURL).into(this)
                        val ownerText = "Owner: ${resource.data?.user}"
                        updateOwnerIdTextViewWithAnimation(ownerText)
                    }

                    Status.ERROR -> setImageDrawable(null)
                }
            }
        }

        binding.pixabayLogoImageView.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://pixabay.com/")),null)
        }

        //This is search icon button.
        binding.searchEditText.setEndIconOnClickListener {
            //TODO("Put the right (image or video)requestModel as parameter.")
            imageRequestModel.search = binding.searchEditText.editText!!.text.toString()
            SearchScreenFragmentDirections.actionSearchScreenFragmentToImagesScreenFragment(imageRequestModel).also {
                findNavController().navigate(it)
            }
        }
    }

    private fun setImageOrVidepSpinner(){

        val imageAndVideo = arrayOf("image","video") //These are shouldn't be changed due to lower case restriction in the api.

        ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, imageAndVideo).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.imageOrVideoSpinner.adapter = it
        }

        binding.imageOrVideoSpinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                p0!!.getItemAtPosition(p2).also { item ->
                    setImageOrVideoTypeSpinnerContents(item as String)
                    imageOrVideoSelection = item as String

                    if(item == "video") imageRequestModel = ImageRequestModel() //It's being set to default.
                    //TODO("If item ='image', videoRequestModel should be set to default.")
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                imageOrVideoSelection = "image"
            }
        }
    }

    private fun setImageOrVideoTypeSpinnerContents(type: String){
        val imageTypes = arrayOf("all","photo","illustration","vector") //all is default in api
        val videoTypes = arrayOf("all","film","animation") //all is default in api
        var types = arrayOf<String>()

        if(type == "image"){
            types = imageTypes
        } else if(type == "video"){
            types = videoTypes
        }

        ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, types).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.imageOrVideoTypeSpinner.adapter = it
        }

        binding.imageOrVideoTypeSpinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                p0!!.getItemAtPosition(p2).also { item ->

                    if(type == "image") imageRequestModel.imageType = item as String

                    //if(type == "video") TODO("Video type in videoRequestModel should be changed.")
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                if(type == "image") imageRequestModel.imageType = "all"
                //TODO("If it's video, set type in videoRequestModel to all, otherwise do the opposite")
            }
        }
    }

    private fun updateOwnerIdTextViewWithAnimation(text: String){
        binding.ownerIdTextCardView.animate()
            .alpha(0f)
            .setDuration(1000L)
            .setListener(object: AnimatorListenerAdapter(){
                override fun onAnimationEnd(animation: Animator) {
                    binding.ownerIdTextView.text = text
                    makeOwnerIdTextCardViewVisibleAgainWithAnimation()
                }
            })
    }
}