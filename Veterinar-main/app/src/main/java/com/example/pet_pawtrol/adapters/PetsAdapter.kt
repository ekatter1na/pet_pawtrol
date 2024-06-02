package com.example.pet_pawtrol.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.asLiveData
import androidx.recyclerview.widget.RecyclerView
import com.example.pet_pawtrol.Entity.Pets
import com.example.pet_pawtrol.MAIN
import com.example.pet_pawtrol.MainDb
import com.example.pet_pawtrol.R
import org.json.JSONObject

class PetsAdapter(private val petList: List<PetsModel>) : RecyclerView.Adapter<PetsAdapter.MyViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.pets_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PetsAdapter.MyViewHolder, position: Int) {
        val currentItem = petList[position]
        holder.tvNickname.text = currentItem.nickname
        holder.tvView.text = currentItem.pet_view
        holder.tvPoroda.text = currentItem.poroda
        holder.delButt.setOnClickListener {
            val db = MainDb.getDb(MAIN)
            val userId = getUserId()
            val petId = getPetId(userId, currentItem.nickname, currentItem.pet_view, currentItem.poroda)
            val pet = Pets(
                petId,
                currentItem.nickname,
                currentItem.pet_view,
                currentItem.poroda,
                userId
            )
            Thread {
                db.getDao().deletePet(pet)
            }.start()
            Toast.makeText(MAIN, "Питомец: ${currentItem.nickname} удалён", Toast.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount(): Int {
        return petList.size
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
    private fun getPetId(user_id: Int, nickname: String, view: String, poroda: String): Int {
        val database = MainDb.getDb(MAIN)
        var petId = 1
        val thread = Thread {
            petId = database.getDao().getPetsId(user_id, nickname, view, poroda)
        }
        thread.start()
        thread.join() // Ожидаем завершения потока
        return petId
    }


        class MyViewHolder(item: View) : RecyclerView.ViewHolder(item){

        val tvNickname : TextView = item.findViewById(R.id.tvItem_nickname)
        val tvView : TextView = item.findViewById(R.id.tv_Item_view)
        val tvPoroda : TextView = item.findViewById(R.id.tvItem_poroda)
        val delButt : ImageButton = item.findViewById(R.id.delImBt)
    }

}