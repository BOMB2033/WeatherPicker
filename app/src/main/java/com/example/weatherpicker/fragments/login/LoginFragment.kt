package com.example.weatherpicker.fragments.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.example.weatherpicker.R
import com.example.weatherpicker.databinding.FragmentLoginBinding
import com.example.weatherpicker.repository.DataViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException

class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    private val viewModel: DataViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding){
            buttonRegistration.setOnClickListener {
                it.findNavController().navigate(R.id.action_loginFragment_to_registrationFragment)
            }

            buttonLogin.setOnClickListener {
                val email = editTextLogin.text.toString()
                val password = editTextPassword.text.toString()
                when{
                    email.isEmpty()|| password.isEmpty() -> showMessage("Заполните поля")
                    else -> toLogin(email, password)
                }
            }
        }
    }
    private fun showMessage(message:String){
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
    }
    private fun handleException(exception:Exception){
        try {
            throw exception
        } catch (e: FirebaseAuthInvalidUserException) {
            showMessage("Пользователь не найден")
            binding.progressBar.visibility = View.GONE
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            showMessage("Неверные учетные данные")
            binding.progressBar.visibility = View.GONE
        } catch (e: Exception) {
            showMessage("Ошибка")
            binding.progressBar.visibility = View.GONE
        }
    }
    private fun toLogin(email:String,password:String){
        binding.progressBar.visibility = View.VISIBLE
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener{
                if (it.isSuccessful) {
                    try {
                        viewModel.loadUser()
                        viewModel.loadWeather()
                        binding.progressBar.visibility = View.GONE
                        binding.root.findNavController()
                            .navigate(R.id.action_loginFragment_to_mainMenuFragment)
                    }catch (e:IllegalStateException){
                        e.printStackTrace()
                    }

                }else
                    handleException(it.exception!!)
            }
    }
}