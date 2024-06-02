package com.example.pet_pawtrol.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pet_pawtrol.MAIN
import com.example.pet_pawtrol.MainDb
import com.example.pet_pawtrol.R
import com.example.pet_pawtrol.adapters.PetsAdapter
import com.example.pet_pawtrol.adapters.PetsModel
import com.example.pet_pawtrol.adapters.UserProfile
import com.example.pet_pawtrol.databinding.FragmentProfileBinding
import org.json.JSONObject

class ProfileFragment : Fragment() {

    private lateinit var binding : FragmentProfileBinding
    private lateinit var adapter: PetsAdapter
    private lateinit var recyclerView: RecyclerView
    private var id_user = 1
    lateinit var bundle: Bundle

    private val db = MainDb.getDb(MAIN)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        binding = FragmentProfileBinding.inflate(inflater,  container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bundle = Bundle()

        binding.backImBt.setOnClickListener{
            MAIN.navController.navigate(R.id.action_profileFragment_to_searchFragment)
        }

        binding.addImBt.setOnClickListener {
            report()
        }

        binding.AppointmentBut.setOnClickListener{
            MAIN.navController.navigate(R.id.action_profileFragment_to_appointmentFragment)
        }

        init()
        initRecycle()
    }
    private fun init() = with(binding) {
        val jsonString = MAIN.pref?.getString("user", """
                                        {
                                            "user":{
                                                "firstname" : "Фамилия",
                                                "lastname" : "Имя",
                                                "phoneNumber" : "89000000",
                                                "email" : "user@mail.ru",
                                                "id_user" : 1
                                            }
                                        }
                                        """)!!
        val mainObject = JSONObject(jsonString)
        val item = UserProfile(
            mainObject.getJSONObject("user").getString("firstname"),
            mainObject.getJSONObject("user").getString("lastname"),
            mainObject.getJSONObject("user").getString("phoneNumber"),
            mainObject.getJSONObject("user").getString("email"),
            mainObject.getJSONObject("user").getInt("id_user")
        )

        tvSurname.text = item.firstname
        tvLastname.text = item.lastname
        tvPhonenumber.text = item.phoneNumber
        tvEmail.text = item.email
        id_user = item.id_user
    }
    private fun initRecycle() = with(binding) {
        val layoutManager = LinearLayoutManager(MAIN)
        recyclerView = binding.rvPets
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)
        genRecyclePets(id_user).observe(viewLifecycleOwner){ plist ->
            adapter = PetsAdapter(plist)
            recyclerView.adapter = adapter
        }
    }

    private fun genRecyclePets(user_id: Int): LiveData<List<PetsModel>> {
        try {
            val listPet = MutableLiveData<List<PetsModel>>()
            val query = db.getDao().getPets(user_id)
            query.asLiveData().observe(MAIN) { list ->
                val petsList = ArrayList<PetsModel>()
                list.forEach { pet ->
                    val item = PetsModel(
                        pet.nickname,
                        pet.petView,
                        pet.poroda
                    )
                    petsList.add(item)
                }
                listPet.value = petsList
            }
            return listPet
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

    private fun report(){
        bundle.putInt("id_user", id_user)
        findNavController().navigate(R.id.action_profileFragment_to_addPetsProfileFragment, bundle)
    }

    companion object {
        @JvmStatic
        fun newInstance() = ProfileFragment()
    }
}