package com.example.daveexpress.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.daveexpress.R
import com.example.daveexpress.db.CardEntity
import com.example.daveexpress.models.Cards

class CardRecyclerViewAdapter(val listener: RowClickListener): RecyclerView.Adapter<CardRecyclerViewAdapter.MyViewHolder>() {

    var items = ArrayList<Cards>()

    fun setListData(data: ArrayList<Cards>){
        this.items = data
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardRecyclerViewAdapter.MyViewHolder {
       val inflater = LayoutInflater.from(parent.context).inflate(R.layout.cardrecyclerview_row, parent, false)
        return MyViewHolder(inflater, listener)
    }

    override fun onBindViewHolder(holder: CardRecyclerViewAdapter.MyViewHolder, position: Int) {
        holder.itemView.setOnClickListener {

//            val tvCardName = holder.itemView.findViewById<TextView>(R.id.tvCardName).text
//
//            holder.itemView.findViewById<TextView>(R.id.et_name)?.text = tvCardName
//
            listener.onItemClickListener(items[position])


        }
       holder.bind(items[position])

    }

    override fun getItemCount(): Int {
        return items.size
    }

    class MyViewHolder(view: View, val listener: RowClickListener): RecyclerView.ViewHolder(view){

        val tvCardName = view.findViewById<TextView>(R.id.tvCardName)
        val tvCardNumber = view.findViewById<TextView>(R.id.tvCardNumber)
        val tvExp = view.findViewById<TextView>(R.id.tvExp)
        val tvCvv = view.findViewById<TextView>(R.id.tvCvv)
        val deleteCard = view.findViewById<ImageView>(R.id.ib_delete_card)

        fun bind(data: Cards){
            tvCardName.text = data.cardname
            tvCardNumber.text = data.cardnumber
            tvExp.text = data.expirydate
            tvCvv.text = data.cvv

            deleteCard.setOnClickListener {
                listener.onDeleteUserClickListener(data)
            }
        }
    }

    interface RowClickListener{
        fun onDeleteUserClickListener(card: Cards)
        fun onItemClickListener(card: Cards)
    }

}