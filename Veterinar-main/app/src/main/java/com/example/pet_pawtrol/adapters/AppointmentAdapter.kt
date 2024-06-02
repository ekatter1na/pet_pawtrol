package com.example.pet_pawtrol.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.pet_pawtrol.Entity.MakeAnAppointment
import com.example.pet_pawtrol.MAIN
import com.example.pet_pawtrol.MainDb
import com.example.pet_pawtrol.R

class AppointmentAdapter (private val appointmentList: List<AppointmentModel>) : RecyclerView.Adapter<AppointmentAdapter.MyViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppointmentAdapter.MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.appointment_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = appointmentList[position]
        holder.tvVetName.text = currentItem.veterinarName
        holder.tvPet.text = currentItem.nicknamePets
        holder.tvEmail.text = currentItem.email
        holder.delBut.setOnClickListener {
            val db = MainDb.getDb(MAIN)
            val zap = MakeAnAppointment(
                currentItem.id,
                currentItem.email,
                currentItem.nicknamePets,
                currentItem.veterinarName,
                currentItem.user_id
            )
            Thread {
                db.getDao().deleteAppointment(zap)
            }.start()
            Toast.makeText(MAIN, "Запись удалена", Toast.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount(): Int {
        return appointmentList.size
    }

    class MyViewHolder(item: View) : RecyclerView.ViewHolder(item) {

        val tvVetName: TextView = item.findViewById(R.id.tvVetName)
        val tvPet: TextView = item.findViewById(R.id.tvPet)
        val tvEmail: TextView = item.findViewById(R.id.tvemail)
        val delBut: Button = item.findViewById(R.id.delBut)

    }
}