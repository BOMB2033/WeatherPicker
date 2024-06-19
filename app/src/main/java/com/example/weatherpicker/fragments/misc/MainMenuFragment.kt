package com.example.weatherpicker.fragments.misc

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.example.weatherpicker.R
import com.example.weatherpicker.databinding.FragmentMainMenuBinding
import com.example.weatherpicker.repository.DataViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup

class MainMenuFragment : Fragment() {
    private lateinit var binding:FragmentMainMenuBinding
    private val viewModel: DataViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainMenuBinding.inflate(inflater, container, false)
        return binding.root
    }
    data class WeatherData(
        val temp:String ="",
        val tempFeel:String ="",
        val tomorrow:String ="",
    )
    private suspend fun loadWhether(url: String):WeatherData {
        return withContext(Dispatchers.IO) {
            val doc = Jsoup.connect(url).get()
            WeatherData(
                 doc.select("div.temp.fact__temp.fact__temp_size_s").text(), // Измените этот селектор в соответствии с вашим сайтом
                doc.select("div.term.term_orient_h.fact__feels-like").text(), // Измените этот селектор в соответствии с вашим сайтом
                doc.select("div.title-icon__text").text() // Измените этот селектор в соответствии с вашим сайтом
            )
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch {
            while (
                viewModel.getUser().uid == "")
                delay(1000)
            with(binding){
                loadWhether(viewModel.getUser().weather.path).apply {
                    textTemp.text = temp
                    textTempFeel.text = tempFeel
                    textTomorrow.text = tomorrow
                }
                nameWeather.text = viewModel.getUser().weather.name
                binding.progressBar.visibility = View.GONE
            }
        }
        with(binding){
            buttonChangeWeather.setOnClickListener {
                it.findNavController().navigate(R.id.action_mainMenuFragment_to_choseWeatherFragment)
            }
        }

    }

}