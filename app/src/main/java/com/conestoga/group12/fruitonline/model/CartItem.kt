package com.conestoga.group12.fruitonline.model

import java.io.Serializable

data class CartItem(
    var productItem: Product? = null,
    var totalPrice: Double = 0.0,
    var totalQuantity: Int = 0,
) : Serializable {
    constructor() : this(null, 0.0, 0)
}
