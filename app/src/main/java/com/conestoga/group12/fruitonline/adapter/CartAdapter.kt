package com.conestoga.group12.fruitonline.adapter

import android.content.Context
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.conestoga.group12.fruitonline.R
import com.conestoga.group12.fruitonline.adapter.CartAdapter.CartViewHolder
import com.conestoga.group12.fruitonline.constant.CommonConstant
import com.conestoga.group12.fruitonline.enums.CollectionName
import com.conestoga.group12.fruitonline.manager.CartManager.Companion.instance
import com.conestoga.group12.fruitonline.model.CartItem
import com.conestoga.group12.fruitonline.utils.FirebaseAuthUtils.currentUserId
import com.conestoga.group12.fruitonline.utils.ImageUtils.loadImageFromStorage
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class CartAdapter(private val cartItemList: MutableList<CartItem>, private val context: Context) :
    RecyclerView.Adapter<CartViewHolder>() {
    private var onCartChangedListener: OnCartChangedListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.row_cart_item, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        Log.d(TAG, "Binding view holder for position: $position")

        val cartItem = cartItemList[position]
        val productItem = cartItem.productItem

        if (productItem != null) {
            holder.productNameTextView.text = productItem.productName
        }
        holder.productPriceTextView.text = String.format("$%.2f", cartItem.totalPrice)
        holder.quantityTextView.text = cartItem.totalQuantity.toString()
        loadImageFromStorage(
            context, holder.productImageView, productItem?.productImageUrl,
            productItem?.productName, CommonConstant.IMAGE_THUMBNAIL_TYPE
        )

        // 减少
        holder.decreaseQuantityButton.setOnClickListener { view: View? ->
            if (cartItem.totalQuantity > 1) {
                cartItem.totalQuantity = cartItem.totalQuantity - 1
                cartItem.totalPrice = cartItem.productItem!!.price * cartItem.totalQuantity
                updateOrDeleteCartItemFromDatabase(cartItem)
                notifyItemChanged(position) // 刷新当前项
            } else {
                removeCartItemFromDatabase(cartItem)
                cartItemList.removeAt(position) // 从列表中移除项
                productItem?.let { instance!!.removeFromCart(it.productId) }
                notifyItemRemoved(position) // 通知 RecyclerView 项已移除
            }
            // 通知购物车状态发生变化
            if (onCartChangedListener != null) {
                onCartChangedListener!!.onCartChanged()
            }
            Log.d(TAG, "Cart items: $cartItemList")
        }

        // 增加
        holder.increaseQuantityButton.setOnClickListener { view: View? ->
            if (cartItem.totalQuantity >= 9) {
                Toast.makeText(
                    context,
                    "The quantity has exceeded the maximum limit.",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                cartItem.totalQuantity = cartItem.totalQuantity + 1
                cartItem.totalPrice = cartItem.productItem!!.price * cartItem.totalQuantity
                updateOrDeleteCartItemFromDatabase(cartItem)
                notifyItemChanged(position) // 刷新当前项
            }
            // 通知购物车状态发生变化
            if (onCartChangedListener != null) {
                onCartChangedListener!!.onCartChanged()
            }
            Log.d(TAG, "Cart items: $cartItemList")
        }

        holder.removeButton.setOnClickListener { v: View? ->
            removeCartItemFromDatabase(cartItem)
            cartItemList.removeAt(position) // 从列表中移除项
            productItem?.let { instance!!.removeFromCart(it.productId) }
            notifyItemRemoved(position) // 通知 RecyclerView 项已移除

            // 通知购物车状态发生变化
            if (onCartChangedListener != null) {
                onCartChangedListener!!.onCartChanged()
            }
        }
    }

    fun removeCartItemFromDatabase(removedCartItem: CartItem) {
        val cartItemRef =
            FirebaseDatabase.getInstance().getReference(CollectionName.CART_ITEMS.code)
        val currentUserId = currentUserId
        if (!TextUtils.isEmpty(currentUserId)) {
            val productItemRef =
                removedCartItem.productItem?.let { cartItemRef.child(currentUserId!!).child(it.productId) }
            // remove
            productItemRef?.removeValue()?.addOnCompleteListener { task: Task<Void?> ->
                if (task.isSuccessful) {
                    Log.i(
                        TAG,
                        "Successful to remove cart item. currentUserId=" + currentUserId + ", productId=" + removedCartItem.productItem!!.productId
                    )
                } else {
                    Log.i(
                        TAG,
                        "Failed to remove cart item. currentUserId=" + currentUserId + ", productId=" + removedCartItem.productItem!!.productId
                    )
                }
            }
        } else {
            Log.e(TAG, "Failed to get current user id.")
        }
    }

    fun updateOrDeleteCartItemFromDatabase(newCartItem: CartItem) {
        val cartItemRef =
            FirebaseDatabase.getInstance().getReference(CollectionName.CART_ITEMS.code)
        val currentUserId = currentUserId
        if (!TextUtils.isEmpty(currentUserId)) {
            val productItemRef =
                newCartItem.productItem?.let { cartItemRef.child(currentUserId!!).child(it.productId) }

            // delete
            if (newCartItem.totalQuantity == 0) {
                removeCartItemFromDatabase(newCartItem)
                return
            }

            // update
            productItemRef?.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    productItemRef.setValue(newCartItem)
                        .addOnCompleteListener { task: Task<Void?> ->
                            if (task.isSuccessful) {
                                Log.i(
                                    TAG,
                                    "Cart item updated successfully. productId= " + newCartItem.productItem!!.productId + ", currentUserId= " + currentUserId
                                )
                            } else {
                                Log.i(
                                    TAG,
                                    "Failed to update cart item. productId= " + newCartItem.productItem!!.productId + ", currentUserId= " + currentUserId
                                )
                            }
                        }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e(
                        TAG,
                        "Database error. " + error.message + ", currentUserId=" + currentUserId + ", productId=" + newCartItem.productItem!!.productId
                    )
                }
            })
        } else {
            Log.e(TAG, "Failed to get current user id.")
        }
    }

    override fun getItemCount(): Int {
        return cartItemList.size
    }

    class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var productNameTextView: TextView = itemView.findViewById(R.id.productName)
        var productPriceTextView: TextView = itemView.findViewById(R.id.productPrice)
        var quantityTextView: TextView = itemView.findViewById(R.id.quantity)
        var productImageView: ImageView = itemView.findViewById(R.id.productImage)
        var decreaseQuantityButton: Button = itemView.findViewById(R.id.decreaseQuantityButton)
        var increaseQuantityButton: Button = itemView.findViewById(R.id.increaseQuantityButton)
        var removeButton: Button = itemView.findViewById(R.id.removeButton)
    }

    fun interface OnCartChangedListener {
        fun onCartChanged()
    }

    fun setOnCartChangedListener(listener: OnCartChangedListener?) {
        this.onCartChangedListener = listener
    }

    companion object {
        private const val TAG = "CartAdapter"
    }
}
