package com.mutkuensert.pixabaysearchengine.ui.searchscreen

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.mutkuensert.pixabaysearchengine.R
import com.mutkuensert.pixabaysearchengine.databinding.FragmentSearchScreenBinding

const val TAG = "SearchScreenFragment"
class SearchScreenFragment : Fragment() {
    private var _binding: FragmentSearchScreenBinding? = null
    private val binding get() = _binding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setSpinners()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setSpinners(){

        val imageAndVideo = arrayOf("image","video")

        ArrayAdapter(context!!, android.R.layout.simple_spinner_item, imageAndVideo).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.imageOrVideoSpinner.adapter = it
        }

        binding.imageOrVideoSpinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                p0!!.getItemAtPosition(p2).also {
                    setImageOrVideoTypeSpinnerIngredients(it as String)
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                //Will be implemented
            }

        }

    }

    private fun setImageOrVideoTypeSpinnerIngredients(type: String){
        val imageTypes = arrayOf("all","photo","illustration","vector") //all is default in api
        val videoTypes = arrayOf("all","film","animation") //all is default in api
        var types = arrayOf<String>()

        if(type == "image"){
            types = imageTypes
        } else if(type == "video"){
            types = videoTypes
        }

        ArrayAdapter(context!!, android.R.layout.simple_spinner_item, types).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.imageOrVideoTypeSpinner.adapter = it
        }

        binding.imageOrVideoTypeSpinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                //Will be implemented
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                //Will be implemented
            }

        }
    }
}