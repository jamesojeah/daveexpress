package com.example.daveexpress.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.daveexpress.R
import com.example.daveexpress.activities.ProductDetailsActivity
import com.example.daveexpress.activities.SoldProductDetailsActivity
import com.example.daveexpress.models.OS
import com.example.daveexpress.models.SoldProduct
import com.example.daveexpress.utils.Constants
import com.example.daveexpress.utils.GlideLoader

class OutofStockAdapter(private val context: Context,
                        private var list: ArrayList<OS>)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>()
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return SoldProductsListAdapter.MyViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_os,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]

        val quantityint = model.quantity.toInt()

        if(quantityint < 2){


            if (holder is SoldProductsListAdapter.MyViewHolder) {

                GlideLoader(context).loadProductPicture(
                    model.image,
                    holder.itemView.findViewById(R.id.iv_item_image)
                )

                holder.itemView.findViewById<TextView>(R.id.tv_item_name).text = model.title
                holder.itemView.findViewById<TextView>(R.id.tv_item_price).text = "₦${model.price}"
                holder.itemView.findViewById<TextView>(R.id.tv_os_quantityleft).text = "${model.quantity}"

                holder.itemView.setOnClickListener {
                    val intent = Intent(context, ProductDetailsActivity::class.java)
                    intent.putExtra(Constants.EXTRA_OUT_OF_STOCK, model)
                    intent.putExtra(Constants.EXTRA_PRODUCT_ID, model.productId)
                    intent.putExtra(Constants.EXTRA_PRODUCT_OWNER_ID, model.user_id)
                    context.startActivity(intent)
                }
            }
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }
}