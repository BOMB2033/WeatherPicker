package com.example.weatherpicker.repository

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherpicker.databinding.ItemWeatherBinding


class WeatherDiffCallback : DiffUtil.ItemCallback<Weather>(){
    override fun areItemsTheSame(oldItem: Weather, newItem: Weather): Boolean {
        return oldItem.uid==newItem.uid
    }

    override fun areContentsTheSame(oldItem: Weather, newItem: Weather): Boolean {
        return  oldItem == newItem
    }

}
class WeathersViewHolder(private val binding: ItemWeatherBinding)
    :RecyclerView.ViewHolder(binding.root) {
    fun bind(weather: Weather, listener: WeathersAdapter.Listener) {
        binding.apply {
            nameWeather.text = weather.name
            root.setOnClickListener {
                listener.setWeather(weather)
            }
        }
    }
}

class WeathersAdapter(
    private val listener: Listener,
):ListAdapter<Weather, WeathersViewHolder>(WeatherDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeathersViewHolder {
        val binding = ItemWeatherBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WeathersViewHolder(binding)
    }
    override fun onBindViewHolder(holder: WeathersViewHolder, position:Int){
        val post = getItem(position)
        holder.bind(post, listener)
    }

    interface Listener{
        fun setWeather(weather: Weather)
    }
}
