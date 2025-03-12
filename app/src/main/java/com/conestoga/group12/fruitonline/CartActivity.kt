package com.conestoga.group12.fruitonline

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.conestoga.group12.fruitonline.adapter.CartAdapter
import com.conestoga.group12.fruitonline.enums.CollectionName
import com.conestoga.group12.fruitonline.manager.CartManager.Companion.instance
import com.conestoga.group12.fruitonline.model.CartItem
import com.conestoga.group12.fruitonline.utils.FirebaseAuthUtils.currentUserId
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class CartActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var cartAdapter: CartAdapter
    private lateinit var cartItemList: MutableList<CartItem>
    private lateinit var totalPriceText: TextView
    private lateinit var checkoutButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        recyclerView = findViewById(R.id.recyclerView)
        totalPriceText = findViewById(R.id.totalPriceText)
        checkoutButton = findViewById(R.id.checkoutButton)

        recyclerView.setLayoutManager(LinearLayoutManager(this))
        cartItemList = instance!!.getCartItems().toMutableList()

        loadCartItems()

        cartAdapter = CartAdapter(cartItemList!!, this)
        recyclerView.setAdapter(cartAdapter)

        // 设置监听器
        cartAdapter!!.setOnCartChangedListener(CartAdapter.OnCartChangedListener {
            val totalPrice = calculateTotalPrice()
            updateCheckoutButtonState(totalPrice)
        })

        val totalPrice = calculateTotalPrice()

        updateCheckoutButtonState(totalPrice)

        checkoutButton.setOnClickListener(View.OnClickListener { v: View? ->
            val intent = Intent(this@CartActivity, CheckoutActivity::class.java)
            intent.putExtra("cartItems", ArrayList(cartItemList))
            startActivity(intent)
        })
    }

    private fun loadCartItems() {
        val cartItemRef =
            FirebaseDatabase.getInstance().getReference(CollectionName.CART_ITEMS.code)
        val currentUserId = currentUserId
        if (!TextUtils.isEmpty(currentUserId)) {
            cartItemRef.child(currentUserId!!).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    instance!!.clearCart()
                    for (data in snapshot.children) {
                        val cartItem = data.getValue(CartItem::class.java)
                        instance!!.addToCart(cartItem!!)
                    }
                    updateCheckoutButtonState(calculateTotalPrice())

                    cartAdapter!!.notifyDataSetChanged()

                    Log.e(TAG, "Successful to load cart items. currentUserId=$currentUserId")
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e(TAG, "Failed to load cart items. currentUserId=$currentUserId")
                }
            })
        }
    }

    fun calculateTotalPrice(): Double {
        var totalPrice = 0.0
        for (item in cartItemList!!) {
            val productItem = item.productItem
            totalPrice += productItem!!.price * item.totalQuantity
        }
        totalPriceText!!.text = String.format("Total: $%.2f", totalPrice)
        return totalPrice
    }

    private fun updateCheckoutButtonState(totalPrice: Double) {
        checkoutButton!!.setBackgroundResource(R.drawable.checkout_button_selector)
        checkoutButton!!.isEnabled = totalPrice > 0
    }

    companion object {
        private const val TAG = "CartActivity"
    }
}