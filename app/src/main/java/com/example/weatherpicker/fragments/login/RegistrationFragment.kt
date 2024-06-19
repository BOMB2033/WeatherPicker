package com.example.weatherpicker.fragments.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.example.weatherpicker.R
import com.example.weatherpicker.databinding.FragmentRegistrationBinding
import com.example.weatherpicker.repository.DataViewModel
import com.example.weatherpicker.repository.Weather
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class RegistrationFragment : Fragment() {
    private lateinit var binding:FragmentRegistrationBinding
    private val viewModel: DataViewModel by activityViewModels()
    private var weathers = Weather()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegistrationBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch {
            while (weathers.uid == "")
                delay(1000)
            binding.progressBar.visibility = View.GONE
        }
        loadWeather()
        with(binding){
            buttonRegistration.setOnClickListener {
                it.findNavController().navigate(R.id.action_loginFragment_to_registrationFragment)
            }

            buttonRegistration.setOnClickListener {
                val email = editTextLogin.text.toString()
                val password = editTextPassword.text.toString()
                when{
                    email.isEmpty() && password.isEmpty() -> showMessage("Заполните поля")
                    else -> toLogin(email, password)
                }
            }
        }
    }
    private var databaseWeatherReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("weathers")
    private fun loadWeather() {
        val listener = object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var temp = emptyList<Weather>()
                dataSnapshot.children.mapNotNull { it.getValue(Weather::class.java) }.forEach{
                    temp = temp.plus(it)
                }
                binding.spinner.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, temp.map{it.name})

                binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(parent: AdapterView<*>?) {
                        // Do nothing
                    }

                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        weathers = temp[position]
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle error
            }
        }
        databaseWeatherReference.addValueEventListener(listener)
//        databaseWeatherReference.setValue(
//            listOf(
//                Weather("1", "Борисоглебск","https://yandex.ru/pogoda/borisoglebsk"),
//                Weather("2", "Воронеж","https://yandex.ru/pogoda/voronezh"),
//            )
//        )
    }
    private fun showMessage(message:String){
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
    }
    private fun handleException(exception:Exception){
        try {
            throw exception
        } catch (e: FirebaseAuthWeakPasswordException) {
            showMessage("Слабый пароль")
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            showMessage("Неверные учетные данные")
        } catch (e: FirebaseAuthUserCollisionException) {
            showMessage("Пользователь уже существует")
        } catch (e: Exception) {
            showMessage("Ошибка регистрации")
        }
    }
    private fun toLogin(email:String,password:String){
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener{
                if (it.isSuccessful) {
                    showMessage("Регистрация прошла успешно!")
                    viewModel.addUser(email,weathers)
                    binding.root.findNavController().popBackStack()
                }else
                    handleException(it.exception!!)
            }
    }

}