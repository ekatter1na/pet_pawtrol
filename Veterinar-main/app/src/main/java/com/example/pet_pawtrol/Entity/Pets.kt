package com.example.pet_pawtrol.Entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pets")
data class Pets(
    @PrimaryKey(autoGenerate = true)
    val idPet: Int? = null,

    @ColumnInfo(name = "nickname")
    val nickname: String,

    @ColumnInfo(name = "petView")
    val petView: String,

    @ColumnInfo(name = "poroda")
    val poroda: String,

    @ColumnInfo(name = "id_user")
    val id_user: Int
)