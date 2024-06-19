package com.example.weatherpicker.fragments.misc

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.example.weatherpicker.R
import com.example.weatherpicker.databinding.FragmentChoseWeatherBinding
import com.example.weatherpicker.repository.DataViewModel
import com.example.weatherpicker.repository.Weather
import com.example.weatherpicker.repository.WeathersAdapter

class ChoseWeatherFragment : Fragment() {
    private lateinit var binding: FragmentChoseWeatherBinding
    private val viewModel:DataViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChoseWeatherBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding){
            val adapter = WeathersAdapter(object :WeathersAdapter.Listener{
                override fun setWeather(weather: Weather) {
                    viewModel.setWeather(weather)
                    binding.root.findNavController().popBackStack()
                }

            })

            recyclerView.adapter = adapter

            viewModel.data.observe(viewLifecycleOwner){
                adapter.submitList(it.weathers)
            }
            buttonBack.setOnClickListener {
                it.findNavController().popBackStack()
            }
        }
    }
}