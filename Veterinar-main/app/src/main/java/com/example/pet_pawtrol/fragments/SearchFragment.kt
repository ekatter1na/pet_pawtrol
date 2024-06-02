package com.example.pet_pawtrol.fragments

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.example.pet_pawtrol.MAIN
import com.example.pet_pawtrol.R
import com.example.pet_pawtrol.RecycleFragments.DermatologistRecycleFragment
import com.example.pet_pawtrol.RecycleFragments.DogHandlerRecycleFragment
import com.example.pet_pawtrol.RecycleFragments.GrymerSearchFragment
import com.example.pet_pawtrol.RecycleFragments.NutritionistRecycleFragment
import com.example.pet_pawtrol.RecycleFragments.SerchRecycleFragment
import com.example.pet_pawtrol.RecycleFragments.VeterinarSearchFragment
import com.example.pet_pawtrol.RecycleFragments.ZoopsychologistRecycleFragment
import com.example.pet_pawtrol.adapters.VpAdapter
import com.example.pet_pawtrol.databinding.FragmentSearchBinding
import com.google.android.material.tabs.TabLayoutMediator

class SearchFragment : Fragment() {

    private val fList = listOf(
        SerchRecycleFragment.newInstance(),
        VeterinarSearchFragment.newInstance(),
        DogHandlerRecycleFragment.newInstance(),
        GrymerSearchFragment.newInstance(),
        DermatologistRecycleFragment.newInstance(),
        ZoopsychologistRecycleFragment.newInstance(),
        NutritionistRecycleFragment.newInstance()
    )
    private val tList = listOf(
        "Все",
        "Ветеринар",
        "Кинолог",
        "Грумер",
        "Дерматолог",
        "Зоопсихолог",
        "Диетолог"
    )
    private lateinit var binding: FragmentSearchBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        checkNETConnection()

        binding.filterCardView.isVisible = false

        binding.filterImBt.setOnClickListener {
            binding.filterCardView.isVisible = true
        }

        binding.sortByPriceAButton.setOnClickListener{
            SerchRecycleFragment.newInstance().sortByPriceAscending()
            binding.filterCardView.isVisible = false
        }

        binding.sortByPriceDButt.setOnClickListener{
            SerchRecycleFragment.newInstance().sortByPriceDescending()
            binding.filterCardView.isVisible = false
        }

        binding.sortByRatingAButt.setOnClickListener{
            SerchRecycleFragment.newInstance().sortByRatingAscending()
            binding.filterCardView.isVisible = false
        }

        binding.sortByRatingDButt.setOnClickListener{
            SerchRecycleFragment.newInstance().sortByRatingDescending()
            binding.filterCardView.isVisible = false
        }

        binding.cancelButt.setOnClickListener{
            SerchRecycleFragment.newInstance().cancelSort()
            binding.filterCardView.isVisible = false
        }

        binding.backImBt.setOnClickListener{
            MAIN.navController.navigate(R.id.action_searchFragment_to_autorizationFragment)
        }

        binding.profImBt.setOnClickListener{
            MAIN.navController.navigate(R.id.action_searchFragment_to_profileFragment)
        }

        init()
    }

    private fun init() = with(binding) {
        val adapter = VpAdapter(activity as FragmentActivity, fList)
        vpList.adapter = adapter
        TabLayoutMediator(tLSearch, vpList){
            tab, pos -> tab.text = tList[pos]
        }.attach()
    }

    private fun checkNETConnection(){
        val connectivityManager = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
        val isConnected: Boolean = activeNetwork?.isConnected ?: false
        if(isConnected){
            Toast.makeText(activity, "Есть подключение к интернету", Toast.LENGTH_LONG).show()
        }
        else{
            Toast.makeText(activity, "Отсутствует подключение к интернету", Toast.LENGTH_LONG).show()
        }
    }

    companion object {

        @JvmStatic
        fun newInstance() = SearchFragment()
    }
}