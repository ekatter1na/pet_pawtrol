package com.example.pet_pawtrol.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.asLiveData
import com.example.pet_pawtrol.Entity.Users
import com.example.pet_pawtrol.MAIN
import com.example.pet_pawtrol.MainDb
import com.example.pet_pawtrol.R
import com.example.pet_pawtrol.databinding.FragmentRegistrationBinding

class RegistrationFragment : Fragment() {

    private lateinit var binding: FragmentRegistrationBinding
    private val db = MainDb.getDb(MAIN)
    var count = 0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        binding = FragmentRegistrationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.imgButtonAut.setOnClickListener {
            MAIN.navController.navigate(R.id.action_registrationFragment_to_autorizationFragment)
        }

        binding.regButtton.setOnClickListener {
            registrationUser()
        }
    }
    private fun registrationUser() {

        val database = MainDb.getDb(MAIN)
        if(nullPointerExamination()){
            if(lastNameAndFirstNameExamination()) {
                if (phoneNumberExamination()) {
                    if(emailExamination()) {
                        if (passwordExamination()) {
                            try {
                                database.getDao().getAllUser().asLiveData().observe(MAIN) { list ->
                                    if (list.isEmpty()) {
                                        val user = Users(
                                            null,
                                            binding.firstNameEditText.text.toString().trim(),
                                            binding.lastNameEditText.text.toString().trim(),
                                            binding.phoneNumberEditText.text.toString().trim(),
                                            binding.emailEditText.text.toString().trim(),
                                            binding.loginEditText.text.toString().trim(),
                                            binding.pasEditText.text.toString().trim()
                                        )
                                        Thread {
                                            db.getDao().insertUser(user)
                                        }.start()
                                        MAIN.navController.navigate(R.id.action_registrationFragment_to_autorizationFragment)
                                        Toast.makeText(
                                            MAIN,
                                            "Зарегистрирован пользователь ${binding.lastNameEditText.text.toString()}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    } else {

                                        list.forEach { userdata ->
                                            if (userdata.email == binding.emailEditText.text.toString()
                                                    .trim()
                                            ) {
                                                count = 1
                                                return@forEach
                                            }
                                        }
                                        if (count == 1) {
                                            Toast.makeText(
                                                MAIN,
                                                "Такой пользователь уже есть",
                                                Toast.LENGTH_SHORT
                                            )
                                                .show()
                                        }
                                        if (count == 0) {
                                            val user = Users(
                                                null,
                                                binding.firstNameEditText.text.toString(),
                                                binding.lastNameEditText.text.toString(),
                                                binding.phoneNumberEditText.text.toString(),
                                                binding.emailEditText.text.toString(),
                                                binding.loginEditText.text.toString(),
                                                binding.pasEditText.text.toString()
                                            )
                                            Thread {
                                                db.getDao().insertUser(user)
                                            }.start()
                                            MAIN.navController.navigate(R.id.action_registrationFragment_to_autorizationFragment)
                                            Toast.makeText(
                                                MAIN,
                                                "Зарегистрирован пользователь ${binding.lastNameEditText.text.toString()}",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                                }
                            }
                            catch (e: Exception) {
                                Toast.makeText(
                                    MAIN,
                                    "${e}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun nullPointerExamination(): Boolean {
        if (binding.firstNameEditText.text.toString() == "" ||
            binding.lastNameEditText.text.toString() == "" ||
            binding.phoneNumberEditText.text.toString() == "" ||
            binding.emailEditText.text.toString() == "" ||
            binding.loginEditText.text.toString() == "" ||
            binding.pasEditText.text.toString() == "" ||
            binding.dubPasEditText.text.toString() == "") {
            Toast.makeText(MAIN, "Все поля должны быть заполнены", Toast.LENGTH_SHORT).show()
            return false
        }
        else{
            return true
        }
    }

    private fun lastNameAndFirstNameExamination(): Boolean {
        val pattern = Regex(".*[/^1-9\\d!@#\$%^&*()_\\-=+\\\\|[\\]{}:;.,<>?]+\$/].*")
        if(pattern.matches(binding.firstNameEditText.text.toString()) || pattern.matches(binding.lastNameEditText.text.toString())) {
            Toast.makeText(MAIN, "Фамилия и имя не должны содержать цифры и спец символы", Toast.LENGTH_SHORT).show()
            return false
        }
        else{
            return true
        }
    }

    private fun passwordExamination(): Boolean {
        if(binding.pasEditText.text.toString().trim() == binding.dubPasEditText.text.toString().trim()) {
            return true
        }
        else{
            Toast.makeText(MAIN, "Пароли не совпадают", Toast.LENGTH_SHORT).show()
            return false
        }
    }

    private fun phoneNumberExamination(): Boolean {
        val pattern = Regex("^((8|\\+7)[\\- ]?)?(\\(?\\d{3}\\)?[\\- ]?)?[\\d\\- ]{7,10}\$")
        if(pattern.matches(binding.phoneNumberEditText.text.toString())) {
            return true
        }
        else{
            Toast.makeText(MAIN, "Номер телефона должен иметь один из следующих форматов: " +
                    "+79261234567\n" +
                    "89261234567\n" +
                    "79261234567\n" +
                    "+7 926 123 45 67\n" +
                    "8(926)123-45-67\n" +
                    "9261234567\n" +
                    "79261234567\n" +
                    "89261234567\n" +
                    "8-926-123-45-67\n" +
                    "8 927 1234 234\n" +
                    "8 927 12 12 888\n" +
                    "8 927 12 555 12\n" +
                    "8 927 123 8 123", Toast.LENGTH_SHORT).show()
            return false
        }
    }

    private fun emailExamination(): Boolean {
        val pattern = Regex("([a-zA-Z0-9._-]+@[a-zA-Z0-9._-]+\\.[a-zA-Z0-9_-]+)")
        if(pattern.matches(binding.emailEditText.text.toString())) {
            return true
        }
        else{
            Toast.makeText(MAIN, "адрес электронной почты имеет неверный формат", Toast.LENGTH_SHORT).show()
            return false
        }
    }

    companion object {
        fun newInstance() = RegistrationFragment()
    }
}