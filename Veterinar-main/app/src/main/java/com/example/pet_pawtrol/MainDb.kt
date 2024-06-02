package com.example.pet_pawtrol

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.pet_pawtrol.Entity.MakeAnAppointment
import com.example.pet_pawtrol.Entity.Pets
import com.example.pet_pawtrol.Entity.Users
import com.example.pet_pawtrol.Entity.Veterinars

@Database(entities = [Users::class, Pets::class, Veterinars::class, MakeAnAppointment::class], version = 2)
abstract class MainDb : RoomDatabase() {
    abstract fun getDao(): Dao
    companion object{

        fun getDb(context: Context): MainDb{
            return Room.databaseBuilder(
                context.applicationContext,
                MainDb :: class.java,
                "Pet_Pawtrol_Db"
            ).build()
        }

    }
}