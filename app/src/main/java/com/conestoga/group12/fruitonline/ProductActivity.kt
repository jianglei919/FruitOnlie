package com.conestoga.group12.fruitonline

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.conestoga.group12.fruitonline.adapter.ProductAdapter
import com.conestoga.group12.fruitonline.enums.CollectionName
import com.conestoga.group12.fruitonline.model.Product
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ProductActivity : AppCompatActivity() {
    private lateinit var fabCart: FloatingActionButton
    private lateinit var fabOrder: FloatingActionButton
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ProductAdapter
    private lateinit var productList: MutableList<Product>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.setLayoutManager(LinearLayoutManager(this))

        productList = ArrayList()

        adapter = ProductAdapter(productList, this)
        recyclerView.setAdapter(adapter)

        loadProducts()

        fabCart = findViewById(R.id.fabCart)
        fabCart.setOnClickListener(View.OnClickListener { v: View? ->
            val intent = Intent(this@ProductActivity, CartActivity::class.java)
            startActivity(intent)
        })

        fabOrder = findViewById(R.id.fabOrder)
        fabOrder.setOnClickListener(View.OnClickListener { v: View? ->
            val orderIntent = Intent(this@ProductActivity, OrderListActivity::class.java)
            startActivity(orderIntent)
        })
    }

    private fun loadProducts() {
        val productsRef = FirebaseDatabase.getInstance().getReference(CollectionName.PRODUCTS.code)
        productsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (data in snapshot.children) {
                    val product = data.getValue(Product::class.java)
                    product?.let { productList.add(it) }
                }
                adapter!!.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Failed to load products")
            }
        })
    }

    companion object {
        private const val TAG = "ProductActivity"
    }
}