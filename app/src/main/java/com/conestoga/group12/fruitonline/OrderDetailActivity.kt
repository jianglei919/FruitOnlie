package com.conestoga.group12.fruitonline

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.conestoga.group12.fruitonline.adapter.ProductAdapter
import com.conestoga.group12.fruitonline.model.CartItem
import com.conestoga.group12.fruitonline.model.Order
import com.conestoga.group12.fruitonline.model.Product
import com.conestoga.group12.fruitonline.utils.DateTimeUtils.formatTimestamp
import com.google.android.gms.common.util.CollectionUtils
import java.util.Objects
import java.util.stream.Collectors

class OrderDetailActivity : AppCompatActivity() {
    private lateinit var orderIdTextView: TextView
    private lateinit var orderTimeTextView: TextView
    private lateinit var orderStatusTextView: TextView
    private lateinit var paymentNameTextView: TextView
    private lateinit var paymentAddressTextView: TextView
    private lateinit var paymentEmailTextView: TextView
    private lateinit var paymentPhoneTextView: TextView
    private lateinit var totalPriceTextView: TextView
    private lateinit var productRecyclerView: RecyclerView
    private lateinit var productAdapter: ProductAdapter // Adapter for product list

    private var productList: List<Product> = ArrayList() // Assume a ProductItem model

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_detail)

        // Initialize Views
        orderIdTextView = findViewById(R.id.orderId)
        orderTimeTextView = findViewById(R.id.orderTime)
        orderStatusTextView = findViewById(R.id.orderStatus)
        paymentNameTextView = findViewById(R.id.paymentName)
        paymentAddressTextView = findViewById(R.id.paymentAddress)
        paymentEmailTextView = findViewById(R.id.paymentEmail)
        paymentPhoneTextView = findViewById(R.id.paymentPhone)
        totalPriceTextView = findViewById(R.id.totalPrice)
        productRecyclerView = findViewById(R.id.productRecyclerView)

        // Sample data passed via Intent or fetched from Firebase
        val intent = intent
        val order = intent.getSerializableExtra("orderData") as Order? // Deserialize order object

        if (order != null) {
            // Populate order details
            orderIdTextView.append(order.orderId)
            orderTimeTextView.append(formatTimestamp(order.orderTime))
            orderStatusTextView.append(order.status)

            // Populate payment info
            val paymentInfo = order.paymentInfo
            if (paymentInfo != null) {
                paymentNameTextView.append(paymentInfo.firstName + " " + paymentInfo.lastName)
                paymentAddressTextView.append(paymentInfo.address + ", " + paymentInfo.city + ", " + paymentInfo.state + " " + paymentInfo.postalCode)
                paymentEmailTextView.append(paymentInfo.email)
                paymentPhoneTextView.append(paymentInfo.phone)
            }

            // Populate product list
            val cartItemList = order.productItemList
            if (!CollectionUtils.isEmpty(cartItemList)) {
                productList = cartItemList.stream()
                    .map { obj: CartItem? -> obj!!.productItem }
                    .filter { obj: Product? -> Objects.nonNull(obj) }
                    .collect(Collectors.toList()) as List<Product>
            }
            productRecyclerView.setLayoutManager(LinearLayoutManager(this))
            productAdapter = ProductAdapter(productList, this)
            productRecyclerView.setAdapter(productAdapter)

            // Set total price
            totalPriceTextView.append(String.format("$%.2f", order.totalPrice))
        }
    }
}