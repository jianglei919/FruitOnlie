package com.conestoga.group12.fruitonline.model

data class CartItem(
    val productItem: Product,
    val totalPrice: Double,
    val totalQuantity: Int,
)
