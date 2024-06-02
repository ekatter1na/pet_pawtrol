package com.example.pet_pawtrol.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import com.example.pet_pawtrol.Entity.MakeAnAppointment
import com.example.pet_pawtrol.MAIN
import com.example.pet_pawtrol.MainDb
import com.example.pet_pawtrol.R
import com.example.pet_pawtrol.adapters.PetsModel
import com.example.pet_pawtrol.adapters.SearchModel
import com.example.pet_pawtrol.databinding.FragmentMakeAnAppointmentBinding
import org.json.JSONObject

class MakeAnAppointmentFragment : Fragment() {
    
    lateinit var binding: FragmentMakeAnAppointmentBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        binding =  FragmentMakeAnAppointmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        selectPetsArray()

        binding.makeButton.setOnClickListener{
            val database = MainDb.getDb(MAIN)
            val veterinarName = arguments?.getString("veterinarName","")
            var veterinarEmail = ""
            getData().observe(viewLifecycleOwner){ vlist ->
                vlist.forEach{
                    veterinarEmail = it.email!!
                    val appointment = MakeAnAppointment(
                        null,
                        veterinarEmail,
                        binding.spPets.getSelectedItem().toString(),
                        veterinarName.toString(),
                        getUserId()
                    )

                    sendEmail(veterinarEmail, "Запись на приём", "${binding.spPets.getSelectedItem()}", getUserName(), getUserPhoneNumber(), MAIN)

                    Thread{
                        database.getDao().insertAppointment(appointment)
                    }.start()
                }
            }

            Toast.makeText(MAIN, "Питомец: ${binding.spPets.getSelectedItem()} записан к ветеринару: ${veterinarName.toString()}", Toast.LENGTH_SHORT).show()

        }
        binding.backSearchButton.setOnClickListener{
            MAIN.navController.navigate(R.id.action_makeAnAppointmentFragment_to_searchFragment)
        }
    }

    fun spinnerInit(petsArray: MutableList<String>){
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1,  petsArray)
        binding.spPets.adapter = adapter
    }

        fun selectPetsArray(){
        val userId = getUserId()
        val list = mutableListOf<String>()
        genRecyclePets(userId).observe(viewLifecycleOwner){ plist ->
            plist.forEach{ pet->
                list.add(pet.nickname)
            }
            spinnerInit(list)
        }

    }

    private fun getData(): LiveData<List<SearchModel>>{
        val veterinarName = arguments?.getString("veterinarName","")
        val database = MainDb.getDb(MAIN)
        val listVet = MutableLiveData<List<SearchModel>>()
        val query = database.getDao().getVeterinarByName(veterinarName)
        query.asLiveData().observe(viewLifecycleOwner){vetlist->
            val vetirList = ArrayList<SearchModel>()
            vetlist.forEach{ veterinars ->
                val vet = SearchModel(
                    veterinars.name,
                    veterinars.email,
                    veterinars.comment,
                    veterinars.specialization,
                    veterinars.price,
                    veterinars.urlProfile
                )
                vetirList.add(vet)
            }
            listVet.value = vetirList
        }
        return listVet
    }
    fun getUserId(): Int {
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
        val user_id = mainObject.getJSONObject("user").getInt("id_user")
        return user_id
    }
    fun getUserName(): String {
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
        val firstname = mainObject.getJSONObject("user").getString("firstname")
        val lastname = mainObject.getJSONObject("user").getString("lastname")
        val name = firstname + " " + lastname
        return name
    }
    fun getUserPhoneNumber(): String {
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
        val phoneNumber = mainObject.getJSONObject("user").getString("phoneNumber")
        return phoneNumber
    }
    private fun genRecyclePets(user_id: Int): LiveData<List<PetsModel>> {
        val database = MainDb.getDb(MAIN)
        val listPet = MutableLiveData<List<PetsModel>>()
        val query = database.getDao().getPets(user_id)
        query.asLiveData().observe(MAIN){ list ->
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

    fun sendEmail(to: String, subject: String, petName: String, name: String, phoneNumber: String, context: Context) {
        val emailIntent = Intent(Intent.ACTION_SEND).apply {
            type = "message/rfc822"
            putExtra(Intent.EXTRA_EMAIL, arrayOf(to))
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, "Здравствуйте, к вам на приём хотят записать питомца: $petName, " +
                    "пожалуйста свяжитесь с хозяином: $name " +
                    "по номеру: $phoneNumber " +
                    "для подтверждения записи, всего доброго")
        }

        try {
            context.startActivity(Intent.createChooser(emailIntent, "Send mail..."))
            println("Sent message successfully....")
        } catch (ex: android.content.ActivityNotFoundException) {
            println("There is no email client installed.")
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = MakeAnAppointmentFragment()
    }
}