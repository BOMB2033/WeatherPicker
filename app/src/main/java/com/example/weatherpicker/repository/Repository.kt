package com.example.weatherpicker.repository

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class RepositoryInMemoryImpl {

    private var databaseUsersReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("users")
    private var databaseWeatherReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("weathers")
    private val uid:String = Firebase.auth.currentUser!!.uid

    var dataClass = DataClass(
        emptyList(),
        emptyList(),
    )
    private val data = MutableLiveData(dataClass)

    fun getAll() = data

    fun loadUser() {
        val listener = object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                dataClass.users = emptyList()
                dataSnapshot.children.mapNotNull { it.getValue(User::class.java) }.forEach{
                    dataClass.users = dataClass.users.plus(it)
                }
                data.value = dataClass
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle error
            }
        }
        databaseUsersReference.addValueEventListener(listener)
    }

    fun addUser(email:String,weather:Weather) {
        dataClass.users = dataClass.users.plus(User(uid, email,weather))
        data.value = dataClass
        databaseUsersReference.child(uid).removeValue()
        databaseUsersReference.child(uid).setValue(User(uid, email,weather))
    }
    fun setWeather(weather: Weather) {
        dataClass.users.forEach {
            if (it.uid == uid){
                it.weather = weather
                data.value = dataClass
                databaseUsersReference.child(uid).removeValue()
                databaseUsersReference.child(uid).setValue(it)
            }
        }

    }
    fun getCurrentUser():User{
        dataClass.users.forEach {
            if (it.uid == uid) return it
        }
        return User()
    }

    fun loadWeather() {
        val listener = object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                dataClass.weathers = emptyList()
                dataSnapshot.children.mapNotNull { it.getValue(Weather::class.java) }.forEach{
                    dataClass.weathers = dataClass.weathers.plus(it)
                }
                data.value = dataClass
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle error
            }
        }
        databaseWeatherReference.addValueEventListener(listener)
        /*databaseWeatherReference.setValue(
            listOf(
                Weather("1", "Борисоглебск","https://yandex.ru/pogoda/borisoglebsk"),
                Weather("2", "Воронеж","https://yandex.ru/pogoda/voronezh"),
                Weather("3", "Москва","https://yandex.ru/pogoda/moscow"),
                Weather("4", "Лондон","https://yandex.ru/pogoda/london"),
                Weather("5", "Нью Йорк","https://yandex.ru/pogoda/new-york"),
                Weather("6", "Сочи","https://yandex.ru/weather/sochi"),
                Weather("7", "Багдад","https://yandex.ru/pogoda/baghdad"),
            )
        )*/
    }


}


class DataViewModel : ViewModel() {
    private val repository = RepositoryInMemoryImpl()
    val data = repository.getAll()
    val uid:String = Firebase.auth.currentUser!!.uid
    fun getUser() = repository.getCurrentUser()
    fun addUser(email: String,weather: Weather) = repository.addUser(email,weather)
    fun loadUser() = repository.loadUser()
    fun loadWeather() = repository.loadWeather()
    fun setWeather(weather: Weather) = repository.setWeather(weather)
}