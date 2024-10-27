package br.unistanta.aplicativoroom.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_table")
data class User(
    @PrimaryKey(autoGenerate = true) val id:Int = 0 ,
    @ColumnInfo(name = "first_name") val name: String?,
    @ColumnInfo(name = "last_name") val email: String?,
    @ColumnInfo(name = "profile_photo") val profilePhoto: String
)

