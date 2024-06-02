package com.example.pet_pawtrol.Entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "make_an_appointment")
data class MakeAnAppointment(
    @PrimaryKey(autoGenerate = true)
    val ID: Int? = null,

    @ColumnInfo(name = "email")
    val email: String,

    @ColumnInfo(name = "nicknamePets")
    val nicknamePets: String?,

    @ColumnInfo(name = "veterinarName")
    val veterinarName: String?,

    @ColumnInfo(name = "id_user")
    val id_user: Int
)
