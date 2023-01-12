package com.example.daveexpress.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cardinfo")
data class CardEntity(
  @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val id : Int = 0,
  @ColumnInfo(name = "cardname") val cardname: String,
  @ColumnInfo(name = "cardnumber") val cardnumber: String,
  @ColumnInfo(name = "exp") val exp: String,
  @ColumnInfo(name = "cvv") val cvv: String
)