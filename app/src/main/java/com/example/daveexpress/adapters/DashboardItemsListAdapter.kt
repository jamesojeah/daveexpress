package com.example.daveexpress.adapters

import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.daveexpress.R
import com.example.daveexpress.activities.ProductDetailsActivity
import com.example.daveexpress.activities.ui.fragments.DashboardFragment
import com.example.daveexpress.models.Product
import com.example.daveexpress.utils.Constants
import com.example.daveexpress.utils.GlideLoader
import java.util.*
import kotlin.collections.ArrayList

class DashboardItemsListAdapter(
    private val context: Context,
//    private var list: ArrayList<Product>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), Filterable {

    var itemsModalList = ArrayList<Product>();
     var itemsModalListFilter = ArrayList<Product>();

    // A global variable for OnClickListener interface.
    private var onClickListener: OnClickListener? = null

    fun setData (itemsModalList: ArrayList<Product>){
    this.itemsModalList = itemsModalList
        this.itemsModalListFilter = itemsModalList
        notifyDataSetChanged()
}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_dashboard_layout,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = itemsModalList[position]

        if (holder is MyViewHolder) {

            GlideLoader(context).loadProductPicture(
                model.image,
                holder.itemView.findViewById(R.id.iv_dashboard_item_image)
            )
            holder.itemView.findViewById<TextView>(R.id.tv_dashboard_item_title).text = model.title
            holder.itemView.findViewById<TextView>(R.id.tv_dashboard_item_price).text =
                "₦${model.price}"

            if (model.sale_status == Constants.YES){
                holder.itemView.findViewById<TextView>(R.id.tv_on_sale).visibility = View.VISIBLE
                holder.itemView.findViewById<TextView>(R.id.tv_dashboard_item_sale_price).visibility = View.VISIBLE
                holder.itemView.findViewById<TextView>(R.id.tv_percent_off).visibility = View.VISIBLE
                holder.itemView.findViewById<TextView>(R.id.tv_dashboard_item_price).paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
            } else{
                holder.itemView.findViewById<TextView>(R.id.tv_on_sale).visibility = View.GONE
                holder.itemView.findViewById<TextView>(R.id.tv_dashboard_item_sale_price).visibility = View.GONE
                holder.itemView.findViewById<TextView>(R.id.tv_percent_off).visibility = View.GONE
            }

            holder.itemView.findViewById<TextView>(R.id.tv_dashboard_item_sale_price).text = "₦${model.sale_price}"
            holder.itemView.findViewById<TextView>(R.id.tv_percent_off).text = "${model.percentage_off}% OFF"

            holder.itemView.setOnClickListener {
                val intent = Intent(context, ProductDetailsActivity::class.java)
                intent.putExtra(Constants.EXTRA_PRODUCT_ID, model.productId)
                intent.putExtra(Constants.EXTRA_PRODUCT_OWNER_ID, model.user_id)
                context.startActivity(intent)
            }
//            holder.itemView.setOnClickListener {
//                if (onClickListener != null) {
//                    onClickListener!!.onClick(position, model)
//                }
//            }
        }
    }

    override fun getItemCount(): Int {
        return itemsModalList.size

    }


    //
    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    interface OnClickListener {
        fun onClick(position: Int, product: Product)
    }

    /**
     * A ViewHolder describes an item view and metadata about its place within the RecyclerView.
     */
    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    }

    override fun getFilter(): Filter {
        return object: Filter(){
            override fun performFiltering(charsequence: CharSequence?): FilterResults {
                val filterResults = FilterResults();
                if (charsequence == null || charsequence.length<0){
                    filterResults.count = itemsModalListFilter.size
                    filterResults.values = itemsModalListFilter
                }else{
                    var searchChr = charsequence.toString()
                    val itemModal = ArrayList<Product>()
                    for (item in itemsModalListFilter){
                        if (item.title.contains(searchChr, ignoreCase = true) || item.description.contains(searchChr, ignoreCase = true)){
                            itemModal.add(item)
                        }
                    }
                    filterResults.count = itemModal.size
                    filterResults.values = itemModal
                }
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, filterResults: FilterResults?) {
                itemsModalList = filterResults?.values as ArrayList<Product>
                notifyDataSetChanged()
            }

        }
    }


}

