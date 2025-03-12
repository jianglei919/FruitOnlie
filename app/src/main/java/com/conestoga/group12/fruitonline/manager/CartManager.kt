package com.conestoga.group12.fruitonline.manager

import com.conestoga.group12.fruitonline.model.CartItem


class CartManager private constructor() {
    private val cartItems: MutableList<CartItem> = ArrayList()

    fun getCartItems(): List<CartItem> {
        return cartItems
    }

    fun addToCart(item: CartItem) {
        for (cartItem in cartItems) {
            val productItem = cartItem.productItem
            if (productItem != null) {
                if (productItem.productId == item.productItem!!.productId) {
                    // If the product exist, add quantity
                    cartItem.totalQuantity = cartItem.productItem!!.quantity + item.productItem!!.quantity
                    return
                }
            }
        }
        // If the product not exist, add new product
        cartItems.add(item)
    }

    fun removeFromCart(productId: String) {
        cartItems.removeIf { item: CartItem -> item.productItem!!.productId == productId }
    }

    fun clearCart() {
        cartItems.clear()
    }

    val totalPrice: Double
        get() {
            var total = 0.0
            for (item in cartItems) {
                total += item.productItem!!.price * item.productItem!!.quantity
            }
            return total
        }

    fun updateItemQuantity(productId: String, quantity: Int) {
        for (item in cartItems) {
            if (item.productItem!!.productId == productId) {
                item.totalQuantity = quantity
                break
            }
        }
    }

    val cartItemCount: Int
        get() = cartItems.size

    companion object {
        @JvmStatic
        @get:Synchronized
        var instance: CartManager? = null
            get() {
                if (field == null) {
                    field = CartManager()
                }
                return field
            }
            private set
    }
}
