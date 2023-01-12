package com.example.daveexpress.activities.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*

import androidx.appcompat.widget.SearchView

import androidx.recyclerview.widget.GridLayoutManager
import com.example.daveexpress.R
import com.example.daveexpress.activities.CartListActivity
import com.example.daveexpress.activities.ProductDetailsActivity
import com.example.daveexpress.activities.SettingsActivity
import com.example.daveexpress.adapters.DashboardItemsListAdapter
import com.example.daveexpress.databinding.FragmentDashboardBinding
import com.example.daveexpress.firestore.FirestoreClass
import com.example.daveexpress.models.Product
import com.example.daveexpress.utils.Constants
import kotlin.collections.ArrayList

class DashboardFragment : BaseFragment(), View.OnClickListener {

lateinit var adapter: DashboardItemsListAdapter
//    val displayList = ArrayList<Product>()
//    val arrayList = ArrayList<Product>()

    private var _binding: FragmentDashboardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // If we want to use the option menu in fragment we need to add it.
        setHasOptionsMenu(true)

    }

    override fun onResume() {
        super.onResume()
        getDashboardItemsList()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

//        val dashboardViewModel = ViewModelProvider(this).get(DashboardViewModel::class.java)
        adapter = DashboardItemsListAdapter(requireActivity())

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.tabAll.setOnClickListener(this@DashboardFragment)
        binding.tabShoes.setOnClickListener(this@DashboardFragment)
        binding.tabShirts.setOnClickListener(this@DashboardFragment)
        binding.tabTrousers.setOnClickListener(this@DashboardFragment)
        binding.tabHoodies.setOnClickListener(this@DashboardFragment)
        binding.tabOthercategories.setOnClickListener(this@DashboardFragment)

        return root


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.dashboard_menu, menu)
        val menuItem = menu!!.findItem(R.id.action_search)
        val searchView = menuItem.actionView as SearchView
        searchView.maxWidth = Int.MAX_VALUE
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(filterString: String?): Boolean {

                adapter!!.filter.filter(filterString)

                return true
            }

            override fun onQueryTextChange(filterString: String?): Boolean {

                adapter.filter.filter(filterString)
                return true
            }

        })

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        when (id) {

            R.id.action_settings -> {

                // TODO Step 9: Launch the SettingActivity on click of action item.
                // START
                startActivity(Intent(activity, SettingsActivity::class.java))
                // END
                return true
            }
            R.id.action_cart -> {
                startActivity(Intent(activity, CartListActivity::class.java))
                return true
            }
            R.id.action_search ->{


            }

        }
        return super.onOptionsItemSelected(item)
    }
    /**
     * A function to get the dashboard items list from cloud firestore.
     */
    private fun getDashboardItemsList() {
        // Show the progress dialog.
        showProgressDialog(resources.getString(R.string.please_wait))

        FirestoreClass().getDashboardItemsList(this@DashboardFragment)
    }

    /**
     * A function to get the success result of the dashboard items from cloud firestore.
     *
     * @param dashboardItemsList
     */

    fun successDashboardItemsList(dashboardItemsList: ArrayList<Product>) {

        // Hide the progress dialog.
        hideProgressDialog()


                if (dashboardItemsList.size > 0) {

            binding.rvDashboardItems.visibility = View.VISIBLE
            binding.tvNoDashboardItemsFound.visibility = View.GONE


            binding.rvDashboardItems.layoutManager = GridLayoutManager(activity, 2)
            binding.rvDashboardItems.setHasFixedSize(true)

             adapter = DashboardItemsListAdapter(requireActivity())

            binding.rvDashboardItems.adapter = adapter
                    adapter.setData(itemsModalList = dashboardItemsList)
            adapter.setOnClickListener(object :
                DashboardItemsListAdapter.OnClickListener {
                override fun onClick(position: Int, product: Product) {

                    // TODO Step 7: Launch the product details screen from the dashboard.
                    // START
                    val intent = Intent(context, ProductDetailsActivity::class.java)
                    intent.putExtra(Constants.EXTRA_PRODUCT_ID, product.productId)
                    intent.putExtra(Constants.EXTRA_PRODUCT_OWNER_ID, product.user_id)
                    startActivity(intent)
                    // END
                }
            })
        }else {
            binding.rvDashboardItems.visibility = View.GONE
            binding.tvNoDashboardItemsFound.visibility = View.VISIBLE
        }

    }
    override fun onClick(v: View?) {
        if (v!=null){
            when(v.id){
                R.id.tab_shoes -> {
                    showProgressDialog(resources.getString(R.string.please_wait))
                    Log.d("Shoe button has been clicked", "Shoe button has been clicked")
                    FirestoreClass().getShoeItemsList(this@DashboardFragment)
                }
                R.id.tab_all -> {
                    showProgressDialog(resources.getString(R.string.please_wait))
                    FirestoreClass().getDashboardItemsList(this@DashboardFragment)
                }
                R.id.tab_shirts -> {
                    showProgressDialog(resources.getString(R.string.please_wait))
                    FirestoreClass().getShirtsItemsList(this@DashboardFragment)
                }
                R.id.tab_hoodies -> {
                    showProgressDialog(resources.getString(R.string.please_wait))
                    FirestoreClass().getHoodiesItemsList(this@DashboardFragment)
                }
                R.id.tab_trousers -> {
                    showProgressDialog(resources.getString(R.string.please_wait))
                    FirestoreClass().getTrousersItemsList(this@DashboardFragment)
                }
                R.id.tab_othercategories -> {
                    showProgressDialog(resources.getString(R.string.please_wait))
                    FirestoreClass().getOtherCategoryItemsList(this@DashboardFragment)
                }
            }
        }
    }

}
