package com.example.pet_pawtrol.adapters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.pet_pawtrol.R
import com.example.pet_pawtrol.databinding.SerchItemBinding

class SearchAdapter: ListAdapter<SearchModel, SearchAdapter.Holder>(Comparator()) {

    class Holder(view: View) : RecyclerView.ViewHolder(view){
        val binding = SerchItemBinding.bind(view)
        lateinit var bundle: Bundle

        init {
            bundle = Bundle()
        }

        fun bind(item: SearchModel) = with(binding){
            tvName.text = item.name
            //tvDate.text = item.ph_number
            tvprice.text = item.price.toString()
            tvOtz.text = item.otz.toString()
            moreInfBt.setOnClickListener{
                bundle.putString("urlProfile", item.urlProfile)
                findNavController(it).navigate(R.id.action_searchFragment_to_moreInformationFragment, bundle)
            }
            zapBut.setOnClickListener{
                bundle.putString("veterinarName", item.name)
                findNavController(it).navigate(R.id.action_searchFragment_to_makeAnAppointmentFragment, bundle)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.serch_item, parent, false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(getItem(position))
    }

    class Comparator: DiffUtil.ItemCallback<SearchModel>(){
        override fun areItemsTheSame(oldItem: SearchModel, newItem: SearchModel): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: SearchModel, newItem: SearchModel): Boolean {
            return oldItem == newItem
        }

    }
}