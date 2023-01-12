package com.example.daveexpress.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.daveexpress.db.CardEntity
import com.example.daveexpress.db.RoomAppDb
import com.example.daveexpress.models.Cards
import com.example.daveexpress.models.Order
import java.util.ArrayList

class CardViewModel(val repository: MyRepository): ViewModel() {

    private val cards: MutableLiveData<ArrayList<Cards>> by lazy {
        MutableLiveData<ArrayList<Cards>>().also { data ->
            getCards()
            repository.allthecards = data

//            _viewState.postValue(ProductListViewState.Content(data))
        }
    }

    fun myCards(): MutableLiveData<ArrayList<Cards>> {
        return cards
    }

    fun getCards(){
        repository.getCardsList()
    }


}

//class CardViewModel(app: Application): AndroidViewModel(app) {
//    lateinit var allCards : MutableLiveData<List<CardEntity>>
//
//    init {
//        allCards = MutableLiveData()
//    }
//
//    fun getAllCardObservers(): MutableLiveData<List<CardEntity>>{
//        return allCards
//    }
//
//    fun getAllCards(){
//        val cardDao = RoomAppDb.getAppDatabase((getApplication()))?.cardDao()
//        val list = cardDao?.getAllCardInfo()
//
//        allCards.postValue(list!!)
//    }
//
//    fun insertCardInfo(entity: CardEntity){
//        val cardDao = RoomAppDb.getAppDatabase((getApplication()))?.cardDao()
//        cardDao?.insertCard(entity)
//        getAllCards()
//    }
//
//    fun updateCardInfo(entity: CardEntity){
//        val cardDao = RoomAppDb.getAppDatabase((getApplication()))?.cardDao()
//        cardDao?.updateCard(entity)
//        getAllCards()
//    }
//
//    fun deleteCardInfo(entity: CardEntity){
//        val cardDao = RoomAppDb.getAppDatabase((getApplication()))?.cardDao()
//        cardDao?.deleteCard(entity)
//        getAllCards()
//    }
//
//}