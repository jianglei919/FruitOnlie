package com.conestoga.group12.fruitonline.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.conestoga.group12.fruitonline.ProductDetailActivity
import com.conestoga.group12.fruitonline.R
import com.conestoga.group12.fruitonline.constant.CommonConstant
import com.conestoga.group12.fruitonline.model.Product
import com.conestoga.group12.fruitonline.utils.ImageUtils.loadImageFromStorage

class OrderProductAdapter(private val productList: List<Product>, private val context: Context) :
    RecyclerView.Adapter<OrderProductAdapter.ProductViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.row_layout, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = productList[position]

        holder.productNameTextView.text = product.productName
        holder.productPriceTextView.text = String.format("$%.2f", product.price)
        holder.productDescriptionTextView.text = product.description

        // loading product images
        loadImageFromStorage(
            context, holder.productImageImageView, product.productImageUrl,
            product.productName, CommonConstant.IMAGE_THUMBNAIL_TYPE
        )

        // clicked the cardView goes to the ProductDetailActivity
        holder.itemView.setOnClickListener { v: View? ->
            val intent = Intent(context, ProductDetailActivity::class.java)
            intent.putExtra("productId", product.productId)
            intent.putExtra("productName", product.productName)
            intent.putExtra("productImageUrl", product.productImageUrl)
            intent.putExtra("productImageDetailUrl", product.productImageDetailUrl)
            intent.putExtra("price", product.price)
            intent.putExtra("store", product.store)
            intent.putExtra("description", product.description)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return productList.size
    }

    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var productNameTextView: TextView = itemView.findViewById(R.id.productName)
        var productPriceTextView: TextView = itemView.findViewById(R.id.productPrice)
        var productDescriptionTextView: TextView = itemView.findViewById(R.id.productDescription)
        var productImageImageView: ImageView = itemView.findViewById(R.id.productImage)
    }
}
