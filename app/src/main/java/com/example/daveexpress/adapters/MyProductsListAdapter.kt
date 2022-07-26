package com.example.daveexpress.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.daveexpress.R
import com.example.daveexpress.activities.ProductDetailsActivity
import com.example.daveexpress.activities.ui.fragments.ProductsFragment
import com.example.daveexpress.models.Product
import com.example.daveexpress.utils.Constants
import com.example.daveexpress.utils.GlideLoader
import com.example.daveexpress.utils.ProductListViewState

open class MyProductsListAdapter(
    val context: Context,
    var list: ArrayList<Product>,
    val fragment: ProductsFragment
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_list_layout,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]

        if (holder is MyViewHolder) {

            GlideLoader(context).loadProductPicture(model.image, holder.itemView.findViewById<ImageView>(R.id.iv_item_image))

            holder.itemView.findViewById<TextView>(R.id.tv_item_name).text = model.title
            holder.itemView.findViewById<TextView>(R.id.tv_item_price).text = "₦${model.price}"

            holder.itemView.findViewById<ImageButton>(R.id.ib_delete_product).setOnClickListener {

                fragment.deleteProduct(model.productId)
            }
            holder.itemView.setOnClickListener {
                // Launch Product details screen.
                val intent = Intent(context, ProductDetailsActivity::class.java)
                intent.putExtra(Constants.EXTRA_PRODUCT_ID, model.productId)
                intent.putExtra(Constants.EXTRA_PRODUCT_OWNER_ID, model.user_id)
                context.startActivity(intent)
            }
            // END
        }
    }


    override fun getItemCount(): Int {
        return list.size
    }
}

/**
 * A ViewHolder describes an item view and metadata about its place within the RecyclerView.
 */
class MyViewHolder(view: View) : RecyclerView.ViewHolder(view){
}

