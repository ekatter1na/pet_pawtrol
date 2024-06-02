package com.example.pet_pawtrol

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.pet_pawtrol.Entity.MakeAnAppointment
import com.example.pet_pawtrol.Entity.Pets
import com.example.pet_pawtrol.Entity.Users
import com.example.pet_pawtrol.Entity.Veterinars
import kotlinx.coroutines.flow.Flow

@Dao
interface Dao {

    @Insert
    fun insertUser(user: Users)

    @Query("SELECT * FROM USERS")
    fun getAllUser(): Flow<List<Users>>

    @Insert
    fun insertPet(pet: Pets)

    @Query("SELECT * FROM PETS where id_user = :id_user")
    fun getPets(id_user: Int): Flow<List<Pets>>

    @Query("SELECT idPet FROM pets WHERE id_user = :id and nickname = :nickname and petView = :view and poroda = :poroda")
    fun getPetsId(id : Int, nickname: String, view: String, poroda: String): Int

    @Delete
    fun deletePet(pet: Pets)

    @Insert
    fun insertVeterinar(veterinars: Veterinars)

    @Query("SELECT * FROM veterinars")
    fun getAllVeterinar(): Flow<List<Veterinars>>

    @Query("SELECT * FROM veterinars where name = :name")
    fun getVeterinarByName(name: String?): Flow<List<Veterinars>>

    @Query("SELECT COUNT(*) FROM veterinars")
    fun countTableRowsVeterinars(): LiveData<Int>

    @Query("SELECT * FROM veterinars where specialization = :spec")
    fun getVeterinarToSpec(spec: String): Flow<List<Veterinars>>

    @Query("SELECT * FROM veterinars where specialization = :spec1 or specialization = :spec2 ")
    fun getVeterinarTo2Spec(spec1: String, spec2: String): Flow<List<Veterinars>>

    @Insert
    fun insertAppointment(appointment: MakeAnAppointment)

    @Query("SELECT * FROM make_an_appointment where id_user = :id_user")
    fun getAppointment(id_user: Int): Flow<List<MakeAnAppointment>>

    @Delete
    fun deleteAppointment(appointment: MakeAnAppointment)
}