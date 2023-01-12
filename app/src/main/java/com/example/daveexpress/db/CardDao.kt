package com.example.daveexpress.db

import androidx.room.*

@Dao
interface CardDao {

    @Query("SELECT * FROM cardinfo ORDER BY id DESC")
    fun getAllCardInfo(): List<CardEntity>?

    @Insert
    fun insertCard(card: CardEntity?)

    @Delete
    fun deleteCard(card: CardEntity?)

    @Update
    fun updateCard(card: CardEntity?)
}