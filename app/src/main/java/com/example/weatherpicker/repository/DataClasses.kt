package com.example.weatherpicker.repository
data class DataClass(
    var users: List<User>,
    var weathers: List<Weather>,
)
data class User(
    var uid: String = "",
    var email:String = "",
    var weather: Weather = Weather(),
)
data class Weather(
    var uid:String = "",
    var name:String = "",
    var path:String = "",
)