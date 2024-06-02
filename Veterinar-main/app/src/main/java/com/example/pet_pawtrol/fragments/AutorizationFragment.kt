package com.example.pet_pawtrol.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.asLiveData
import com.example.pet_pawtrol.MAIN
import com.example.pet_pawtrol.MainDb
import com.example.pet_pawtrol.R
import com.example.pet_pawtrol.databinding.FragmentAutorizationBinding

class AutorizationFragment : Fragment(){

    private lateinit var binding: FragmentAutorizationBinding
    var count = 0


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        binding = FragmentAutorizationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.regButton.setOnClickListener{
            MAIN.navController.navigate(R.id.action_autorizationFragment_to_registrationFragment)
        }

        binding.enterButt.setOnClickListener{
            autorization()
        }
    }

    fun autorization(){
        if(binding.loginEditText.text.toString() == "" || binding.pasEditText.text.toString() == ""){
            Toast.makeText(MAIN, "Все поля должны быть заполнены", Toast.LENGTH_SHORT).show()
        }
        else {
            try {
                val database = MainDb.getDb(MAIN)
                database.getDao().getAllUser().asLiveData().observe(MAIN) { list ->

                    list.forEach { user ->

                        if (binding.loginEditText.text.toString().trim() == user.login) {

                            if (binding.pasEditText.text.toString().trim() == user.password) {
                                val userString = """
                                        {
                                            "user":{
                                                "firstname" : "${user.firstname}",
                                                "lastname" : "${user.lastname}",
                                                "phoneNumber" : "${user.phoneNumber}",
                                                "email" : "${user.email}",
                                                "id_user" : ${user.ID}
                                            }
                                        }
                                        """
                                MAIN.saveData(userString)
                                MAIN.navController.navigate(R.id.action_autorizationFragment_to_searchFragment)

                                count = 1
                                Toast.makeText(
                                    MAIN,
                                    "Пользователь: ${user.lastname} авторизирован",
                                    Toast.LENGTH_SHORT
                                ).show()
                                return@forEach
                            }
                        }
                    }
                    if (count == 0) {
                        Toast.makeText(MAIN, "Неверный логин или пароль", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            catch (e: Exception) {
                Toast.makeText(
                    MAIN,
                    "${e}",
                    Toast.LENGTH_SHORT
                ).show()
                return TODO("Provide the return value")
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = AutorizationFragment()
    }
}