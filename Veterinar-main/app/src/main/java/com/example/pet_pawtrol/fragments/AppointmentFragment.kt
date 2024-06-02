package com.example.pet_pawtrol.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pet_pawtrol.MAIN
import com.example.pet_pawtrol.MainDb
import com.example.pet_pawtrol.R
import com.example.pet_pawtrol.adapters.AppointmentAdapter
import com.example.pet_pawtrol.adapters.AppointmentModel
import com.example.pet_pawtrol.databinding.FragmentAppointmentBinding
import org.json.JSONObject

class AppointmentFragment : Fragment() {

    lateinit var binding: FragmentAppointmentBinding
    private var id_user = 1
    private lateinit var adapter: AppointmentAdapter
    private lateinit var recyclerView: RecyclerView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        binding = FragmentAppointmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backBut.setOnClickListener {
            MAIN.navController.navigate(R.id.action_appointmentFragment_to_profileFragment)
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
        id_user = mainObject.getJSONObject("user").getInt("id_user")
    }

    private fun initRecycle() = with(binding) {
        val layoutManager = LinearLayoutManager(MAIN)
        recyclerView = binding.rcAppointment
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)
        genRecycleAppointment(id_user).observe(viewLifecycleOwner){ alist ->
            adapter = AppointmentAdapter(alist)
            recyclerView.adapter = adapter
        }
    }

    private fun genRecycleAppointment(user_id: Int): LiveData<List<AppointmentModel>> {
        val database = MainDb.getDb(MAIN)
        val listAppoint = MutableLiveData<List<AppointmentModel>>()
        val query = database.getDao().getAppointment(user_id)
        query.asLiveData().observe(MAIN){ list ->
            val appoinList = ArrayList<AppointmentModel>()
            list.forEach { appoint ->
                val item = AppointmentModel(
                    appoint.ID,
                    appoint.email,
                    appoint.nicknamePets,
                    appoint.veterinarName,
                    appoint.id_user
                )
                appoinList.add(item)
            }
            listAppoint.value = appoinList
        }
        return listAppoint
    }
    companion object {
        @JvmStatic
        fun newInstance() = AppointmentFragment()
    }
}