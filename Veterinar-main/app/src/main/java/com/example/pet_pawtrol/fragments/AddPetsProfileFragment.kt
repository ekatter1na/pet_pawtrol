package com.example.pet_pawtrol.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.pet_pawtrol.Entity.Pets
import com.example.pet_pawtrol.MAIN
import com.example.pet_pawtrol.MainDb
import com.example.pet_pawtrol.R
import com.example.pet_pawtrol.databinding.FragmentAddPetsProfileBinding

class AddPetsProfileFragment : Fragment() {

    lateinit var binding: FragmentAddPetsProfileBinding
    private val db = MainDb.getDb(MAIN)


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddPetsProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val id = arguments?.getInt("id_user")
        binding.addPetBut.setOnClickListener {
            if (binding.etNickname.text.toString() == "" || binding.etPetView.text.toString() == "" || binding.etPoroda.text.toString() == "") {
                Toast.makeText(
                    MAIN,
                    "Все поля должны быть заполнены",
                    Toast.LENGTH_SHORT
                ).show()
            }
            else {
                val pattern = Regex(".*[/^1-9\\d!@#\$%^&*()_\\-=+\\\\|[\\]{}:;.,<>?]+\$/].*")
                if (pattern.matches(binding.etNickname.text.toString()) || pattern.matches(binding.etPetView.text.toString()) || pattern.matches(binding.etPoroda.text.toString())) {
                    Toast.makeText(
                        context,
                        "Были введены недопустимые символы",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    try {
                        val pet = Pets(
                            null,
                            binding.etNickname.text.toString(),
                            binding.etPetView.text.toString(),
                            binding.etPoroda.text.toString(),
                            id!!
                        )
                        Thread {
                            db.getDao().insertPet(pet)
                        }.start()
                        MAIN.navController.navigate(R.id.action_addPetsProfileFragment_to_profileFragment)
                        Toast.makeText(
                            MAIN,
                            "Питомец: ${binding.etNickname.text} добавлен",
                            Toast.LENGTH_SHORT
                        ).show()
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
        binding.backImBt.setOnClickListener {
            MAIN.navController.navigate(R.id.action_addPetsProfileFragment_to_profileFragment)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = AddPetsProfileFragment()
    }
}