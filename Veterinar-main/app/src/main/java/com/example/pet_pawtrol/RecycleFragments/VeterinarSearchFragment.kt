package com.example.pet_pawtrol.RecycleFragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pet_pawtrol.MAIN
import com.example.pet_pawtrol.MainDb
import com.example.pet_pawtrol.adapters.SearchAdapter
import com.example.pet_pawtrol.adapters.SearchModel
import com.example.pet_pawtrol.databinding.FragmentVeterinarSearchBinding
import kotlinx.coroutines.coroutineScope

class VeterinarSearchFragment : Fragment() {
    private lateinit var binding: FragmentVeterinarSearchBinding
    private lateinit var adapter: SearchAdapter
    var list = arrayListOf<SearchModel>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        binding = FragmentVeterinarSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?){
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launchWhenStarted {
            init()
        }
    }

    suspend fun init() = coroutineScope{
        initRcView()
    }
    private fun getData(): LiveData<List<SearchModel>> {
        val database = MainDb.getDb(MAIN)
        val listVet = MutableLiveData<List<SearchModel>>()
        val query = database.getDao().getVeterinarToSpec("Ветеринар")
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

    private fun initRcView() = with(binding){
        vetRecycleView.layoutManager = LinearLayoutManager(activity)
        adapter = SearchAdapter()
        getData().observe(viewLifecycleOwner){ vlist ->
            adapter.submitList(vlist)
            vetRecycleView.adapter = adapter
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = VeterinarSearchFragment()
    }
}