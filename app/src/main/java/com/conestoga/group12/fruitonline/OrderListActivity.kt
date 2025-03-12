package com.conestoga.group12.fruitonline

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.conestoga.group12.fruitonline.adapter.OrderListAdapter
import com.conestoga.group12.fruitonline.enums.CollectionName
import com.conestoga.group12.fruitonline.model.Order
import com.conestoga.group12.fruitonline.utils.FirebaseAuthUtils.currentUserId
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class OrderListActivity : AppCompatActivity() {
    private lateinit var orderRecyclerView: RecyclerView
    private lateinit var emptyOrderLayout: LinearLayout
    private lateinit var goToProductsButton: Button
    private lateinit var orderListAdapter: OrderListAdapter
    private lateinit var orderList: MutableList<Order>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_list)

        orderRecyclerView = findViewById(R.id.orderRecyclerView)
        orderRecyclerView.setLayoutManager(LinearLayoutManager(this))

        emptyOrderLayout = findViewById(R.id.emptyOrderLayout)
        goToProductsButton = findViewById(R.id.goToProductsButton)

        // 添加按钮事件，跳转到产品列表
        goToProductsButton.setOnClickListener(View.OnClickListener { v: View? ->
            val intent = Intent(this@OrderListActivity, ProductActivity::class.java)
            startActivity(intent)
        })

        orderList = ArrayList()

        orderListAdapter = OrderListAdapter(orderList, this)
        orderRecyclerView.setAdapter(orderListAdapter)

        loadOrders()
    }

    private fun loadOrders() {
        val orderRef = FirebaseDatabase.getInstance().getReference(CollectionName.ORDERS.code)
        val currentUserId = currentUserId
        if (TextUtils.isEmpty(currentUserId)) {
            showEmptyOrderLayout()
            return
        }
        orderRef.child(currentUserId!!).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                orderList!!.clear()
                for (orderSnapshot in snapshot.children) {
                    val order = orderSnapshot.getValue(Order::class.java)
                    if (order != null) {
                        orderList!!.add(order)
                    }
                }

                if (orderList!!.isEmpty()) {
                    showEmptyOrderLayout()
                } else {
                    showOrderRecyclerView()
                }

                Log.e(TAG, "Successful to load orders. currentUserId=$currentUserId")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Failed to load orders. currentUserId=$currentUserId")
                showEmptyOrderLayout()
            }
        })
    }

    private fun showEmptyOrderLayout() {
        emptyOrderLayout!!.visibility = View.VISIBLE
        orderRecyclerView!!.visibility = View.GONE
    }

    private fun showOrderRecyclerView() {
        emptyOrderLayout!!.visibility = View.GONE
        orderRecyclerView!!.visibility = View.VISIBLE
        orderListAdapter!!.notifyDataSetChanged()
    }

    companion object {
        private const val TAG = "OrderListActivity"
    }
}