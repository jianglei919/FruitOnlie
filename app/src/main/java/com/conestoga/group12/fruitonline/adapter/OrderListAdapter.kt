package com.conestoga.group12.fruitonline.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.conestoga.group12.fruitonline.OrderDetailActivity
import com.conestoga.group12.fruitonline.R
import com.conestoga.group12.fruitonline.model.Order
import com.conestoga.group12.fruitonline.utils.DateTimeUtils.formatTimestamp

class OrderListAdapter(private val orderList: List<Order>, private val context: Context) :
    RecyclerView.Adapter<OrderListAdapter.OrderViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.row_order_item, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orderList[position]

        holder.orderIdTextView.append(order.orderId)
        holder.orderStatusTextView.append(order.status)
        holder.totalPriceTextView.append(String.format("$%.2f", order.totalPrice))
        holder.orderTimeTextView.append(formatTimestamp(order.orderTime))

        holder.itemView.setOnClickListener { v: View? ->
            val intent = Intent(context, OrderDetailActivity::class.java)
            intent.putExtra("orderData", order)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return orderList.size
    }

    class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal val orderIdTextView: TextView = itemView.findViewById(R.id.orderId)
        val orderStatusTextView: TextView = itemView.findViewById(R.id.orderStatus)
        val totalPriceTextView: TextView = itemView.findViewById(R.id.totalPrice)
        val orderTimeTextView: TextView = itemView.findViewById(R.id.orderTime)
    }
}
