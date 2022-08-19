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
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import coil.load
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
    private val selectedSearchOptions = mutableMapOf("imageOrVideo" to "image", "imageOrVideoType" to "all")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(requireActivity().window, false)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchScreenBinding.inflate(inflater, container, false)
        setDecorToFitSystemWindow()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setSpinners()
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
                        load(resource.data?.largeImageURL){
                            crossfade(true)
                        }
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

        binding.searchEditText.setEndIconOnClickListener {
            Log.d(TAG, "Search button has been clicked.")
        }
    }

    private fun setSpinners(){

        val imageAndVideo = arrayOf("image","video") //These are shouldn't be changed due to lower case restriction in the api.

        ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, imageAndVideo).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.imageOrVideoSpinner.adapter = it
        }

        binding.imageOrVideoSpinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                p0!!.getItemAtPosition(p2).also { item ->
                    setImageOrVideoTypeSpinnerContents(item as String)
                    selectedSearchOptions["imageOrVideo"] = item as String
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                selectedSearchOptions["imageOrVideo"] = "image"
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
                    selectedSearchOptions["imageOrVideoType"] = item as String
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                selectedSearchOptions["imageOrVideoType"] = "all"
            }
        }
    }

    private fun updateOwnerIdTextViewWithAnimation(text: String){
        binding.ownerIdTextCardView.animate()
            .alpha(0f)
            .setDuration(1000L)
            .setListener(object: AnimatorListenerAdapter(){
                override fun onAnimationEnd(animation: Animator?) {
                    binding.ownerIdTextView.text = text
                    makeOwnerIdTextCardViewVisibleAgainWithAnimation()
                }
            })
    }

    private fun setDecorToFitSystemWindow(){
        ViewCompat.setOnApplyWindowInsetsListener(binding.ownerIdTextCardView, OnApplyWindowInsetsListener { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())

            view.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                bottomMargin = insets.bottom
            }

            WindowInsetsCompat.CONSUMED
        })
    }
}