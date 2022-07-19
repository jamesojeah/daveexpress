package com.example.daveexpress.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.daveexpress.R
import com.example.daveexpress.activities.CartListActivity
import com.example.daveexpress.firestore.FirestoreClass
import com.example.daveexpress.models.CartItem
import com.example.daveexpress.utils.Constants
import com.example.daveexpress.utils.GlideLoader

class CartItemsListAdapter(private val context: Context,
                           private var list: ArrayList<CartItem>,
                           private val updateCartItems: Boolean
):     RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_cart_layout,
                parent,
                false
            )
        )
    }

    /**
     * Binds each item in the ArrayList to a view
     *
     * Called when RecyclerView needs a new {@link ViewHolder} of the given type to represent
     * an item.
     *
     * This new ViewHolder should be constructed with a new View that can represent the items
     * of the given type. You can either create a new View manually or inflate it from an XML
     * layout file.
     */
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]

        if (holder is MyViewHolder) {
            GlideLoader(context).loadProductPicture(model.image, holder.itemView.
                                                    findViewById(R.id.iv_cart_item_image))

            holder.itemView.findViewById<TextView>(R.id.tv_cart_item_title).text = model.title
            holder.itemView.findViewById<TextView>(R.id.tv_cart_item_price).text = "₦${model.price}"
            holder.itemView.findViewById<TextView>(R.id.tv_cart_quantity).text = model.cart_quantity
            holder.itemView.findViewById<TextView>(R.id.tv_cart_item_size).text = model.chosen_size


            // TODO Step 1: Show the text Out of Stock when cart quantity is zero.
            // START
            if (model.cart_quantity == "0") {
                holder.itemView.findViewById<ImageButton>(R.id.ib_remove_cart_item).visibility = View.GONE
                holder.itemView.findViewById<ImageButton>(R.id.ib_add_cart_item).visibility = View.GONE

                holder.itemView.findViewById<TextView>(R.id.tv_cart_quantity).text =
                    context.resources.getString(R.string.lbl_out_of_stock)

                // TODO Step 6: Update the UI components as per the param.
                // START
                if (updateCartItems) {
                    holder.itemView.findViewById<ImageButton>(R.id.ib_delete_cart_item).visibility = View.VISIBLE
                } else {
                    holder.itemView.findViewById<ImageButton>(R.id.ib_delete_cart_item).visibility = View.GONE
                }

                holder.itemView.findViewById<TextView>(R.id.tv_cart_quantity).setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.colorSnackBarError
                    )
                )
            } else {

                //Update the UI components as per the param.

                if (updateCartItems) {
                    holder.itemView.findViewById<ImageButton>(R.id.ib_remove_cart_item).visibility = View.VISIBLE
                    holder.itemView.findViewById<ImageButton>(R.id.ib_add_cart_item).visibility = View.VISIBLE
                    holder.itemView.findViewById<ImageButton>(R.id.ib_delete_cart_item).visibility = View.VISIBLE
                } else {

                    holder.itemView.findViewById<ImageButton>(R.id.ib_remove_cart_item).visibility = View.GONE
                    holder.itemView.findViewById<ImageButton>(R.id.ib_add_cart_item).visibility = View.GONE
                    holder.itemView.findViewById<ImageButton>(R.id.ib_delete_cart_item).visibility = View.GONE
                }

                holder.itemView.findViewById<TextView>(R.id.tv_cart_quantity).setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.colorSecondaryText
                    )
                )
            }
            // TODO Step 3: Assign the onclick event to the ib_delete_cart_item.
            // START
            holder.itemView.findViewById<ImageButton>(R.id.ib_delete_cart_item).setOnClickListener {

                // TODO Step 7: Call the firestore class function to remove the item from cloud firestore.
                // START

                when (context) {
                    is CartListActivity -> {
                        context.showProgressDialog(context.resources.getString(R.string.please_wait))
                    }
                }

                FirestoreClass().removeItemFromCart(context, model.id)
                // END
            }
            // TODO Step 1: Assign the click event to the ib_remove_cart_item.
            // START
            holder.itemView.findViewById<ImageButton>(R.id.ib_remove_cart_item).setOnClickListener {

                // TODO Step 6: Call the update or remove function of firestore class based on the cart quantity.
                // START
                if (model.cart_quantity == "1") {
                    FirestoreClass().removeItemFromCart(context, model.id)
                } else {

                    val cartQuantity: Int = model.cart_quantity.toInt()

                    val itemHashMap = HashMap<String, Any>()

                    itemHashMap[Constants.CART_QUANTITY] = (cartQuantity - 1).toString()

                    // Show the progress dialog.

                    if (context is CartListActivity) {
                        context.showProgressDialog(context.resources.getString(R.string.please_wait))
                    }

                    FirestoreClass().updateMyCart(context, model.id, itemHashMap)
                }
                // END
            }
            // END

            // TODO Step 7: Assign the click event to the ib_add_cart_item.
            // START
            holder.itemView.findViewById<ImageButton>(R.id.ib_add_cart_item).setOnClickListener {

                // TODO Step 8: Call the update function of firestore class based on the cart quantity.
                // START
                val cartQuantity: Int = model.cart_quantity.toInt()

                if (cartQuantity < model.stock_quantity.toInt()) {

                    val itemHashMap = HashMap<String, Any>()

                    itemHashMap[Constants.CART_QUANTITY] = (cartQuantity + 1).toString()

                    // Show the progress dialog.
                    if (context is CartListActivity) {
                        context.showProgressDialog(context.resources.getString(R.string.please_wait))
                    }

                    FirestoreClass().updateMyCart(context, model.id, itemHashMap)
                } else {
                    if (context is CartListActivity) {
                        context.showErrorSnackBar(
                            context.resources.getString(
                                R.string.msg_for_available_stock,
                                model.stock_quantity
                            ),
                            true
                        )
                    }
                }
                // END
            }
            // END
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
    /**
     * A ViewHolder describes an item view and metadata about its place within the RecyclerView.
     */
    private class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)
}