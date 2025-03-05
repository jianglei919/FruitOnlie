package com.conestoga.group12.fruitonline.model

import java.io.Serializable

data class CartItem(
    var productItem: Product,
    var totalPrice: Double,
    var totalQuantity: Int,
) : Serializable
