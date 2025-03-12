package com.conestoga.group12.fruitonline

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.conestoga.group12.fruitonline.constant.CommonConstant
import com.conestoga.group12.fruitonline.enums.CollectionName
import com.conestoga.group12.fruitonline.manager.CartManager.Companion.instance
import com.conestoga.group12.fruitonline.model.CartItem
import com.conestoga.group12.fruitonline.model.Product
import com.conestoga.group12.fruitonline.utils.FirebaseAuthUtils.currentUserId
import com.conestoga.group12.fruitonline.utils.ImageUtils.loadImageFromStorage
import com.google.android.gms.tasks.Task
import com.google.android.material.badge.ExperimentalBadgeUtils
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.FirebaseDatabase

class ProductDetailActivity : AppCompatActivity() {
    private lateinit var productImageView: ImageView
    private lateinit var productNameTextView: TextView
    private lateinit var productPriceTextView: TextView
    private lateinit var productDescriptionTextView: TextView
    private lateinit var productStoreTextView: TextView
    private lateinit var quantityInputEditText: EditText
    private lateinit var addToCartButton: Button
    private lateinit var fabGoToCart: FloatingActionButton

    private lateinit var productId: String
    private lateinit var productName: String
    private lateinit var productImageUrl: String
    private lateinit var productImageDetailUrl: String
    private lateinit var productDescription: String
    private lateinit var store: String
    private var productPrice = 0.0

    @ExperimentalBadgeUtils
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_detail)

        // init ui component
        productImageView = findViewById(R.id.productImage)
        productNameTextView = findViewById(R.id.productName)
        productPriceTextView = findViewById(R.id.productPrice)
        productDescriptionTextView = findViewById(R.id.productDescription)
        productStoreTextView = findViewById(R.id.store)
        quantityInputEditText = findViewById(R.id.quantityInput)
        addToCartButton = findViewById(R.id.addToCartButton)
        fabGoToCart = findViewById(R.id.fabGoToCart)

        // get intent data
        val intent = intent
        productId = intent.getStringExtra("productId").toString()
        productName = intent.getStringExtra("productName").toString()
        productImageUrl = intent.getStringExtra("productImageUrl").toString()
        productImageDetailUrl = intent.getStringExtra("productImageDetailUrl").toString()
        productPrice = intent.getDoubleExtra("price", 0.0)
        store = intent.getStringExtra("store").toString()
        productDescription = intent.getStringExtra("description").toString()

        // set product detail
        productNameTextView.setText(productName)
        productPriceTextView.setText(String.format("$%.2f", productPrice))
        productDescriptionTextView.setText(productDescription)
        productStoreTextView.setText(store)

        // loading product images
        loadImageFromStorage(
            this,
            productImageView,
            productImageDetailUrl,
            productName,
            CommonConstant.IMAGE_DETAIL_TYPE
        )

        addToCartButton.setOnClickListener{ v: View? ->
            val quantityStr = quantityInputEditText.getText().toString().trim { it <= ' ' }
            if (quantityStr.isEmpty() || quantityStr.toInt() <= 0) {
                Toast.makeText(this, "Please enter a valid quantity.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (quantityStr.toInt() > 9) {
                Toast.makeText(
                    this,
                    "The quantity has exceeded the maximum limit.",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            val quantity = quantityStr.toInt()

            val productItem = Product(
                productId, productName, productImageUrl,
                productImageDetailUrl, productPrice, store, productDescription
            )

            instance!!.addToCart(
                CartItem(productItem, productItem.price, quantity)
            )

            // 把购物车中的数据存到数据库中, 用userId作主键
            val cartItems = instance!!.getCartItems()
            val currentUserId = currentUserId
            if (!TextUtils.isEmpty(currentUserId)) {
                val cartItemRef = FirebaseDatabase.getInstance()
                    .getReference(CollectionName.CART_ITEMS.code)
                    .child(currentUserId!!)
                for (cartItem in cartItems) {
                    cartItem.productItem?.let {
                        cartItemRef.child(it.productId).setValue(cartItem)
                            .addOnCompleteListener { task: Task<Void?> ->
                                if (task.isSuccessful) {
                                    Log.i(
                                        TAG,
                                        "$quantity $productName save to database. currentUserId=$currentUserId"
                                    )
                                } else {
                                    Log.i(
                                        TAG,
                                        "$quantity $productName failed to save cart item to database.. currentUserId=$currentUserId"
                                    )
                                }
                            }
                    }
                }
            } else {
                Log.w(TAG, "Failed to get current user id.")
            }
            Toast.makeText(this, "$quantity $productName added to cart", Toast.LENGTH_SHORT).show()
        }

        fabGoToCart.setOnClickListener(View.OnClickListener { v: View ->
            v.animate().scaleX(0.9f).scaleY(0.9f).setDuration(100).withEndAction {
                v.animate().scaleX(1f).scaleY(1f).setDuration(100)
                val cartIntent = Intent(this@ProductDetailActivity, CartActivity::class.java)
                startActivity(cartIntent)
            }
        })
    }

    companion object {
        private const val TAG = "ProductDetailActivity"
    }
}