package com.example.daveexpress.firestore

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.daveexpress.R
import com.example.daveexpress.activities.*
import com.example.daveexpress.activities.ui.fragments.DashboardFragment
import com.example.daveexpress.activities.ui.fragments.OrdersFragment
import com.example.daveexpress.activities.ui.fragments.ProductsFragment
import com.example.daveexpress.activities.ui.fragments.SoldProductsFragment
import com.example.daveexpress.models.*
import com.example.daveexpress.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class FirestoreClass {
    private val mFireStore = FirebaseFirestore.getInstance()

    /**
     * A function to make an entry of the registered user in the FireStore database.
     */
    fun registerUser(activity: RegisterActivity, userInfo: User) {

        // The "users" is collection name. If the collection is already created then it will not create the same one again.
        mFireStore.collection(Constants.USERS)
            // Document ID for users fields. Here the document it is the User ID.
            .document(userInfo.id)
            // Here the userInfo are Field and the SetOption is set to merge. It is for if we wants to merge later on instead of replacing the fields.
            .set(userInfo, SetOptions.merge())
            .addOnSuccessListener {

                // Here call a function of base activity for transferring the result to it.
                activity.userRegistrationSuccess()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while registering the user.",
                    e
                )
            }
    }

    /**
     * A function to get the user id of current logged user.
     */
    fun getCurrentUserID(): String {
        // An Instance of currentUser using FirebaseAuth
        val currentUser = FirebaseAuth.getInstance().currentUser

        // A variable to assign the currentUserId if it is not null or else it will be blank.
        var currentUserID = ""
        if (currentUser != null) {
            currentUserID = currentUser.uid
        }

        return currentUserID
    }

    /**
     * A function to get the logged user details from from FireStore Database.
     */
    fun getUserDetails(activity: Activity) {

        // Here we pass the collection name from which we wants the data.
        mFireStore.collection(Constants.USERS)
            // The document id to get the Fields of user.
            .document(getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->

                Log.i(activity.javaClass.simpleName, document.toString())

                // Here we have received the document snapshot which is converted into the User Data model object.
                val user = document.toObject(User::class.java)!!

                val sharedPreferences =
                    activity.getSharedPreferences(
                        Constants.DAVEEXPRESS_PREFERENCES,
                        Context.MODE_PRIVATE
                    )

                val editor: SharedPreferences.Editor = sharedPreferences.edit()
                if (user != null) {
                    editor.putString(
                        Constants.LOGGED_IN_USERNAME,
                        "${user.firstName} ${user.lastName}"
                    )
                }
                editor.apply()

                // TODO Step 6: Pass the result to the Login Activity.
                // START
                when (activity) {
                    is LoginActivity -> {
                        // Call a function of base activity for transferring the result to it.
                        if (user != null) {
                            activity.userLoggedInSuccess(user)
                        }
                    }
                    is SettingsActivity -> {
                        if (user != null) {
                            activity.userDetailsSuccess(user)
                        }
                    }
                    is DashboardActivity ->{
                        if (user != null){
                            activity.adminSuccess(user)
                        }
                    }
                }
                // END
            }
            .addOnFailureListener { e ->
                // Hide the progress dialog if there is any error. And print the error in log.
                when (activity) {
                    is LoginActivity -> {
                        activity.hideProgressDialog()
                    }
                    is SettingsActivity -> {
                        activity.hideProgressDialog()
                    }
                }

                Log.e(
                    activity.javaClass.simpleName,
                    "Error while getting user details.",
                    e
                )
            }
    }

    /**
     * A function to update the user profile data into the database.
     *
     * @param activity The activity is used for identifying the Base activity to which the result is passed.
     * @param userHashMap HashMap of fields which are to be updated.
     */
    fun updateUserProfileData(activity: Activity, userHashMap: HashMap<String, Any>) {
        // Collection Name
        mFireStore.collection(Constants.USERS)
            // Document ID against which the data to be updated. Here the document id is the current logged in user id.
            .document(getCurrentUserID())
            // A HashMap of fields which are to be updated.
            .update(userHashMap)
            .addOnSuccessListener {

                // TODO Step 9: Notify the success result to the base activity.
                // START
                // Notify the success result.
                when (activity) {
                    is UserProfileActivity -> {
                        // Call a function of base activity for transferring the result to it.
                        activity.userProfileUpdateSuccess()
                    }
                }
                // END
            }
            .addOnFailureListener { e ->

                when (activity) {
                    is UserProfileActivity -> {
                        // Hide the progress dialog if there is any error. And print the error in log.
                        activity.hideProgressDialog()
                    }
                }

                Log.e(
                    activity.javaClass.simpleName,
                    "Error while updating the user details.",
                    e
                )
            }
    }

    // A function to upload the image to the cloud storage.
    fun uploadImageToCloudStorage(activity: Activity, imageFileURI: Uri?, imageType: String) {

        //getting the storage reference
        val sRef: StorageReference = FirebaseStorage.getInstance().reference.child(
            imageType + System.currentTimeMillis() + "."
                    + Constants.getFileExtension(
                activity,
                imageFileURI
            )
        )
        //adding the file to reference
        sRef.putFile(imageFileURI!!)
            .addOnSuccessListener { taskSnapshot ->
                // The image upload is success
                Log.e(
                    "Firebase Image URL",
                    taskSnapshot.metadata!!.reference!!.downloadUrl.toString()
                )

                // Get the downloadable url from the task snapshot
                taskSnapshot.metadata!!.reference!!.downloadUrl
                    .addOnSuccessListener { uri ->
                        Log.e("Downloadable Image URL", uri.toString())

                        // TODO Step 8: Pass the success result to base class.
                        // START
                        // Here call a function of base activity for transferring the result to it.
                        when (activity) {
                            is UserProfileActivity -> {
                                activity.imageUploadSuccess(uri.toString())
                            }
                            is AddProductActivity -> {
                                activity.imageUploadSuccess(uri.toString())
                            }
                        }
                        // END
                    }
            }
            .addOnFailureListener { exception ->

                // Hide the progress dialog if there is any error. And print the error in log.
                when (activity) {
                    is UserProfileActivity -> {
                        activity.hideProgressDialog()
                    }
                    is AddProductActivity -> {
                        activity.hideProgressDialog()
                    }
                }
                Log.e(
                    activity.javaClass.simpleName,
                    exception.message,
                    exception
                )
            }
    }

    fun uploadProductDetails(activity: AddProductActivity, productInfo: Product) {

        mFireStore.collection(Constants.PRODUCTS)
            .document()
            .set(productInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.productUploadSuccess()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while uploading the product details.", e
                )
            }
    }

    fun getProductsList(fragment: Fragment) {
        mFireStore.collection(Constants.PRODUCTS)
            .whereEqualTo(Constants.USER_ID, getCurrentUserID())
            .orderBy("product_datetime", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { document ->
                Log.e("Products List", document.documents.toString())
                val productsList: ArrayList<Product> = ArrayList()
                // A for loop as per the list of documents to convert them into Products ArrayList.
                for (i in document.documents) {

                    val product = i.toObject(Product::class.java)
                    product!!.product_id = i.id

                    productsList.add(product)
                }
                when (fragment) {
                    is ProductsFragment -> {
                        fragment.successProductsListFromFireStore(productsList)
                    }
                }
            }


    }

    fun getShoeItemsList(fragment: DashboardFragment){
        mFireStore.collection(Constants.PRODUCTS)
            .whereEqualTo("category", "Shoes")
            .get()
            .addOnSuccessListener {
             document ->   Log.d("Shoe categories", document.documents.toString())

                val shoesList: ArrayList<Product> = ArrayList()
                for (i in document.documents){
                    val product = i.toObject(Product::class.java)!!
                    product.product_id = i.id
                    shoesList.add(product)
                }
                // Pass the success result to the base fragment.
                fragment.successDashboardItemsList(shoesList)
            }
    }

    fun getShirtsItemsList(fragment: DashboardFragment){
        mFireStore.collection(Constants.PRODUCTS)
            .whereEqualTo("category", "Shirts")
            .get()
            .addOnSuccessListener {
                    document ->   Log.d("Shirts categories", document.documents.toString())

                val shirtsList: ArrayList<Product> = ArrayList()
                for (i in document.documents){
                    val product = i.toObject(Product::class.java)!!
                    product.product_id = i.id
                    shirtsList.add(product)
                }
                // Pass the success result to the base fragment.
                fragment.successDashboardItemsList(shirtsList)
            }
    }

    fun getTrousersItemsList(fragment: DashboardFragment){
        mFireStore.collection(Constants.PRODUCTS)
            .whereEqualTo("category", "Trousers")
            .get()
            .addOnSuccessListener {
                    document ->   Log.d("Trousers categories", document.documents.toString())

                val trousersList: ArrayList<Product> = ArrayList()
                for (i in document.documents){
                    val product = i.toObject(Product::class.java)!!
                    product.product_id = i.id
                    trousersList.add(product)
                }
                // Pass the success result to the base fragment.
                fragment.successDashboardItemsList(trousersList)
            }
    }

    fun getHoodiesItemsList(fragment: DashboardFragment){
        mFireStore.collection(Constants.PRODUCTS)
            .whereEqualTo("category", "Hoodies")
            .get()
            .addOnSuccessListener {
                    document ->   Log.d("Hoodies categories", document.documents.toString())

                val hoodiesList: ArrayList<Product> = ArrayList()
                for (i in document.documents){
                    val product = i.toObject(Product::class.java)!!
                    product.product_id = i.id
                    hoodiesList.add(product)
                }
                // Pass the success result to the base fragment.
                fragment.successDashboardItemsList(hoodiesList)
            }
    }

    fun getOtherCategoryItemsList(fragment: DashboardFragment){
        mFireStore.collection(Constants.PRODUCTS)
            .whereEqualTo("category", "Other_category")
            .get()
            .addOnSuccessListener {
                    document ->   Log.d("Other_category", document.documents.toString())

                val otherCategoryList: ArrayList<Product> = ArrayList()
                for (i in document.documents){
                    val product = i.toObject(Product::class.java)!!
                    product.product_id = i.id
                    otherCategoryList.add(product)
                }
                // Pass the success result to the base fragment.
                fragment.successDashboardItemsList(otherCategoryList)
            }
    }
    //A function to get the dashboard items list. The list will be an overall items list, not based on the user's id.
    fun getDashboardItemsList(fragment: DashboardFragment) {
        // The collection name for PRODUCTS
        mFireStore.collection(Constants.PRODUCTS)
            .orderBy("product_datetime", Query.Direction.DESCENDING)
            .get() // Will get the documents snapshots.
            .addOnSuccessListener { document ->

                // Here we get the list of boards in the form of documents.
                Log.e(fragment.javaClass.simpleName, document.documents.toString())

                // Here we have created a new instance for Products ArrayList.
                val productsList: ArrayList<Product> = ArrayList()

                // A for loop as per the list of documents to convert them into Products ArrayList.
                for (i in document.documents) {
                    val product = i.toObject(Product::class.java)!!
                    product.product_id = i.id
                    productsList.add(product)
                }

                // Pass the success result to the base fragment.
                fragment.successDashboardItemsList(productsList)
            }
            .addOnFailureListener { e ->
                // Hide the progress dialog if there is any error which getting the dashboard items list.
                fragment.hideProgressDialog()
                Log.e(
                    fragment.javaClass.simpleName,
                    "Error while getting dashboard items list.",
                    e
                )
            }
    }

    fun addCartItems(activity: ProductDetailsActivity, addToCart: CartItem) {

        mFireStore.collection(Constants.CART_ITEMS)
            .document()
            // Here the userInfo are Field and the SetOption is set to merge. It is for if we wants to merge
            .set(addToCart, SetOptions.merge())
            .addOnSuccessListener {

                // Here call a function of base activity for transferring the result to it.
                activity.addToCartSuccess()
            }
            .addOnFailureListener { e ->

                activity.hideProgressDialog()

                Log.e(
                    activity.javaClass.simpleName,
                    "Error while creating the document for cart item.",
                    e
                )
            }
    }

    /**
     * A function to delete the product from the cloud firestore.
     */
    fun deleteProduct(fragment: ProductsFragment, productId: String) {

        mFireStore.collection(Constants.PRODUCTS)
            .document(productId)
            .delete()
            .addOnSuccessListener {

                // Notify the success result to the base class.
                fragment.productDeleteSuccess()
            }
            .addOnFailureListener { e ->

                // Hide the progress dialog if there is an error.
                fragment.hideProgressDialog()

                Log.e(
                    fragment.requireActivity().javaClass.simpleName,
                    "Error while deleting the product.",
                    e
                )
            }
    }

    /**
     * A function to get all the product list from the cloud firestore.
     *
     * @param activity The activity is passed as parameter to the function because it is called from activity and need to the success result.
     */
    fun getAllProductsList(activity: Activity) {
        // The collection name for PRODUCTS
        mFireStore.collection(Constants.PRODUCTS)
            .get() // Will get the documents snapshots.
            .addOnSuccessListener { document ->

                // Here we get the list of boards in the form of documents.
                Log.e("Products List", document.documents.toString())

                // Here we have created a new instance for Products ArrayList.
                val productsList: ArrayList<Product> = ArrayList()

                // A for loop as per the list of documents to convert them into Products ArrayList.
                for (i in document.documents) {

                    val product = i.toObject(Product::class.java)
                    product!!.product_id = i.id

                    productsList.add(product)
                }
                when (activity) {
                    is CartListActivity -> {
                        activity.successProductsListFromFireStore(productsList)
                    }
                    is CheckoutActivity -> {
                        activity.successProductsListFromFireStore(productsList)
                    }
                    is PaymentCardDetails ->{
                        activity.successProductsListFromFireStore(productsList)
                    }
                }

            }
            .addOnFailureListener { e ->
                when (activity) {
                    is CartListActivity -> {
                        activity.hideProgressDialog()
                        Log.e("Get Product List", "Error while getting all product list.", e)
                    }
                    is CheckoutActivity -> {
                        activity.hideProgressDialog()
                        Log.e("Get Product List", "Error while getting all product list.", e)
                    }
                }
            }
    }

    /**
     * A function to get the product details based on the product id.
     */
    fun getProductDetails(activity: ProductDetailsActivity, productId: String) {

        // The collection name for PRODUCTS
        mFireStore.collection(Constants.PRODUCTS)
            .document(productId)
            .get() // Will get the document snapshots.
            .addOnSuccessListener { document ->

                // Here we get the product details in the form of document.
                Log.e(activity.javaClass.simpleName, document.toString())

                // Convert the snapshot to the object of Product data model class.
                val product = document.toObject(Product::class.java)!!

                // TODO Step 4: Notify the success result.
                // START
                activity.productDetailsSuccess(product)
                // END
            }
            .addOnFailureListener { e ->

                // Hide the progress dialog if there is an error.
                activity.hideProgressDialog()

                Log.e(activity.javaClass.simpleName, "Error while getting the product details.", e)
            }
    }

    /**
     * A function to check whether the item already exist in the cart or not.
     */
    fun checkIfItemExistInCart(activity: ProductDetailsActivity, productId: String) {

        mFireStore.collection(Constants.CART_ITEMS)
            .whereEqualTo(Constants.USER_ID, getCurrentUserID())
            .whereEqualTo(Constants.PRODUCT_ID, productId)
            .get()
            .addOnSuccessListener { document ->

                Log.e(activity.javaClass.simpleName, document.documents.toString())

                // If the document size is greater than 1 it means the product is already added to the cart.
                if (document.documents.size > 0) {
                    activity.productExistsInCart()
                } else {
                    activity.hideProgressDialog()
                }
            }
            .addOnFailureListener { e ->
                // Hide the progress dialog if there is an error.
                activity.hideProgressDialog()

                Log.e(
                    activity.javaClass.simpleName,
                    "Error while checking the existing cart list.",
                    e
                )
            }
    }

    /**
     * A function to get the cart items list from the cloud firestore.
     *
     * @param activity
     */
    fun getCartList(activity: Activity) {
        // The collection name for PRODUCTS
        mFireStore.collection(Constants.CART_ITEMS)
            .whereEqualTo(Constants.USER_ID, getCurrentUserID())
            .get() // Will get the documents snapshots.
            .addOnSuccessListener { document ->

                // Here we get the list of cart items in the form of documents.
                Log.e(activity.javaClass.simpleName, document.documents.toString())

                // Here we have created a new instance for Cart Items ArrayList.
                val list: ArrayList<CartItem> = ArrayList()

                // A for loop as per the list of documents to convert them into Cart Items ArrayList.
                for (i in document.documents) {

                    val cartItem = i.toObject(CartItem::class.java)!!
                    cartItem.id = i.id

                    list.add(cartItem)
                }

                when (activity) {
                    is CartListActivity -> {
                        activity.successCartItemsList(list)
                    }
                    is CheckoutActivity -> {
                        activity.successCartItemsList(list)
                    }
                    is PaymentCardDetails ->{
                        activity.successCartItemsList(list)
                    }
                }
            }
            .addOnFailureListener { e ->
                // Hide the progress dialog if there is an error based on the activity instance.
                when (activity) {
                    is CartListActivity -> {
                        activity.hideProgressDialog()
                    }
                    is CheckoutActivity -> {
                        activity.hideProgressDialog()
                    }
                }

                Log.e(activity.javaClass.simpleName, "Error while getting the cart list items.", e)
            }
    }

    // TODO Step 4: Create a function to remove the cart item from the cloud firestore.
    // START
    /**
     * A function to remove the cart item from the cloud firestore.
     *
     * @param activity activity class.
     * @param cart_id cart id of the item.
     */
    fun removeItemFromCart(context: Context, cart_id: String) {

        // Cart items collection name
        mFireStore.collection(Constants.CART_ITEMS)
            .document(cart_id) // cart id
            .delete()
            .addOnSuccessListener {

                // TODO Step 6: Notify the success result of the removed cart item from the list to the base class.
                // START
                // Notify the success result of the removed cart item from the list to the base class.
                when (context) {
                    is CartListActivity -> {
                        context.itemRemovedSuccess()
                    }
                }
                // END
            }
            .addOnFailureListener { e ->

                // Hide the progress dialog if there is any error.
                when (context) {
                    is CartListActivity -> {
                        context.hideProgressDialog()
                    }
                }
                Log.e(
                    context.javaClass.simpleName,
                    "Error while removing the item from the cart list.",
                    e
                )
            }
    }

    /**
     * A function to update the cart item in the cloud firestore.
     *
     * @param activity activity class.
     * @param id cart id of the item.
     * @param itemHashMap to be updated values.
     */
    fun updateMyCart(context: Context, cart_id: String, itemHashMap: HashMap<String, Any>) {

        // Cart items collection name
        mFireStore.collection(Constants.CART_ITEMS)
            .document(cart_id) // cart id
            .update(itemHashMap) // A HashMap of fields which are to be updated.
            .addOnSuccessListener {

                // TODO Step 4: Notify the success result of the updated cart items list to the base class.
                // START
                // Notify the success result of the updated cart items list to the base class.
                when (context) {
                    is CartListActivity -> {
                        context.itemUpdateSuccess()
                    }
                }
                // END
            }
            .addOnFailureListener { e ->

                // Hide the progress dialog if there is any error.
                when (context) {
                    is CartListActivity -> {
                        context.hideProgressDialog()
                    }
                }

                Log.e(
                    context.javaClass.simpleName,
                    "Error while updating the cart item.",
                    e
                )
            }
    }

    /**
     * A function to add address to the cloud firestore.
     *
     * @param activity
     * @param addressInfo
     */
    fun addAddress(activity: AddEditAddressActivity, addressInfo: Address) {

        // Collection name address.
        mFireStore.collection(Constants.ADDRESSES)
            .document()
            // Here the userInfo are Field and the SetOption is set to merge. It is for if we wants to merge
            .set(addressInfo, SetOptions.merge())
            .addOnSuccessListener {

                // TODO Step 5: Notify the success result to the base class.
                // START
                // Here call a function of base activity for transferring the result to it.
                activity.addUpdateAddressSuccess()
                // END
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while adding the address.",
                    e
                )
            }
    }

    /**
     * A function to get the list of address from the cloud firestore.
     *
     * @param activity
     */
    fun getAddressesList(activity: AddressListActivity) {
        // The collection name for PRODUCTS
        mFireStore.collection(Constants.ADDRESSES)
            .whereEqualTo(Constants.USER_ID, getCurrentUserID())
            .get() // Will get the documents snapshots.
            .addOnSuccessListener { document ->
                // Here we get the list of boards in the form of documents.
                Log.e(activity.javaClass.simpleName, document.documents.toString())
                // Here we have created a new instance for address ArrayList.
                val addressList: ArrayList<Address> = ArrayList()

                // A for loop as per the list of documents to convert them into Boards ArrayList.
                for (i in document.documents) {

                    val address = i.toObject(Address::class.java)!!
                    address.id = i.id

                    addressList.add(address)
                }

                // TODO Step 4: Notify the success result to the base class.
                // START
                activity.successAddressListFromFirestore(addressList)
                // END
            }
            .addOnFailureListener { e ->
                // Here call a function of base activity for transferring the result to it.
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error while getting the address list.", e)
            }
    }

    /**
     * A function to update the existing address to the cloud firestore.
     *

     */
    fun updateAddress(activity: AddEditAddressActivity, addressInfo: Address, addressId: String) {

        mFireStore.collection(Constants.ADDRESSES)
            .document(addressId)
            // Here the userInfo are Field and the SetOption is set to merge. It is for if we wants to merge
            .set(addressInfo, SetOptions.merge())
            .addOnSuccessListener {

                // Here call a function of base activity for transferring the result to it.
                activity.addUpdateAddressSuccess()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while updating the Address.",
                    e
                )
            }
    }

    /**
     * A function to delete the existing address from the cloud firestore.
     *
     * @param activity Base class
     * @param addressId existing address id
     */
    fun deleteAddress(activity: AddressListActivity, addressId: String) {

        mFireStore.collection(Constants.ADDRESSES)
            .document(addressId)
            .delete()
            .addOnSuccessListener {

                // Here call a function of base activity for transferring the result to it.
                activity.deleteAddressSuccess()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while deleting the address.",
                    e
                )
            }
    }
    // TODO Step 7: Create a function to place an order of the user in the cloud firestore.
    // START
    /**
     * A function to place an order of the user in the cloud firestore.
     *
     * @param activity base class
     * @param order Order Info
     */
    fun placeOrder(activity: Activity, order: Order) {

        mFireStore.collection(Constants.ORDERS)
            .document()
            // Here the userInfo are Field and the SetOption is set to merge. It is for if we wants to merge
            .set(order, SetOptions.merge())
            .addOnSuccessListener {

                when (activity) {
                    is CheckoutActivity -> {
                        // TODO Step 9: Notify the success result.
                        // START
                        // Here call a function of base activity for transferring the result to it.
                        activity.orderPlacedSuccess()
                        Log.d("Order Placed Success", "the order was placed")
                        // END
                    }
                    is PaymentCardDetails -> {
                        activity.orderPlacedSuccess()
                        Log.d("Order Placed Success", "the order was placed")
                    }
                }

            }
            .addOnFailureListener { e ->
                when (activity) {
                    is CheckoutActivity -> {
                        // Hide the progress dialog if there is any error.
                        activity.hideProgressDialog()
                        Log.e(
                            activity.javaClass.simpleName,
                            "Error while placing an order.",
                            e
                        )
                        Log.d("Order Placed Failed", "the order was failed")
                    }
                    is PaymentCardDetails -> {
                        // Hide the progress dialog if there is any error.
                        activity.hideProgressDialog()
                        Log.e(
                            activity.javaClass.simpleName,
                            "Error while placing an order.",
                            e
                        )
                        Log.d("Order Placed Failed", "the order was failed")
                    }
                }

            }
    }
    // TODO Step 2: Create a function to update all the required details in the cloud firestore after placing the order successfully.
    // START
    /**
     * A function to update all the required details in the cloud firestore after placing the order successfully.
     *
     * @param activity Base class.
     * @param cartList List of cart items.
     */
    fun updateAllDetails(activity: Activity, cartList: ArrayList<CartItem>, order: Order) {

        val writeBatch = mFireStore.batch()

        // Here we will update the product stock in the products collection based to cart quantity.
        for (cartItem in cartList) {

//            val productHashMap = HashMap<String, Any>()
//
//            productHashMap[Constants.STOCK_QUANTITY] =
//                (cartItem.stock_quantity.toInt() - cartItem.cart_quantity.toInt()).toString()
            // Prepare the sold product details

            val soldProduct = SoldProduct(
                // Here the user id will be of product owner.
                cartItem.product_owner_id,
                cartItem.title,
                cartItem.price,
                cartItem.cart_quantity,
                cartItem.image,
                order.title,
                order.order_datetime,
                order.sub_total_amount,
                order.shipping_charge,
                order.total_amount,
                order.address
            )
            val documentReference = mFireStore.collection(Constants.SOLD_PRODUCTS)
                .document(cartItem.product_id)

            writeBatch.set(documentReference, soldProduct)
        }

        // Delete the list of cart items
        for (cart in cartList) {

            val documentReference = mFireStore.collection(Constants.CART_ITEMS)
                .document(cart.id)
            writeBatch.delete(documentReference)
        }

        writeBatch.commit().addOnSuccessListener {

            // TODO Step 4: Finally after performing all the operation notify the user with the success result.
            // START
            when (activity) {
                is CheckoutActivity -> {
                    activity.allDetailsUpdatedSuccessfully()
                    Log.d("Write batch success", "Write batch success")
                    // END
                }
                is PaymentCardDetails -> {
                    activity.allDetailsUpdatedSuccessfully()
                    Log.d("Write batch success", "Write batch success")
                    // END
                }

            }


        }.addOnFailureListener { e ->

            when (activity) {
                is CheckoutActivity -> {
                    activity.hideProgressDialog()
                    Log.d("Write batch failed", "Write batch failed")
                    Log.e(
                        activity.javaClass.simpleName,
                        "Error while updating all the details after order placed.",
                        e
                    )
                    // END
                }
                is PaymentCardDetails -> {
                    activity.hideProgressDialog()
                    Log.d("Write batch failed", "Write batch failed")
                    Log.e(
                        activity.javaClass.simpleName,
                        "Error while updating all the details after order placed.",
                        e
                    )
                }
                // Here call a function of base activity for transferring the result to it.

            }
        }
    }
        // TODO Step 5: Create a function to get the list of orders from cloud firestore.
        // START
        /**
         * A function to get the list of orders from cloud firestore.
         */
        fun getMyOrdersList(fragment: OrdersFragment) {
            mFireStore.collection(Constants.ORDERS)
                .orderBy("order_datetime", Query.Direction.DESCENDING)
                .whereEqualTo(Constants.USER_ID, getCurrentUserID())
                .get() // Will get the documents snapshots.
                .addOnSuccessListener { document ->
                    Log.e(fragment.javaClass.simpleName, document.documents.toString())
                    val list: ArrayList<Order> = ArrayList()

                    for (i in document.documents) {

                        val orderItem = i.toObject(Order::class.java)!!
                        orderItem.id = i.id

                        list.add(orderItem)
                    }

                    // TODO Step 7: Notify the success result to base class.
                    // START
                    fragment.populateOrdersListInUI(list)
                    // END
                }
                .addOnFailureListener { e ->
                    // Here call a function of base activity for transferring the result to it.

                    fragment.hideProgressDialog()

                    Log.e(fragment.javaClass.simpleName, "Error while getting the orders list.", e)
                }
        }

        //A function to get the list of sold products from the cloud firestore.

        fun getSoldProductsList(fragment: SoldProductsFragment) {
            // The collection name for SOLD PRODUCTS
            mFireStore.collection(Constants.SOLD_PRODUCTS)
                .whereEqualTo(Constants.USER_ID, getCurrentUserID())
                .get() // Will get the documents snapshots.
                .addOnSuccessListener { document ->
                    // Here we get the list of sold products in the form of documents.
                    Log.e(fragment.javaClass.simpleName, document.documents.toString())

                    // Here we have created a new instance for Sold Products ArrayList.
                    val list: ArrayList<SoldProduct> = ArrayList()

                    // A for loop as per the list of documents to convert them into Sold Products ArrayList.
                    for (i in document.documents) {

                        val soldProduct = i.toObject(SoldProduct::class.java)!!
                        soldProduct.id = i.id

                        list.add(soldProduct)
                    }

                    // TODO Step 3: Notify the success result to the base class.
                    // START
                    fragment.successSoldProductsList(list)
                    // END
                }
                .addOnFailureListener { e ->
                    // Hide the progress dialog if there is any error.
                    fragment.hideProgressDialog()

                    Log.e(
                        fragment.javaClass.simpleName,
                        "Error while getting the list of sold products.",
                        e
                    )
                }
        }
    }
